package com.qiankun.jdk.reflect;

/**
 * @Description:
 * @Date : 2023/10/27 11:40
 * @Auther : tiankun
 */
public abstract class People {

    Integer age;

    String name;

    String hobby = "唱跳Rap篮球";

    public People() {
    }

    public People(Integer age, String name) {
        this.age = age;
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
