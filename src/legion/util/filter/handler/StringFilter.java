package legion.util.filter.handler;

import legion.util.DataFO;
import legion.util.filter.FilterFunction;
import legion.util.filter.FilterOperation.FilterCompareOp;

public class StringFilter implements FilterFunction<Object, Boolean> {
	private static final StringFilter INSTANCE = new StringFilter();

	private StringFilter() {
	}

	public final static StringFilter getInstance() {
		return INSTANCE;
	}

	// -------------------------------------------------------------------------------
	@Override
	public Boolean apply(Object _t1, FilterCompareOp _op, Object _t2) throws Exception {
		String t1 = (String) _t1;
		String t2 = (String) _t2;
		switch (_op) {
		case equal:
			return t1.equals(t2);
		case like:
			return DataFO.wildcardStringEqual(t1, t2);
		case greater:
			return t1.compareTo(t2) > 0;
		case greaterOrequal:
			return t1.compareTo(t2) >= 0;
		case less:
			return t1.compareTo(t2) < 0;
		case lessOrequal:
			return t1.compareTo(t2) <= 0;
		case notEqual:
			return t1.compareTo(t2) != 0;
		default:
			throw new IllegalArgumentException("Unexpected value: " + _op);
		}
	}
}
