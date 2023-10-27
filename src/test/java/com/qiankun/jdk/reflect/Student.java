package com.qiankun.jdk.reflect;

/**
 * @Description:
 * @Date : 2023/10/27 11:41
 * @Auther : tiankun
 */
public class Student extends People{

    String className;

    double score;



    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
