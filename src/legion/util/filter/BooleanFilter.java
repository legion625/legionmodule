package legion.util.filter;

import legion.util.filter.FilterOperation.FilterCompareOp;

public class BooleanFilter implements FilterFunction<Object, Boolean> {
	private static final BooleanFilter INSTANCE = new BooleanFilter();

	private BooleanFilter() {
	}

	public final static BooleanFilter getInstance() {
		return INSTANCE;
	}

	@Override
	public Boolean apply(Object _t1, FilterCompareOp _op, Object _t2) throws Exception {
		boolean t1 =(boolean) _t1;
		boolean t2 = (boolean)_t2;
		switch (_op) {
		case equal:
			return t1==t2;

		default:
			break;
		}
		// TODO Auto-generated method stub
		return null;
	}

}
