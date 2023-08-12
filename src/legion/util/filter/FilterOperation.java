package legion.util.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import org.slf4j.event.Level;

import legion.util.BeanUtil;
import legion.util.LogUtil;

@SuppressWarnings("serial")
public class FilterOperation<C extends FilterParam<?>, R> implements Serializable {
	private List<FilterCondition<C>> operations = new ArrayList<>();

	// -------------------------------------------------------------------------------
	public FilterOperation<C, R> addCondition(FilterValue<C, ?> _query) {
		operations.add(_query);
		return this;
	}

	public FilterOperation<C, R> addCondition(FilterGroup<C> _query) {
		operations.add(_query);
		return this;
	}

	public FilterOperation<C, R> addCondition(FilterCondition<C> _query) {
		operations.add(_query);
		return this;
	}

	// -------------------------------------------------------------------------------
	public List<FilterCondition<C>> getCondition() {
		return operations;
	}

	// -------------------------------------------------------------------------------
	@SafeVarargs
	public static <E extends FilterParam<?>> FilterGroup<E> group(FilterConjunctiveOp _conjunctiveOp,
			FilterCondition<E>... _values) {
		return new FilterGroup<>(_conjunctiveOp, _values);
	}

	public static <E extends FilterParam<?>, K> FilterValue<E, K> value(E _condition, FilterCompareOp _compareOp,
			K _value) {
		return new FilterValue<>(_condition, _compareOp, _value);
	}

	// -------------------------------------------------------------------------------
	public static class FilterValue<C extends FilterParam<?>, T> implements FilterCondition<C> {
		private C condition;
		private FilterCompareOp compareOp;
		private T value;

		public FilterValue(C condition,FilterCompareOp compareOp,  T value) {
			this.condition = condition;
			this.compareOp = compareOp;
			this.value = value;
		}

		public FilterCompareOp getCompareOp() {
			return compareOp;
		}

		public C getCondition() {
			return condition;
		}

		public Optional<T> getValue() {
			return Optional.ofNullable(value);
		}

	}

	// -------------------------------------------------------------------------------
	public static class FilterGroup<C extends FilterParam<?>> implements FilterCondition<C> {
		private FilterConjunctiveOp conjunctiveOp;
		private FilterCondition<C>[] values;

		@SafeVarargs
		public FilterGroup(FilterConjunctiveOp conjunctiveOp, FilterCondition<C>... values) {
			this.conjunctiveOp = conjunctiveOp;
			this.values = values;
		}

		public FilterConjunctiveOp getConjunctiveOp() {
			return conjunctiveOp;
		}

		public FilterCondition<C>[] getValues() {
			return values;
		}

		public FilterCondition<C> getValue(int _idx) {
			return values[_idx];
		}
	}

	// -------------------------------------------------------------------------------
	public <D> List<D> filterConditions(List<D> _datas) throws Exception {
		return filterConditions(_datas, 0);
	}

	public <D> List<D> filterConditions(List<D> _datas, int _groupCount) throws Exception {
		// 先複製集合
		// 判斷數量分組
		if (_groupCount > 0 && _groupCount < _datas.size()) {
			int groupSize = _datas.size() / _groupCount + (_datas.size() % _groupCount == 0 ? 0 : 1);

			@SuppressWarnings("unchecked")
			List<D>[] groups = new List[groupSize];
			for (int i = 0; i < groupSize; i++) {
				int from = i * _groupCount;
				int to = 1 * _groupCount + _groupCount;
				to = to > _datas.size() ? _datas.size() : to;
				List<D> result = new LinkedList<D>(_datas.subList(from, to));
				groups[i] = result;
			}

			// fork
			ForkJoinPool execPool = new ForkJoinPool(groupSize > 8 ? 8 : groupSize);
			try {
				ForkJoinTask<Void> job = execPool.submit(new RecursiveAction() {
					@Override
					protected void compute() {
						// 進行節點比對
						List<RecursiveAction> tasks = new ArrayList<>();
						for (List<D> group : groups) {
							tasks.add(new RecursiveAction() {
								@Override
								protected void compute() {
									try {
										filterCondition(group);
									} catch (Exception e) {
										LogUtil.log(e, Level.DEBUG);
									}
								}
							});
						}
						invokeAll(tasks);
					}
				});
				job.join();
			} finally {
				execPool.shutdown();
			}
			// join
			List<D> result = new LinkedList<D>(_datas);
			filterConditions(result);
			return result;
		} else {
			List<D> result = new LinkedList<D>(_datas);
			filterCondition(result);
			return result;
		}
	}

