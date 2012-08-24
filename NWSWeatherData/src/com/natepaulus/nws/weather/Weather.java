package com.natepaulus.nws.weather;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
import com.natepaulus.nws.weather.fcc.Response;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class Weather {

	
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
		MultivaluedMap queryParams = new MultivaluedMapImpl();
		   queryParams.add("latitude", lat);
		   queryParams.add("longitude", lon);		   	   
		   r.accept(MediaType.APPLICATION_XML);
		Response fccResponse = r.queryParams(queryParams).get(Response.class);
		
		String state = fccResponse.getState().getCode();
		String fips = state +"C" + fccResponse.getCounty().getFIPS().toString().substring(2);
		
		return fips;

	}
	
	/**
	 * Retrieves the alerts from the NWS at the URL provided
	 * @param url - NWS URL that is used to retrieve alert information for specific county
	 * @return
	 */
	public static String getAtomFeed(URL url) {
			
		String text = "";
		InputStream in = null;
		BOMInputStream bomIn = null;
		try{		
			in = url.openStream();
			bomIn = new BOMInputStream(in);
		} catch (IOException e){
			return "<p>Unable to get the weather data.  Please refresh the page</p>";
		}
		
		Feed feed = null;
		try {
			JAXBContext capFeed = JAXBContext.newInstance(Feed.class);
    		Unmarshaller capFeedData = capFeed.createUnmarshaller();
			feed = (Feed) capFeedData.unmarshal(bomIn);
		} catch (JAXBException e1) {
			
			e1.printStackTrace();
		}
		text +="<h1>"+ feed.getTitle() + "</h1>\n";
		
		List<Entry> entry = feed.getEntry();
		
		for(Entry e : entry){
			if(e.getTitle().equals("There are no active watches, warnings or advisories")){
				text += "<p>No active watches, warnings or advisories.</p>";
			}
			else {
				Alert alert = null;
				URL capURL = null;
				InputStream capIn = null;
				
				try {
					capURL = new URL(e.getId());
					capIn = capURL.openStream();
					JAXBContext capAlert = JAXBContext.newInstance(Alert.class);
	        		Unmarshaller capAlertData = capAlert.createUnmarshaller();
					alert = (Alert) capAlertData.unmarshal(capIn);
				} catch (JAXBException | IOException e1) {
					return "<p>There appears to be an alert, but the data wasn't all available.  Please refresh the page to try again.</p>";
				}        		
				
        		List<Alert.Info> details = alert.getInfo();
        		for(Alert.Info a: details){
        			
        			String desc = a.getDescription().replaceAll("\n\\*", "<br /><br >\\*")
        					.replaceAll("\n\\.", "<br /><br >\\.")
        					.replaceAll("\\.\\.\\.\nTHE", "\\.\\.\\.<br /><br >THE")
        					.replaceAll("\\.\\.\\.\nA", "\\.\\.\\.<br /><br >A");
        			
        			text += "<h3>" + a.getHeadline() + "<h3>\n";
    				text += "<p>" + desc + "</p> \n";
    				text += "<p>" + a.getInstruction() + "</p><br /> \n";	
        		}
			}
		}	
		
		return text;
		
	}

}
