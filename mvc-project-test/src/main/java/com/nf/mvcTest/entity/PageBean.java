package com.nf.mvcTest.entity;

import java.util.List;

//分页查询的javaBean
public class PageBean<T> {

    //总记录数
    private int total;

    //当前页数
    private List<T> records;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    @Override
    public String toString() {
        return "PageBean{" +
                "total=" + total +
                ", records=" + records +
                '}';
    }
}
