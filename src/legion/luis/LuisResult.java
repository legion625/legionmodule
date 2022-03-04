package legion.luis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class LuisResult {
	private String query;
	private String topScoringIntentName;
	private double score;
	private List<LuisEntity> entities;

	LuisResult(JSONObject result) {
		/* query */
		query = result.getString("query");
		/* top scoring intent */
		JSONObject intentJsonObj = result.getJSONObject("topScoringIntent");
		topScoringIntentName = (String) intentJsonObj.get("intent");
		score = intentJsonObj.getDouble("score");
		/* entities */
		entities = new ArrayList<>();
		JSONArray jsonarr = result.getJSONArray("entities");
		for (int i = 0; i < jsonarr.length(); i++) {
			JSONObject ejsonobj = jsonarr.getJSONObject(i);
			System.out.println("ejsonobj: " + ejsonobj);
			String e = (String) ejsonobj.get("entity");
			String t = (String) ejsonobj.get("type");
			String r = ejsonobj.getJSONObject("resolution").getJSONArray("values").getString(0);
			System.out.println("r: " + r);
			entities.add(new LuisEntity(e, t, r));
		}
	}

	public String getQuery() {
		return query;
	}

	public String getTopScoringIntentName() {
		return topScoringIntentName;
	}

	public double getScore() {
		return score;
	}

	public List<LuisEntity> getEntities() {
		return entities;
	}

}
