package legion.util.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import legion.LegionContext;
import legion.util.DataFO;

/**
 * @author Min-Hua Chao
 *
 * @param <C>
 * @param <R>
 */
public class QueryOperation<C extends QueryParam, R> implements Serializable {
	private static Logger log = LoggerFactory.getLogger(QueryOperation.class);

	// -------------------------------------------------------------------------------
	private List<QueryCondition<C>> conditions = new ArrayList<>();
	private List<R> queryResult;
	private int total;

	/* limit */
	private int start = -1; // -1表示沒有在程式中設定，則優先參考system-conf設定檔。
	private int size = -1; // -1表示沒有在程式中設定，則優先參考system-conf設定檔。

	/* sort */
	private List<SortTuple<C>> sorts;

	// -------------------------------------------------------------------------------
	public List<QueryCondition<C>> getConditions() {
		return conditions;
	}

	public List<R> getQueryResult() {
		return queryResult == null ? Collections.emptyList() : queryResult;
	}

	public void setQueryResult(List<R> queryResult) {
		this.queryResult = queryResult;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	// -------------------------------------------------------------------------------
	public int[] getLimit() {
//		if(isDefaultLimitSize() && start<0) {
//			return new int[] { 0, getLimitSize() };
//		}
//		else
//			return new int[] {start, getLimitSize()};
		return new int[] { Math.max(start, 0), getLimitSize() };
	}

	public void setLimit(int start, int size) {
		if (start < 0 || size < 0) {
			log.error("setLimit error. 'start' or 'size' must NOT lesser than 0.");
			return;
		}
		this.start = start;
		this.size = size;
	}

	public void setLimit(int size) {
		setLimit(0, size);
	}

	/**
	 * 因為Serializable的關係，若宣告類別中的defaultSize屬性，會產生版本差異的marshalling異常，所以只能在方法中取得相關參數及判別。
	 * 
	 * @return
	 */
	private int getDefaultLimitSize() {
		int defaultSize = -1;
		//
		String defaultSizeStr = LegionContext.getInstance().getSystemInfo()
				.getAttribute("system.queryOperation.limit");
		if (DataFO.isInt(defaultSizeStr))
			defaultSize = Integer.parseInt(defaultSizeStr);
		return defaultSize;
	}

	private int getLimitSize() {
		// 有在程式中設定
		if (size >= 0)
			return size;
		// 有在設定檔中設定
		int defaultLimitSize = getDefaultLimitSize();
		if (defaultLimitSize >= 0)
			return defaultLimitSize;
		// 都沒有設定
		return -1;
	}

	// XXX 是否非必要?
	private boolean isDefaultLimitSize() {
		// 有在程式中設定
		if (size >= 0)
			return false;
		// 有在設定檔中設定
		int defaultLimitSize = getDefaultLimitSize();
		if (defaultLimitSize >= 0)
			return true;
		// 都沒有設定
		return false;
	}

	// -------------------------------------------------------------------------------
	// -------------------------------------Sort--------------------------------------
	public static class SortTuple<C> implements Serializable {
		private C sort;
		private SortDir dir;

		SortTuple(C sort, SortDir dir) {
			super();
			this.sort = sort;
			this.dir = dir;
		}

		public C getSort() {
			return sort;
		}

		public SortDir getDir() {
			return dir;
		}

	}

	private void initSortContainer() {
		if (sorts == null)
			sorts = new ArrayList<>();
	}

	public List<SortTuple<C>> getSorts() {
		return sorts;
	}

	// -------------------------------------------------------------------------------
	// -------------------------------appendConditions--------------------------------
	// XXX 已經有用QueryCondition<C>作為參數的方法，QueryValue<C,?>又implements
	// QueryCondition<C>，此方法是否有必要?
	public QueryOperation<C, R> appendCondition(QueryValue<C, ?> _query) {
		conditions.add(_query);
		return this;
	}

	// XXX 已經有用QueryCondition<C>作為參數的方法，QueryGroup<C>又implements
	// QueryCondition<C>，此方法是否有必要?
	public QueryOperation<C, R> appendCondition(QueryGroup<C> _query) {
		conditions.add(_query);
		return this;
	}

	// XXX 已經有用QueryCondition<C>作為參數的方法，QueryBoolean<C>又implements
	// QueryCondition<C>，此方法是否有必要?
	public QueryOperation<C, R> appendCondition(QueryBoolean<C> _query) {
		conditions.add(_query);
		return this;
	}

	// XXX 已經有用QueryCondition<C>作為參數的方法，QueryNullBoolean<C>又implements
	// QueryCondition<C>，此方法是否有必要?
	public QueryOperation<C, R> appendCondition(QueryNullBoolean<C> _query) {
		conditions.add(_query);
		return this;
	}

	public QueryOperation<C, R> appendCondition(QueryCondition<C> _query) {
		conditions.add(_query);
		return this;
	}

	/**
	 * 建立一個新的QueryOperation物件，並且複製其設定值。
	 * 
	 * @return
	 */
	public QueryOperation<C, ?> copy() {
		QueryOperation<C, ?> copy = new QueryOperation<>();
		copy.conditions = this.conditions;
		// no results
		copy.total = this.getTotal();
		int[] limit = getLimit();
		copy.start = limit[0];
		copy.size = limit[1];
		copy.sorts = this.getSorts();
		return copy;
	}

	// -------------------------------------------------------------------------------
	/**
	 * 解析單一條件的「條件值組合字串」，封裝成QueryCondition
	 * 
	 * @param <C>
	 * @param _data
	 * @param queryParam
	 * @param _compareOp
	 * @param _autoWildValue
	 * @return
	 */
	public static <C extends QueryParam> QueryCondition<C>[] parseCombineQueryString(String _data, C queryParam,
			CompareOp _compareOp, boolean _autoWildValue) {
		if (DataFO.isEmptyString(_data)) {
			log.debug("_data null.");
			return null;
		}

		List<QueryCondition<C>> querys = new ArrayList<>();
		do {
			// 先清除前端、尾端是";"符號
			while (_data.charAt(0) == ';')
				_data = _data.substring(1);
			// 清除尾端的";"符號
			while (_data.charAt(_data.length() - 1) == ';')
				_data = _data.substring(0, _data.length() - 1);

			// 找出對應的段落
			if ((_data.startsWith("&&(") || _data.startsWith("||(")) && _data.indexOf(')') > 0) {
				int e = -1;
				int count = 0;
				do {
					int s = e;
					int eNext = _data.indexOf(')', e + 1);
					if (eNext < 0)
						break;
					// 計算在目前範疇有幾個"("
					while (true) {
						int sNext = _data.indexOf('(', s + 1);
						if (sNext > eNext || sNext < 0)
							break;
						count++;
						s = sNext;
					}
					// 扣掉目前
					count--;
					e = eNext;
				} while (count > 0);

				if (count != 0) {
					log.error("群組語法錯誤...");
					return null;
				}

				String item = _data.substring(0, e + 1);
				if (item.startsWith("&&(")) {
					QueryCondition<C>[] subQuery = parseCombineQueryString(item.substring(3, item.length() - 1),
							queryParam, _compareOp, _autoWildValue);
					querys.add(QueryOperation.group(ConjunctiveOp.and, subQuery));
				} else if (item.startsWith("||(")) {
					QueryCondition<C>[] subQuery = parseCombineQueryString(item.substring(3, item.length() - 1),
							queryParam, _compareOp, _autoWildValue);
					querys.add(QueryOperation.group(ConjunctiveOp.or, subQuery));
				}
				_data = _data.substring(e + 1);
			} else {
				int eIdx = _data.indexOf(";");
				String item;
				if (eIdx > 0) {
					item = _data.substring(0, eIdx);
					_data = _data.substring(eIdx + 1);
				} else {
					item = _data;
					_data = "";
				}
				item = _autoWildValue ? "%" + item + "%" : item;
				querys.add(value(queryParam, _compareOp, item));
			}
		} while (!DataFO.isEmptyString(_data));
		return querys.toArray(new QueryCondition[0]);
	}

	// -------------------------------------------------------------------------------
	// ----------------------------------operations-----------------------------------
	public static <E extends QueryParam> QueryGroup<E> group(ConjunctiveOp _conjunctiveOp,
			QueryCondition<E>... _values) {
		return new QueryGroup<>(_conjunctiveOp, _values);
	}

	public static <E extends QueryParam, K> QueryValue<E, K> value(E _condition, CompareOp _compareOp, K _value) {
		return new QueryValue<>(_condition, _compareOp, _value);
	}

	public static <C extends QueryParam> QueryBoolean<C> booleanOp(CompareBoolean _compareBoolean,
			QueryCondition<C> _condition) {
		return new QueryBoolean<>(_compareBoolean, _condition);
	}

	public static <C extends QueryParam> QueryNullBoolean<C> nullBooleanOp(CompareNullBoolean _compareNullBoolean,
			C _param) {
		return new QueryNullBoolean<>(_compareNullBoolean, _param);
	}

	// -------------------------------------------------------------------------------
	// ------------------------------------classes------------------------------------
	public static class QueryValue<C extends QueryParam, T> implements QueryCondition<C> {
		private C condition;
		private CompareOp compareOp;
		private T value;

		public QueryValue(C condition, CompareOp compareOp, T value) {
			this.condition = condition;
			this.compareOp = compareOp;
			this.value = value;
		}

		public C getCondition() {
			return condition;
		}

		public CompareOp getCompareOp() {
			return compareOp;
		}

		public Optional<T> getValue() {
			return Optional.ofNullable(value);
		}
	}

	public static class QueryGroup<C extends QueryParam> implements QueryCondition<C> {
		private ConjunctiveOp conjunctiveOp;
		private QueryCondition<C>[] values;

		public QueryGroup(ConjunctiveOp conjunctiveOp, QueryCondition<C>... values) {
			super();
			this.conjunctiveOp = conjunctiveOp;
			this.values = values;
		}

		public ConjunctiveOp getConjunctiveOp() {
			return conjunctiveOp;
		}

		public QueryCondition<C>[] getValues() {
			return values;
		}

		public QueryCondition<C> getValue(int _idx) {
			return values[_idx];
		}
	}
	
	public static class QueryBoolean<C extends QueryParam> implements QueryCondition<C> {
		private CompareBoolean compareBoolean;
		private QueryCondition<C> condition;

		public QueryBoolean(CompareBoolean compareBoolean, QueryCondition<C> condition) {
			this.compareBoolean = compareBoolean;
			this.condition = condition;
		}

		public CompareBoolean getCompareBoolean() {
			return compareBoolean;
		}

		public QueryCondition<C> getCondition() {
			return condition;
		}

	}

	public static class QueryNullBoolean<C extends QueryParam> implements QueryCondition<C> {
		private CompareNullBoolean compareNullBoolean;
		private C queryParam;

		public QueryNullBoolean(CompareNullBoolean compareNullBoolean, C queryParam) {
			super();
			this.compareNullBoolean = compareNullBoolean;
			this.queryParam = queryParam;
		}

		public CompareNullBoolean getCompareNullBoolean() {
			return compareNullBoolean;
		}

		public C getQueryParam() {
			return queryParam;
		}
	}

	
	
	// -------------------------------------------------------------------------------
	// ------------------------------------combine------------------------------------
	public String combineSorts(Function<C, String> _sortParser) {
		List<SortTuple<C>> sorts = getSorts();
		String sort = "";
		if (sorts != null && !sorts.isEmpty()) {
			for (SortTuple<C> p : sorts) {
				String alias = _sortParser.apply(p.getSort());
				if (!DataFO.isEmptyString(alias)) {
					if (DataFO.isEmptyString(sort))
						sort += alias;
					else
						sort += "," + alias;

					sort += (p.getDir().equals(SortDir.undefined) ? "" : " " + p.getDir());
				}
			}
		}
		return sort;
	}
	
	/** 遞迴組裝條件到wstr */
	public String combineConditions(List<Object> datas, String wstr, Function<QueryValue<C, ?>, String> _sqlParser,
			Function<C, String> _queryParamMappingParser) {
		List<QueryCondition<C>> ops = getConditions();
		if (ops != null && !ops.isEmpty())
			for (QueryCondition<C> op : ops)
				wstr = combineCondition(datas, wstr, op, _sqlParser, _queryParamMappingParser);
		return wstr;
	}

	private static <C extends QueryParam> String combineCondition(List<Object> datas, String wstr, QueryCondition<C> op,
			Function<QueryValue<C, ?>, String> _sqlParser, Function<C, String> _queryParamMappingParser) {
		if(op instanceof QueryValue)
			return combineValueCondition(datas, wstr,(QueryValue<C,?>) op, _sqlParser);
		else if(op instanceof QueryGroup)
			return combineGroupCondition(datas, wstr,(QueryGroup<C>) op, _sqlParser, _queryParamMappingParser);
		else if(op instanceof QueryBoolean)
			return combineBooleanCondition(datas, wstr,(QueryBoolean<C>) op, _sqlParser, _queryParamMappingParser);
		else if(op instanceof QueryNullBoolean)
			return combineNullBooleanCondition(datas, wstr,(QueryNullBoolean<C>) op, _queryParamMappingParser);
		return wstr;
	}

	// -------------------------------------------------------------------------------
	// ---------------------------------combine_value---------------------------------
	private static <C extends QueryParam> String combineValueCondition(List<Object> datas, String wstr,
			QueryValue<C, ?> qv, Function<QueryValue<C, ?>, String> _sqlParser) {
		if (qv == null || !qv.getValue().isPresent()) {
			log.debug("qv == null || !qv.getValue().isPresent()");
			return wstr;
		}
		String sqlParam = _sqlParser.apply(qv);
		if (!DataFO.isEmptyString(sqlParam)) {
			wstr += " AND " + sqlParam;
			Object vals = qv.getValue().get();
			if (vals instanceof Object[])
				for (Object val : (Object[]) vals)
					datas.add(val);
			else
				datas.add(vals);
		}
		return wstr;
	}
	
	private static <C extends QueryParam> ValueCombineSupplier<Object> getValueCombineSupplier(QueryValue<C, ?> qv,
			Function<QueryValue<C, ?>, String> _sqlParser) {
		String sqlParam = _sqlParser.apply(qv);
		if (!DataFO.isEmptyString(sqlParam))
			return new ValueCombineSupplier<Object>(qv.getValue().get(), sqlParam);
		else {
			log.error("not suport {} ...", qv.getCondition());
			return null;
		}
	}
	
	// -------------------------------------------------------------------------------
	// ---------------------------------combine_group---------------------------------
	private static <C extends QueryParam> String combineGroupCondition(List<Object> datas, String wstr,
			QueryGroup<C> qg, Function<QueryValue<C, ?>, String> _sqlParser,
			Function<C, String> _queryParamMappingParser) {
		ValueCombineSupplier<List<?>> result = getValueCombineSupplier(qg, _sqlParser, _queryParamMappingParser);
		if (result == null) {
			log.debug("combineGroupCondition return null.");
			return null;
		}
			
		wstr += " AND " + result.getSegment();
		datas.addAll(result.getData());
		return wstr;
	}

	private static <C extends QueryParam> ValueCombineSupplier<List<?>> getValueCombineSupplier(QueryGroup<C> qg,
			Function<QueryValue<C, ?>, String> _sqlParser, Function<C, String> _queryParamMappingParser) {
		QueryCondition<C>[] qos = qg.getValues();
		String combineStr = "(";
		List<Object> datas = new ArrayList<>();
		for (int i = 0; i < qos.length; i++) {
			QueryCondition<C> qo = qos[i];
			if (qo instanceof QueryValue) {
				QueryValue<C, ?> qv = (QueryValue<C, ?>) qo;
				ValueCombineSupplier<Object> result = getValueCombineSupplier(qv, _sqlParser);
				if (result == null) {
					log.error("not support {} ...", qv.getCondition());
					return null;
				}
				combineStr += result.getSegment();
				Object vals = result.getData();
				if (vals instanceof Object[])
					for (Object val : (Object[]) vals)
						datas.add(val);
				else
					datas.add(vals);
			} else if (qo instanceof QueryGroup<C>) {
				ValueCombineSupplier<List<?>> result = getValueCombineSupplier((QueryGroup<C>) qo, _sqlParser,
						_queryParamMappingParser);
				if (result == null) {
					log.error("not support...");
					return null;
				}
				combineStr += result.getSegment();
				datas.addAll(result.getData());
			} else if (qo instanceof QueryBoolean<C>) {
				ValueCombineSupplier<List<?>> result = getValueCombineSupplier((QueryBoolean<C>) qo, _sqlParser,
						_queryParamMappingParser);
				if (result == null) {
					log.error("not support...");
					return null;
				}
				combineStr += result.getSegment();
				datas.addAll(result.getData());
			} else if (qo instanceof QueryNullBoolean<C>) {
				ValueCombineSupplier<List<?>> result = getValueCombineSupplier((QueryNullBoolean<C>) qo,
						_queryParamMappingParser);
				if (result == null) {
					log.error("not support...");
					return null;
				}
				combineStr += result.getSegment();
			}
			if (i < qos.length - 1)
				combineStr += " " + qg.getConjunctiveOp() + " ";
		}
		combineStr += ")";
		return new ValueCombineSupplier<>(datas, combineStr);
	}

	// -------------------------------------------------------------------------------
	// --------------------------------combine_boolean--------------------------------
	private static <C extends QueryParam> String combineBooleanCondition(List<Object> datas, String wstr,
			QueryBoolean<C> qb, Function<QueryValue<C, ?>, String> _sqlParser,
			Function<C, String> _queryParamMappingParser) {
		if (qb == null || qb.getCondition() == null)
			return wstr;

		ValueCombineSupplier<List<?>> result = getValueCombineSupplier(qb, _sqlParser, _queryParamMappingParser);
		if (result == null) {
			log.debug("combineBooleanCondition return null.");
			return null;
		}
		wstr += " AND " + result.getSegment();
		datas.addAll(result.getData());
		return wstr;
	}
	
	private static <C extends QueryParam> ValueCombineSupplier<List<?>> getValueCombineSupplier(QueryBoolean<C> qb,
			Function<QueryValue<C, ?>, String> _sqlParser, Function<C, String> _queryParamMappingParser) {
		QueryCondition<C> qo = qb.getCondition();
		if (qo instanceof QueryValue) {
			ValueCombineSupplier<Object> result = getValueCombineSupplier((QueryValue<C, ?>) qo, _sqlParser);
			if (result == null) {
				log.error("not support {} ...", (QueryValue<C, ?>) qo);
				return null;
			}
			List<Object> datas = new ArrayList<>();
			Object vals = result.getData();
			if (vals instanceof Object[]) {
				for (Object val : (Object[]) vals)
					datas.add(val);
			} else
				datas.add(vals);
			return new ValueCombineSupplier<List<?>>(datas,
					qb.getCompareBoolean() + " ( " + result.getSegment() + " ) ");
		} else if (qo instanceof QueryGroup) {
			ValueCombineSupplier<List<?>> result = getValueCombineSupplier((QueryGroup<C>) qo, _sqlParser,
					_queryParamMappingParser);
			return new ValueCombineSupplier<List<?>>(result.getData(),
					qb.getCompareBoolean() + " ( " + result.getSegment() + " ) ");
		} else if (qo instanceof QueryBoolean) {
			ValueCombineSupplier<List<?>> result = getValueCombineSupplier((QueryBoolean<C>) qo, _sqlParser,
					_queryParamMappingParser);
			return new ValueCombineSupplier<List<?>>(result.getData(),
					qb.getCompareBoolean() + " ( " + result.getSegment() + " ) ");
		} else if (qo instanceof QueryNullBoolean) {
			ValueCombineSupplier<List<?>> result = getValueCombineSupplier((QueryNullBoolean<C>) qo,
					_queryParamMappingParser);
			return new ValueCombineSupplier<List<?>>(new ArrayList<>(),
					qb.getCompareBoolean() + " ( " + result.getSegment() + " ) ");
		} else {
			log.error("not support...");
			return null;
		}
	}
	
	// -------------------------------------------------------------------------------
	// ------------------------------combine_nullBoolean------------------------------
	private static <C extends QueryParam> String combineNullBooleanCondition(List<Object> datas, String wstr, QueryNullBoolean<C> qnb, 
			Function<C, String> _queryParamMappingParser) {
		if (qnb == null)
			return wstr;
		
		ValueCombineSupplier<List<?>> result = getValueCombineSupplier(qnb, _queryParamMappingParser);
		if (result == null) {
			log.debug("combineNullBooleanCondition return null.");
			return null;
		}
		wstr += " AND " + result.getSegment();
		datas.addAll(result.getData());
		return wstr;
	}
	
	private static <C extends QueryParam> ValueCombineSupplier<List<?>> getValueCombineSupplier(QueryNullBoolean<C> qnb,Function<C, String> _queryParamMappingParser ) {
		if (_queryParamMappingParser == null) {
			log.error("_queryParamMappingParser null...");
			return null;
		}
		
		String paramName = _queryParamMappingParser.apply(qnb.getQueryParam());
		if(!DataFO.isEmptyString(paramName))
			return new ValueCombineSupplier<>(null, paramName+" "+qnb.getCompareNullBoolean());
		else {
			log.error("not support or set QueryNullBoolean error...");
			return null;	
		}
	}

	// -------------------------------------------------------------------------------
	// -----------------------------ValueCombineSupplier------------------------------
	static class ValueCombineSupplier<T> implements Serializable {
		private T data;
		private String segment;

		ValueCombineSupplier(T data, String segment) {
			super();
			this.data = data;
			this.segment = segment;
		}

		public T getData() {
			return data;
		}

		public String getSegment() {
			return segment;
		}
	}
	
	// -------------------------------------------------------------------------------
	// --------------------------------------op---------------------------------------
	public enum ConjunctiveOp {
		and("and", "AND"), or("or", "OR");

		private String id;
		private String alias;

		private ConjunctiveOp(String id, String alias) {
			this.id = id;
			this.alias = alias;
		}

		public String getId() {
			return id;
		}

		@Override
		public String toString() {
			return alias;
		}

		public static ConjunctiveOp getConjunctiveOp(String _id) {
			for (ConjunctiveOp o : values())
				if (o.getId().equals(_id))
					return o;
			return null;
		}
	}
	
	public enum CompareOp {
		equal("equal", "="), notEqual("notEqual", "!="), like("like", "LIKE"), //
		greater("greater", ">"), greaterOrequal("greaterOrequal", ">="), //
		less("less", "<"), lessOrequal("lessOrequal", "<="), //
		;

		private String id;
		private String alias;

		private CompareOp(String id, String alias) {
			this.id = id;
			this.alias = alias;
		}

		public String getId() {
			return id;
		}

		@Override
		public String toString() {
			return alias;
		}

		public static CompareOp getCompareOp(String _id) {
			for (CompareOp o : values())
				if (o.getId().equals(_id))
					return o;
			return null;
		}

	}
	
	public enum CompareBoolean{
		not("not","NOT");

		private String id;
		private String alias;

		private CompareBoolean(String id, String alias) {
			this.id = id;
			this.alias = alias;
		}

		public String getId() {
			return id;
		}

		@Override
		public String toString() {
			return alias;
		}

		public static CompareBoolean getCompareBoolean(String _id) {
			for (CompareBoolean o : values())
				if (o.getId().equals(_id))
					return o;
			return null;
		}
	}

	public enum CompareNullBoolean {
		isNull("isNull", "is NULL"), isNotNull("isNotNull", "is Not NULL");

		private String id;
		private String alias;

		private CompareNullBoolean(String id, String alias) {
			this.id = id;
			this.alias = alias;
		}

		public String getId() {
			return id;
		}

		@Override
		public String toString() {
			return alias;
		}

		public static CompareNullBoolean getCompareNullBoolean(String _id) {
			for (CompareNullBoolean o : values())
				if (o.getId().equals(_id))
					return o;
			return null;
		}
	}
	
	public enum SortDir{
		asc("asc", "ASC"), desc("desc", "DESC"), undefined("undefined", "undefined");
		;

		private String id;
		private String alias;

		private SortDir(String id, String alias) {
			this.id = id;
			this.alias = alias;
		}

		public String getId() {
			return id;
		}

		@Override
		public String toString() {
			return alias;
		}

		public static SortDir getSortDir(String _id) {
			for (SortDir o : values())
				if (o.getId().equals(_id))
					return o;
			return null;
		}
	}

}

