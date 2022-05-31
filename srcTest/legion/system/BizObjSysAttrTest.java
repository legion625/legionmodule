package legion.system;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import legion.AbstractLegionInitTest;
import legion.DataServiceFactory;
import legion.TestUtil;
import legion.data.SystemDataService;
import legion.data.service.system.SystemDataServiceImp;
import legion.system.type.SysAttrType;

public class BizObjSysAttrTest extends AbstractLegionInitTest{

	private static SystemDataService dataService = DataServiceFactory.getInstance().getService(SystemDataService.class);
	
	private String targetUid;
	private Target target1,target2;
	
	@Before
	public void before() {
		target1 = new Target(SysAttrType.UNDEFINED, "targetUid1", "targetUid1");
		target2 = new Target(SysAttrType.SYS, "targetUid2", "targetUid2");
	}
	
	@Test
	public void testCRUD() throws Throwable{
		testCreateSysAttr();
		testUpdateSysAttr();
		testDeleteSysAttr();
	}
	
	@Test
//	@Ignore
	public void testCreateSysAttr() throws Throwable {
		/* create */
		SysAttr obj = SysAttr.newInstance(target1.type);
		log.debug("obj.getUid(): {}", obj.getUid());
		
		PropertyUtils.copyProperties(obj, target1);
		log.debug("obj.getUid(): {}", obj.getUid());
		log.debug("obj key: {}\tobj value: {}", obj.getKey(), obj.getValue());
		assert obj.save();
		targetUid = obj.getUid();
		
		/* load */
		obj = dataService.loadSysAttr(targetUid);
		TestUtil.assertObjEqual(target1, obj);
	}
	
	@Test
	@Ignore
	public void testUpdateSysAttr() throws Throwable {
		SysAttr obj = dataService.loadSysAttr(targetUid);
		PropertyUtils.copyProperties(obj, target2);
		assert obj.save();
		/* load */
		obj = dataService.loadSysAttr(targetUid);
		TestUtil.assertObjEqual(target2, obj);
	}
	
	@Test
	@Ignore
	public void testDeleteSysAttr() {
		assert dataService.deleteSysAttr(targetUid);
	}
	
	// -------------------------------------------------------------------------------
	public class Target {
		private SysAttrType type;
		private String key;
		private String value;

		private Target(SysAttrType type, String key, String value) {
			super();
			this.type = type;
			this.key = key;
			this.value = value;
		}

		public SysAttrType getType() {
			return type;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

	}

}
