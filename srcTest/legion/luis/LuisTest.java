package legion.luis;

import java.net.URISyntaxException;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

public class LuisTest {
	
	private static String authKey = "58c4d495240648bdb1634d77b87fa06d";
	private static String appId =  "6cd2bbba-705c-4d03-b7b1-ea384b628022"; // EK
//	private static String appId =  "15f98363-8e51-4ccd-961f-0d69763d82d3"; // Travel Agent
	private static String versionId = "0.1";
	
	private static LuisProxy proxy ;
	
	@BeforeClass
	public static void beforeClass() throws URISyntaxException {
		proxy= new LuisProxy(authKey, appId, versionId);
	}

	@Test
	public void getAllIntents() throws Exception {
		LuisIntent[] intents = proxy.getAllIntents();
		for (LuisIntent intent : intents) {
			System.out.println(intent.getId() + "\t" + intent.getName() + "\t" + intent.getTypeId() + "\t"
					+ intent.getReadableType());
		}
	}

	@Test
	public void getIntent() throws Exception {
//		LuisIntent intent = proxy.getIntent("4974315c-8aa7-4cdb-bb81-15ae3a05a1ab");
		LuisIntent intent = proxy.getIntent("testfail");
		System.out.println(
				intent.getId() + "\t" + intent.getName() + "\t" + intent.getTypeId() + "\t" + intent.getReadableType());
	}

	@Test
	public void createIntent() throws Exception {
		JSONObject newIntent = new JSONObject();
		newIntent.put("name", "BookFlight6");
		boolean result = proxy.createIntent(newIntent);
		System.out.println("result: " + result);
	}


	@Test
	public void understanding() throws Exception {
		LuisResult result = proxy.understanding("tell me about ifb");
		System.out.println("result.getQuery(): " + result.getQuery());
		System.out.println("top scoring intent: " + result.getTopScoringIntentName() + "\t" + result.getScore());

		for (LuisEntity entity : result.getEntities()) {
			System.out.println("entity: " + entity.getName() + "\t" + entity.getType() + "\t" + entity.getResolution());
		}
	}

	@Test
	public void addUtterance() throws Exception{
		String text = "Hi how are you";
		String intentName = "None";
		AddUtteranceObj obj = new AddUtteranceObj(text, intentName);
//		obj.addEntity("Location::LocationFrom", 22, 26);
//		obj.addEntity("Location::LocationTo", 31, 37);
		
		boolean result = proxy.addUtterance(obj);
		System.out.println("result: " + result);
//		"entityLabels":
//			[
//				{
//					"entityName": "Location::From",
//					"startCharIndex": 22,
//					"endCharIndex": 26
//				},
//				{
//					"entityName": "Location::To",
//					"startCharIndex": 31,
//					"endCharIndex": 37
//				}
//			]
	}
	
	@Test
	public void train() throws Exception {
		boolean result = proxy.train();
		System.out.println("result: " +result);
	}
	
	@Test
	public void publish() throws Exception {
		boolean result = proxy.publish();
		System.out.println("result: " +result);
	}

}
