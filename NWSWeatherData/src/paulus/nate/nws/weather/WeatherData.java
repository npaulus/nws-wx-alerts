package paulus.nate.nws.weather;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class WeatherData
 */
@WebServlet("/WeatherData")
public class WeatherData extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WeatherData() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String lat = request.getParameter("lat");
		String lon = request.getParameter("lon");
		
		lat = lat.substring(0, 5);
		lon = lon.substring(0, 6);
		
		String result = Weather.getData(lat, lon);
		URL urlFeed = new URL("http://alerts.weather.gov/cap/wwaatmget.php?x="+result+"&y=1");
		//URL urlFeed = new URL("http://alerts.weather.gov/cap/pa.php?x=0");
		try {
			result = Weather.getAtomFeed(urlFeed); 
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		PrintWriter out = response.getWriter();
		out.println(result);
		out.flush();
		out.close();
		
	}

}
