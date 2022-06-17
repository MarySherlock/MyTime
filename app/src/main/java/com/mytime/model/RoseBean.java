package com.mytime.model;


public class RoseBean {

    private float count;
    private String ClassName;

    public RoseBean() {
    }

    public RoseBean(float count, String className) {
        this.count = count;
        ClassName = className;
    }

    public float getCount() {
        return count;
    }

    public void setCount(float count) {
        this.count = count;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }
}