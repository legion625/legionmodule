package legion.luis;

public class LuisIntent {
	// "id": "8285a9ee-6bc0-4409-87f4-82d539f70529",
	// "name": "None",
	// "typeId": 0,
	// "readableType": "Intent Classifier"

	private String id;
	private String name;
	private int typeId;
	private String readableType;

	LuisIntent(String id, String name, int typeId, String readableType) {
		this.id = id;
		this.name = name;
		this.typeId = typeId;
		this.readableType = readableType;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getTypeId() {
		return typeId;
	}

	public String getReadableType() {
		return readableType;
	}

}
