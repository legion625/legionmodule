package legion.google;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import legion.util.DataFO;

public class GoogleSearchProxy {
	
	private final static GoogleSearchProxy INSTANCE = new GoogleSearchProxy();
	private GoogleSearchProxy() {}
	public final static GoogleSearchProxy getInstance() {return INSTANCE;}
	
	private final static String KEY = "AIzaSyANWft2f_iGvgoy8QrLs9-gHpyNcdeLZM8";
	
	
	public String getFirstResultUrl(String _query) throws IOException {
		String q = DataFO.toUrlFormat(_query);
		if(q==null)
			return null;
		
		
		URL url = new URL("https://www.googleapis.com/customsearch/v1?key=" + KEY
				+ "&cx=013036536707430787589:_pqjad5hr1a&q=" + q + "&alt=json");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

		String output;
		System.out.println("Output from Server .... \n");
		String result = "";
		while ((output = br.readLine()) != null) {

			if (output.contains("\"link\": \"")) {
				String link = output.substring(output.indexOf("\"link\": \"") + ("\"link\": \"").length(),
						output.indexOf("\","));
				System.out.println(link); // Will print the google search links
				result = link;
				break;
			}
		}
		conn.disconnect();
		return result;
	}
//	String qry = "Android";
//	URL url = new URL("https://www.googleapis.com/customsearch/v1?key=" + key
//			+ "&cx=013036536707430787589:_pqjad5hr1a&q=" + qry + "&alt=json");
//	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//	conn.setRequestMethod("GET");
//	conn.setRequestProperty("Accept", "application/json");
//	BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
//
//	String output;
//	System.out.println("Output from Server .... \n");
//	while ((output = br.readLine()) != null) {
//
//		if (output.contains("\"link\": \"")) {
//			String link = output.substring(output.indexOf("\"link\": \"") + ("\"link\": \"").length(),
//					output.indexOf("\","));
//			System.out.println(link); // Will print the google search links
//		}
//	}
//	conn.disconnect();
}
