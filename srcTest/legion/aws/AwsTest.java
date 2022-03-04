package legion.aws;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.DetectEntitiesRequest;
import com.amazonaws.services.comprehend.model.DetectEntitiesResult;
import com.amazonaws.services.comprehend.model.DetectSentimentRequest;
import com.amazonaws.services.comprehend.model.DetectSentimentResult;
import com.amazonaws.services.comprehend.model.Entity;
import com.amazonaws.services.greengrass.model.SageMakerMachineLearningModelResourceData;
import com.amazonaws.services.identitymanagement.model.Role;
import com.amazonaws.services.s3.event.S3EventNotification.S3BucketEntity;
import com.amazonaws.services.s3.model.S3ObjectIdBuilder;
import com.amazonaws.services.sagemaker.AmazonSageMaker;
import com.amazonaws.services.sagemaker.AmazonSageMakerAsync;
import com.amazonaws.services.sagemaker.AmazonSageMakerAsyncClientBuilder;
import com.amazonaws.services.sagemaker.AmazonSageMakerClientBuilder;
import com.amazonaws.services.sagemaker.model.CreateModelRequest;
import com.amazonaws.services.sagemaker.model.CreateNotebookInstanceRequest;
import com.amazonaws.services.sagemaker.model.CreateNotebookInstanceResult;
import com.amazonaws.services.transcribe.AmazonTranscribeAsyncClientBuilder;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.AmazonTranslateClientBuilder;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;

/**
 * must use jar aws-java-sdk-1.11.475
 * @author 使用者
 *
 */
public class AwsTest {
	@Test
	@Ignore
	public void testDetect() {
		AmazonComprehend cph = AmazonComprehendClientBuilder.defaultClient();
		System.out.println("cph: " + cph);

		DetectEntitiesRequest req = new DetectEntitiesRequest();
		req.setLanguageCode("en");
		System.out.println("req: " + req);
		String text = "The ohm egg is really tender, 4-5 points; "
				+ "the rest of the chicken chops and tomato sauce fried corn ham rice pop.";
		req = req.withText(text);
		System.out.println("---------------------detect entities---------------------");
		DetectEntitiesResult result = cph.detectEntities(req);

		List<Entity> entityList = result.getEntities();
		for (Entity e : entityList) {
			System.out.println(e.getText() + "\t" + e.getType() + "\t" + e.getScore() + "\t" + e.getBeginOffset() + "\t"
					+ e.getEndOffset());
		}
		DetectSentimentRequest dsRequest = new DetectSentimentRequest();
		dsRequest.setLanguageCode("en");
		dsRequest = dsRequest.withText(text);
		System.out.println("---------------------detect sentiment---------------------");
		DetectSentimentResult dsResult = cph.detectSentiment(dsRequest);
		System.out.println(dsResult.getSentiment() + "\t" + dsResult.getSentimentScore());
	}
	
	@Test
	public void testTranslate() {
		AmazonTranslate translate = AmazonTranslateClientBuilder.defaultClient();
		System.out.println("translate: " + translate);
		TranslateTextRequest req = new TranslateTextRequest();
		req.setSourceLanguageCode("zh-TW");
		req.setTargetLanguageCode("en");
		req.setText("這是一間好吃的餐廳。");
		
		TranslateTextResult result = translate.translateText(req);
		System.out.println("result.getTranslatedText(): " + result.getTranslatedText());
	}
	
	@Test
	public void testSageMaker() {
		AmazonSageMaker asm = AmazonSageMakerClientBuilder.defaultClient();
		System.out.println("asm: " + asm);
		CreateNotebookInstanceResult result = new CreateNotebookInstanceResult();
		result.withNotebookInstanceArn("arn:aws:sagemaker:us-east-1:414867812600:notebook-instance/mysagemakerinstance");
		System.out.println("result: " +result);

		
	}
}
