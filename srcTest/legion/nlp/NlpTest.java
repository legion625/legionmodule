package legion.nlp;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.stanford.nlp.util.CoreMap;

public class NlpTest {
	
	private static Nlp nlp;
	
	@BeforeClass
	public static void beforeClass() {
		nlp = Nlp.getInstance();
	}
	
	@Test
	public void test() {
		System.out.println("test");
	}
	
	@Test
	public void testParseLemma() {
//		String _text = "Fig 4 is A method [12] and system of processing an information technology (IT) electronic request is provided. The electronic request is received in natural language from a user. Parameters of the electronic request are extracted. A risk of the electronic request is determined. A policy based on the parameters and the risk of the electronic request is determined and executed. A level of trust between the user and the computer device is calculated based on the determined risk and an outcome of the execution of the policy. ";
		String _text = "A device may receive first data associated with a delivery of an item or service. The first data may be received from a system in association with an order being placed for the item or service. The device may receive second data associated with scheduling the delivery from another device. A portion of the second data may include natural language text data, or natural language audio data. The device may process the first data and the second data using a processing technique to identify information related to scheduling the delivery. The device may perform an action related to the delivery. The action may include scheduling the delivery based on a result of processing the first data and the second data, monitoring the first data and the second data, or modifying the delivery based on monitoring the first data and the second data.\r\n";
		
		List<CoreMap> sentences = nlp.parseSentences(_text);
		List<String> lemmaList = nlp.parseLemma(sentences);
		for(String lemma:lemmaList )
			System.out.println(lemma);
	}
	
