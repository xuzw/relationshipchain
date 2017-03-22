package com.github.xuzw.relationshipchain.model;

import java.util.TreeMap;

/**
 * @author 徐泽威 xuzewei_2012@126.com
 * @time 2017年3月20日 下午5:14:23
 */
public class Element {
    private String value;
    private String tag;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public TreeMap<String, Object> toTreeMap() {
        TreeMap<String, Object> treeMap = new TreeMap<>();
        treeMap.put("value", value);
        treeMap.put("tag", tag);
        return treeMap;
    }
}
