package legion.util.filter.handler;

import legion.util.filter.FilterFunction;
import legion.util.filter.FilterOperation.FilterCompareOp;

public class EnumEqualFilter implements FilterFunction<Object, Boolean>{
	private static final EnumEqualFilter INSTANCE = new EnumEqualFilter();

	private EnumEqualFilter() {
	}

	public final static EnumEqualFilter getInstance() {
		return INSTANCE;
	}
	
	// -------------------------------------------------------------------------------
	@Override
	public Boolean apply(Object _t1, FilterCompareOp _op, Object _t2) throws Exception {
		switch (_op) {
		case equal:
			return _t1 == _t2;
		case notEqual:
			return _t1 != _t2;
		default:
			throw new IllegalArgumentException("Unexpected value: " + _op);
		}
	}
}
