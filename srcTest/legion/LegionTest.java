package legion;

import org.junit.Test;

public class LegionTest {
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
		System.out.println(str1+"\t"+str1.matches(REGEX_INVALID_POS));
		System.out.println(str2+"\t"+str2.matches(REGEX_INVALID_POS));
		System.out.println(str3+"\t"+str3.matches(REGEX_INVALID_POS));
	}
	
}
