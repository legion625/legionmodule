package legion.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateUtil {
	private final static ZoneOffset ZONE_OFFSET_8 = ZoneOffset.ofHours(+8);
	
	// -------------------------------------------------------------------------------
	public static LocalDate toLocalDate(Date _date) {
		if (_date == null)
			return null;
		return _date.toInstant().atOffset(ZONE_OFFSET_8).toLocalDate();
	}

	public static LocalDate toLocalDate(long _l) {
		if (_l <= 0)
			return null;
		return toLocalDate(new Date(_l));
	}

	public static LocalDateTime toLocalDateTime(Date _date) {
		if (_date == null)
			return null;
		return _date.toInstant().atOffset(ZONE_OFFSET_8).toLocalDateTime();
	}

	public static LocalDateTime toLocalDateTime(long _l) {
		if (_l <= 0)
			return null;
		return toLocalDateTime(new Date(_l));
	}

	// -------------------------------------------------------------------------------
	public static long toLong(LocalDate _localDate) {
		Date date = toDate(_localDate);
		return date == null ? 0 : date.getTime();
	}

	public static Date toDate(LocalDate _localDate) {
		return _localDate == null ? null : toDate(_localDate.atStartOfDay());
	}

	public static long toLong(LocalDateTime _localDateTime) {
		Date date = toDate(_localDateTime);
		return date == null ? 0 : date.getTime();
	}

	public static Date toDate(LocalDateTime _localDateTime) {
		if (_localDateTime == null)
			return null;
		return Date.from(_localDateTime.toInstant(ZONE_OFFSET_8));
	}
	
	
}