	@Test
	public void testFilterStopWords() {
		String _text = "Fig 4 is A method [12] and system of processing an information technology (IT) electronic request is provided. The electronic request is received in natural language from a user. Parameters of the electronic request are extracted. A risk of the electronic request is determined. A policy based on the parameters and the risk of the electronic request is determined and executed. A level of trust between the user and the computer device is calculated based on the determined risk and an outcome of the execution of the policy. ";
//		String _text = "A method and system of processing an information technology (IT) electronic request is provided. The electronic request is received in natural language from a user. Parameters of the electronic request are extracted. A risk of the electronic request is determined. A policy based on the parameters and the risk of the electronic request is determined and executed. A level of trust between the user and the computer device is calculated based on the determined risk and an outcome of the execution of the policy. A conversation pattern of the computer device toward the user is adjusted based on the calculated level of trust. \"What is claimed is: \r\n" + 
//				" | 1. A computing device comprising: \r\n" + 
//				"a processor; \r\n" + 
//				"a network interface coupled to the processor to enable communication over a network; \r\n" + 
//				"a storage device coupled to the processor; \r\n" + 
//				"a conversation agent software stored in the storage device, wherein an execution of the software by the processor configures the computing device to perform acts comprising: \r\n" + 
//				"receiving an information technology (IT) electronic request in natural language from a user, via a user device, over the network; \r\n" + 
//				"extracting parameters of the electronic request; \r\n" + 
//				"determining a risk of the electronic request; \r\n" + 
//				"determining a policy based on the parameters and the risk of the electronic request; \r\n" + 
//				"executing the policy; \r\n" + 
//				"calculating a level of trust between the user and the conversation agent based on the determined risk and an outcome of the execution of the policy; and \r\n" + 
//				"adjusting a conversation pattern of the conversation agent toward the user based on the calculated level of trust. \r\n" + 
//				" | 2. The computing device of claim 1, wherein extracting parameters of the electronic request comprises: \r\n" + 
//				"performing speech recognition to determine a textual representation of the electronic request; \r\n" + 
//				"performing speech segmentation on the textual representation of the electronic request; and \r\n" + 
//				"applying concept expansion on the textual representation of the electronic request to determine an intent of the electronic request. \r\n" + 
//				" | 3. The computing device of claim 1, wherein determining the risk of the electronic request comprises determining how fulfillment of the electronic request would affect one or more resources of an IT network of a business enterprise.\r\n" + 
//				" | 4. The computing device of claim 1, wherein determining the policy comprises determining whether the user is authorized to submit the electronic request.\r\n" + 
//				" | 5. The computing device of claim 1, wherein calculating the level of trust between the user and the conversation agent comprises calculating a level of trust of the conversation agent toward the user.\r\n" + 
//				" | 6. The computing device of claim 5, wherein calculating the level of trust of the conversation agent toward the user is further based on a relevance of the electronic request with respect to resources of a private network of the computing device.\r\n" + 
//				" | 7. The computing device of claim 1, wherein calculating a level of trust between the user and the conversation agent comprises calculating a level of trust of the user toward the conversation agent.\r\n" + 
//				" | 8. The computing device of claim 1: \r\n" + 
//				"wherein calculating the level of trust between the user and the conversation agent comprises:  \r\n" + 
//				"calculating a first level of trust of the conversation agent toward the user; and \r\n" + 
//				"calculating a second level of trust of the user toward the conversation agent; and \r\n" + 
//				"wherein the adjustment of the behavioral approach of the conversation agent toward the user is based on the calculated first level of trust and the calculated second level of trust. \r\n" + 
//				" | 9. The computing device of claim 1, wherein execution of the conversation agent by the processor further configures the computing device to perform acts comprising: \r\n" + 
//				"soliciting a satisfaction feedback regarding the execution of the electronic request, from the user via the user device, wherein calculating a level of trust between the user and the conversation agent is further based on the satisfaction feedback. \r\n" + 
//				" | 10. The computing device of claim 1, wherein execution of the conversation agent by the processor further configures the computing device to perform acts comprising: \r\n" + 
//				"retrieving a prior level of trust between the user and the conversation agent, wherein calculating the level of trust between the user and the conversation agent is further based on the prior level of trust between the user and the conversation agent. \r\n" + 
//				" | 11. A non-transitory computer readable storage medium tangibly embodying a computer readable program code having computer readable instructions that, when executed, causes a computer device to carry out a method of processing an information technology (IT) electronic request, the method comprising: \r\n" + 
//				"receiving the electronic request in natural language from a user; \r\n" + 
//				"extracting parameters of the electronic request; \r\n" + 
//				"determining a risk of the electronic request; \r\n" + 
//				"determining a policy based on the parameters and the risk of the electronic request; \r\n" + 
//				"executing the policy; \r\n" + 
//				"calculating a level of trust between the user and the computer device based on the determined risk, and an outcome of the execution of the policy; and \r\n" + 
//				"adjusting a conversation pattern of the computer device toward the user based on the calculated level of trust. \r\n" + 
//				" | 12. The non-transitory computer readable storage medium of claim 11, wherein extracting parameters of the electronic request comprises: \r\n" + 
//				"performing speech recognition to determine a textual representation of the electronic request; \r\n" + 
//				"performing speech segmentation on the textual representation of the electronic request; and \r\n" + 
//				"applying concept expansion on the textual representation of the electronic request to determine an intent of the electronic request. \r\n" + 
//				" | 13. The non-transitory computer readable storage medium of claim 11, wherein determining the risk of the electronic request comprises determining how fulfillment of the electronic request would affect one or more resources of an IT network of a business enterprise.\r\n" + 
//				" | 14. The non-transitory computer readable storage medium of claim 11, wherein determining the policy comprises determining whether the user is authorized to submit the electronic request.\r\n" + 
//				" | 15. The non-transitory computer readable storage medium of claim 11, wherein calculating the level of trust between the user and the computer device comprises calculating a level of trust of the computer device toward the user.\r\n" + 
//				" | 16. The non-transitory computer readable storage medium of claim 15, wherein calculating the level of trust of the computer device toward the user is further based on a relevance of the electronic request with respect to resources of a private network of the computing device.\r\n" + 
//				" | 17. The non-transitory computer readable storage medium of claim 11, wherein calculating a level of trust between the user and the computer device comprises calculating a level of trust of the user toward the computer device.\r\n" + 
//				" | 18. The non-transitory computer readable storage medium of claim 11: \r\n" + 
//				"wherein calculating the level of trust between the user and the computer device comprises:  \r\n" + 
//				"calculating a first level of trust of the computer device toward the user; and \r\n" + 
//				"calculating a second level of trust of the user toward the computer device; and \r\n" + 
//				"wherein the adjustment of the behavioral approach of the computer device toward the user is based on the calculated first level of trust and the calculated second level of trust. \r\n" + 
//				" | 19. The non-transitory computer readable storage medium of claim 11, wherein execution of the computer device by the processor further configures the computing device to perform acts comprising: \r\n" + 
//				"soliciting a satisfaction feedback regarding the execution of the electronic request, from the user via the user device, wherein calculating a level of trust between the user and the computer device is further based on the satisfaction feedback. \r\n" + 
//				" | 20. The non-transitory computer readable storage medium of claim 11, wherein execution of the computer device by the processor further configures the computing device to perform acts comprising: \r\n" + 
//				"retrieving a prior level of trust between the user and the computer device, wherein calculating the level of trust between the user and the computer device is further based on the prior level of trust between the user and the computer device.\"\r\n" + 
//				"";
		List<CoreMap> sentences = nlp.parseSentences(_text);
		List<String> lemmaList = nlp.parseLemma(sentences);
		
		//
		List<String> _wordList = lemmaList;
		List<String> filteredWordList = nlp.filterStopWords(_wordList, null);
		System.out.println("words count before filtering stopwords:\t"+_wordList.size());
		System.out.println("words count after filtering stopwords:\t"+filteredWordList.size());
		
		for(String word:filteredWordList )
			System.out.println(word);
	}
	
}
