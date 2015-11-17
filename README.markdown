NWS Weather Alerts
==================

This is a simple java web application that will check for weather alerts by the US National Weather Service based on a user's location.  It will plot the user's location on a google map as well as draw any polygons impacting the user's county.  The polygon's are color coded similar to the National Weather Service color scheme (Red = Tornado, Severe Thunderstorm = Yellow, Flash Flood = Green) Please see the Roadmap for more details on future enhancements. Additionally, the current radar is now overlayed on the google map as well.  The page is optimized for mobile or a larger screen.

The goal is to provide a way to easily pull weather alert information while traveling using a mobile device such as an iPhone.  Using the GeoLocation API, the user's GPS coordinates are used to determine the appropriate URL to retrieve weather alerts for the specific location. AJAX is used to send/receive information to make the process easy for the user. 

I started this project to learn a little about using java for RESTful web services. Also, I wanted something that wasn't tied to a specific app for determining if I am in an area with a severe weather alert. If anyone has any enhancement ideas or sees any bugs please let me know!

You can see this code in action [here.](http://alerts.vtmnts.com)

Usage
-----

This project is designed to run on Apache Tomcat 8.  It uses the following:

* Apache Tomcat 8
* Java 8
* [Jersey 1.13](http://jersey.java.net/) 
* [JAX-RS 1.1](http://jax-rs-spec.java.net/)
* [JAXB](http://jaxb.java.net/)
* [Apache Commons IO 2.4](https://commons.apache.org/io/)
* jQuery - This is included in the WebContent folder
* [Google Maps API V3](https://developers.google.com/maps/documentation/javascript/) - API Key is required to use this

I have converted this project to using Maven.  So downloading the files individually is no longer required and should make it easier to test it out.

How it works
------------

The general idea is when a user visits the page, some JavaScript tries to get the users location and then sends it to the servlet.  The servlet sends the GPS coordinates to the FCC's [web service](http://www.fcc.gov/developers/census-block-conversions-api) for determining a county in the United States.  The FIPS code for the county is used from the FCC response to generate the appropriately formatted code for the NWS weather alert [feeds](http://alerts.weather.gov).  Then the weather alert feed is retrieved to check for any alerts.  If there are alerts, the details are returned to the user on the webpage.  The user's location is plotted on Google Maps. If any polygon coordinates are included with the weather alerts those are drawn on the map as well and color coded appropriately.  This makes it easy for the user to see if their location is impacted.  Recently, I added in a radar overlay for the google map.  I'm getting the radar data from [IEM Open GIS Consortium Web Services](http://mesonet.agron.iastate.edu/ogc/).

Roadmap
-------

* Make the polygons color coded based on type of alert: (Added 2/6/2013)
    * Red = Tornado Warning
	* Yellow = Severe Thunderstorm Warning
	* Green = Flash Flood Warning
	* Grey = All others
* Add Radar overlay to google maps (Added 2/5/2013)
* Replace "Retrieving weather info...." message with spinner to indicate page is loading
* Restructure the javascript so the map loads with user's location while waiting for the weather alert data to come back from server
* Add color coded "boxing" around alerts to make it easier to determine which alert goes with which polygon
