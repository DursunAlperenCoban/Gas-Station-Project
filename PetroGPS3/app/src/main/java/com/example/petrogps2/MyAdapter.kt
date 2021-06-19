package com.example.petrogps2

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.petrogps2.model.Feature
import kotlinx.android.synthetic.main.item_view.view.*


class MyAdapter (private val dataList: MutableList<Feature>) : Adapter<MyHolder>() {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        context = parent.context
        return MyHolder(LayoutInflater.from(context).inflate(R.layout.item_view,parent,false))
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val data = dataList[position]
        val gasStationNameTextView = holder.itemView.gas_station_name
        val gasStationCoordinatesTextView = holder.itemView.gas_station_coordinates

        val gasStationName = "${data.properties.companyMetaData.name}"
        gasStationNameTextView.text = gasStationName

        val gasStationCoordinates = "${data.geometry.coordinates}"
        gasStationCoordinatesTextView.text = gasStationCoordinates

        holder.itemView.setOnClickListener {
            Toast.makeText(context, gasStationName, Toast.LENGTH_SHORT).show()
        }

    }
}