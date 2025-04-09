package legion.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Test;

public class TestDataFO {
	@Test
	public void testFillString() {
		String _str = "aa";
		int _length = 9;
		char _char = 'R';
		
		System.out.println(DataFO.fillString(_str, _length, _char));
	}
	
	@Test
	public void testIsInt() {
		assertTrue(DataFO.isInt("0"));
		assertTrue(DataFO.isInt("-123"));
		assertTrue(DataFO.isInt("123"));
		assertFalse(DataFO.isInt("0123"));
		assertFalse(DataFO.isInt("123.123"));
		assertFalse(DataFO.isInt("-123.123"));
		assertFalse(DataFO.isInt("0.123"));
		assertFalse(DataFO.isInt("abc123"));
	}
	
	@Test
	public void testIsPositiveInt() {
		assertFalse(DataFO.isPositiveInt("0"));
		assertFalse(DataFO.isPositiveInt("-123"));
		assertTrue(DataFO.isPositiveInt("123"));
		assertFalse(DataFO.isPositiveInt("0123"));
		assertFalse(DataFO.isPositiveInt("123.123"));
		assertFalse(DataFO.isPositiveInt("-123.123"));
		assertFalse(DataFO.isPositiveInt("0.123"));
		assertFalse(DataFO.isPositiveInt("abc123"));
	}
	
	@Test
	public void testIsNumber() {
		assertTrue(DataFO.isNumber("0"));
		assertTrue(DataFO.isNumber("-123"));
		assertTrue(DataFO.isNumber("123"));
		assertFalse(DataFO.isNumber("0123"));
		assertTrue(DataFO.isNumber("123.123"));
		assertTrue(DataFO.isNumber("-123.123"));
		assertTrue(DataFO.isNumber("0.123"));
		assertFalse(DataFO.isNumber("abc123"));
	}
	
	
	
	@Test
	public void toUrlFormat() {
		System.out.println(DataFO.toUrlFormat("Request for information"));
	}

	@Test
	public void testRegex() {
//		String text = "8c41161e-e693-4e3b-88a7-0cb03ffdddc6";
		String text ="\"3fc2d217-c41b-4dbd-9cfd-3c13f2f0cd16\"";
//		System.out.println(text.matches("{36}"));
		System.out.println(text.matches("\"[a-z0-9-]{36}\""));
	}
	
	// -------------------------------------------------------------------------------
	@Test
	public void otherTest() {
		System.out.println(BigDecimal.ROUND_CEILING);
		System.out.println(RoundingMode.CEILING.ordinal());
	}
}
