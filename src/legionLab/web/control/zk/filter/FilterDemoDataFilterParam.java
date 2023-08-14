package legionLab.web.control.zk.filter;

import legion.util.filter.FilterFunction;
import legion.util.filter.FilterOperation.FilterCompareOp;
import legion.util.filter.FilterParam;
import legion.util.filter.handler.BooleanFilter;
import legion.util.filter.handler.DoubleFilter;
import legion.util.filter.handler.EnumEqualFilter;
import legion.util.filter.handler.StringFilter;

public enum FilterDemoDataFilterParam implements FilterParam<Object> {
	AAA("aaa", "AAA", StringFilter.getInstance()), //
	BBB("bbb", "BBB", BooleanFilter.getInstance()), //
	DDD("ddd", "DDD", DoubleFilter.getInstance()), //
	TYPE("type", "TYPE", EnumEqualFilter.getInstance()), //
	;

	private String id;
	private String desp;
	private FilterFunction ff;

	// -------------------------------------------------------------------------------

	private FilterDemoDataFilterParam(String id, String desp, FilterFunction ff) {
		this.id = id;
		this.desp = desp;
		this.ff = ff;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getDescription() {
		return desp;
	}

	@Override
	public boolean filter(Object _targetVal, FilterCompareOp _filterCompareOp, Object _filterVal)
			throws Exception {
		return (boolean) ff.apply(_targetVal, _filterCompareOp, _filterVal);
	}

}
