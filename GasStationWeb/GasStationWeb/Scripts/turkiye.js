var iscountyselected = false;
var previouscountyselected = "blank";
var start = true;
var past = null;
var content_dir = "details";

$(document).ready(function () {

	var r = Raphael('map'),
		attributes = {
			fill: '#bbb',
			stroke: '#000',
			'stroke-width': .2,
			'stroke-linejoin': 'round'
		},
		arr = new Array();

	for (var county in paths) {

		var obj = r.path(paths[county].path);

		obj.attr(attributes);

		arr[obj.id] = county;


		if (arr[obj.id] != 'blank') {
			obj.data('selected', 'notSelected');

			obj.node.id = arr[obj.id];

			obj.attr(attributes).attr({ title: paths[arr[obj.id]].name_display, id: paths[arr[obj.id]].name });
		}

	}

	$("#map svg path").each(function (index, element) {
		var title = $("title", this);
		title.remove();
	});

	$("#map svg path").hover(function () {
		var sehirTitle = $(this).data("title");
		$(this).css({ fill: "#12356e", transition: ".2s" });
	},
		function () {
			$(this).css({ fill: "#BBB", transition: ".2s" });
		});

	for (var county in paths) {
		if (paths[county].name != "blank") {
			var myarc = document.getElementById(paths[county].name);
			var svg = myarc.ownerSVGElement;
			var circle = document.createElementNS("http://www.w3.org/2000/svg", "circle");
			circle.setAttribute("cx", paths[county].markerX);
			circle.setAttribute("cy", paths[county].markerY);
			circle.setAttribute("r", "1.5")
			circle.setAttribute("fill", "#000");
			circle.setAttribute("data-id", paths[county].name);
			svg.appendChild(circle);
			topOffset = -20;
			if (paths[county].markerTitle == "down") topOffset = 5;
			$("#map .cities").append('<a href=#  id="citylink' + paths[county].name + '"  class="cityname" onclick=getDistricts('+paths[county].county+'); style="top:' + (parseFloat(paths[county].markerY) + topOffset - 140) + 'px;left:' + (paths[county].markerX) + 'px"> ' + paths[county].name_display + ' </a>');//Link for label href!!
		}
	}

	$(".cityname").each(function (index, element) {
		var leftOffset = ($(this).width() / 2) * -1;
		$(this).css({ "margin-left": leftOffset + "px" });
	});

	$(".cityname").hover(function () {
		var id = $(this).attr("id").replace("citylink", "");
		$("#" + id).css({ fill: "#12356e", transition: ".2s" });
	},
		function () {
			var id = $(this).attr("id").replace("citylink", "");
			$("#" + id).css({ fill: "#bbb", transition: ".2s" });
		});

	$("#map svg path").click(function (e) {
		var city = $(this).attr("id");
		var id = paths[city].county;
		getDistricts(id);
	});//Event listener path area click

	$("#map svg circle").click(function () {
		var city = $(this).data("id");
		var id = paths[city].county;
		getDistricts(id);
	});//Event Listener circle click

	$("#map svg circle").hover(function () {
		var id = $(this).data("id");
		$("#" + id).css({ fill: "#12356e", transition: ".2s" });
	},
		function () {
			var id = $(this).data("id");
			$("#" + id).css({ fill: "#bbb", transition: ".2s" });
		});
});

function getDistricts(id){
	$.ajax({
		url: '/Home/GetDistricts/' + id,
		type: 'POST',
		dataType: 'json',
		beforeSuccess: function () { $(".loader-wrapper").fadeIn("slow"); },
		success: function (data) {
			$(".loader-wrapper").fadeOut("slow");
			$(".select-box").css("display", "flex");
			$(".option").remove();
			$(".selected").text("Select District");
			$.each(data, function (i, dist) {

				$("#districts").append('<div class="option" onclick=selectDist("' + dist.IlceID.toString() + '","' + dist.Ilce + '"); > <input type="radio" class="radio"	id="' + dist.IlceID.toString() + '"name="category"  /> <label for="' + dist.Ilce + '">' + dist.Ilce + '</label>	</div > ');
			});
		},
		error: function (hata, ajaxOptions, thrownError) {
			alert(hata.responseText);
		}
	});

}
function selectDist(id,name) {

	$(".select-box2").css("display", "flex");
	var distr = document.getElementById(id);
	selected.innerHTML = name;
	var distid = document.getElementById("distid");
	distid.innerHTML = id;
	optionsContainer.classList.remove("active");
	

}