package legion.util.filter.handler;

import java.util.Date;

import legion.util.DateFormatUtil;
import legion.util.filter.FilterFunction;
import legion.util.filter.FilterOperation.FilterCompareOp;

public class DateFilter implements FilterFunction<Object, Boolean> {
	private static final DateFilter INSTANCE = new DateFilter();

	private DateFilter() {
	}

	public final static DateFilter getInstance() {
		return INSTANCE;
	}

	// -------------------------------------------------------------------------------
	@Override
	public Boolean apply(Object _t1, FilterCompareOp _op, Object _t2) throws Exception {
		Date t1 = (Date) _t1;
		Date t2 = (Date) _t2;
		String s1 = DateFormatUtil.transToDate(t1);
		String s2 = DateFormatUtil.transToDate(t2);

		if (_op.equals(FilterCompareOp.like))
			throw new IllegalArgumentException("Unexpected value: " + _op);
		return StringFilter.getInstance().apply(s1, _op, s2);
	}

}
