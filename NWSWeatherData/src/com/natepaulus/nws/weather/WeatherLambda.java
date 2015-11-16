package com.natepaulus.nws.weather;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.io.IOException;
import java.net.URL;

/**
 * Created by npaulus on 11/14/15.
 */
public class WeatherLambda  implements RequestHandler<Request, Response> {

//    public Response handleRequest(String lat, String lon) throws IOException{
//        lat = lat.substring(0, 5);
//        lon = lon.substring(0, 6);
//
//        String result = Weather.getData(lat, lon);
//        URL urlFeed = new URL("http://alerts.weather.gov/cap/wwaatmget.php?x="+result+"&y=0");
//
//        return Weather.getAtomFeed(urlFeed);
//    }

    @Override
    public Response handleRequest(Request request, Context context) {

        String lat = request.getLat().substring(0, 5);
        String lon = request.getLon().substring(0, 6);

        String result = Weather.getData(lat, lon);
        URL urlFeed = null;
        try {
            urlFeed = new URL("http://alerts.weather.gov/cap/wwaatmget.php?x=" + result + "&y=0");
        } catch (IOException e){
            //do something
        }
        Response response = new Response();
        response.setAlerts(Weather.getAtomFeed(urlFeed));
        return response;
    }
}
