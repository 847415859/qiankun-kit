package com.qiankun.jdk.reflect;

import com.qiankun.jdk.reflect.invoker.Invoker;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

/**
 * @Description: 测试反射器
 * @Date : 2023/10/27 11:39
 * @Auther : tiankun
 */
public class TestReflector {

    @Test
    public void testReflector() throws InvocationTargetException, IllegalAccessException {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Reflector reflector = reflectorFactory.findForClass(Student.class);
        String findPropertyName1 = reflector.findPropertyName("name");
        String findPropertyName2 = reflector.findPropertyName("name1");
        System.out.println("findPropertyName1 = " + findPropertyName1);
        System.out.println("findPropertyName2 = " + findPropertyName2);
        // findPropertyName1 = name
        // findPropertyName2 = null

        boolean hasDefaultConstructor = reflector.hasDefaultConstructor();
        System.out.println("是否有默认的构造器 :"+hasDefaultConstructor);

        boolean hasGetter = reflector.hasGetter("name");
        System.out.println("是否有name属性的getter方法 :"+hasGetter);

        boolean hasSetter = reflector.hasSetter("hobby");
        System.out.println("是否有name属性的setter方法 :"+hasSetter);

        Invoker invoker = reflector.getGetInvoker("hobby");
        Student student = new Student();
        Object hobbyVal = invoker.invoke(student, null);
        System.out.println("hobbyVal = " + hobbyVal);
    }
}
