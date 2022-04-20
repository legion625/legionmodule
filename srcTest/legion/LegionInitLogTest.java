package legion;


import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
public abstract class LegionInitLogTest {
	protected static Logger log = LoggerFactory.getLogger(TestLogMark.class);
	private static volatile boolean logFlag = false;
	
	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		System.out.println("LegionModuleTestInit setupBeforeClass...");
		// 啟始系統設定
		if (!logFlag) {
			synchronized (LegionInitLogTest.class) {
				if (!logFlag) {
					initLog();
					logFlag = true;
				}
			}
		}
		assertNotNull("log null", log);
	}
	
	static void initLog() throws Exception {
//		String logfile = "srcTest\\conf\\lo4j-conf.xml";
//		log.debug("log4j-init-file: {}", logfile);
//		// log資訊
//		// if the log4j-init-file is not set, then nopoint in trying.
//		DOMConfigurator config = new DOMConfigurator();
//		DOMConfigurator.configure(logfile);
		
		// 啟始logback
		String logfile = "srcTest\\conf\\logback-conf.xml";
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(lc);
		// to clear any previous configuration
		lc.reset();
		configurator.doConfigure(logfile);
		
		log = LoggerFactory.getLogger(TestLogMark.class);
	}

}