	private <D> void filterCondition(List<D> result) throws Exception {
		List<FilterCondition<C>> fcs = getCondition();
		if (fcs != null && !fcs.isEmpty()) {
			for (FilterCondition<C> op : fcs) {
				result = combineCondition(result, op);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static <C extends FilterParam<?>, D> List<D> combineCondition(List<D> _datas, FilterCondition<C> op)
			throws Exception {
		if (op instanceof FilterValue)
			return combineCondition(_datas, (FilterValue<C, ?>) op);
		else if (op instanceof FilterGroup)
			return combineCondition(_datas, (FilterGroup<C>) op);
		return _datas;

	}
	
	private static <C extends FilterParam<?>, D> List<D> combineCondition(List<D> _datas, FilterValue<C, ?> _fv)
			throws Exception {
		if (_fv == null || !_fv.getValue().isPresent())
			return _datas;

		for (int i = _datas.size() - 1; i >= 0; i--) {
			if (!filter(_datas.get(i), _fv))
				_datas.remove(i);
		}
		return _datas;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <C extends FilterParam<?>, D> boolean filter(D _data, FilterValue<C, ?> _fv) throws Exception {
		FilterParam filterparam = _fv.getCondition();
		Object val = BeanUtil.getProperty(_data, filterparam.getId());
		return filterparam.filter(val, _fv.getCompareOp(), _fv.getValue().get());
	}
	
	private static <C extends FilterParam<?>, D> List<D> combineCondition(List<D> _datas, FilterGroup<C> _fg)
			throws Exception {
		FilterCondition<C>[] fos = _fg.getValues();
		switch (_fg.getConjunctiveOp()) {
		case and: 
			for (int j = _datas.size() - 1; j >= 0; j--) {
				if (!filterAnd(_datas.get(j), fos))
					_datas.remove(j);
			}
			break;
		case or: 
			for (int j = _datas.size() - 1; j >= 0; j--) {
				if (!filterOr(_datas.get(j), fos))
					_datas.remove(j);
			}
			break;
		}
		return _datas;
	}
	
	@SuppressWarnings("unchecked")
	private static <C extends FilterParam<?>, D> boolean filterAnd(D _targetData, FilterCondition<C>[] _fcs)
			throws Exception {
		boolean filterFlag = true;
		for (int i = 0; i < _fcs.length; i++) {
			FilterCondition<C> fc = _fcs[i];
			if (fc instanceof FilterValue) {
				if (!filter(_targetData, (FilterValue<C, ?>) fc)) {
					filterFlag = false;
					break;
				}
			} else if (fc instanceof FilterGroup<C>) {
				if (!filter(_targetData, (FilterGroup<C>) fc)) {
					filterFlag = false;
					break;
				}
			}
		}
		return filterFlag;
	}
	
	private static <C extends FilterParam<?>, D> boolean filterOr(D _targetData, FilterCondition<C>[] _fcs)
			throws Exception {
		boolean filterFlag = false;
		for (int i = 0; i < _fcs.length; i++) {
			FilterCondition<C> fc = _fcs[i];
			if (fc instanceof FilterValue) {
				if (!filter(_targetData, (FilterValue<C, ?>) fc)) {
					filterFlag = true;
					break;
				}
			} else if (fc instanceof FilterGroup<C>) {
				if (!filter(_targetData, (FilterGroup<C>) fc)) {
					filterFlag = true;
					break;
				}
			}
		}
		return filterFlag;
	}
	
	private static <C extends FilterParam<?>, D> boolean filter(D _targetData, FilterGroup<C> _fg)throws Exception {
		FilterCondition<C>[] fcs = _fg.getValues();
		switch (_fg.getConjunctiveOp()) {
		case and:
			return filterAnd(_targetData, fcs);
		case or:
			return filterOr(_targetData, fcs);
		}
		return false;
	}
	
	// -------------------------------------------------------------------------------
	static class ValueCombineSupplier<T> implements Serializable {
		private T data;
		private String segment;

		ValueCombineSupplier(T data, String segment) {
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
	public enum FilterConjunctiveOp {
		and("and", "AND"), or("or", "OR");

		private String alias;
		private String id;

		FilterConjunctiveOp(String alias, String id) {
			this.alias = alias;
			this.id = id;
		}

		public String getId() {
			return id;
		}

		@Override
		public String toString() {
			return alias;
		}

		public static FilterConjunctiveOp getConjunctiveOp(String _id) {
			for (FilterConjunctiveOp item : FilterConjunctiveOp.values())
				if (item.getId().equalsIgnoreCase(_id))
					return item;
			return null;
		}

	}
	
	public enum FilterCompareOp {
		equal("equal","="), like("like","like"), greater("greater",">"),
		greaterOrequal("greaterOrequal", ">="), 
		less("less","<"), lessOrequal("lessOrequal", "<="), 
		notEqual("notEqual", "!=");
		private String alias;
		private String id;
		
		FilterCompareOp(String alias, String id) {
			this.alias = alias;
			this.id = id;
		}

		public String getId() {
			return id;
		}
		
		@Override
		public String toString() {
			return alias;
		} 
		
		public static FilterCompareOp getCompareOp(String _id) {
			for (FilterCompareOp item : FilterCompareOp.values())
				if (item.getId().equalsIgnoreCase(_id))
					return item;
			return null;
		}
		
	}
}
