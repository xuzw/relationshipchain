package com.github.xuzw.relationshipchain.model;

/**
 * @author 徐泽威 xuzewei_2012@126.com
 * @time 2017年3月20日 下午5:28:53
 */
public class RelationshipBuilder {
    private Relationship relationship = new Relationship();

    public RelationshipBuilder value(String value) {
        relationship.setValue(value);
        return this;
    }

    public RelationshipBuilder begin(String begin) {
        relationship.setBegin(begin);
        return this;
    }

    public RelationshipBuilder end(String end) {
        relationship.setEnd(end);
        return this;
    }

    public Relationship build() {
        return relationship;
    }
}
