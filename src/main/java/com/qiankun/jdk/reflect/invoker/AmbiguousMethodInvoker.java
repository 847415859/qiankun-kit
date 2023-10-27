package com.qiankun.jdk.reflect.invoker;

import com.qiankun.jdk.reflect.exception.ReflectionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 不明确方法调用器
 */
public class AmbiguousMethodInvoker extends MethodInvoker {
  private final String exceptionMessage;

  public AmbiguousMethodInvoker(Method method, String exceptionMessage) {
    super(method);
    this.exceptionMessage = exceptionMessage;
  }

  @Override
  public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
    throw new ReflectionException(exceptionMessage);
  }
}
