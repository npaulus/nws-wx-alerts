package com.natepaulus.nws.weather;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

/**
 * Created by npaulus on 11/14/15.
 */
public class WeatherLambda  implements RequestHandler<Request, Response> {

    private final static Logger logger = LoggerFactory.getLogger(WeatherLambda.class);

    @Override
    public Response handleRequest(Request request, Context context) {

        String lat = request.getLat().substring(0, 5);
        String lon = request.getLon().substring(0, 6);
        logger.error("Latitude Received: " + lat);
        logger.error("Longitude Received: " + lon);


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
