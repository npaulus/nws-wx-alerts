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

The java libraries are included in the WebContent/WEB-INF/lib folder.  It should be as easy as downloading the project into Eclipse, export as a WAR, and deploy it to Tomcat.

How it works
------------

The general idea is when a user visits the page, some JavaScript tries to get the users location and then sends it to the servlet.  The servlet sends the GPS coordinates to the FCC's [web service](http://www.fcc.gov/developers/census-block-conversions-api) for determining a county in the United States.  The FIPS code for the county is used from the FCC response to generate the appropriately formatted code for the NWS weather alert feeds.  Then the weather alert feed is retrieved to check for any alerts.  If there are alerts, the details are returned to the user on the webpage.  

Roadmap
-------

* Add zip code box to search for alerts based on zip code.