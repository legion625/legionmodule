package legion;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import legion.data.SystemDataService;
import legion.system.SysAttr;

public class LegionTest extends AbstractLegionInitTest {
	
	@Test
	public void test0() {
		log.debug("test0");
		log.debug("SystemInfoDefault.getInstance().getVersion(): {}", SystemInfoDefault.getInstance().getVersion());
		
		SystemDataService systemDataSerivce = DataServiceFactory.getInstance().getService(SystemDataService.class);
		log.debug("systemDataSerivce: {}", systemDataSerivce);
		assertTrue(systemDataSerivce.testCallback());
		
		List<SysAttr> sysAttrList = systemDataSerivce.loadSysAttrList();
		log.debug("sysAttrList: {}", sysAttrList);
		log.debug("sysAttrList.size(): {}", sysAttrList.size());
		for(SysAttr sa: sysAttrList) {
			log.debug("{}\t{}\t{}", sa.getTypeIdx(), sa.getKey(), sa.getValue());
		}
	}
	
	@Test
	public void test() {
//		String str = "aaa[123]";
//		str = str.replaceAll("[^A-Za-z0-9]", "");
//		System.out.println("str:\t" + str);
//
//		String str1 = "12";
//		System.out.println("str.matches(\"[0-9]\"):\t" + str.matches("[0-9]+"));
//		System.out.println("str1.matches(\"[0-9]\"):\t" + str1.matches("[0-9]+"));
		
//		String REGEX_INVALID_POS ="[WP|LS|NFP|DT|TO|IN|CC|CD|WRB]"; 
		String REGEX_INVALID_POS ="WP|WRB";
		
		String str1 = "NN";
		String str2 = "WP";
		String str3 = "WRB";
		log.debug(str1+"\t"+str1.matches(REGEX_INVALID_POS));
		log.debug(str2+"\t"+str2.matches(REGEX_INVALID_POS));
		log.debug(str3+"\t"+str3.matches(REGEX_INVALID_POS));
	}
	
	
}
