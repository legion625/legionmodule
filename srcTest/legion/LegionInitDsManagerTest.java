package legion;

import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import legion.datasource.manager.DSManager;
import legion.util.LogUtil;

public class LegionInitDsManagerTest extends LegionInitLogTest {
	private static volatile boolean initFlag = false;
	
	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		LegionInitLogTest.setupBeforeClass();
		assertNotNull("log null", log);
		log.debug("LegionInitDsManagerTest setupBeforeClass...");
		if (!initFlag) {
			synchronized (LegionInitLogTest.class) {
				if (!initFlag) {
					init();
					initFlag = true;
				}
			}
		}
	}
	
	@AfterClass
	public static void teardownAfterClass() {
		log.debug("teardownAfterClass");
	}
	
	// -------------------------------------------------------------------------------
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
		log.debug("Application Init......");
		String datasourceFile = "srcTest\\conf\\datasource.xml";
		
		InputStream fis = null;
		try {
			 fis = new FileInputStream(datasourceFile);
			DSManager.getInstance().registerDatasourceXml(fis, false);
		} catch (FileNotFoundException e) {
			LogUtil.log(e);
			log.warn("init datasource fail ... {}", e.getMessage());
		}finally {
			try {
				if(fis!=null)
					fis.close();
			}catch (IOException e) {
				LogUtil.log(e);
			}
			
		}
	}
}
