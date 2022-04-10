package legion.luis;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.zkoss.lang.Threads;

import legion.luis.AddUtteranceObj.AddUtterenceEntityObj;

public class LuisProxy {
	private String authKey;
	private String appId;
	private String versionId;
	private HttpClient httpclient = HttpClients.createDefault();
	private URIBuilder endpointURLbuilder;

	protected LuisProxy(String authKey, String appId, String versionId)  {
		this.authKey = authKey;
		this.appId = appId;
		this.versionId = versionId;
		httpclient = HttpClients.createDefault();
	}

//	public static LuisProxy of(String authKey, String appId, String versionId) throws URISyntaxException {
//		return new LuisProxy(authKey, appId, versionId);
//	}

	public final LuisIntent[] getAllIntents() throws Exception {
		endpointURLbuilder = new URIBuilder("https://westus.api.cognitive.microsoft.com/luis/api/v2.0/apps/" + appId
				+ "/versions/" + versionId + "/intents?take=500");

		// create URL from string
		URI endpointURL = endpointURLbuilder.build();

		// create HTTP object from URL
		HttpGet request = new HttpGet(endpointURL);
		// set key to access LUIS endpoint
		request.setHeader("Ocp-Apim-Subscription-Key", authKey);
		// access LUIS endpoint - analyze text
		HttpResponse response = httpclient.execute(request);

		// get response
		HttpEntity entity = response.getEntity();

		JSONArray jsonArr = new JSONArray(EntityUtils.toString(entity));
		LuisIntent[] intents = new LuisIntent[jsonArr.length()];
		for (int i = 0; i < jsonArr.length(); i++) {
			JSONObject jsonObj = jsonArr.getJSONObject(i);
			intents[i] = parseLuisIntent(jsonObj);
		}
		return intents;
	}

	public final LuisIntent getIntent(String _intentId) throws Exception {
		endpointURLbuilder = new URIBuilder("https://westus.api.cognitive.microsoft.com/luis/api/v2.0/apps/" + appId
				+ "/versions/" + versionId + "/intents/" + _intentId + "");

		URI uri = endpointURLbuilder.build();
		HttpGet request = new HttpGet(uri);
		request.setHeader("Ocp-Apim-Subscription-Key", authKey);

		// // Request body
		// StringEntity reqEntity = new StringEntity("{body}");
		// request.setEntity(reqEntity);

		HttpResponse response = httpclient.execute(request);
		HttpEntity entity = response.getEntity();

		JSONObject jsonObj = new JSONObject(EntityUtils.toString(entity));
		return parseLuisIntent(jsonObj);
	}

	public final boolean createIntent(JSONObject _newIntent) throws Exception {
		endpointURLbuilder = new URIBuilder("https://westus.api.cognitive.microsoft.com/luis/api/v2.0/apps/" + appId
				+ "/versions/" + versionId + "/intents");

		URI uri = endpointURLbuilder.build();
		HttpPost request = new HttpPost(uri);
		request.setHeader("Content-Type", "application/json");
		request.setHeader("Ocp-Apim-Subscription-Key", authKey);

		StringEntity reqEntity = new StringEntity(_newIntent.toString());
		request.setEntity(reqEntity);

		HttpResponse response = httpclient.execute(request);
		HttpEntity entity = response.getEntity();
		String intentId = EntityUtils.toString(entity);
		System.out.println("intentId: " + intentId);

		return intentId.matches("\"[a-z0-9-]{36}\"");
	}

	public final LuisResult understanding(String _utterance) throws Exception {
		endpointURLbuilder = new URIBuilder("https://westus.api.cognitive.microsoft.com/luis/v2.0/apps/" + appId + "?");
		// query text
		// endpointURLbuilder.setParameter("q", "turn on the left light");
		endpointURLbuilder.setParameter("q", _utterance);

		// create URL from string
		URI endpointURL = endpointURLbuilder.build();

		// create HTTP object from URL
		HttpGet request = new HttpGet(endpointURL);

		// set key to access LUIS endpoint
		request.setHeader("Ocp-Apim-Subscription-Key", authKey);

		// access LUIS endpoint - analyze text
		HttpResponse response = httpclient.execute(request);

		// get response
		HttpEntity entity = response.getEntity();

		JSONObject jsonobj = new JSONObject(EntityUtils.toString(entity));

		LuisResult result = new LuisResult(jsonobj);

		return result;
	}

