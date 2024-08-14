package legion.google.bard.domain;

//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.ToString;

import java.util.List;

//@AllArgsConstructor
//@Getter
//@Builder // TODO
//@ToString // TODO
public class Answer {
	private final AnswerStatus status;

	private final String chosenAnswer;

	private final List<Image> images;

	public Answer(AnswerStatus status, String chosenAnswer, List<Image> images) {
		this.status = status;
		this.chosenAnswer = chosenAnswer;
		this.images = images;
	}

	public AnswerStatus getStatus() {
		return status;
	}

	public String getChosenAnswer() {
		return chosenAnswer;
	}

	public List<Image> getImages() {
		return images;
	}
	
	public String markdown() {

		String markdown = this.chosenAnswer;

		if (images != null && images.size() > 0) {
			for (Image image : images) {
				markdown = markdown.replaceFirst(image.labelRegex(), image.markdown());
			}

		}

		return markdown;
	}
	
	// -------------------------------------------------------------------------------
	public static class AnswerBuilder {
		private AnswerStatus status;
		private String chosenAnswer;
		private List<Image> images;
		private AnswerBuilder() {
		}
		
		public AnswerBuilder status(AnswerStatus status) {
			this.status = status;
			return this;
		}
		
		public AnswerBuilder chosenAnswer(String chosenAnswer) {
			this.chosenAnswer = chosenAnswer;
			return this;
		}
		
		public AnswerBuilder images(List<Image> images) {
			this.images = images;
			return this;
		}
		
		public Answer build() {
			return new Answer(status, chosenAnswer, images);
		}
		
	}

	public static AnswerBuilder builder() {
		return new AnswerBuilder();
	}

}
