package legion.util.filter.handler;

import legion.util.filter.FilterFunction;
import legion.util.filter.FilterOperation.FilterCompareOp;

public class DoubleFilter implements FilterFunction<Object, Boolean>{
	private static final DoubleFilter INSTANCE = new DoubleFilter();

	private DoubleFilter() {
	}

	public final static DoubleFilter getInstance() {
		return INSTANCE;
	}

	// -------------------------------------------------------------------------------
	@Override
	public Boolean apply(Object _t1, FilterCompareOp _op, Object _t2) throws Exception {
		double t1 =(double)_t1;
		double t2 =(double)_t2;
		switch (_op) {
		case equal:
			return t1 == t2;
		case greater:
			return t1 > t2;
		case greaterOrequal:
			return t1 >= t2;
		case less:
			return t1 < t2;
		case lessOrequal:
			return t1 <= t2;
		case notEqual:
			return t1 != t2;
		default:
			throw new IllegalArgumentException("Unexpected value: " + _op);
		}
	}

}
