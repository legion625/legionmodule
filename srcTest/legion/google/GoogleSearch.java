package legion.google;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import legion.util.DataFO;


public class GoogleSearch {
	@Test
	public void test() throws Exception {
		String key = "AIzaSyANWft2f_iGvgoy8QrLs9-gHpyNcdeLZM8";
//		String qry = "Android";
		String qry = DataFO.toUrlFormat("Request for information");
		URL url = new URL("https://www.googleapis.com/customsearch/v1?key=" + key
				+ "&cx=013036536707430787589:_pqjad5hr1a&q=" + qry + "&alt=json");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

		String output;
		System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {

			if (output.contains("\"link\": \"")) {
				String link = output.substring(output.indexOf("\"link\": \"") + ("\"link\": \"").length(),
						output.indexOf("\","));
				System.out.println(link); // Will print the google search links
			}
		}
		conn.disconnect();
	}
	
	@Test
	public void testProxy() throws IOException {
		String rfiResult = GoogleSearchProxy.getInstance().getFirstResultUrl("Request for information");
		System.out.println(rfiResult);
	}

}
