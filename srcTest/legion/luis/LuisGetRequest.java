package legion.luis;

// This sample uses the Apache HTTP client from HTTP Components (http://hc.apache.org/httpcomponents-client-ga/)

// You need to add the following Apache HTTP client libraries to your project:
// httpclient-4.5.3.jar
// httpcore-4.4.6.jar
// commons-logging-1.2.jar

import java.net.URI;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import legion.util.JsonUtil;

public class LuisGetRequest {
	
	private final static String AUTH_KEY = "58c4d495240648bdb1634d77b87fa06d";

	public static void main(String[] args) {
		HttpClient httpclient = HttpClients.createDefault();

		try {

			// The ID of a public sample LUIS app that recognizes intents for turning on and
			// off lights
//			String appId = "df67dcdb-c37d-46af-88e1-8b97951ca1c2";
			String appId = "6cd2bbba-705c-4d03-b7b1-ea384b628022";

			// Add your endpoint key
			// You can use the authoring key instead of the endpoint key.
			// The authoring key allows 1000 endpoint queries a month.
			String EndpointKey = AUTH_KEY;

			// Begin endpoint URL string building
			URIBuilder endpointURLbuilder = new URIBuilder(
					"https://westus.api.cognitive.microsoft.com/luis/v2.0/apps/" + appId + "?");
//			URIBuilder endpointURLbuilder = new URIBuilder(
//					"https://westus.api.cognitive.microsoft.com/luis/api/v2.0/apps/"+appId+"/versions/0.1/intents?take=500");

			// query text
//			endpointURLbuilder.setParameter("q", "turn on the left light");
			endpointURLbuilder.setParameter("q", "show me rfi of project tsebo");

			// create URL from string
			URI endpointURL = endpointURLbuilder.build();

			// create HTTP object from URL
			HttpGet request = new HttpGet(endpointURL);

			// set key to access LUIS endpoint
			request.setHeader("Ocp-Apim-Subscription-Key", EndpointKey);

			// access LUIS endpoint - analyze text
			HttpResponse response = httpclient.execute(request);

			// get response
			HttpEntity entity = response.getEntity();

			String jsonStr = "";
			if (entity != null) {
				jsonStr = EntityUtils.toString(entity);
				System.out.println(jsonStr);
			}
//			List<Object> objList = JsonUtil.parseJsonArrayString(jsonStr);
//			for(Object obj:objList ) {
//				System.out.println(obj.toString());
//			}
			System.out.println("---------------------------------------------------");
			JSONObject jsonobj = new JSONObject(jsonStr);
			System.out.println(jsonobj);
			String intent = (String) jsonobj.getJSONObject("topScoringIntent").get("intent");
			System.out.println("intent: " + intent);
			JSONArray jsonarr = jsonobj.getJSONArray("entities");
			for(int i=0;i<jsonarr.length();i++) {
				JSONObject jsonObj = jsonarr.getJSONObject(i);
				String e = (String) jsonObj.get("entity");
				String t = (String) jsonObj.get("type");
				System.out.println(i + "\t"+"entity: " + e+"\ttype: " +t );
			}
		}

		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
