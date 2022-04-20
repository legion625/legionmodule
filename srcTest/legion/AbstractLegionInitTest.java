package legion;

import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import legion.datasource.manager.DSManager;
import legion.util.DataFO;
import legion.util.LogUtil;

public class AbstractLegionInitTest extends LegionInitLogTest {
	private static volatile boolean initFlag = false;

	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		LegionInitLogTest.setupBeforeClass();
		assertNotNull("log null", log);
		log.debug("AbstractLegionInitTest setupBeforeClass...");
		// 啟始系統設定
		if (!initFlag) {
			synchronized (AbstractLegionInitTest.class) {
				if (!initFlag) {
					init();
					initFlag = true;
				}
			}
		}
	}

	@AfterClass
	public static void tearDownAfterClass() {
		log.debug("tearDownAfterClass...");
	}

	@Before
	public void setup() {
		log.debug("setup...");
	}

	@After
	public void teardown() {
		log.debug("teardown...");
	}

	// -------------------------------------------------------------------------------
	public static void init() {
		log.debug("Application init...");
		// String sslfilepath = "WebContent\\WEB-INF\\ippm-client.keystore";
		// String clientKeyStorePass = "rms!1030";
		// String clientKeyStoreType = "jks";
		/* system-conf */
		String systemInfoFile = "WebContent\\WEB-INF\\system-conf.properties";

		/* datasource */
		String datasourceFile = "srcTest\\conf\\datasource.xml";
		/* service module */
		String serviceModuleFile = "srcTest\\conf\\ServiceModule.xml";

		//
		initSystemInfo(systemInfoFile);
		// registerServerRmiSSL(_sysInfo)
		// registerClientRmiSSL(sslfilePath, clientKeyStorePass, clientKeyStoreType);
		// registerClientWsSSL(sslfilePath, clientKeyStorePass, clientKeyStoreType);
		initDatasource(datasourceFile);
		initIntegrationServiceModule(serviceModuleFile);
		initBusinessServiceModule(serviceModuleFile);
		// initFw
		// initAspectManager(_sysInfo);
//		verifyLegionConn(datasourceFile);
	}

	private static void initSystemInfo(String _systemInfoFile) {
		log.debug("init SystemInfo...");
		try {
			SystemInfoDefault systemInfo = SystemInfoDefault.getInstance();
			if (!DataFO.isEmptyString(_systemInfoFile)) {
				PropertiesConfiguration cfg = new PropertiesConfiguration();
				cfg.setEncoding("UTF-8");
				cfg.load(_systemInfoFile);
				for (Iterator<String> it = cfg.getKeys(); it.hasNext();) {
					String key = it.next();
					systemInfo.putAttribute(key, cfg.getString(key));
					log.debug("{}\t{}", key, cfg.getString(key));
				}
				String[] paths = cfg.getStringArray("CLASS_ANALYSE_CLASSPATH");
				if (paths != null && paths.length > 0) {
					for (int i = 0; i < paths.length; i++) {
						paths[i] = "file:/" + paths[i];
						log.debug("Class Analyse Classpath Urls [{}]:", paths[i]);
					}
					systemInfo.setClassAnalyseClasspath(paths);
				}
			}
		} catch (Exception e) {
			LogUtil.log(e);
			log.warn("init SystemInfo Fail ...... {} ", e.getMessage());
		}
	}
	
	private static void initDatasource(String _datasourceFile) {
		log.debug("init datasource...");
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(_datasourceFile);
			DSManager.getInstance().registerDatasourceXml(inStream, false);
		} catch (Exception e) {
			LogUtil.log(e);
			log.error("init Datasource Fail ...... {} ", e.getMessage());
		} finally {
			try {
				if (inStream != null)
					inStream.close();
			} catch (IOException e) {
				LogUtil.log(e);
			}
		}
	}
	
	private static void initIntegrationServiceModule(String _serviceModuleFile) {
		log.debug("init IntegrationService...");
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(_serviceModuleFile);
			// data service
			DataServiceFactory.getInstance().registerService(inStream);
		} catch (Exception e) {
			LogUtil.log(e);
			log.error("init IntegrationService Fail ...... {} ", e.getMessage());
		} finally {
			try {
				if (inStream != null)
					inStream.close();
			} catch (IOException e) {
				LogUtil.log(e);
			}
		}
	}
	
	private static void initBusinessServiceModule(String _serviceModuleFile) {
		log.debug("init BusinessService...");
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(_serviceModuleFile);
			// business service
			BusinessServiceFactory.getInstance().registerService(inStream);

			log.info("*.Legion Module Version [{}]", LegionContext.getInstance().getVersion()); // XXX
			// 這裡在讀BusinessServiceModule，有需要SHOW版本嗎?
			LegionContext.getInstance().getSystemInfo().putAttribute("legionmodule.version",
					LegionContext.getInstance().getVersion());
		} catch (Exception e) {
			LogUtil.log(e);
			log.error("init BusinessService Fail ...... {} ", e.getMessage());
		} finally {
			try {
				if (inStream != null)
					inStream.close();
			} catch (IOException e) {
				LogUtil.log(e);
			}
		}
	}
}
