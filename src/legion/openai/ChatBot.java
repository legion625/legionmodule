package legion.openai;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.logging.Log;

import com.google.gson.Gson;

/**
 * /**
 * Shared by Matthew Tyson
 * https://www.infoworld.com/article/3697151/build-a-java-application-to-talk-to-chatgpt.html
 * @author Min-Hua Chao
 *
 */
public class ChatBot {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatBot.class);

	public static String sendQuery(String input, String endpoint, String apiKey) {
		// Build input and API key params
		JSONObject payload = new JSONObject();
		JSONObject message = new JSONObject();
		JSONArray messageList = new JSONArray();

		message.put("role", "user");
		message.put("content", input);
		messageList.put(message);

		payload.put("model", "gpt-3.5-turbo"); // model is important
//		payload.put("model", "text-davinci-003");

		payload.put("messages", messageList);
		payload.put("temperature", 0.7);
//		payload.put("max_tokens", 300); // 調整回答的長度，預設是16，但我試了100、300都還是得到一樣的簡短回答，設1000跑不出來。

		LOGGER.debug("payload: {}", payload);
		LOGGER.debug("test 1");

		StringEntity inputEntity = new StringEntity(payload.toString(), ContentType.APPLICATION_JSON);
		LOGGER.debug("payload.toString(): {}", payload.toString());
		LOGGER.debug("inputEntity: {}", inputEntity);

		// Build POST request
		HttpPost post = new HttpPost(endpoint);
		post.setEntity(inputEntity);
		post.setHeader("Authorization", "Bearer " + apiKey);
		post.setHeader("Content-Type", "application/json");

		LOGGER.debug("test 2");
		LOGGER.debug("post: {}", post);

		// Send POST request and parse response
		try (CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(post)) {
			HttpEntity resEntity = response.getEntity();
			LOGGER.debug("resEntity: {}", resEntity);
			LOGGER.debug("resEntity.getContent().toString(): {}", resEntity.getContent().toString());
			String resJsonString = new String(resEntity.getContent().readAllBytes(), StandardCharsets.UTF_8);
			LOGGER.debug("resJsonString: {}", resJsonString);
			JSONObject resJson = new JSONObject(resJsonString);
			LOGGER.debug("resJson: {}", resJson);

			LOGGER.debug("test 3");

			if (resJson.has("error")) {
				String errorMsg = resJson.getString("error");
				LOGGER.error("Chatbot API error: {}", errorMsg);
				return "Error: " + errorMsg;
			}

			LOGGER.debug("test 4");

			// Parse JSON response
			JSONArray responseArray = resJson.getJSONArray("choices");
			LOGGER.debug("test 4");
			List<String> responseList = new ArrayList<>();

			LOGGER.debug("test 5");

			for (int i = 0; i < responseArray.length(); i++) {
				JSONObject responseObj = responseArray.getJSONObject(i);
				String responseString = responseObj.getJSONObject("message").getString("content");
				responseList.add(responseString);
			}

			LOGGER.debug("test 6");

			// Convert response list to JSON and return it
			Gson gson = new Gson();
			String jsonResponse = gson.toJson(responseList);

			LOGGER.debug("test 7");

			return jsonResponse;
		} catch (IOException | JSONException e) {
			LOGGER.error("Error sending request: {}", e.getMessage());
			return "Error: " + e.getMessage();
		}
	}

	// -------------------------------------------------------------------------------
	/*
	 * MODEL FAMILIES API ENDPOINT
	 * 
	 * Newer models (2023–) gpt-4, gpt-3.5-turbo
	 * https://api.openai.com/v1/chat/completions Older models (2020–2022)
	 * 
	 * text-davinci-003, text-davinci-002, davinci, curie, babbage, ada
	 * https://api.openai.com/v1/completions
	 */
	// 預設的端點，GPT-3.5和GPT-4的model適用。
	public final static String END_POINT_DEFAULT = "https://api.openai.com/v1/chat/completions";
	
	public static String sendQuery(String input, String apiKey) {
		return sendQuery(input, END_POINT_DEFAULT, apiKey);
	}
}
