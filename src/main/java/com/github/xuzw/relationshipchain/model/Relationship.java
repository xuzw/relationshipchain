package com.github.xuzw.relationshipchain.model;

import java.util.TreeMap;

/**
 * @author 徐泽威 xuzewei_2012@126.com
 * @time 2017年3月20日 下午5:17:01
 */
public class Relationship {
    private String value;
    private String begin;
    private String end;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public TreeMap<String, Object> toTreeMap() {
        TreeMap<String, Object> treeMap = new TreeMap<>();
        treeMap.put("value", value);
        treeMap.put("begin", begin);
        treeMap.put("end", end);
        return treeMap;
    }
}
