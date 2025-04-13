package legion.system;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import legion.AbstractLegionInitTest;
import legion.DataServiceFactory;
import legion.TestUtil;
import legion.data.MySqlDataSource;
import legion.data.SystemDataService;
import legion.system.DocFile;
import legion.system.type.SysAttrType;

public class BizObjDocFileTest extends AbstractLegionInitTest {
	private static SystemDataService dataService = DataServiceFactory.getInstance().getService(SystemDataService.class);

	private static String targetUid;

	private Target target1, target2;

	@Before
	public void initMethod() {
		target1 = new Target("fileName1", "path1");
		target2 = new Target("fileName2", "path2");
	}

	@Test
	public void testCRUD() throws Throwable {
		testCreateDocFile();
		testUpdateDocFile();
		testDeleteDocFile();
	}

	@Test
	@Ignore
	public void testCreateDocFile() throws Throwable {
		/* create */
		DocFile obj = DocFile.newInstance();
		PropertyUtils.copyProperties(obj, target1);
		assert obj.save();
		targetUid = obj.getUid();
		/* load */
		obj = dataService.loadDocFile(targetUid);
		TestUtil.assertObjEqual(target1, obj);
	}

	@Test
	@Ignore
	public void testUpdateDocFile() throws Throwable {
		DocFile obj = dataService.loadDocFile(targetUid);
		PropertyUtils.copyProperties(obj, target2);
		assert obj.save();
		/* load */
		obj = dataService.loadDocFile(targetUid);
		TestUtil.assertObjEqual(target2, obj);
	}

	@Test
	@Ignore
	public void testDeleteDocFile() {
		assert dataService.loadDocFile(targetUid).delete();
	}

	// -------------------------------------------------------------------------------
	public class Target {
		private String fileName;
		private String path;

		private Target(String fileName, String path) {
			this.fileName = fileName;
			this.path = path;
		}

		public String getFileName() {
			return fileName;
		}

		public String getPath() {
			return path;
		}

	}
}
