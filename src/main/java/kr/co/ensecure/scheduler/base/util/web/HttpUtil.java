package kr.co.ensecure.scheduler.base.util.web;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UrlPathHelper;
import kr.co.ensecure.scheduler.base.util.StringUtil;

/**
 * Http 관련 Util
 */
public class HttpUtil {

	private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);
	
	
	public static HttpServletRequest getRequest() {
        HttpServletRequest request = null;
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if(requestAttributes != null) {
            request = requestAttributes.getRequest();
        }
        return request;
    }
	
	public static String getContextPath(HttpServletRequest request) {
		return request.getContextPath();
	}
	
	
	public static String getRequestURL(HttpServletRequest request) {
		return request.getRequestURL().toString();
	}
	
	
	public static boolean isMultipart(HttpServletRequest request) {
		String contentType = request.getContentType();
		if(contentType != null && contentType.toLowerCase().startsWith("multipart/form-data")) {
			return true;
		}
		return false;
	}
	
	
	public static String getRemoteIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if(StringUtil.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
			int index = ip.indexOf(",");
			if(index != -1) {
				ip = ip.substring(0, index);
			}
			if(ip != null) {
				ip = ip.trim();
			}
			return ip;
		}
		ip = request.getHeader("http_client_ip");
		if (StringUtil.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
			return ip;
		}
		ip = request.getHeader("http_x_forwarded");
		if (StringUtil.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
			return ip;
		}
		ip = request.getHeader("http_x_forwarded_for");
		if (StringUtil.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
			return ip;
		}
		ip = request.getHeader("http_x_cluster_client_ip");
		if (StringUtil.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
			return ip;
		}
		ip = request.getHeader("http_forwarded");
		if (StringUtil.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
			return ip;
		}
		ip = request.getHeader("remote_addr");
		if (StringUtil.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
			return ip;
		}
		ip = request.getHeader("proxy-client-ip");  // weblogic
		if (StringUtil.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
			return ip;
		}
		ip = request.getHeader("wl-proxy-client-ip");  // weblogic
		if (StringUtil.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
			return ip;
		}
		return request.getRemoteAddr();
	}
	
	
	public static int getServerPort(HttpServletRequest request) {
		String forwardedPort = request.getHeader("x-forwarded-port");
		if(StringUtil.isNotEmpty(forwardedPort) && !"unknown".equalsIgnoreCase(forwardedPort)) {
			int index = forwardedPort.indexOf(",");
			if(index != -1) {
				forwardedPort = forwardedPort.substring(0, index);
			}
			if(forwardedPort != null) {
				forwardedPort = forwardedPort.trim();
			}
			return NumberUtils.toInt(forwardedPort);
		}
		return request.getServerPort();
	}
	
	
	public static String getServerIp() {
		String ip = null;
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			boolean isExit = false;
 			while(en.hasMoreElements()) {
				NetworkInterface ni = en.nextElement();
				if(ni == null || !ni.isUp() || ni.isLoopback() || ni.isVirtual()) {
					continue;
				}
				Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
				while(inetAddresses.hasMoreElements()) { 
					InetAddress ia = inetAddresses.nextElement();
					if(ia.isLoopbackAddress() || ia.isLinkLocalAddress()) {
						continue;
					}
					if(ia.isSiteLocalAddress()) {
						return ia.getHostAddress();
					}
					if(ia.getHostAddress() != null && ia.getHostAddress().indexOf(".") != -1) {
						ip = ia.getHostAddress();
						isExit = true;
						break;
					}
				}
				if(isExit) {
					break;
				}
			}
		} catch (Exception e) {
			log.error("error : {}", e.getMessage());
		}
		return ip;
	}

	
	public static Map<String, Object> getHeaderMap(Header[] headers) {
		Map<String, Object> headerMap = null;
		if(headers != null) {
			headerMap = new HashMap<String, Object>();
			String key = null, value = null;
			for(Header header : headers) {
				key = header.getName();
				if(StringUtil.isNotEmpty(key)) {
					value = header.getValue();
					headerMap.put(key.toLowerCase(), value);
				}
			}
		}
		return headerMap;
	}
	
	
	public static Map<String, Object> getHeaderMap(HttpServletRequest request) {
		Map<String, Object> headerMap = null;
		if(request != null) {
			headerMap = new HashMap<String, Object>();
			Enumeration<String> headerNames = request.getHeaderNames();
			String key = null, value = null;
			while(headerNames.hasMoreElements()) {
				key = headerNames.nextElement();
				value = request.getHeader(key);
				if(StringUtil.isNotEmpty(value)) {
					headerMap.put(key.toLowerCase(), value);
				}
			}
		}
		return headerMap;
	}
	
	
	public static Map<String, Object> getHeaderMap(HttpServletResponse response) {
		Map<String, Object> headerMap = null;
		if(response != null) {
			headerMap = new HashMap<String, Object>();
			Iterator<String> iter = response.getHeaderNames().iterator();
			String key = null, value = null;
			while(iter.hasNext()) {
				key = iter.next();
				value = response.getHeader(key);
				if(StringUtil.isNotEmpty(value)) {
					headerMap.put(key.toLowerCase(), value);
				}
			}
		}
		return headerMap;
	}
	
	
	public static Map<String, Object> getParamMap(HttpServletRequest request) {
		Map<String, Object> paramMap = null;
		if(request != null) {
			paramMap = new HashMap<String, Object>();
			Enumeration<String> enumeration = request.getParameterNames();
			String key = null;
			String[] values = null;
			while (enumeration.hasMoreElements()) {
				key = enumeration.nextElement();
				values = request.getParameterValues(key);
				if (values != null) {
					try {
						if(values.length > 1) {
							paramMap.put(key, values);
						} else {
							paramMap.put(key, values[0]);
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return paramMap;
	}
	
	
	public static String getBody(HttpServletRequest request, String charset) {
        String reqData = null;
        byte[] reqByte;
        try {
            InputStream in = request.getInputStream();
            reqByte = IOUtils.toByteArray(in);
            if(reqByte.length > 0) {
                reqData = new String(reqByte, charset);
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return reqData;
    }
	
	
	public static String getBody(HttpServletRequest request) {
		return HttpUtil.getBody(request, "UTF-8");
	}
	
	
	public static String getMethod(HttpServletRequest request) {
		return request.getMethod().toUpperCase();
	}
	
	
	public static String getRefererURL(HttpServletRequest request) {
		return request.getHeader("referer");
	}
	
	
	public static String getRefererUri(HttpServletRequest request) {
		String refererUrl = HttpUtil.getRefererURL(request);
		if(StringUtil.isNotEmpty(refererUrl)) {
			int idx = -1;
			String contextPath = HttpUtil.getContextPath(request);
			if(StringUtil.isNotEmpty(contextPath)) {
				idx = refererUrl.indexOf(contextPath);
			} else {
				idx = refererUrl.indexOf("/", request.getScheme().length() + 3);
			}
			if(idx > -1) {
				return refererUrl.substring(idx);
			}
		}
		return null;
	}
	
	
	public static String getRefererPath(HttpServletRequest request) {
		String refererUrl = HttpUtil.getRefererURL(request);
		if(StringUtil.isNotEmpty(refererUrl)) {
			int idx = -1;
			String contextPath = HttpUtil.getContextPath(request);
			if(StringUtil.isNotEmpty(contextPath)) {
				idx = refererUrl.indexOf(contextPath);
				if(idx != -1) {
					idx += contextPath.length();
				}
			} else {
				idx = refererUrl.indexOf("/", request.getScheme().length() + 3);
			}
			if(idx > -1) {
				return refererUrl.substring(idx);
			}
		}
		return null;
	}
	
	
	public static boolean isExcelExtension(HttpServletRequest request) {
		String uri = getOriginatingServletPath(request);
		if(uri.endsWith(".excel")) {
			return true;
		}
		return false;
	}
	
	
	public static boolean isFileExtension(HttpServletRequest request) {
		String uri = getOriginatingServletPath(request);
		if(uri.endsWith(".file")) {
			return true;
		}
		return false;
	}
	
	
	public static boolean isReportExtension(HttpServletRequest request) {
		String uri = getOriginatingServletPath(request);
		if(uri.endsWith(".report")) {
			return true;
		}
		return false;
	}
	
	public static String getOriginatingServletPath(HttpServletRequest request) {
		return new UrlPathHelper().getOriginatingServletPath(request);
	}
	
}
