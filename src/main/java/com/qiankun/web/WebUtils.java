package com.qiankun.web;

import com.qiankun.common.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

public abstract class WebUtils {
	private final static Logger logger = LoggerFactory.getLogger(WebUtils.class);

	
	public final static String ENCODING_UTF_8 = "UTF-8";
	public final static String KEY_ERR_MSG = "__msg_on_err";
	
	public final static String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";
	public final static String CONTENT_TYPE_XML = "text/xml; charset=UTF-8";
	public final static String CONTENT_TYPE_PNG = "image/png; charset=UTF-8";
	public final static String CONTENT_TYPE_CSS = "text/css; charset=UTF-8";
	public final static String CONTENT_TYPE_HTML = "text/html; charset=UTF-8";
	public final static String CONTENT_TYPE_PDF = "application/pdf; charset=UTF-8";
	public static final String CONTENT_TYPE_XLS = "application/vnd.ms-excel; charset=UTF-8";
	public static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=UTF-8";
	public static final String CONTENT_TYPE_CSV = "text/csv; charset=UTF-8";
	public static final String CONTENT_TYPE_DOC = "application/msword; charset=UTF-8";
	public static final String CONTENT_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document; charset=UTF-8";
	public final static String CONTENT_TYPE_OCTET = "application/octet-stream; charset=UTF-8";
	public final static String AJAX_HEADER = "X-Requested-With";
	public final static String IFRAME_HEADER = "X-IFrame-With";
	
