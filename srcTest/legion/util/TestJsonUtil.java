package legion.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestJsonUtil {

	@Test
	public void testGetParseStringFromObjs() {
		/* get json string */
		String[] strs = new String[] { "1111", "2222", "3333", "4444" };
		String jsonStr = JsonUtil.getJsonArrayString(strs);
		System.out.println("jsonStr: " + jsonStr);
		assertTrue(jsonStr.equalsIgnoreCase("[\"1111\",\"2222\",\"3333\",\"4444\"]"));

		/* parse json string */
		Object[] objs = JsonUtil.parseJsonArrayStringToObjs(jsonStr);
		boolean[] actuals = new boolean[4];
		for (int i = 0; i < 4; i++)
			actuals[i] = objs[i].getClass().getSimpleName().equalsIgnoreCase("String")
					&& ((String) objs[i]).equalsIgnoreCase(strs[i]);
		assertArrayEquals(new boolean[] { true, true, true, true }, actuals);
	}
	
	@Test
	public void testGetParseString() {
		/* get json string */
		List<String> strList = new ArrayList<>();
		strList.add("a");
		strList.add("b");
		strList.add("c");
		String jsonStr = JsonUtil.getJsonArrayString(strList);
		assertTrue(jsonStr.equalsIgnoreCase("[\"a\",\"b\",\"c\"]"));

		/* parse json string */
		List<Object> objList = JsonUtil.parseJsonArrayString(jsonStr);
		boolean[] actuals = new boolean[3];
		for (int i = 0; i < 3; i++)
			actuals[i] = objList.get(i).getClass().getSimpleName().equalsIgnoreCase("String")
					&& ((String) objList.get(i)).equalsIgnoreCase(strList.get(i));
		assertArrayEquals(new boolean[] { true, true, true }, actuals);
	}
	
	
}
