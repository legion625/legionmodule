package legion.util.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Min-Hua Chao
 *
 * @param <C>
 * @param <R>
 */
public class QueryOperation<C extends QueryParam, R> implements Serializable {
	private Logger log = LoggerFactory.getLogger(QueryOperation.class);

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
	 * @return
	 */
	private int getDefaultLimitSize() {
		int defaultSize = -1;
		// 
		String defaultSizeStr = LegionContext.getInstance().getSystemInfo()
				.getAttributes("system.queryOperation.limit");
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
	// -----------------------------------Operation-----------------------------------
	// XXX 已經有用QueryCondition<C>作為參數的方法，QueryValue<C,?>又implements QueryCondition<C>，此方法是否有必要?
	public QueryOperation<C, R> appendCondition(QueryValue<C, ?> _query){
		conditions.add(_query);
		return this;
	}
	// XXX 已經有用QueryCondition<C>作為參數的方法，QueryGroup<C>又implements QueryCondition<C>，此方法是否有必要?
	public QueryOperation<C, R> appendCondition(QueryGroup<C> _query){
		conditions.add(_query);
		return this;
	}
	// XXX 已經有用QueryCondition<C>作為參數的方法，QueryBoolean<C>又implements QueryCondition<C>，此方法是否有必要?
	public QueryOperation<C, R> appendCondition(QueryBoolean<C> _query){
		conditions.add(_query);
		return this;
	}
	// XXX 已經有用QueryCondition<C>作為參數的方法，QueryNullBoolean<C>又implements QueryCondition<C>，此方法是否有必要?
	public QueryOperation<C, R> appendCondition(QueryNullBoolean<C> _query){
		conditions.add(_query);
		return this;
	}
	public QueryOperation<C, R> appendCondition(QueryCondition<C> _query){
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
	
	
	

}
