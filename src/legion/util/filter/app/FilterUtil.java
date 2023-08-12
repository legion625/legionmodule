package legion.util.filter.app;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import legion.util.DataFO;
import legion.util.filter.FilterCondition;
import legion.util.filter.FilterOperation;
import legion.util.filter.FilterOperation.FilterCompareOp;
import legion.util.filter.FilterOperation.FilterConjunctiveOp;
import legion.util.filter.FilterOperation.FilterGroup;
import legion.util.filter.FilterOperation.FilterValue;
import legion.util.filter.FilterParam;

public class FilterUtil {
	
	public static JSONObject encodeJson(FilterCondition<FilterParam<?>> _condition,
			FilterParamBridge _filterParamBridge) {
		JSONObject json = new JSONObject();
		/* FilterValue */
		if (_condition instanceof FilterValue) {
			FilterValue<FilterParam<?>, ?> condition = (FilterValue<FilterParam<?>, ?>) _condition;
			json.put("type", "FilterValue");
			json.put("param", condition.getCondition().getId());
			json.put("compareOp", condition.getCompareOp().getId());
			json.put("value", _filterParamBridge.encode(condition.getValue().get()));
		}
		/* FilterGroup */
		else if (_condition instanceof FilterGroup) {
			FilterGroup<FilterParam<?>> condition = (FilterGroup<FilterParam<?>>) _condition;
			json.put("type", "FilterGroup");
			json.put("conjunctiveOp", condition.getConjunctiveOp().getId());
			// 子階
			FilterCondition<FilterParam<?>>[] subs = condition.getValues();
			if (subs != null) {
				JSONArray cArrays = new JSONArray();
				for (FilterCondition<FilterParam<?>> c : subs)
					cArrays.put(encodeJson(c, _filterParamBridge));
				json.put("conditions", cArrays);
			}
		}
		return json;
	}
	
	public static FilterCondition<FilterParam<?>> decodeJson(JSONObject json, FilterParamBridge _filterParamBridge) {
		if(json==null)
			return null;
		
		/* FilterValue */
		if("FilterValue".equalsIgnoreCase(json.getString("type"))) {
			FilterParam<?> param = _filterParamBridge.getParam(json.getString("type"));
			if (param == null)
				return null;
			FilterCompareOp op = FilterCompareOp.getCompareOp(json.getString("compareOp"));
			Object value = _filterParamBridge.decode(param, json.getString("value"));
			return new FilterValue<>(param, op, value);
		}
		/* FilterGroup */
		else if("FilterGroup".equalsIgnoreCase(json.getString("type"))) {
			FilterConjunctiveOp op = FilterConjunctiveOp.getConjunctiveOp(json.getString("conjunctiveOp"));
			JSONArray cjs = json.getJSONArray("conditions");
//			if(cjs==null || cjs.length()==0) // 允許匯入不完整的condition 
//				return null;
			FilterCondition<FilterParam<?>>[] subs = new FilterCondition[cjs.length()];
			for(int i=0;i<cjs.length();i++)
				subs[i] = decodeJson(cjs.getJSONObject(i), _filterParamBridge);
			return new FilterGroup<>(op, subs);
		}
		
		return null;
	}
	
	public static String getFilterConditionDesplayStr(FilterOperation _operation, FilterParamBridge _bridge) {
		if(_operation==null)
			return "";
		List<FilterCondition> conditionList = _operation.getCondition();
		String result = "";
		for(FilterCondition condition: conditionList) {
			if(!DataFO.isEmptyString(result))
				result+="\n";
			result+=getFilterConditionDesplayStr(condition, _bridge,0);
		}
		return result;
	}
	
	public static String getFilterConditionDesplayStr(FilterCondition _condition, FilterParamBridge _bridge,
			int _level) {
		// TODO validate

		/**/
		if (_condition instanceof FilterGroup) {
			FilterGroup<FilterParam<?>> condition = (FilterGroup<FilterParam<?>>) _condition;
			String conjunctiveOpStr = " " + condition.getConjunctiveOp().toString() + " ";
			// 子階
			FilterCondition<FilterParam<?>>[] subs = condition.getValues();
			if (subs != null) {
				String[] subStrs = new String[subs.length];
				for (int i = 0; i < subs.length; i++)
					subStrs[i] = getFilterConditionDesplayStr(subs[i], _bridge, _level + 1);
				String wStr = "";
				for (String subStr : subStrs) {
					if (!DataFO.isEmptyString(wStr))
						wStr += conjunctiveOpStr;
					wStr += subStr;
				}
				String resultStr = _level > 0 && subs.length > 1 ? "(" + wStr + ")" : wStr;
				return resultStr;
			} else
				return "";
		}
		/**/
		else if (_condition instanceof FilterValue) {
			FilterValue<FilterParam<?>, ?> condition = (FilterValue<FilterParam<?>, ?>) _condition;
			return _bridge.getFilterStringParam(condition.getCondition()) + " " + condition.getCompareOp().toString()
					+ " " + _bridge.getFilterStringValue(condition.getCondition(), condition.getValue().get());
		}
		/**/
		else
			return "";
	}
	

}
