package legion.util;

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
}
