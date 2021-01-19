package kr.co.ensecure.scheduler.base.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public final class DateUtil {
	
	public static final String BASE_PATTERN = "yyyyMMddHHmmssSSS";
	public static final int BASE_PATTERN_LEN = BASE_PATTERN.length();
	private static final String[] PATTERN_CACHE = new String[BASE_PATTERN_LEN + 1];
	static {
		PATTERN_CACHE[BASE_PATTERN_LEN] = BASE_PATTERN;
		PATTERN_CACHE[14] = BASE_PATTERN.substring(0, 14);
		PATTERN_CACHE[12] = BASE_PATTERN.substring(0, 12);
		PATTERN_CACHE[10] = BASE_PATTERN.substring(0, 10);
		PATTERN_CACHE[8] = BASE_PATTERN.substring(0, 8);
		PATTERN_CACHE[6] = BASE_PATTERN.substring(0, 6);
		PATTERN_CACHE[4] = BASE_PATTERN.substring(0, 4);
		PATTERN_CACHE[0] = "";
	}
	public static final String DATE_PATTERN = PATTERN_CACHE[8];
	public static final String DATETIME_PATTERN = PATTERN_CACHE[14];
	
	
	public static LocalDateTime now() {
		return LocalDateTime.now();
	}
	
	
	public static long nowMilli() {
		return Instant.now().toEpochMilli();
	}
	
	
	public static Date toDate(LocalDate ld) {
		Date date = null;
		if(ld != null) {
			Instant instant = ld.atStartOfDay(ZoneId.systemDefault()).toInstant();
			date = Date.from(instant);
		}
		return date;
	}
	
	
	public static Date toDate(LocalDateTime ldt) {
		Date date = null;
		if(ldt != null) {
			Instant instant = ldt.atZone(ZoneId.systemDefault()).toInstant();
			date = Date.from(instant);
		}
		return date;
	}
	
	
	public static LocalDate toLocalDate(Date date) {
		LocalDate ld = null;
		if(date != null) {
			ld = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		}
		return ld;
	}
	
	
	public static LocalDateTime toLocalDateTime(Date date) {
		LocalDateTime ldt = null;
		if(date != null) {
			ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
		}
		return ldt;
	}
	
	
	public static LocalDateTime toLocalDateTime(long milli) {
		return Instant.ofEpochMilli(milli).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
	
	
	public static long toMilli(LocalDateTime ldt) {
		return ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
	
	
	public static Date parse(String source, String pattern) {
		Date date = null;
		if(source == null) {
			return date;
		}
		int sourceLen = source.length();
		if(sourceLen < 1) {
			return date;
		}
		if(sourceLen > 8) {
			LocalDateTime ldt = DateUtil.parseLocalDateTime(source, pattern);
			date = DateUtil.toDate(ldt);
		} else {
			LocalDate ld = DateUtil.parseLocalDate(source, pattern);
			date = DateUtil.toDate(ld);
		}
		return date;
	}
	
	
	public static Date parse(String source) {
		return DateUtil.parse(source, null);
	}
	
	
	public static LocalDate parseLocalDate(String source, String pattern) {
		LocalDate ld = null;
		if(source == null) {
			return ld;
		}
		int sourceLen = source.length();
		if(sourceLen < 1) {
			return ld;
		}
		if(StringUtil.isEmpty(pattern)) {
			pattern = PATTERN_CACHE[sourceLen];
			if(StringUtil.isEmpty(pattern)) {
				return ld;
			}
		}
		ld = LocalDate.parse(source, DateTimeFormatter.ofPattern(pattern));
		return ld;
	}
	
	
	public static LocalDate parseLocalDate(String source) {
		return DateUtil.parseLocalDate(source, null);
	}
	
	
	public static LocalDateTime parseLocalDateTime(String source, String pattern) {
		LocalDateTime ldt = null;
		if(source == null) {
			return ldt;
		}
		int sourceLen = source.length();
		if(sourceLen < 1) {
			return ldt;
		}
		if(StringUtil.isEmpty(pattern)) {
			pattern = PATTERN_CACHE[sourceLen];
			if(StringUtil.isEmpty(pattern)) {
				return ldt;
			}
		}
		ldt = LocalDateTime.parse(source, DateTimeFormatter.ofPattern(PATTERN_CACHE[sourceLen]));
		return ldt;
	}
	
	
	public static LocalDateTime parseLocalDateTime(String source) {
		return DateUtil.parseLocalDateTime(source, null);
	}
	
	
	public static String getTimeZoneOffsetId(TimeZone timeZone) {
		String timeZoneOffset = null;
		if (timeZone != null) {
			if (timeZone.getRawOffset() == 0) {
				timeZoneOffset = DateUtil.getTimeZoneUtcOffsetId();
			} else {
				timeZoneOffset = timeZone.toZoneId().getRules().getStandardOffset(Instant.now()).toString();
			}
		}
		return timeZoneOffset;
	}
	
	
	public static String getTimeZoneUtcOffsetId() {
		return "+00:00";
	}
	
	
	public static String getTimeZoneOffsetId() {
		return DateUtil.getTimeZoneOffsetId(TimeZone.getDefault());
	}
	

}
