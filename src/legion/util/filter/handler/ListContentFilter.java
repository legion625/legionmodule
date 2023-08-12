package legion.util.filter.handler;

import java.util.List;

import legion.util.filter.FilterFunction;
import legion.util.filter.FilterOperation.FilterCompareOp;

public class ListContentFilter<T, C extends FilterFunction> implements FilterFunction<Object, Boolean> {
	private C c;

	private ListContentFilter(C c) {
		this.c = c;
	}

	public static <C extends FilterFunction> ListContentFilter of(C _c) {
		return new ListContentFilter<>(_c);
	}

	// -------------------------------------------------------------------------------
	@Override
	public Boolean apply(Object _t1, FilterCompareOp _op, Object _t2) throws Exception {
		List<T> t1 = (List<T>) _t1;
		Object t2 = _t2;

		switch (_op) {
		case equal:
		case like:
			for (T t : t1)
				if ((boolean) c.apply(t, _op, _t2))
					return true;
			return false;
		case notEqual: // 必須要所有list的內容都符合notEqual，才能回傳true。
			for (T t : t1)
				if ((boolean) c.apply(t, _op, _t2))
					return false;
			return true;
		default:
			throw new IllegalArgumentException("Unexpected value: " + _op);
		}

//		 TODO Auto-generated method stub
//		return null;
	}
}
