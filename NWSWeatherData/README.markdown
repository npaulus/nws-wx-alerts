NWS Weather Alerts
==================

This is a simple java web application that will check for weather alerts by the US National Weather Service based on a user's location.  The goal is to provide a way to easily pull weather alert information while traveling using a mobile device such as an iPhone.  Using the GeoLocation API, the user's GPS coordinates are used to determine the appropriate URL to retrieve the weather alerts for the specific location. Additionally, the site uses AJAX to get the weather data by sending the user's location once it has been determined. So the user just needs to access the URL and the website takes care of the rest. 

I started this project to learn a little about using java for RESTful web services. Also, I wanted something that wasn't tied to a specific app for determining if I am in an area with a severe weather alert. If anyone has any enhancement ideas or sees any bugs please let me know!

You can see this code in action [here.](http://www.natepaulus.com/NWSWeatherData)

Usage
-----

This project is designed to run on Apache Tomcat 7.  It uses the following:

* Java 7
* Jersey 1.13
* JAXB 2.2.6
* Apache Commons IO 2.4
* jQuery

These java libraries are included in the Webcontent/WEB-INF/lib folder.  It should be as easy as downloading the project into Eclipse, export as a WAR, and deploy it to Tomcat.

Roadmap
-------

The next thing I want to do is set it up to build with maven.