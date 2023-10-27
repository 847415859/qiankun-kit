package com.qiankun.jdk.reflect.invoker;

import java.lang.reflect.Field;

import com.qiankun.jdk.reflect.Reflector;

/**
 * get属性方法调用器
 */
public class GetFieldInvoker implements Invoker {
  private final Field field;

  public GetFieldInvoker(Field field) {
    this.field = field;
  }

  @Override
  public Object invoke(Object target, Object[] args) throws IllegalAccessException {
    try {
      return field.get(target);
    } catch (IllegalAccessException e) {
      if (Reflector.canControlMemberAccessible()) {
        field.setAccessible(true);
        return field.get(target);
      } else {
        throw e;
      }
    }
  }

  @Override
  public Class<?> getType() {
    return field.getType();
  }
}
