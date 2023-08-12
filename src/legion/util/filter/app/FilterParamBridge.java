package legion.util.filter.app;

import legion.util.filter.FilterOperation.FilterCompareOp;
import legion.util.filter.FilterParam;

public interface FilterParamBridge {
	/** getInstance */
	public FilterParam<Object> getParam(String _paramId);
	
	/** 取得允許的條件選單 */
	public FilterParam<?>[] getParams();
	
	/** 取得允許的運算元列表 */
	public FilterCompareOp[] getCompareOps(FilterParam<?> _targetParam);
	
//	/** 取得允許的排序條件列表 */
//	public FilterParam<?>[] getSortParams();
	
	/** 取得條件參數顯示的名稱 */
	public String getFilterStringParam(FilterParam<?> _targetParam);
	
	/** 將條件參數和條件值轉換成以使用者可以瀏覽的方式呈現。 */
	public String getFilterStringValue(FilterParam<?> _targetParam, Object _value);
	
	/** encode */
	public String encode(Object _value);
	
	/** decode */
	public Object decode(FilterParam<?> _targetParam, String _value);

}
