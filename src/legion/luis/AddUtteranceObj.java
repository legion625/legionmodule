package legion.luis;

import java.util.ArrayList;
import java.util.List;

public class AddUtteranceObj {
	private String text;
	private String intentName;
	private List<AddUtterenceEntityObj> entityList;

	public AddUtteranceObj(String text, String intentName) {
		this.text = text;
		this.intentName = intentName;
		entityList = new ArrayList<>();
	}

	public void addEntity(String entityName, int startCharIndex, int endCharIndex) {
		entityList.add(new AddUtterenceEntityObj(entityName, startCharIndex, endCharIndex));
	}

	public String getText() {
		return text;
	}

	public String getIntentName() {
		return intentName;
	}

	public List<AddUtterenceEntityObj> getEntityList() {
		return entityList;
	}

	// -------------------------------------------------------------------------------
	class AddUtterenceEntityObj {
		private String entityName;
		private int startCharIndex;
		private int endCharIndex;

		AddUtterenceEntityObj(String entityName, int startCharIndex, int endCharIndex) {
			this.entityName = entityName;
			this.startCharIndex = startCharIndex;
			this.endCharIndex = endCharIndex;
		}

		public String getEntityName() {
			return entityName;
		}

		public int getStartCharIndex() {
			return startCharIndex;
		}

		public int getEndCharIndex() {
			return endCharIndex;
		}

	}
}
