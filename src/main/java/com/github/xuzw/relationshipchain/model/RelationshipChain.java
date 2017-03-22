package com.github.xuzw.relationshipchain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * @author 徐泽威 xuzewei_2012@126.com
 * @time 2017年3月20日 下午5:18:21
 */
public class RelationshipChain {
    private List<Element> elements;
    private List<Relationship> relationships;
    private String uuid;
    private long timestamp;

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    public List<Relationship> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<Relationship> relationships) {
        this.relationships = relationships;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public TreeMap<String, Object> toTreeMap() {
        TreeMap<String, Object> treeMap = new TreeMap<>();
        List<TreeMap<String, Object>> elements = new ArrayList<>();
        for (Element element : this.elements) {
            elements.add(element.toTreeMap());
        }
        List<TreeMap<String, Object>> relationships = new ArrayList<>();
        for (Relationship relationship : this.relationships) {
            relationships.add(relationship.toTreeMap());
        }
        treeMap.put("elements", elements);
        treeMap.put("relationships", relationships);
        treeMap.put("uuid", uuid);
        treeMap.put("timestamp", new Long(timestamp));
        return treeMap;
    }
}
