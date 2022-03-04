package legion.luis;

public class LuisEntity {
	private String name;
	private String type;
	private String resolution;

	LuisEntity(String name, String type, String resolution) {
		this.name = name;
		this.type = type;
		this.resolution = resolution;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getResolution() {
		return resolution;
	}

}
