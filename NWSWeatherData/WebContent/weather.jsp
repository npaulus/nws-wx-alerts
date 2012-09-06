<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Weather Alerts</title>
<script type="text/javascript"
      src="http://maps.googleapis.com/maps/api/js?key=<YOURAPIKEYHERE>&sensor=true">
    </script>
<script src="jquery-1.7.2.min.js"></script>
<script>
$(document).ready(function(){
		
	if(navigator && navigator.geolocation){
		navigator.geolocation.getCurrentPosition(success, error, {timeout:50000});
	}
	
	
});

function initializeMap(lat, lon, polyInfoPaths) {
	
	var myPosition = new google.maps.LatLng(lat, lon);
	
	var mapOptions = {
			 center : myPosition,
			 zoom : 10,
	         mapTypeId: google.maps.MapTypeId.ROADMAP
	        };
	
	var polygonOpts = [];
	
	var map = new google.maps.Map(document.getElementById("map"),
	            mapOptions);	
	
    var marker = new google.maps.Marker({
		position: myPosition,
	    map: map,
	    title:"Your Location"
	 });
	
	for(var i = 0; i < polyInfoPaths.length; i++){ 
		if(polyInfoPaths[i].getLength() > 1){
			polygonOpts[i] = new google.maps.Polygon({
				paths : polyInfoPaths[i],
				fillColor : "FF0000",
				fillOpacity : 0.3,			
				strokeColor : "FF0000",
				strokeOpacity : 0.3,
				strokeWeight : 2	
			});
			polygonOpts[i].setMap(map);
		}
	}	
    	
}

function success(location) {
	
	var longitude = location.coords.longitude;
	var latitude = location.coords.latitude;
	
			
	$.post("/NWSWeatherData/WeatherData",{
		lon : longitude,
		lat : latitude } ,
		function(data) {
			var results = "test";
			$(data).find("alerts").each(function(){
				results = "<h1>"+$(this).attr("title")+"</h1>";
			});
			
			var i = 0;
			var polyInfoPaths = [];
			
			$(data).find("alert").each(function(){
				results += "<h3>"+$(this).find("headline").text()+"</h3>";
				results += "<p>"+$(this).find("description").text()+"</p>";
				results += "<p>"+$(this).find("instructions").text()+"</p>";
				var coords = $(this).find("coordinates").text().split(" ");
				
				if(coords.length > 1){
					polyInfoPaths[i] = new google.maps.MVCArray();
					for(var j = 0; j < coords.length; j++){						
						var points = coords[j].split(",");
						polyInfoPaths[i].push(new google.maps.LatLng(points[0], points[1]));
					}
					i++;				
				}
			});
			
			initializeMap(latitude, longitude, polyInfoPaths);
			
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
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=2.0, user-scalable=yes" />
</head>
<body>
<div id="map"></div>
<div id="weatherData" style="display: block">
<p>Retrieving weather alert data for your location....</p>
</div>
</body>
</html>