<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Weather Alerts</title>
<script type="text/javascript"
      src="https://maps.googleapis.com/maps/api/js?key=AIzaSyAFzR4XGDNaVJRt52d3iaivONmPd5b-ntg&sensor=true">
    </script>
<script src="jquery-1.7.2.min.js"></script>
<script>
$(document).ready(function(){
	
	
	
	if(navigator && navigator.geolocation){
		navigator.geolocation.getCurrentPosition(success, error, {timeout:50000});
	}
	
	
});

function initializeMap(lat, lon, polygonOptions) {
	
	var radarMapType = new google.maps.ImageMapType({
		 getTileUrl: function(tile, zoom) {
		 return "https://mesonet.agron.iastate.edu/cache/tile.py/1.0.0/nexrad-n0q-900913/" + zoom + "/" + tile.x + "/" + tile.y +".png?"+ (new Date()).getTime();
		 },
		 tileSize: new google.maps.Size(256, 256),
		 opacity:0.30,
		 name : 'Radar',
		 isPng: true
		 }); 
	
	//var radarMapType = new google.maps.ImageMapType(radarMapTypeOptions);

	var myPosition = new google.maps.LatLng(lat, lon);
	
	var mapOptions = {
			 center : myPosition,
			 zoom : 10,
	         mapTypeId: google.maps.MapTypeId.ROADMAP	         
	        };
	
	var polygonOpts = [];
	
	var map = new google.maps.Map(document.getElementById("map"),
	            mapOptions);	
			
	map.overlayMapTypes.push(null);
	map.overlayMapTypes.setAt("0", radarMapType);
		
		
	
    var marker = new google.maps.Marker({
		position: myPosition,
	    map: map,
	    title:"Your Location"
	 });
	
	for(var i = 0; i < polygonOptions.length; i++){ 
		/* if(polyInfoPaths[i].getLength() > 1){
			polygonOpts[i] = new google.maps.Polygon({
				paths : polyInfoPaths[i],
				fillColor : "FF0000",
				fillOpacity : 0.3,			
				strokeColor : "FF0000",
				strokeOpacity : 0.3,
				strokeWeight : 2	
			}); 			
		}*/
		polygonOptions[i].setMap(map);
	}	
    	
}

function success(location) {
		
	var longitude = location.coords.longitude;
	var latitude = location.coords.latitude;
	//var longitude = -98.5491;
	//var latitude = 28.4594;
			
	$.post("/alerts/WeatherData",{
		lon : longitude,
		lat : latitude } ,
		function(data) {
			var results = "test";
			$(data).find("alerts").each(function(){
				results = "<h1>"+$(this).attr("title")+"</h1>";
			});
			var polygonOptions = [];
			var i = 0;
			var polyInfoPaths = [];
			
			$(data).find("alert").each(function(){
				results += "<h3>"+$(this).find("headline").text()+"</h3>";
				results += "<p>"+$(this).find("description").text()+"</p>";
				results += "<p>"+$(this).find("instructions").text()+"</p>";
				var coords = $(this).find("coordinates").text().split(" ");
				var phenomena = $(this).find("phenomena").text();
				
				var color = "#808080"; //the default color is gray
				
				if(phenomena == "FF"){ //green for flash flood
					color = "#33FF33";
				} else if(phenomena == "SV"){ //yellow for severe thunderstorm
					color = "#EEEE00"; 
				} else if(phenomena == "TO"){ //red for tornado
					color = "FF0000";
				}
				
				if(coords.length > 1){
					polyInfoPaths[i] = new google.maps.MVCArray();
					for(var j = 0; j < coords.length; j++){						
						var points = coords[j].split(",");
						polyInfoPaths[i].push(new google.maps.LatLng(points[0], points[1]));						
					}
					polygonOptions[i] = new google.maps.Polygon({
						paths : polyInfoPaths[i],
						fillColor : color,
						fillOpacity : 0.35,			
						strokeColor : color,
						strokeOpacity : 0.35,
						strokeWeight : 2	
					});
					i++;				
				}
			});
			
			initializeMap(latitude, longitude, polygonOptions);
			
			$("#weatherData").html(results);				
			$("#weatherData").show();
		}, "xml");
	
}

function error(err){
	
	$("#weatherData").html("<h1>Oops!</h1><p>It looks like your browser doesn't support geolocation. For best results please use" +
	" a mobile phone and share your location when prompted.</p>");
}

</script>
<link media="only screen and (max-device-width: 480px)" href="mobile.css" type="text/css" rel="stylesheet"   />
<link rel="stylesheet" type="text/css" href="weather.css" media="only screen and (min-device-width: 481px)" />
<link rel="apple-touch-icon" href="alert.png" />
<link rel="shortcut icon" href="alert.ico" />
<link rel="icon" href="alert.ico" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=2.0, user-scalable=yes" />
</head>
<body>
<div id="map"></div>
<div id="weatherData" style="display: block">
<p>Retrieving weather alert data for your location....</p>
</div>
</body>
</html>