package legion.util.filter;

import java.io.Serializable;

import legion.util.filter.FilterOperation.FilterCompareOp;

public interface FilterParam<T extends Object> extends Serializable {
	public String getId();

	public String getDescription();

	public boolean filter(T _targetVal, FilterCompareOp _filterCompareOp, T _filterVal) throws Exception;
}
