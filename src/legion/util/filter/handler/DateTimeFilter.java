package legion.util.filter.handler;

import java.util.Date;

import legion.util.filter.FilterFunction;
import legion.util.filter.FilterOperation.FilterCompareOp;

public class DateTimeFilter implements FilterFunction<Object, Boolean> {
	private static final DateTimeFilter INSTANCE = new DateTimeFilter();

	private DateTimeFilter() {
	}

	public final static DateTimeFilter getInstance() {
		return INSTANCE;
	}

	// -------------------------------------------------------------------------------
	@Override
	public Boolean apply(Object _t1, FilterCompareOp _op, Object _t2) throws Exception {
		Date t1 = (Date) _t1;
		Date t2 = (Date) _t2;
		switch (_op) {
		case equal:
			return t1.getTime() == t2.getTime();
		case greater:
			return t1.getTime() > t2.getTime();
		case greaterOrequal:
			return t1.getTime() >= t2.getTime();
		case less:
			return t1.getTime() < t2.getTime();
		case lessOrequal:
			return t1.getTime() <= t2.getTime();
		case notEqual:
			return t1.getTime() != t2.getTime();
		default:
			throw new IllegalArgumentException("Unexpected value: " + _op);
		}
	}
}
