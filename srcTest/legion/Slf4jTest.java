package legion;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.*;

class Slf4jTest {
	
	@BeforeClass
	public static void beforeClass() {
//		System.setProperty("java.util.logging.config.file", ClassLoader.getSystemResource("logback-conf.properties").getPath());
	}

	@Test
	void test() {
//		fail("Not yet implemented");
		Logger log = LoggerFactory.getLogger(Slf4jTest.class);
//		System.out.println("a");
		System.out.println(log.getName());

		log.debug("test debug: {}", "debug");
		log.warn("test warn: {}", "warn");
		log.info("test info: {}", "info");
		log.trace("test trace: {}", "trace");
		log.error("test error: {}", "error");
		
//		System.out.println("b");
	}

}
