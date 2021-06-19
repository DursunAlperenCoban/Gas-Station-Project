package com.example.petrogps2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.petrogps2.model.Feature
import com.example.petrogps2.model.Reqres
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_maps.*
import okhttp3.*
import java.io.IOException
import java.time.LocalDate


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private val dataList: MutableList<Feature> = mutableListOf()
    private lateinit var myAdapter: MyAdapter
    private val LOCATION_PERMISSION_REQUEST = 1
    private val GOOGLE_API_KEY = "YOUR_API_KEY"
    private val client = OkHttpClient()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    var alreadyExecuted = false
    var markerOpen = false
    var currentstationcoords = ""

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }


    private fun getLocationAccess() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            getLocationUpdates()
            startLocationUpdates()
        }
        else
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                getLocationAccess()
            }
            else {
                Toast.makeText(this, "User has not granted location access permission", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun getLocationUpdates() {
        locationRequest = LocationRequest()
        locationRequest.interval = 30000
        locationRequest.fastestInterval = 20000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationCallback = object : LocationCallback() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.locations.isNotEmpty()) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        if (!alreadyExecuted){
                            val latLng = LatLng(location.latitude, location.longitude)
                            val markerOptions = MarkerOptions().position(latLng)
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                            alreadyExecuted = true
                        }

                        getNearestGasStation(location)
                        //val latLng = LatLng(location.latitude, location.longitude)
                        //val markerOptions = MarkerOptions().position(latLng)
                        //mMap.addMarker(markerOptions)
                        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
        )
    }

    private fun run(url: String){
        val request = Request.Builder()
                .url(url)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }
            override fun onResponse(call: Call, response: Response) = println(response.body()?.string())

        })

    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun getNearestGasStation(location: Location) {
/*
        val gasST = java.lang.StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
        gasST.append("location=" + location.latitude.toString() + "," + location.longitude)
        gasST.append("&rankby=distance")
        gasST.append("&type=" + "gas_station")
        gasST.append("&key=$GOOGLE_API_KEY")
*/
        //run("https://search-maps.yandex.ru/v1/?text=Benzin%20istasyonu%20Istanbul&type=biz&lang=tr_TR&ll="+location.latitude+"," + location.longitude + "&apikey=YOUR_API_KEY")
        //println("test12313123123213123")

        //myAdapter = MyAdapter(dataList)

        /*
        my_recycler_view.layoutManager = LinearLayoutManager(this)
        my_recycler_view.addItemDecoration(DividerItemDecoration(this,OrientationHelper.VERTICAL))
        my_recycler_view.adapter = myAdapter
        */

        //my_text_view2.text = "testtest"
        val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)



        var searchArea = sp.getInt("searcharea",0).toFloat() / 10
        if (searchArea == 0.00f){
            searchArea = 0.03f
        }
        val tomorrowdate = LocalDate.now().plusDays(1)
        date2.text = tomorrowdate.toString()

        AndroidNetworking.initialize(this)
        AndroidNetworking.get("https://search-maps.yandex.ru/v1/?text=gas_station&type=biz&lang=tr_TR&ll="+location.longitude+"," + location.latitude + "&spn="+searchArea.toString()+","+searchArea.toString()+"&results=400&apikey=YOUR_API_KEY")
            .build()
            .getAsObject(Reqres::class.java, object : ParsedRequestListener<Reqres>{
                override fun onResponse(response: Reqres) {
                    dataList.addAll(response.features)

                    //println(dataList[0].geometry.coordinates[0].toString())

                    for (data in dataList) {
                        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude,location.longitude), 15f))
                        val latLng = LatLng(data.geometry.coordinates[1], data.geometry.coordinates[0])
                        val markerOptions = MarkerOptions()

                        if (data.properties.name.contains("Shell")){
                            markerOptions.position(latLng).icon(bitmapDescriptorFromVector(applicationContext,R.drawable.ic_shellv2)).title(data.properties.name + " " + data.geometry.coordinates[1]+ " " + data.geometry.coordinates[0]).snippet(data.properties.companyMetaData.address)
                            mMap.addMarker(markerOptions)
                        }
                        else if (data.properties.name.contains("BP")){
                            markerOptions.position(latLng).icon(bitmapDescriptorFromVector(applicationContext,R.drawable.ic_bpv)).title(data.properties.name+ " " + data.geometry.coordinates[1]+ " " + data.geometry.coordinates[0]).snippet(data.properties.companyMetaData.address)
                            mMap.addMarker(markerOptions)
                        }
                        else if (data.properties.name.contains("Total")){
                            markerOptions.position(latLng).icon(bitmapDescriptorFromVector(applicationContext,R.drawable.ic_totalv)).title(data.properties.name+ " " + data.geometry.coordinates[1]+ " " + data.geometry.coordinates[0]).snippet(data.properties.companyMetaData.address)
                            mMap.addMarker(markerOptions)
                        }
                        else if (data.properties.name.contains("Petrol Ofisi")){
                            markerOptions.position(latLng).icon(bitmapDescriptorFromVector(applicationContext,R.drawable.ic_pov)).title(data.properties.name+ " " + data.geometry.coordinates[1]+ " " + data.geometry.coordinates[0]).snippet(data.properties.companyMetaData.address)
                            mMap.addMarker(markerOptions)
                        }
                        else if (data.properties.name.contains("Opet")){
                            markerOptions.position(latLng).icon(bitmapDescriptorFromVector(applicationContext,R.drawable.ic_opetv)).title(data.properties.name+ " " + data.geometry.coordinates[1]+ " " + data.geometry.coordinates[0]).snippet(data.properties.companyMetaData.address)
                            mMap.addMarker(markerOptions)
                        }
                        else if (data.properties.name.contains("Aytemiz")){
                            markerOptions.position(latLng).icon(bitmapDescriptorFromVector(applicationContext,R.drawable.ic_aytemizv)).title(data.properties.name+ " " + data.geometry.coordinates[1]+ " " + data.geometry.coordinates[0]).snippet(data.properties.companyMetaData.address)
                            mMap.addMarker(markerOptions)
                        }
                        else if (data.properties.name.contains("Türk Petrol")){
                            markerOptions.position(latLng).icon(bitmapDescriptorFromVector(applicationContext,R.drawable.ic_tpv)).title(data.properties.name+ " " + data.geometry.coordinates[1]+ " " + data.geometry.coordinates[0]).snippet(data.properties.companyMetaData.address)
                            mMap.addMarker(markerOptions)
                        }
                        else if (data.properties.name.contains("GO")){
                            markerOptions.position(latLng).icon(bitmapDescriptorFromVector(applicationContext,R.drawable.ic_gov)).title(data.properties.name+ " " + data.geometry.coordinates[1]+ " " + data.geometry.coordinates[0]).snippet(data.properties.companyMetaData.address)
                            mMap.addMarker(markerOptions)
                        }
                        else if (data.properties.name.contains("Alpet")){
                            markerOptions.position(latLng).icon(bitmapDescriptorFromVector(applicationContext,R.drawable.ic_alpetv)).title(data.properties.name+ " " + data.geometry.coordinates[1]+ " " + data.geometry.coordinates[0]).snippet(data.properties.companyMetaData.address)
                            mMap.addMarker(markerOptions)
                        }
                        /*
                        else{
                            markerOptions.position(latLng).icon(bitmapDescriptorFromVector(applicationContext,R.drawable.ic_gasicondefault)).title(data.properties.name).title(data.properties.name).snippet(data.properties.companyMetaData.address)
                            mMap.addMarker(markerOptions)
                        }
                        */
                        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                        mMap.setOnMarkerClickListener(GoogleMap.OnMarkerClickListener { markerOptions ->

                            //markerOptions.showInfoWindow()
                            //my_text_view2.text = markerOptions.snippet

                            //db.collection("Prices").document(markerOptions.snippet.split(",").toTypedArray()[markerOptions.snippet.split(",").toTypedArray().size - 2]).collection(markerOptions.snippet.split(",").toTypedArray()[markerOptions.snippet.split(",").toTypedArray().size - 3].replace("\\s".toRegex(),"")).document(getGasStationName(markerOptions.title))

                            val il = markerOptions.snippet.split(",").toTypedArray()[markerOptions.snippet.split(",").toTypedArray().size - if(markerOptions.snippet.contains("Türkiye")){2}else{1}].replace("\\s".toRegex(),"")
                            val ilce = markerOptions.snippet.split(",").toTypedArray()[markerOptions.snippet.split(",").toTypedArray().size - if(markerOptions.snippet.contains("Türkiye")){3}else{2}].replace("\\s".toRegex(),"")

                            currentstationcoords = markerOptions.title.toString()
                            //db.collection("Prices").document(markerOptions.snippet.split(",").toTypedArray()[markerOptions.snippet.split(",").toTypedArray().size - 2]).collection(markerOptions.snippet.split(",").toTypedArray()[markerOptions.snippet.split(",").toTypedArray().size - 3].replace("\\s".toRegex(),"")).document(getGasStationName(markerOptions.title))

                            if (!markerOptions.title.contains("Opet")) {
                                db.collection("Prices").document(il).collection(ilce).document(getGasStationName(markerOptions.title))
                                        .get()
                                        .addOnSuccessListener { result ->
                                            //my_text_view2.text = result.id + " " + result.data?.toString()
                                            var sb = StringBuilder()
                                            var benzin = result.data?.getValue("benzin")
                                            var motorin = result.data?.getValue("motorin")
                                            var otogaz = result.data?.getValue("otogaz")
                                            var station = result.id
                                            sb.append("$station benzin: $benzin motorin: $motorin otogaz: $otogaz")

                                            //my_text_view2.text = sb.toString() + il + " " + ilce
                                            benzinvalue.text = benzin.toString() + " ₺"
                                            motorinvalue.text = motorin.toString() + " ₺"
                                            otogazvalue.text = otogaz.toString() + " ₺"
                                            stationname.text = markerOptions.title.split(" ")[0]
                                            stationadress.text = ilce + ", " + il

                                            db.collection("Predictions").document("2021-05-05")
                                                    .get()
                                                    .addOnSuccessListener { result ->
                                                        var benzinf = result.data?.getValue("benzin")
                                                        var motorinf = result.data?.getValue("motorin")
                                                        var otogazf = result.data?.getValue("otogaz")
                                                        var benzinEm = result.data?.getValue("rmse_b")
                                                        var motorinEm = result.data?.getValue("rmse_m")
                                                        var otogazEm = result.data?.getValue("rmse_o")
                                                        benzinfuture.text = benzinf.toString().substring(0,4)
                                                        motorinfuture.text = motorinf.toString().substring(0,4)
                                                        otogazfuture.text = otogazf.toString().substring(0,4)
                                                        benzinerrormargin.text = benzinEm.toString()
                                                        motorinerrormargin.text = motorinEm.toString()
                                                        otogazerrormargin.text = otogazEm.toString()
                                                        //var benzinP : Float? =  ((benzinf.toString().replaceFirst(",",".").toFloat() - benzin.toString().replaceFirst(",",".").toFloat()) / benzin.toString().replaceFirst(",",".").toFloat()) * 100
                                                        //benzinpers.text = benzinP.toString()
                                                    }
                                        }
                            }else{
                                db.collection("Prices").document(il).collection(ilce).document(getGasStationName(markerOptions.title))
                                        .get()
                                        .addOnSuccessListener {result ->
                                            //my_text_view2.text = result.id + " " + result.data?.toString()
                                            var sb = StringBuilder()
                                            var benzin = result.data?.getValue("benzin")
                                            var motorin= result.data?.getValue("motorin")
                                            var station = result.id

                                            sb.append("$station benzin: $benzin motorin: $motorin")

                                            //my_text_view2.text =  sb.toString() + il + " " + ilce
                                            benzinvalue.text = benzin.toString() + " ₺"
                                            motorinvalue.text = motorin.toString() + " ₺"
                                            otogazvalue.text = "No Value"
                                            stationname.text = markerOptions.title.split(" ")[0]
                                            stationadress.text = ilce + ", " + il
                                            db.collection("Predictions").document("2021-05-05")
                                                    .get()
                                                    .addOnSuccessListener { result ->
                                                        var benzinf = result.data?.getValue("benzin")
                                                        var motorinf = result.data?.getValue("motorin")
                                                        var otogazf = result.data?.getValue("otogaz")
                                                        var benzinEm = result.data?.getValue("rmse_b")
                                                        var motorinEm = result.data?.getValue("rmse_m")
                                                        var otogazEm = result.data?.getValue("rmse_o")
                                                        benzinfuture.text = benzinf.toString()
                                                        motorinfuture.text = motorinf.toString()
                                                        otogazfuture.text = otogazf.toString()
                                                        benzinerrormargin.text = benzinEm.toString()
                                                        motorinerrormargin.text = motorinEm.toString()
                                                        otogazerrormargin.text = otogazEm.toString()
                                                        //var benzinP : Float? =  ((benzinf.toString().replaceFirst(",",".").toFloat() - benzin.toString().replaceFirst(",",".").toFloat()) / benzin.toString().replaceFirst(",",".").toFloat()) * 100
                                                        //benzinpers.text = benzinP.toString()
                                                    }

                                        }
                            }



                            //val layoutTransition: LayoutTransition = my_constraint_layout.getLayoutTransition()
                            //layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

                            //my_recycler_view.animate().translationYBy(120f).translationY(0f)

                            //        .translationX(my_recycler_view.width.toFloat())

                            if (!markerOpen) {
                                markerOpen=false

                                my_text_view2.visibility = if (my_text_view2.visibility == View.VISIBLE) {
                                    my_text_view2.animate()
                                            .translationYBy(0f)
                                            .translationY(my_text_view2.height.toFloat())
                                            .alpha(1.0f)
                                            .setDuration(300)
                                    View.GONE
                                } else {
                                    markerOpen=true
                                    my_text_view2.animate()
                                            .translationYBy(my_text_view2.height.toFloat())
                                            .translationY(0f)
                                            .alpha(1.0f)
                                            .setDuration(300)
                                    View.VISIBLE
                                }

                            }
                            println("testtest" + markerOptions.title.toString())


                            true
                        })

                        my_return_button2.setOnClickListener{

                            my_text_view2.visibility = if (my_text_view2.visibility == View.VISIBLE){
                                markerOpen=false
                                my_text_view2.animate()
                                        .translationYBy(0f)
                                        .translationY(my_text_view2.height.toFloat())
                                        .alpha(1.0f)
                                        .setDuration(300)
                                View.GONE
                            } else{
                                markerOpen=true
                                my_text_view2.animate()
                                        .translationYBy(my_text_view2.height.toFloat())
                                        .translationY(0f)
                                        .alpha(1.0f)
                                        .setDuration(300)
                                View.VISIBLE
                            }

                        }

                        my_route_button.setOnClickListener{
                            val myRouteCoords = currentstationcoords.split(" ")
                            if (myRouteCoords.size == 1){
                                false
                            }else{
                                val uri = "http://maps.google.com/maps?saddr=" + location.latitude + "," + location.longitude + "&daddr=" +  myRouteCoords[myRouteCoords.size - 2 ] + "," + myRouteCoords[myRouteCoords.size - 1]
                                //val uri = String.format(Locale.ENGLISH, "geo:%f,%f", data.geometry.coordinates[1], data.geometry.coordinates[0])
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                                startActivity(intent)

                                true
                            }

                        }

                        my_settings_button.setOnClickListener {
                            val intent = Intent(this@MapsActivity, SettingsActivity2::class.java)
                            startActivity(intent)
                        }

                        my_refresh_button.setOnClickListener{
                            finish()
                            overridePendingTransition(0,0)
                            val intent = intent
                            startActivity(intent)
                            overridePendingTransition(0,0)
                        }
                       //myAdapter.notifyDataSetChanged()
                    }

                }

                override fun onError(anError: ANError?) {}

            })


        //val gasStation = LatLng()
        /**
        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions().position(latLng)
        mMap.addMarker(markerOptions)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        */

        my_test_button.setOnClickListener{

        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        val sp = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        mMap = googleMap

        if (sp.getString("mapstyle","").toString() == "dark"){
            googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this,R.raw.darkmapstyle
                )
            )
        }

