//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.qiankun.excel;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EvalAnalysisUtils {
    private static final Map<Class<?>, Map<String, Method>> METHOD_CACHE_MAP = new ConcurrentHashMap();
    private static final Map<Class<?>, Map<String, Field>> FIELD_CACHE_MAP = new ConcurrentHashMap();
    private static final Map<Class<?>, Map<String, Method>> GET_METHOD_CACHE_MAP = new ConcurrentHashMap();
    private static final String _L = "[";
    private static final String _R = "]";
    private static final Pattern EL_PATTERN = Pattern.compile("\\$\\{([^}]*)\\}");
    private static final Pattern EL_UNIQUE_PATTERN = Pattern.compile("^\\$\\{([^}]*)\\}$");
    private static final Pattern LR_PATTERN = Pattern.compile("([^\\[\\]]*)\\[([^\\[\\]]*)\\]");

    public EvalAnalysisUtils() {
    }

    public static void main(String[] args) {
        Map<String, Object> object = new HashMap();
        object.put("age", 18);
        object.put("var", "name");
        object.put("idx", 1);
        Map<String, Object> user = new HashMap();
        user.put("name", "veasion");
        user.put("var", "name");
        user.put("index", 0);
        object.put("list", new Object[]{user, new Integer("100")});
        // object.put("common", (key) -> "common_" + key);
        System.out.println("变量解析：");
        System.out.println(parse("name", user));
        System.out.println(parse("list[0]", object));
        System.out.println(parse("list[0].name", object));
        System.out.println(parse("$[0]", new String[]{"index0"}));
        System.out.println("表达式：");
        System.out.println(eval("${abc|default}", object));
        System.out.println(eval("${list[0]}", object));
        System.out.println(eval("${list[idx].value}", object));
        System.out.println(eval("${common.test}", object));
        System.out.println(eval("${ $[0] }-${ $[1] }", new String[]{"name1", "name2"}));
        System.out.println(eval("name: ${list[0][list[list[0].index].var]}", object));
        System.out.println(eval("name1: ${list[0][var]}, name2: ${list[0]['name']}", object));
    }


    public static Object eval(String str, Object object) {
        if (str == null) {
            return null;
        } else if ("".equals(str.trim())) {
            return str;
        } else {
            Matcher matcher;
            if (str.startsWith("${") && str.endsWith("}")) {
                matcher = EL_UNIQUE_PATTERN.matcher(str);
                if (matcher.find()) {
                    return parse(matcher.group(1), object);
                }
            }

            int index = 0;
            StringBuilder sb = new StringBuilder();

            for(matcher = EL_PATTERN.matcher(str); matcher.find(); index = matcher.end()) {
                sb.append(str.substring(index, matcher.start()));
                sb.append(parse(matcher.group(1), object));
            }

            sb.append(str.substring(index));
            return sb.toString();
        }
    }

    public static Object parse(String text, Object object) {
        if (text == null) {
            return null;
        } else if ("".equals(text.trim())) {
            return text;
        } else {
            String str = text.trim();
            Object defaultValue = null;
            int defIndex = str.lastIndexOf("|");
            if (defIndex > 0) {
                defaultValue = str.substring(defIndex + 1).trim();
                str = str.substring(0, defIndex);
            }

            Object result;
            if (hasBrackets(str)) {
                result = parseGroup(SplitGroupUtils.group(str, "[", "]", true), object);
            } else {
                result = parseConsecutive(object, str);
            }

            return result != null ? result : defaultValue;
        }
    }

    private static Object parseGroup(List<SplitGroupUtils.Group> groupList, Object object) {
        StringBuilder sb = new StringBuilder();
        Iterator var4 = groupList.iterator();

        while(true) {
            while(var4.hasNext()) {
                SplitGroupUtils.Group g = (SplitGroupUtils.Group)var4.next();
                if (g.getChildren() != null && g.getChildren().size() > 0) {
                    Object result = parseGroup(g.getChildren(), object);
                    if (result == null) {
                        throw new RuntimeException(String.format("%s is null", g.getContext()));
                    }

                    sb.append("[");
                    if (isNumber(result)) {
                        sb.append(result);
                    } else {
                        sb.append("'").append(result).append("'");
                    }

                    sb.append("]");
                } else {
                    sb.append(g.getValue());
                }
            }

            return parseConsecutive(object, sb.toString());
        }
    }

    private static Object parseConsecutive(Object object, String str) {
        Object result = object;
        String[] split = str.trim().split("\\.");
        String[] var4 = split;
        int var5 = split.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            String key = var4[var6];
            if (hasBrackets(key)) {
                result = parseBrackets(object, result, key);
            } else {
                result = parseObject(result, key);
            }

            if (result == null) {
                break;
            }
        }

        return result;
    }

    private static Object parseBrackets(Object object, Object result, String text) {
        Matcher matcher = LR_PATTERN.matcher(text);

        while(matcher.find()) {
            String group = matcher.group();
            String groupKey = matcher.group(1).trim();
            text = matcher.group(2);
            if (!"".equals(groupKey) && (!"$".equals(groupKey) || !isArray(result) || !isNumber(text))) {
                result = parseObject(result, groupKey);
            }

            if (isNumber(text)) {
                result = parseArray(result, group, Integer.parseInt(text));
            } else if (text.startsWith("'") && text.endsWith("'")) {
                result = parseObject(result, text.substring(1, text.length() - 1));
            } else {
                Object varValue = parseObject(object, text);
                if (varValue == null) {
                    throw new RuntimeException(String.format("%s 变量不存在 => %s", group, text));
                }

                String var = String.valueOf(varValue);
                if (isNumber(var) && isArray(result)) {
                    result = parseArray(result, group, Integer.parseInt(var));
                } else {
                    result = parseObject(result, var);
                }
            }

            if (result == null) {
                break;
            }
        }

        return result;
    }

    private static Object parseArray(Object object, String text, int index) {
        if (object != null) {
            try {
                if (object instanceof Collection) {
                    return ((Collection)object).toArray()[index];
                } else if (object instanceof Object[]) {
                    Object[] array = (Object[])((Object[])object);
                    return array[index];
                } else if (object.getClass().isArray()) {
                    return Array.get(object, index);
                } else {
                    throw new RuntimeException(String.format("%s (%s) 不是一个 array 类型 => %s", object, object.getClass().getName(), text));
                }
            } catch (ArrayIndexOutOfBoundsException var4) {
                throw new RuntimeException(String.format("%s 数组越界 => %s", object, text));
            }
        } else {
            return null;
        }
    }

    private static Object parseObject(Object object, String key) {
        if (object instanceof Map) {
            return ((Map)object).get(key);
        } else if (object instanceof Function) {
            return ((Function)object).apply(key);
        } else {
            try {
                return reflect(object, key);
            } catch (Exception var3) {
                return null;
            }
        }
    }

    private static boolean hasBrackets(String text) {
        return text != null && text.contains("[") && text.contains("]");
    }

    private static boolean isNumber(Object object) {
        return object != null && object.toString().matches("\\d+");
    }

    private static boolean isArray(Object object) {
        return object != null && (object instanceof Collection || object.getClass().isArray());
    }

    private static Object reflect(Object object, String key) throws InvocationTargetException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Method methodCache = (Method)getCache(METHOD_CACHE_MAP, clazz, key);
        if (methodCache != null) {
            return methodCache.invoke(object);
        } else {
            Field fieldCache = (Field)getCache(FIELD_CACHE_MAP, clazz, key);
            if (fieldCache != null) {
                return fieldCache.get(object);
            } else {
                Method getMethodCache = (Method)getCache(GET_METHOD_CACHE_MAP, clazz, key);
                if (getMethodCache != null) {
                    return getMethodCache.invoke(object, key);
                } else {
                    Method getMethod = null;
                    Method[] methods = clazz.getDeclaredMethods();
                    Method[] var8 = methods;
                    int var9 = methods.length;

                    int var10;
                    for(var10 = 0; var10 < var9; ++var10) {
                        Method method = var8[var10];
                        String methodName = method.getName();
                        if (Modifier.isPublic(method.getModifiers())) {
                            if (method.getParameterCount() == 0 && methodName.equalsIgnoreCase("get" + key)) {
                                putCache(METHOD_CACHE_MAP, clazz, key, method);
                                return method.invoke(object);
                            }

                            if ("get".equals(methodName) && method.getParameterCount() == 1 && method.getParameterTypes()[0].isAssignableFrom(key.getClass())) {
                                getMethod = method;
                            }
                        }
                    }

                    Field[] fields = clazz.getDeclaredFields();
                    Field[] var14 = fields;
                    var10 = fields.length;

                    for(int var15 = 0; var15 < var10; ++var15) {
                        Field field = var14[var15];
                        field.setAccessible(true);
                        if (field.getName().equals(key)) {
                            putCache(FIELD_CACHE_MAP, clazz, key, field);
                            return field.get(object);
                        }
                    }

                    if (getMethod != null) {
                        putCache(GET_METHOD_CACHE_MAP, clazz, key, getMethod);
                        return getMethod.invoke(object, key);
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    private static <T> T getCache(Map<Class<?>, Map<String, T>> cacheMap, Class<?> clazz, String key) {
        Map<String, T> clazzMap = (Map)cacheMap.get(clazz);
        return clazzMap != null ? clazzMap.get(key) : null;
    }

    private static <T> void putCache(Map<Class<?>, Map<String, T>> cacheMap, Class<?> clazz, String key, T value) {
        cacheMap.compute(clazz, (k, v) -> {
            if (v == null) {
                v = new ConcurrentHashMap();
            }

            ((Map)v).put(key, value);
            return (Map)v;
        });
    }
}
