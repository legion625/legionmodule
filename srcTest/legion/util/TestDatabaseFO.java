package legion.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.Test;

public class TestDatabaseFO {
	// public static String toDbString(LocalDateTime _localDateTime) {
	// return _localDateTime.toString();
	// }
	//
	// public static String toDbString(LocalDate _localDate) {
	// return _localDate.toString();
	// }
	//
	// public static LocalDateTime parseLocalDateTime(String _dbString) {
	// return LocalDateTime.parse(_dbString);
	// }

	//
	@Test
	public void testToDbString() {
		LocalDateTime origTime = LocalDateTime.of(2018, 12, 19, 23, 57, 55, 123);
		System.out.println("origTime: " + origTime);
		String timeStr = DatabaseFO.toDbString(origTime);
		System.out.println("timeStr: " + timeStr);
		LocalDateTime afterTime = DatabaseFO.parseLocalDateTime(timeStr);
		System.out.println("afterTime: " + afterTime);
		assert origTime.equals(afterTime);

		LocalDate origDate = LocalDate.of(2018, 12, 19);
		System.out.println("origDate: " + origDate);
		String dateStr = DatabaseFO.toDbString(origDate);
		System.out.println("dateStr: " + dateStr);
		LocalDate afterDate = DatabaseFO.parseLocalDate(dateStr);
		System.out.println("afterDate: " + afterDate);
		assert origDate.equals(afterDate);
	}
}
