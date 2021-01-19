package kr.co.ensecure.scheduler.base.util;

import java.util.Locale;
import javax.annotation.Resource;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

/**
 * messageSource를 편하게 사용하기 위한 Util 클래스
 */
@Service
public final class MsgUtil {

	private static MessageSource messageSource;
	
	@Resource(name="messageSource")
	private void setMessageSource(MessageSource messageSource) {
		MsgUtil.messageSource = messageSource;
	}
	
	
	public static String getMessage(String code, Object[] args, Locale locale) {
		String msg = null;
		if(StringUtil.isEmpty(code)) {
			return msg;
		}
		if(locale == null) {
			locale = Locale.getDefault();
		}
		return messageSource.getMessage(code, args, locale);
	}
	
	
	public static String getMessage(String code, Object arg, Locale locale) {
		return MsgUtil.getMessage(code, new Object[]{arg}, locale);
	}
	
	
	public static String getMessage(String code, Object[] args) {
		return MsgUtil.getMessage(code, args, null);
	}
	
	
	public static String getMessage(String code, Object arg) {
		return MsgUtil.getMessage(code, new Object[]{arg}, null);
	}
	
	public static String getMessage(String code, Locale locale) {
        return MsgUtil.getMessage(code, null, locale);
    }
	
	
	public static String getMessage(String code) {
		return MsgUtil.getMessage(code, null, null);
	}
	
	
}
