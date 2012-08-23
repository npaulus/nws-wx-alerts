<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Weather Alerts</title>
<script src="jquery-1.7.2.min.js"></script>
<script>
$(document).ready(function(){
	
	if(navigator && navigator.geolocation){
		navigator.geolocation.getCurrentPosition(success, error, {timeout:50000});
	}
});

function success(location) {
	
	var longitude = location.coords.longitude;
	var latitude = location.coords.latitude;

	
	$.post("/NWSWeatherData/WeatherData",{
		lon : longitude,
		lat : latitude } ,
		function(data) {
			
			$("#weatherData").html(data);				
			$("#weatherData").show();
		}, "html");	
	
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
<div id="weatherData" style="display: block">
<p>Retrieving weather alert data for your location....</p>
</div>
</body>
</html>