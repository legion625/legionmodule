package legion.util;

import java.util.function.Predicate;
import java.util.function.Supplier;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRetryUtil {
	@Test
	public void testworkRetryWithException() {
		int times = 3;
		int interval = 1000;
		Supplier<String> work = () -> {
			String str = 	"test" + System.currentTimeMillis();
			return str;
		};
		Predicate<String> checkSuccess = str -> false;
		
		RetryUtil.workRetryWithException(times, interval, work, checkSuccess);
	}
}