	/**
	 * 设置 Json 附件响应头
	 *
	 * @param response
	 * @param fileName
	 */
	public static void setJsonAttachmentHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
	    setContentTypeAttachementHeader(request, response, CONTENT_TYPE_JSON, fileName);
	}
	/**
	 * 设置 xml 附件响应头
	 *
	 * @param response
	 * @param fileName
	 */
	public static void setXmlAttachmentHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
	    setContentTypeAttachementHeader(request, response, CONTENT_TYPE_XML, fileName);
	}
	/**
	 * 设置 html 附件响应头
	 *
	 * @param response
	 * @param fileName
	 */
	public static void setHtmlAttachmentHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
	    setContentTypeAttachementHeader(request, response, CONTENT_TYPE_HTML, fileName);
	}
	
	/**
	 * 设置 excel xls 附件响应头
	 *
	 * @param response
	 * @param fileName
	 */
	public static void setXlsAttachmentHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
	    setContentTypeAttachementHeader(request, response, CONTENT_TYPE_XLS, fileName);
	}
	
	/**
	 * 设置 excel xlsx 附件响应头
	 *
	 * @param response
	 * @param fileName
	 */
	public static void setXlsxAttachmentHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
	    setContentTypeAttachementHeader(request, response, CONTENT_TYPE_XLSX, fileName);
	}
	/**
	 * 设置word doc附件响应头
	 * @param response
	 * @param fileName
	 */
	public static void setDocAttachmentHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
	    setContentTypeAttachementHeader(request, response, CONTENT_TYPE_DOC, fileName);
	}
	/**
	 * 设置word docx附件响应头
	 * @param response
	 * @param fileName
	 */
	public static void setDocxAttachmentHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
	    setContentTypeAttachementHeader(request, response, CONTENT_TYPE_DOCX, fileName);
	}
	
	/**
	 * 设置 csv 附件响应头
	 *
	 * @param response
	 * @param fileName
	 */
	public static void setCsvAttachmentHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
	    setContentTypeAttachementHeader(request, response, CONTENT_TYPE_CSV, fileName);
	}
	
	
	public static void setPdfInlineHeader(HttpServletRequest request, HttpServletResponse response) {
		setContentTypeInlineHeader(request, response, CONTENT_TYPE_PDF);
	}
	
	public static void setOctetAttachmentHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
	    setContentTypeAttachementHeader(request, response, CONTENT_TYPE_OCTET, fileName);
	}
	
	public static void setContentTypeInlineHeader(HttpServletRequest request, HttpServletResponse response, String contentType) {
	    response.setContentType(contentType);
	    setInlineHeader(response);
	}
	
	public static void setContentTypeAttachementHeader(HttpServletRequest request, HttpServletResponse response, String contentType, String fileName) {
	    response.setContentType(contentType);
	    setAttachmentHeader(request, response, fileName);
	}
	
	/**
	 * 设置附件响应头
	 */
	public static void setAttachmentHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
	    try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
	    } catch (UnsupportedEncodingException ignore) {
	    }
	    response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
	}
	
	/**
	 * 设置内联显示响应头
	 */
	public static void setInlineHeader(HttpServletResponse response) {
	    response.setHeader("Content-Disposition", "inline");
	}
	
	public static String urlEncode(String str) {
		if (StringUtils.isEmpty(str)) {
			return str;
		}
		String val = null;
		try {
			val = URLEncoder.encode(str, ENCODING_UTF_8);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
		return val;
	}
	
	public static String urlDecode(String s) {
	    if (s == null) return s;
	    try {
	        return URLDecoder.decode(s, ENCODING_UTF_8);
	    } catch (Exception ignore) {
	        return s;
	    }
	}
	
	public static boolean isAjaxRequest(HttpServletRequest request) {
	    String ajaxHeader = request.getHeader(AJAX_HEADER);
	    if (ajaxHeader == null) {
	        ajaxHeader = (String) request.getAttribute(AJAX_HEADER);
	    }
	    if(ajaxHeader != null && "XMLHttpRequest".equalsIgnoreCase(ajaxHeader)) {
	        return true;
	    }
	    
	    return false;
	}
	
	public static boolean isAppRequest(HttpServletRequest request) {
	    String appHeader = request.getHeader(AJAX_HEADER);
	    if (appHeader == null) {
	        appHeader = (String) request.getAttribute(AJAX_HEADER);
	    }
	    if(appHeader != null && "appHttpRequest".equalsIgnoreCase(appHeader)) {
	        return true;
	    }
	    
	    return false;
	}
	
	public static String getHomeHost(HttpServletRequest request) {
	    String name = request.getServerName();
	    int port = request.getServerPort();
	    String contextPath = request.getContextPath();
	    return new StringBuilder().append("http://").append(name).append(":")
	    		.append(String.valueOf(port)).append(contextPath).toString();
	}
	
	public static String getHost(HttpServletRequest request) {
	    String name = request.getServerName();
	    int port = request.getServerPort();
	    if (port == 80) {
	    	return new StringBuilder().append("http://").append(name).toString();
	    }
	    return new StringBuilder().append("http://").append(name).append(":").append(String.valueOf(port)).toString();
	}
	
	public static String getWebPath(HttpServletRequest request, String path) {
	    return getWebPath(request.getSession().getServletContext(), path);
	}
	public static String getWebPath(ServletContext ctx, String path) {
	    return ctx.getRealPath(path);
	}
	
	/**
	 * 设置不缓存响应头
	 */
	public static void setNoCacheHeader(HttpServletResponse response) {
	    // http 1.0
	    response.setDateHeader("Expires", 0L);
	    // http 1.1
	    response.addHeader("Cache-Control", "no-cache, no-store, max-age=0");
	}
	
	public static void write(String content, String charset, OutputStream os) {
	    BufferedOutputStream bos = null;
	    try {
	        bos = new BufferedOutputStream(os);
	        bos.write(content.getBytes(charset));
	    } catch (IOException ie) {
	        throw new RuntimeException("An error occured on writing content", ie);
	    }
	}
	
	public static void write(String content, HttpServletResponse res) {
		PrintWriter writer = null;
	    try{
	    	writer = res.getWriter();
	    	writer.write(content);
	    } catch (IOException ie) {
	        throw new RuntimeException("An error occured on writing content", ie);
	    } finally {
	    	writer.close();
	    }
	}
	
	public static String getCorrectEncoding(String s, String encoding) {
	    if (s == null) return s;
	    try {
	        return new String(s.getBytes("ISO-8859-1"), encoding);
	    } catch (Exception ignore) {
	        return s;
	    }
	}
	
	public static boolean isFirefox(HttpServletRequest request) {
	    String userAgent = request.getHeader("User-Agent");
	    return userAgent.indexOf("Firefox") != -1;
	}
	
	public static String getRequestURI(HttpServletRequest request) {
		try {
			String contextPath = request.getContextPath();
			String uri = request.getRequestURI().replaceAll(contextPath, "");
			if (uri.startsWith("//")) {
				return uri.substring(1);
			}
			return uri;
		} catch (NullPointerException e) {
			// 在tomcat7.0.65版本发现调用requset.getServletContext()方法会偶发NPE，查看源码发现Request的context为null, 原因不明
			logger.error("NPE occured on getRequestURI(HttpServletRequest)");
			return null;
		}
	}

	public static Map<String, Object> getParameterMapByIncludes(HttpServletRequest request, String ... includeParams) {
		return doGetParameterMap(request, includeParams, null);
	}
	
	public static Map<String, Object> getParameterMap(HttpServletRequest request, String ... ignoreParams) {
		return doGetParameterMap(request, null, ignoreParams);
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> doGetParameterMap(HttpServletRequest request, String[] includeParams, String[] ignoreParams) {
		List<String> includeList = Collections.EMPTY_LIST;
		if (includeParams != null) {
			includeList = Arrays.asList(includeParams);
		}
		List<String> ignoreList = Collections.EMPTY_LIST;
		if (ignoreParams != null) {
			ignoreList = Arrays.asList(ignoreParams);
		}
		
		Enumeration<String> enumer = request.getParameterNames();
		Map<String, Object> map = new HashMap<>();
		while (enumer.hasMoreElements()) {
			String name = enumer.nextElement();
			if (! ignoreList.contains(name)) {
				boolean allowPut = includeList.isEmpty() || includeList.contains(name);
				if (allowPut) {
					map.put(name, request.getParameter(name));
				}
			}
		}
		return map;
	}
	
	private WebUtils() {}
}
