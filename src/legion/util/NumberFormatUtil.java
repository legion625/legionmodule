package legion.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Comparator;

public class NumberFormatUtil {

//	private final static Comparator<String> COMPARATOR_INT_STRING = new Comparator<String>() {
//		@Override
//		public int compare(String o1, String o2) {
//			return parse(o1).compareTo(parse(o2));
//		}
//	};
	private final static Comparator<String> COMPARATOR_INT_STRING = (o1, o2) -> parse(o1).compareTo(parse(o2));

	public static NumberFormat getIntegerFormat(boolean _groupingUsed) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setGroupingUsed(_groupingUsed);
		nf.setMaximumFractionDigits(0);
		nf.setMinimumFractionDigits(0);
		return nf;
	}

	public static String getIntegerString(Number _n) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(0);
		nf.setMinimumFractionDigits(0);
		return nf.format(_n);
	}
	
	public static String getDecimalString(Number _n, int _fractionDigits) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(_fractionDigits);
		nf.setMinimumFractionDigits(_fractionDigits);
		return nf.format(_n);
	}

	public static Integer parse(String _integerString) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		try {
			return nf.parse(_integerString).intValue();
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Comparator<String> getIntegerStringComparator() {
		return COMPARATOR_INT_STRING;
	}

}