/*
        my_text_view2.animate()
                .translationY(-my_text_view2.height.toFloat())
                .alpha(1.0f)
                .setDuration(300)
*/    

        my_text_view2.visibility = View.GONE
/*
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
 */
        getLocationAccess()



        //mMap.setOnMarkerClickListener(this)

    }

    fun getGasStationName(stationName: String): String {

        if (stationName.contains("Shell"))
            return "Shell"
        if (stationName.contains("Türk Petrol"))
            return "Türk Petrol"
        if (stationName.contains("Total"))
            return "Total"
        if (stationName.contains("BP"))
            return "BP"
        if (stationName.contains("Petrol Ofisi"))
            return "Petrol Ofisi"
        if (stationName.contains("Opet"))
            return "Opet"
        if (stationName.contains("GO"))
            return "GO"
        if (stationName.contains("Aytemiz"))
            return "Aytemiz"
        if (stationName.contains("Alpet"))
            return "Alpet"
        return "null"
    }
/**
   override fun onMarkerClick(marker : MarkerOptions): Boolean{

        println("testtest"+marker.title.toString())

       // Retrieve the data from the marker.
       val clickCount = marker.tag as? Int

       // Check if a click count was set, then display the click count.
       clickCount?.let {
           val newClickCount = it + 1
           marker.tag = newClickCount
           Toast.makeText(
                   this,
                   "${marker.title} has been clicked $newClickCount times.",
                   Toast.LENGTH_SHORT
           ).show()
       }

       // Return false to indicate that we have not consumed the event and that we wish
       // for the default behavior to occur (which is for the camera to move such that the
       // marker is centered and for the marker's info window to open, if it has one).
       return false

    }
*/

}