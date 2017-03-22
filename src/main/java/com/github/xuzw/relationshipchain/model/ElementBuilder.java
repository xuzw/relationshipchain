package com.github.xuzw.relationshipchain.model;

/**
 * @author 徐泽威 xuzewei_2012@126.com
 * @time 2017年3月20日 下午5:25:57
 */
public class ElementBuilder {
    private Element element = new Element();

    public ElementBuilder value(String value) {
        element.setValue(value);
        return this;
    }

    public ElementBuilder tag(String tag) {
        element.setTag(tag);
        return this;
    }

    public Element build() {
        return element;
    }
}
