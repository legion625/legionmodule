package legion.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
/**
 * Some useful additions to the built-in functions in ({@link Math}.
 * 
 * @version $Revision: 321510 $ $Date: 2005-10-15 15:33:14 -0700 (Sat, 15 Oct
 *          2005) $
 *
 */
public final class MathUtils {
	/** -1.0 cast as a byte. */
	private static final byte NB = (byte) -1;

	/** -1.0 cast as a short */
	private static final short NS = (short) -1;

	/** 1.0 cast as a byte. */
	private static final byte PB = (byte) 1;

	/** 1.0 cast as a short */
	private static final short PS = (short) 1;

	/** 0.0 cast as a byte */
	private static final byte ZB = (byte) 0;

	/** 0.0 cast as a short */
	private static final short ZS = (short) 0;

	private MathUtils() {
		super();
	}

	// -------------------------------------------------------------------------------
	/**
	 * @deprecated This can be replaced with Math.addExact
	 */
	@Deprecated
	public static int addAndCheck(int x, int y) {
		long s = (long) x + (long) y;
		if (s < Integer.MIN_VALUE || s > Integer.MAX_VALUE)
			throw new ArithmeticException("overflow: add");
		return (int) s;
	}
	
	/**
	 * For a double precision value x, this method returns +1.0 if x>=0 and -1.0 if
	 * x<0. Returns <code>NaN</code> if <code>x</code> is <code>NaN</code>
	 * 
	 * @param x the value, a double
	 * @return +1.0 or -1.0, depending on the sign of x
	 */
	public static double indicator(final double x) {
		if (Double.isNaN(x))
			return Double.NaN;
		return x >= 0 ? 1.0 : -1.0;
	}

	/**
	 * Round the given value to the specified number of decimal places. The value is
	 * rounded using the {@link RoundingMode#HALF_UP} method.
	 * 
	 * @param x     the value to round
	 * @param scale the number of digits to the right of the decimal point
	 * @return the rounded value
	 */
	public static double round(double x, int scale) {
		return round(x, scale, RoundingMode.HALF_UP);
	}
	
	/**
	 * Round the given value to the specified number of decimal places. The value is
	 * rounded using the given method which is any method defined in {@link BigDecimal}.
	 * @param x	the value to round
	 * @param scale	the number of digits to the right of the decimal point
	 * @param roundingMethod	the rounding method as defined in {@link BigDecimal}
	 * @return	the rounded value
	 */
	public static double round(double x, int scale, RoundingMode roundingMode) {
		double sign = indicator(x);
		double factor = Math.pow(10.0, scale) * sign;
		return roundUnscaled(x * factor, sign, roundingMode) / factor;
	}

//	private static double roundUnscaled(double unscaled, double sign, int roundingMethod) {
	private static double roundUnscaled(double unscaled, double sign, RoundingMode roundingMode) {
		switch (roundingMode) {
		case CEILING:
			unscaled = sign == -1 ? Math.floor(unscaled) : Math.ceil(unscaled);
			break;
		case DOWN:
			unscaled = Math.floor(unscaled);
			break;
		case FLOOR:
			unscaled = sign == -1 ? Math.ceil(unscaled) : Math.floor(unscaled);
			break;
		case HALF_DOWN: { // XXX not verified yet
			double fraction = Math.abs(unscaled - Math.floor(unscaled));
			unscaled = fraction > 0.5 ? Math.ceil(unscaled) : Math.floor(unscaled);
			break;
		}
		case HALF_EVEN: { // XXX not verified yet
			double fraction = Math.abs(unscaled - Math.floor(unscaled));
			if (fraction > 0.5)
				unscaled = Math.ceil(unscaled);
			else if (fraction < 0.5)
				Math.floor(unscaled);
			else {
				// even
				if (Math.floor(unscaled) / 2.0 == Math.floor(Math.floor(unscaled) / 2.0))
					unscaled = Math.floor(unscaled);
				// odd
				else
					unscaled = Math.ceil(unscaled);
			}
			break;
		}
		case HALF_UP: {
			double fraction = Math.abs(unscaled - Math.floor(unscaled));
			unscaled = fraction >= 0.5 ? Math.ceil(unscaled) : Math.floor(unscaled);
			break;
		}
		case UNNECESSARY: // XXX not verified yet
			if (unscaled != Math.floor(unscaled))
				throw new ArithmeticException("Inexact result from rounding");
			break;
		case UP:
			unscaled = Math.ceil(unscaled);
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + roundingMode);
		}
		return unscaled;
	}
}
