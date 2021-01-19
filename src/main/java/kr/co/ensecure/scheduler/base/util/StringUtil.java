package kr.co.ensecure.scheduler.base.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import org.apache.commons.lang3.StringUtils;
import com.google.common.base.CaseFormat;


public final class StringUtil {
    
    public final static String CHARSET_UTF8 = "UTF-8";
    public final static String CHARSET_EUCKR = "EUC-KR";
    
	
	public static String toString(Object o) {
		if (o == null) {
			return null;
		}
		return o.toString();
	}
	
	
	public static String toString(Object o, String defaultValue) {
		if (o == null) {
			return defaultValue;
		}
		return o.toString();
	}
	
	
	public static boolean isEmpty(String value) {
		return (value == null || value.length() == 0);
	}
	
	
	public static boolean isNotEmpty(String value) {
		return !isEmpty(value);
	}
	
	
	public static String trim(String value) {
		if(value == null) {
			return null;
		}
		return value.trim();
	}
	
	
	public static String lpad(String value, int len, String pad) {
		if(value == null) {
			value = "";
		}
		return StringUtils.leftPad(value, len, pad);
	}
	
	
	public static String rpad(String value, int len, String pad) {
		if(value == null) {
			value = "";
		}
		return StringUtils.rightPad(value, len, pad);
	}
	
	
	public static String camelLower(String name) {
    	return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name);
    }
	
	
	public static String camelUpper(String name) {
    	return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
    }
	
	
	/**
	 * 메시지 Format
	 * 
	 * @param msg - "{0}는 {1}자리입니다."
	 * @param params - new Object[]{"ID", 8}
	 * @return
	 */
	public static String msgFormat(String msg, Object ... params) {
		String formatMsg = null;
		if(StringUtil.isNotEmpty(msg)) {
			if(params != null && params.length > 0) {
				formatMsg = MessageFormat.format(msg, params);
			} else {
				formatMsg = msg;
			}
		}
		return formatMsg;
	}
	
	
	/**
	 * 메시지 Parse
	 * 
	 * @param pattern - "{0}는 {1}자리입니다."
	 * @param msg - "ID는 8자리입니다."
	 * @return new Object[]{"ID", 8}
	 */
	public static Object[] msgParse(String pattern, String msg) {
		Object[] params = null;
		if(StringUtil.isEmpty(pattern)) {
			return params;
		}
		if(StringUtil.isEmpty(msg)) {
			return params;
		}
		try {
			MessageFormat formatter = new MessageFormat(pattern);
			params = formatter.parse(msg);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return params;
	}
	
	
	public static String formatAmount(String value, String pattern) {
		String format = null;
		if(StringUtil.isNotEmpty(value)) {
			Double number = Double.parseDouble(value);
			DecimalFormat formatter = new DecimalFormat(pattern);
			format = formatter.format(number);
		}
		return format;
	}
	
	
	public static String formatAmount(Object value, String pattern) {
		return StringUtil.formatAmount(String.valueOf(value), pattern);
	}
	
	
	public static String formatAmount(String value) {
		return StringUtil.formatAmount(value, "#,###");
	}
	
	
	public static String formatAmount(Object value) {
		return StringUtil.formatAmount(String.valueOf(value));
	}
	
	
	/**
	 * 문자열을 Byte로 (한글 중간에 안 짤리게) 자름
	 * 
	 * @param str
	 * @param len
	 * @param encoding
	 * @return
	 */
	public static String subByte(String str, int len, String encoding) {
		try {
			if (StringUtil.isEmpty(str)) {
				return str;
			}
			byte[] strBytes = str.getBytes(encoding);
			int strLength = strBytes.length;
			int minusByteNum = 0;
			int offset = 0;
			int hangulByteNum = encoding.equals(CHARSET_UTF8) ? 3 : 2;
			if (strLength > len) {
				minusByteNum = 0;
				offset = len;
				for (int j = 0; j < offset; j++) {
					if (((int) strBytes[j] & 0x80) != 0) {
						minusByteNum++;
					}
				}
				if (minusByteNum % hangulByteNum != 0) {
					offset -= minusByteNum % hangulByteNum;
				}
				return new String(strBytes, 0, offset, encoding);
			} else {
				return str;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 문자열을 Byte로 (한글 중간에 안 짤리게) 자름 (UTF-8)
	 * 
	 * @param str
	 * @param len
	 * @return
	 */
	public static String subByte(String str, int len) {
		return StringUtil.subByte(str, len, CHARSET_UTF8);
	}
	
	/**
	 * 문자열을 Byte로 (한글 중간에 안 짤리게) 자름 (EUC-KR)
	 * 
	 * @param str
	 * @param len
	 * @return
	 */
	public static String subByteEucKr(String str, int len) {
		return StringUtil.subByte(str, len, CHARSET_EUCKR);
	}
	
	
	public static String urlEncoding(String value, String charset) {
		String encode = null;
		try {
			encode = URLEncoder.encode(value, charset);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return encode;
	}
	
	
	public static String urlEncoding(String value) {
		return StringUtil.urlEncoding(value, CHARSET_UTF8);
	}
	
	
	public static String urlDecoding(String value, String charset) {
		String encode = null;
		try {
			encode = URLDecoder.decode(value, charset);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return encode;
	}
	
	
	public static String urlDecoding(String value) {
		return StringUtil.urlDecoding(value, CHARSET_UTF8);
	}
	
	
}
