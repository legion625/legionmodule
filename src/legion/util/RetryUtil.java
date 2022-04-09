package legion.util;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.slf4j.event.Level;

public class RetryUtil {
	// FIXME not tested yet...
	public static <T> T workRetryWithException(int times, int interval, Supplier<T> _do, Predicate<T> _checkSuccess) {
		if (times <= 0)
			return null;

		do {
			T t = _do.get();
			if (_checkSuccess.test(t)) {
				return t;
			} else {
				times--;
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					LogUtil.log(e, Level.ERROR);
					e.printStackTrace();
				}
			}
		} while (times > 0);

		return null;
	}
}
