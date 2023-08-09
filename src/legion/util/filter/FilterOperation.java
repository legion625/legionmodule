package legion.util.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import org.slf4j.event.Level;

import legion.util.LogUtil;

@SuppressWarnings("serial")
public class FilterOperation<C extends FilterParam<?>, R> implements Serializable {
	private List<FilterCondition<C>> operations = new ArrayList<>();

	// -------------------------------------------------------------------------------
	public FilterOperation<C, R> addCondition(FilterValue<C, ?> _query) {
		operations.add(_query);
		return this;
	}

	public FilterOperation<C, R> addCondition(FilterGroup<C, ?> _query) {
		operations.add(_query);
		return this;
	}

	public FilterOperation<C, R> addCondition(FilterCondition<C, ?> _query) {
		operations.add(_query);
		return this;
	}

	// -------------------------------------------------------------------------------
	public List<FilterCondition<C>> getOperations() {
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
		private FilterCompareOp compareOp;
		private C condition;
		private T value;

		public FilterValue(FilterCompareOp compareOp, C condition, T value) {
			super();
			this.compareOp = compareOp;
			this.condition = condition;
			this.value = value;
		}

		public FilterCompareOp getCompareOp() {
			return compareOp;
		}

		public C getCondition() {
			return condition;
		}

		public T getValue() {
			return value;
		}

	}

	// -------------------------------------------------------------------------------
	public static class FilterGroup<C extends FilterParam<?>> implements FilterCondition<C> {
		private FilterConjunctiveOp conjunctiveOp;
		private FilterCondition<C>[] values;

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
		List<FilterCondition<C>> ops = getCondition();
		if (ops != null && !ops.isEmpty()) {
			for (FilterCondition<C> op : ops) {
				result = combineCondition(result, op);
			}
		}
	}

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

	// TODO

	public interface FilterCompareOp {
	}
}
