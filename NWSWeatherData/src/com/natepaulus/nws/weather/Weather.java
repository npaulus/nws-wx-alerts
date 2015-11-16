package com.natepaulus.nws.weather;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.input.BOMInputStream;

import com.natepaulus.nws.weather.atom.Entry;
import com.natepaulus.nws.weather.atom.Feed;
import com.natepaulus.nws.weather.cap.Alert;
import com.natepaulus.nws.weather.cap.Alert.Info.Area;
import com.natepaulus.nws.weather.cap.Alert.Info.Parameter;
import com.natepaulus.nws.weather.fcc.Response;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Weather {

	private final static Logger logger = LoggerFactory.getLogger(Weather.class);
	
	/**
	 * Returns the FIPS code for the county that GPS coordinates reside in formatted for the NWS system.
	 * @param lat - latitude used to get county
	 * @param lon - longitude used to get county
	 * @return String that represents the National Weather Service county code
	 */
	public static String getData(String lat, String lon) {
		Client c = Client.create();
		c.setFollowRedirects(true);
		WebResource r = c.resource("http://data.fcc.gov/api/block/find");
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		   queryParams.add("latitude", lat);
		   queryParams.add("longitude", lon);		   	   
		   r.accept(MediaType.APPLICATION_XML);
		Response fccResponse = r.queryParams(queryParams).get(Response.class);
		
		String state = fccResponse.getState().getCode();
		logger.error("FCC Response: " + fccResponse.getCounty());
		logger.error("FCC Response: " + fccResponse.getCounty().getFIPS());
		String fipsCountyCode = fccResponse.getCounty().getFIPS().toString().substring(2);
		if(fipsCountyCode.length() == 2){
			fipsCountyCode = "0" + fipsCountyCode;
		} else if(fipsCountyCode.length() == 1){
			fipsCountyCode = "00" + fipsCountyCode;
		}
		String fips = state +"C" + fipsCountyCode;
		
		return fips;

	}
	
	/**
	 * Retrieves the alerts from the NWS at the URL provided
	 * @param url - NWS URL that is used to retrieve alert information for specific county
	 * @return
	 */
	public static Alerts getAtomFeed(URL url) {
			
		String text = "";
		Alerts alerts = new Alerts();
		InputStream in = null;
		BOMInputStream bomIn = null;
		try{		
			in = url.openStream();
			bomIn = new BOMInputStream(in);
		} catch (IOException e){
			alerts.setTitle("Error");
			Alerts.Alert alert = new Alerts.Alert();
			alert.setHeadline("Unable to get the weather data.  Please refresh the page");
			alerts.getAlert().add(alert);
			return alerts;
		}
		
		Feed feed = null;
		try {
			JAXBContext capFeed = JAXBContext.newInstance(Feed.class);
    		Unmarshaller capFeedData = capFeed.createUnmarshaller();
			feed = (Feed) capFeedData.unmarshal(bomIn);
		} catch (JAXBException e1) {
			
			e1.printStackTrace();
		}

		alerts.setTitle(feed.getTitle());

		List<Entry> entry = feed.getEntry();
		
		for(Entry e : entry){



			if(e.getTitle().equals("There are no active watches, warnings or advisories")){
				Alerts.Alert alert = new Alerts.Alert();
				alert.setDescription(e.getTitle());
				alerts.getAlert().add(alert);
			}
			else {
				Alert alertCap = null;
				URL capURL = null;
				InputStream capIn = null;
				
				try {
					capURL = new URL(e.getId());
					capIn = capURL.openStream();
					JAXBContext capAlert = JAXBContext.newInstance(Alert.class);
	        		Unmarshaller capAlertData = capAlert.createUnmarshaller();
					alertCap = (Alert) capAlertData.unmarshal(capIn);
				} catch (JAXBException | IOException e1) {
					Alerts.Alert alert = new Alerts.Alert();
					alerts.setTitle("Error");
					alert.setDescription("There appears to be an alert, but the data wasn't all available.  Please refresh the page to try again.");
					alert.setHeadline("Error");
					alerts.getAlert().add(alert);
					return alerts;
				}        		
				
        		List<Alert.Info> details = alertCap.getInfo();
        		for(Alert.Info a: details){
        			Alerts.Alert alert = new Alerts.Alert();

        			String desc = a.getDescription().replaceAll("\n\\*", "&lt;br /&gt;&lt;br /&gt;\\*")
        					.replaceAll("\n\\.", "&lt;br /&gt;&lt;br /&gt;\\.")
        					.replaceAll("\\.\\.\\.\nTHE", "\\.\\.\\.&lt;br /&gt;&lt;br /&gt;THE")
        					.replaceAll("\\.\\.\\.\nA", "\\.\\.\\.&lt;br /&gt;&lt;br /&gt;A");

					alert.setHeadline(a.getHeadline());
					alert.setDescription(desc);
					alert.setInstructions(a.getInstruction());
					alert.setCoordinates(getCoordinates(a));
					alert.setPhenomena(getVTECPhenomena(a));
					alerts.getAlert().add(alert);
        		}
			}
		}	

		return alerts;
		
	}
	
	private static String getCoordinates(Alert.Info a) {
		String result = "";
		
		List<Area> areaList = a.getArea();
		Iterator<Area> i = areaList.iterator();
		
		if(i.hasNext()){
			Area poly = i.next();
			List<String> polyCoords = poly.getPolygon();
			Iterator<String> j = polyCoords.iterator();
			
			while(j.hasNext()){
				result += j.next();
			}
		}
		return result;
	}
	
	private static String getVTECPhenomena(Alert.Info a){
		String result = "";
		
		List<Parameter> parameters = a.getParameter();
		Iterator<Parameter> iteratorParameters = parameters.iterator();
		while(iteratorParameters.hasNext()){
			Parameter p = iteratorParameters.next();
			if(p.getValueName().equals("VTEC")){
				String vtecValues = p.getValue();
				
				String vtecs[] = vtecValues.split("/");
				
				for (String v : vtecs){
					String[] VTEC = v.split("\\.");								
					if(VTEC.length > 1){
						if(VTEC[4].equals("W")){
							return result = VTEC[3];
						}
					}
				}
			}
		} 
		
		return result;
	}

}
