package legion.util;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

public class TestDateUtil {

	@Test
	public void testToDate() {
		LocalDate ld1 = LocalDate.now();
		System.out.println("ld1: " + ld1);
		Date date = DateUtil.toDate(ld1);
		System.out.println("date: " + date);

		LocalDate ld2 = DateUtil.toLocalDate(date);
		System.out.println("ld2: " + ld2);
	}
	@Test
	public void testLocalTime() {
		LocalTime lt1 = LocalTime.now();
		System.out.println("lt1: "+lt1);
		long l1 = lt1.toNanoOfDay();
		System.out.println("l1: "+l1);
		LocalTime lt2 = LocalTime.ofNanoOfDay(l1);
		System.out.println("lt2: "+lt2);
		
		long l2 = lt1.toSecondOfDay();
		System.out.println("l2: " +l2);
	}

	// @Test
	// public void testGetParseStringFromObjs() {
	// /* get json string */
	// String[] strs = new String[] { "1111", "2222", "3333", "4444" };
	// String jsonStr = JsonUtil.getJsonArrayString(strs);
	// System.out.println("jsonStr: " + jsonStr);
	// assertTrue(jsonStr.equalsIgnoreCase("[\"1111\",\"2222\",\"3333\",\"4444\"]"));
	//
	// /* parse json string */
	// Object[] objs = JsonUtil.parseJsonArrayStringToObjs(jsonStr);
	// boolean[] actuals = new boolean[4];
	// for (int i = 0; i < 4; i++)
	// actuals[i] = objs[i].getClass().getSimpleName().equalsIgnoreCase("String")
	// && ((String) objs[i]).equalsIgnoreCase(strs[i]);
	// assertArrayEquals(new boolean[] { true, true, true, true }, actuals);
	// }
	//
	// @Test
	// public void testGetParseString() {
	// /* get json string */
	// List<String> strList = new ArrayList<>();
	// strList.add("a");
	// strList.add("b");
	// strList.add("c");
	// String jsonStr = JsonUtil.getJsonArrayString(strList);
	// assertTrue(jsonStr.equalsIgnoreCase("[\"a\",\"b\",\"c\"]"));
	//
	// /* parse json string */
	// List<Object> objList = JsonUtil.parseJsonArrayString(jsonStr);
	// boolean[] actuals = new boolean[3];
	// for (int i = 0; i < 3; i++)
	// actuals[i] =
	// objList.get(i).getClass().getSimpleName().equalsIgnoreCase("String")
	// && ((String) objList.get(i)).equalsIgnoreCase(strList.get(i));
	// assertArrayEquals(new boolean[] { true, true, true }, actuals);
	// }
	//

}
