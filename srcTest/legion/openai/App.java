package legion.openai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import legion.TestLogMark;
import legion.openai.ChatBot;

/**
 * Shared by Matthew Tyson
 * https://www.infoworld.com/article/3697151/build-a-java-application-to-talk-to-chatgpt.html
 * @author Min-Hua Chao
 *
 */
public class App {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestLogMark.class);
	
	  public static void main(String[] args) {
	    // Set ChatGPT endpoint and API key
//	    String endpoint = "https://api.openai.com/v1/chat/completions";
//	    String apiKey = "<YOUR-API-KEY>";
//	    String apiKey = "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";
//	    String apiKey = "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";
	String apiKey = "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";    
	        
	    // Prompt user for input string
	    try {
	      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	      System.out.print("Enter your message: ");
	      String input = reader.readLine();
	      LOGGER.debug("input: {}", input);
	            
	      // Send input to ChatGPT API and display response
//	      String response = ChatBot.sendQuery(input, endpoint, apiKey);
	      String response = ChatBot.sendQuery(input, apiKey);
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
