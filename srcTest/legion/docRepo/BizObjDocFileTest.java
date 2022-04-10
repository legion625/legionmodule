package legion.docRepo;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Ignore;
import org.junit.Test;

import legion.TestUtil;
import legion.data.MySqlDataSource;
import legionLab.DataSource;
import legionLab.docRepo.MyDocFileDao;

public class BizObjDocFileTest {
	private static MySqlDataSource ds = DataSource.getMySqlDs();
	private static MyDocFileDao dao = MyDocFileDao.getInstance();
	private static String targetDocFileUid;

	private Target target1 = new Target("fileName1", "path1");
	private Target target2 = new Target("fileName2", "path2");

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
		DocFile obj = DocFile.newInstance(ds, target1.fileName, target1.path);
		PropertyUtils.copyProperties(obj, target1);
		assert obj.save();
		targetDocFileUid = obj.getUid();

		/* load */
		obj = dao.loadDocFile(targetDocFileUid);
		TestUtil.assertObjEqual(target1, obj);
	}

	@Test
	@Ignore
	public void testUpdateDocFile() throws Throwable {
		DocFile obj = dao.loadDocFile(targetDocFileUid);
		PropertyUtils.copyProperties(obj, target2);
		assert obj.save();
		/* load */
		obj = dao.loadDocFile(targetDocFileUid);
		TestUtil.assertObjEqual(target2, obj);
	}

	@Test
	@Ignore
	public void testDeleteDocFile() {
		assert dao.loadDocFile(targetDocFileUid).delete();
	}

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
