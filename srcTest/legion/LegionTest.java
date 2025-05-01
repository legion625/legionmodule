package legion;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import legion.data.SystemDataService;
import legion.system.SysAttr;
import legion.util.AesEncryptionUtil;
import legion.util.StrHasher;

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
	
	@Test
	public void testMap() {
		Map<String, String> map = new HashMap<>();
		String str1 = map.putIfAbsent("a", "x");
		String str2 = map.get("a");
		log.debug("{}\t{}", str1, str2);
	}
	
	@Test
	public void testAesEncrptionUtil() throws Exception {
		String aesKey = "MySecret16KeyABC";
		String text1 = "abcdefgABCDEFG123456789!@#";
		String text2 =AesEncryptionUtil.encrypt(aesKey, text1);
		String text3 =AesEncryptionUtil.decrypt(aesKey, text2);
		log.debug("text1: {}", text1);
		log.debug("text2: {}", text2);
		log.debug("text3: {}", text3);
		
	}
	
	@Test
	public void testStrHasher() throws Exception {
		String text1 = "abcdefgABCDEFG123456789!@#";
		String text2 = StrHasher.hashPassword(text1);
		boolean b = StrHasher.verifyPassword(text1, text2);
		log.debug("text1: {}", text1);
		log.debug("text2: {}", text2);
		log.debug("verifyPassword: {}", b);
	}
}
