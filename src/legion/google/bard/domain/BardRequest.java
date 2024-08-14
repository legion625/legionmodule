package legion.google.bard.domain;

//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.Setter;

import java.util.Objects;

import static legion.google.bard.util.Constants.EMPTY_STRING;

//@AllArgsConstructor
//@Getter
//@Setter
public class BardRequest {
	private String strSNlM0e;
	private String question;
	private String conversationId;
	private String responseId;
	private String choiceId;

	public BardRequest(String strSNlM0e, String question, String conversationId, String responseId, String choiceId) {
		this.strSNlM0e = strSNlM0e;
		this.question = question;
		this.conversationId = conversationId;
		this.responseId = responseId;
		this.choiceId = choiceId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		BardRequest that = (BardRequest) o;
		return strSNlM0e.equals(that.strSNlM0e) && question.equals(that.question)
				&& conversationId.equals(that.conversationId) && responseId.equals(that.responseId)
				&& choiceId.equals(that.choiceId);
	}

	public String getStrSNlM0e() {
		return strSNlM0e;
	}

	public void setStrSNlM0e(String strSNlM0e) {
		this.strSNlM0e = strSNlM0e;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public String getResponseId() {
		return responseId;
	}

	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}

	public String getChoiceId() {
		return choiceId;
	}

	public void setChoiceId(String choiceId) {
		this.choiceId = choiceId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(strSNlM0e, question, conversationId, responseId, choiceId);
	}

	@Override
	public String toString() {
		return "BardRequest{" + "strSNlM0e=" + strSNlM0e + ", question=" + question + ", conversationId="
				+ conversationId + ", responseId=" + responseId + ", choiceId=" + choiceId + '}';
	}

	public static BardRequest newEmptyBardRequest() {
		return new BardRequest(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING);
	}
}
