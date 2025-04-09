package legion.util.filter.handler;

import legion.util.filter.FilterFunction;
import legion.util.filter.FilterOperation.FilterCompareOp;

public class IntFilter implements FilterFunction<Object, Boolean> {

	private static final IntFilter INSTANCE = new IntFilter();

	private IntFilter() {
	}

	public final static IntFilter getInstance() {
		return INSTANCE;
	}

	// -------------------------------------------------------------------------------
	@Override
	public Boolean apply(Object _t1, FilterCompareOp _op, Object _t2) throws Exception {
		int t1 = (int) _t1;
		int t2 = (int) _t2;
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
