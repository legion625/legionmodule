package legion.openai.byMatthewTyson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import legion.TestLogMark;

public class App {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestLogMark.class);
	
	  public static void main(String[] args) {
	    // Set ChatGPT endpoint and API key
	    String endpoint = "https://api.openai.com/v1/chat/completions";
//		  String endpoint = "https://api.openai.com/v1/completions";
//	    String apiKey = "<YOUR-API-KEY>";
	    String apiKey = "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";
	        
	    // Prompt user for input string
	    try {
	      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	      System.out.print("Enter your message: ");
	      String input = reader.readLine();
	            
	      // Send input to ChatGPT API and display response
	      String response = ChatBot.sendQuery(input, endpoint, apiKey);
	      LOGGER.info("Response: {}", response);
	    } catch (IOException e) {
	      LOGGER.error("Error reading input: {}", e.getMessage());
	    } catch (JSONException e) {
	      LOGGER.error("Error parsing API response: {}", e.getMessage());
	    } catch (Exception e) {
	      LOGGER.error("Unexpected error: {}", e.getMessage());
	    }
	  }
}
