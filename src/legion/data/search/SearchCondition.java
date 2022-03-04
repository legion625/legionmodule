package legion.data.search;

import legion.data.CompareOp;

public class SearchCondition<T extends SearchParam> {
	private T param;
	private CompareOp compareOp;
	private Object value;

	SearchCondition(T param, CompareOp compareOp, Object value) {
		this.param = param;
		this.compareOp = compareOp;
		this.value = value;
	}

	public T getParam() {
		return param;
	}

	public CompareOp getCompareOp() {
		return compareOp;
	}

	public Object getValue() {
		return value;
	}

}
