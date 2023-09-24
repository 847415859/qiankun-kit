package com.qiankun.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtils {

	public static final String NUMBER_REGEX = "^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$";

	public static final String EMPTY = "";
	public static final String SPACE = " ";


	/**
	 * 首字母大写的工具类
	 * @param str
	 * @return
	 */
	public static String FirstStrUpperCase(String str) {
		if(isBlank(str)){
			return "";
		}
		if(str.length() == 1){
			return str.substring(0).toUpperCase();
		}
		return str.substring(0,1).toUpperCase()+str.substring(1);
	}


	public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * @param str 字符串
     * @return
     * 是否为空串
     */
    public static boolean isNotEmpty(String str){
    	if(str == null || str.isEmpty()){
    		return false;
    	}
    	return true;
    }

    /**
     * @param str 字符串
     * @return
     * 是否为空串
     */
    public static boolean isEmpty(String str){
    	if(str == null || str.isEmpty()){
    		return true;
    	}
    	return false;
    }

    /**
     * @param str 字符串
     * @param spiltChar
     * @return
     */
    public static List<Long> spiltString(String str, String spiltChar){
    	List<Long> list = new ArrayList<>();
    	if(!StringUtils.isNotEmpty(str)){
    		return null;
    	}
    	String[] arr = str.split(spiltChar);
    	for(String s:arr){
    		list.add(Long.valueOf(s));
    	}
    	return list;
    }

    /**
	 * @param list 列表
	 * @param c 字符
	 * @return
	 */
	public static<T> String contactToString(List<T> list, String c){
		if(list ==null ||list.size()==0){
			return null;
		}
		StringBuffer stringBuffer = new StringBuffer("");
		for(int i=0;i<list.size();i++){
			if(i==(list.size()-1)){
				stringBuffer.append(list.get(i));
			}else{
				stringBuffer.append(list.get(i));
				stringBuffer.append(c);
			}
		}
		return stringBuffer.toString();
	}
    /*-----------------------------------

    笨方法：String s = "你要去除的字符串";

            1.去除空格：s = s.replace('\\s','');

            2.去除回车：s = s.replace('\n','');

    这样也可以把空格和回车去掉，其他也可以照这样做。

    注：\n 回车(\u000a)
    \t 水平制表符(\u0009)
    \s 空格(\u0008)
    \r 换行(\u000d)*/

	  public static String getExceptionMessage(Throwable e){
    	try {
    		StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String string = sw.toString();
            String replaceAll = string.replaceAll("\t","");
            return replaceAll;
		} catch (Exception e2) {
			return e2.toString();
		}
	}

	/**
	 * 连接成字符串
	 *
	 * @param obj
	 * @return
	 */
	public static String appendString(Object... obj) {
		StringBuilder sb = new StringBuilder();
		if (obj.length == 0) {
			return sb.toString();
		}
		for (int i = 0; i < obj.length; i++) {
			if (obj[i] != null) {
				sb.append(obj[i]);
			}
		}
		return sb.toString();
	}
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}
	public static boolean isBlank(String str) {
		int strLen;
		// 若字符串为null或则字符串为"",则判断为空，否则继续判断是否为空白字符串
		if (str != null && (strLen = str.length()) != 0) {
			// 遍历字符串中的字符，只有有一个字符不是空白字符，则判定该字符不为空
			for(int i = 0; i < strLen; ++i) {
				if (!Character.isWhitespace(str.charAt(i))) {
					return false;
				}
			}

			return true;
		} else {
			return true;
		}
	}
	//判断是否两位非空小数
	public static boolean isTwoDecimalNumber(String str) {
		Pattern pattern = Pattern.compile(NUMBER_REGEX);
		Matcher match = pattern.matcher(str);
		return match.matches();
	}

	//判断日期格式是否正确
	public static boolean isVaildDate(String str){
		if(isEmpty(str)){
			return false;
		}
		boolean returnFlag = true;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try{
			format.setLenient(false);
			format.parse(str);
		}catch(Exception e){
			returnFlag = false;
		}
		return  returnFlag;

	}

}
