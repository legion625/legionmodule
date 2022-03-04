package legion.util;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DatabaseFO {
	public static String toDbString(LocalDateTime _localDateTime) {
		return _localDateTime.toString();
	}

	public static LocalDateTime parseLocalDateTime(String _dbString) {
		return LocalDateTime.parse(_dbString);
	}

	public static String toDbString(LocalDate _localDate) {
		return _localDate.toString();
	}

	public static LocalDate parseLocalDate(String _dbString) {
		return LocalDate.parse(_dbString);
	}

}
