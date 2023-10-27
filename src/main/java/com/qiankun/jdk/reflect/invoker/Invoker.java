package com.qiankun.jdk.reflect.invoker;

import java.lang.reflect.InvocationTargetException;

/**
 * 调用器接口
 */
public interface Invoker {
  Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException;

  Class<?> getType();
}