	public final boolean addUtterance(AddUtteranceObj _addObj) throws Exception {
		JSONObject addJsonObj = new JSONObject();
		addJsonObj.put("text", _addObj.getText());
		addJsonObj.put("intentName", _addObj.getIntentName());
		if (_addObj.getEntityList() != null && _addObj.getEntityList().size() > 0) {
			List<JSONObject> entityJsonObjList = new ArrayList<>();
			for (AddUtterenceEntityObj _entityObj : _addObj.getEntityList()) {
				JSONObject entityJsonObj = new JSONObject();
				entityJsonObj.put("entityName", _entityObj.getEntityName());
				entityJsonObj.put("startCharIndex", _entityObj.getStartCharIndex());
				entityJsonObj.put("endCharIndex", _entityObj.getEndCharIndex());
				entityJsonObjList.add(entityJsonObj);
			}

			addJsonObj.put("entityLabels", entityJsonObjList);
		}

		URIBuilder builder = new URIBuilder("https://westus.api.cognitive.microsoft.com/luis/api/v2.0/apps/" + appId
				+ "/versions/" + versionId + "/example");

		URI uri = builder.build();
		HttpPost request = new HttpPost(uri);
		request.setHeader("Content-Type", "application/json");
		request.setHeader("Ocp-Apim-Subscription-Key", authKey);

		// Request body
		StringEntity reqEntity = new StringEntity(addJsonObj.toString());
		request.setEntity(reqEntity);

		HttpResponse response = httpclient.execute(request);
		HttpEntity entity = response.getEntity();

		JSONObject r = new JSONObject(EntityUtils.toString(entity));
		boolean result = r.has("ExampleId");
		return result;
	}

	public final boolean train() throws Exception {
		URIBuilder builder = new URIBuilder("https://westus.api.cognitive.microsoft.com/luis/api/v2.0/apps/" + appId
				+ "/versions/" + versionId + "/train");

		/* post */
		URI uri = builder.build();
		HttpPost postRequest = new HttpPost(uri);
		postRequest.setHeader("Ocp-Apim-Subscription-Key", authKey);

		HttpResponse response = httpclient.execute(postRequest);
		HttpEntity entity = response.getEntity();

		if (entity != null) {
			System.out.println("test1");
			String str = EntityUtils.toString(entity);
			System.out.println(str);
			JSONObject jsonObj = new JSONObject(str);
			int statusId = jsonObj.getInt("statusId");
			switch (statusId) {
			case 0:
			case 2:
				return true;
			case 9:
				break;
			default:
				return false;
			}
		} else {
			System.out.println("test2");
			return false;
		}

		/* get */
		// URI uri = builder.build();
		HttpGet getRequest = new HttpGet(uri);
		getRequest.setHeader("Ocp-Apim-Subscription-Key", authKey);

		boolean temp = false;
		while (!temp) {
			response = httpclient.execute(getRequest);
			entity = response.getEntity();

			if (entity != null) {
				// System.out.println(EntityUtils.toString(entity));
				String str = EntityUtils.toString(entity);
				System.out.println(str);
				boolean allPass = true;
				JSONArray arr = new JSONArray(str);
				for (int i = 0; i < arr.length(); i++) {
					JSONObject obj = arr.optJSONObject(i);
					if (obj == null)
						return false;
					int statusId = obj.getJSONObject("details").getInt("statusId");
					System.out.println("statusId: " + statusId);
					// in progress or queued
					if (statusId == 3 || statusId == 9) {
						allPass = false;
						break;
					}
					// failed
					else if (statusId == 1) {
						return false;
					}
					// success
					else if (statusId == 2 || statusId == 0) {
					}
					// unexpected
					else {
						return false;
					}
				}

				if (allPass) {
					temp = true;
				} else {
					Threads.sleep(3000); // 隔3秒再重抓
				}
			} else {
				System.out.println("entity null");
				return false;
			}
		}
		return true;
	}
	
	
	public boolean publish() throws Exception {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("versionId", "0.1");
		jsonObj.put("isStaging",false);
		jsonObj.put("region", "westus");
		
		URIBuilder builder = new URIBuilder(
				"https://westus.api.cognitive.microsoft.com/luis/api/v2.0/apps/" + appId + "/publish");

		URI uri = builder.build();
		HttpPost request = new HttpPost(uri);
		request.setHeader("Content-Type", "application/json");
		request.setHeader("Ocp-Apim-Subscription-Key", authKey);
		
		
		
		 // Request body
		 StringEntity reqEntity = new StringEntity(jsonObj.toString());
		 request.setEntity(reqEntity);

		HttpResponse response = httpclient.execute(request);
		HttpEntity entity = response.getEntity();

//		if (entity != null) {
//			System.out.println(EntityUtils.toString(entity));
//		}
		JSONObject result = new JSONObject(EntityUtils.toString(entity));
		if (result.has("error"))
			return false;
		return true;
	}
	

	// -------------------------------------------------------------------------------
	private LuisIntent parseLuisIntent(JSONObject _jsonObj) {
		String id = _jsonObj.optString("id", "");
		String name = _jsonObj.optString("name", "");
		int typeId = _jsonObj.optInt("typeId", -1);
		String readableType = _jsonObj.optString("readableType", "");
		return new LuisIntent(id, name, typeId, readableType);
	}
}
