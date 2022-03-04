package legion.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

public class JsonUtil {

	public static String getJsonArrayString(Object[] _objs) {
		JSONArray jsonArray = new JSONArray(_objs);
		return jsonArray.toString();
	}
	
	public static Object[] parseJsonArrayStringToObjs(String _jsonArrayString) {
		return parseJsonArrayString(_jsonArrayString).toArray(new Object[0]);
	}
	
	public static String getJsonArrayString(List<? extends Object> _objList){
		JSONArray jsonArray = new JSONArray(_objList);
		return jsonArray.toString();
	}

	public static List<Object> parseJsonArrayString(String _jsonArrayString) {
		JSONArray jsonArray = new JSONArray(_jsonArrayString);
		List<Object> list = new ArrayList<>();
		for (int i = 0; i < jsonArray.length(); i++)
			list.add(jsonArray.get(i));
		return list;
	}
}
