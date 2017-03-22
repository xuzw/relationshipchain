package com.github.xuzw.relationshipchain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 徐泽威 xuzewei_2012@126.com
 * @time 2017年3月20日 下午5:30:41
 */
public class RelationshipChainBuilder {
    private List<Element> elements = new ArrayList<>();
    private List<Relationship> relationships = new ArrayList<>();
    private RelationshipChain chain = new RelationshipChain();

    public RelationshipChainBuilder element(Element element) {
        elements.add(element);
        return this;
    }

    public RelationshipChainBuilder relationship(Relationship relationship) {
        relationships.add(relationship);
        return this;
    }

    public RelationshipChainBuilder uuid(String uuid) {
        chain.setUuid(uuid);
        return this;
    }

    public RelationshipChainBuilder timestamp(long timestamp) {
        chain.setTimestamp(timestamp);
        return this;
    }

    public RelationshipChain build() {
        chain.setElements(elements);
        chain.setRelationships(relationships);
        return chain;
    }
}
