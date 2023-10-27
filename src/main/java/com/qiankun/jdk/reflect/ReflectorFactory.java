package com.qiankun.jdk.reflect;

/**
 * 反射工厂接口
 */
public interface ReflectorFactory {

  boolean isClassCacheEnabled();

  /**
   * 是否对开启 Reflector 解析缓存
   * @param classCacheEnabled
   */
  void setClassCacheEnabled(boolean classCacheEnabled);

  /**
   * 根据class 查询 Reflector
   * @param type
   * @return
   */
  Reflector findForClass(Class<?> type);
}