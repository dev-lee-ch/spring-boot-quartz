package kr.co.ensecure.scheduler.base.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class DateFormatUtil {
	
	public static final String BASE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final int BASE_PATTERN_LEN = BASE_PATTERN.length();
	private static final String[] PATTERN_CACHE = new String[BASE_PATTERN_LEN + 1];
	static {
		PATTERN_CACHE[BASE_PATTERN_LEN] = BASE_PATTERN;
		PATTERN_CACHE[14] = BASE_PATTERN.substring(0, 19);
		PATTERN_CACHE[12] = BASE_PATTERN.substring(0, 16);
		PATTERN_CACHE[10] = BASE_PATTERN.substring(0, 13);
		PATTERN_CACHE[8] = BASE_PATTERN.substring(0, 10);
		PATTERN_CACHE[6] = BASE_PATTERN.substring(0, 7);
		PATTERN_CACHE[4] = BASE_PATTERN.substring(0, 4);
		PATTERN_CACHE[0] = "";
	}
	public static final String DATE_PATTERN = PATTERN_CACHE[8];
	public static final String DATETIME_PATTERN = PATTERN_CACHE[14];
	
	
	public static final String now(String pattern) {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
	}
	
	
	public static final String now() {
		return DateFormatUtil.now(DateFormatUtil.DATE_PATTERN);
	}
	
	
	public static final String format(String source, String pattern) {
		String format = null;
		if(source == null) {
			return format;
		}
		int sourceLen = source.length();
		if(sourceLen < 1) {
			return format;
		}
		if(sourceLen > 8) {
			LocalDateTime ldt = DateUtil.parseLocalDateTime(source);
			format = DateFormatUtil.format(ldt, pattern);
		} else {
			LocalDate ld = DateUtil.parseLocalDate(source);
			format = DateFormatUtil.format(ld, pattern);
		}
		return format;
	}
	
	
	public static final String format(String source) {
		String format = null;
		if(source == null) {
			return format;
		}
		int sourceLen = source.length();
		if(sourceLen < 1) {
			return format;
		}
		if(sourceLen > 8) {
			LocalDateTime ldt = DateUtil.parseLocalDateTime(source);
			format = DateFormatUtil.format(ldt);
		} else {
			LocalDate ld = DateUtil.parseLocalDate(source);
			format = DateFormatUtil.format(ld);
		}
		return format;
	}
	
	
	public static final String format(LocalDate ld, String pattern) {
		String format = null;
		if(ld != null) {
			format = ld.format(DateTimeFormatter.ofPattern(pattern));
		}
		return format;
	}
	
	
	public static final String format(LocalDate ld) {
		return DateFormatUtil.format(ld, DateFormatUtil.DATE_PATTERN);
	}
	
	
	public static final String format(LocalDateTime ldt, String pattern) {
		String format = null;
		if(ldt != null) {
			format = ldt.format(DateTimeFormatter.ofPattern(pattern));
		}
		return format;
	}
	
	
	public static final String format(LocalDateTime ldt) {
		return DateFormatUtil.format(ldt, DateFormatUtil.DATETIME_PATTERN);
	}
	
	
	public static final String formatDate(Date date, String pattern) {
		String format = null;
		if(StringUtil.isEmpty(pattern)) {
			pattern = DateFormatUtil.DATE_PATTERN;
		}
		if(date != null) {
			format = DateFormatUtil.format(DateUtil.toLocalDate(date), pattern);
		}
		return format;
	}
	
	
	public static final String formatDate(Date date) {
		return DateFormatUtil.formatDate(date, DateFormatUtil.DATE_PATTERN);
	}
	
	
	public static final String formatDateTime(Date date, String pattern) {
		String format = null;
		if(date != null) {
			format = DateFormatUtil.format(DateUtil.toLocalDateTime(date), pattern);
		}
		return format;
	}
	
	
	public static final String formatDateTime(Date date) {
		return DateFormatUtil.formatDateTime(date, DateFormatUtil.DATETIME_PATTERN);
	}
	
	
}
