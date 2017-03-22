package com.github.xuzw.relationshipchain.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.github.xuzw.relationshipchain.api.StaticSvg.ElementWrapper;
import com.github.xuzw.relationshipchain.api.StaticSvg.HarmonyColor;
import com.github.xuzw.relationshipchain.api.utils.xml.TagBuilder;
import com.github.xuzw.relationshipchain.api.utils.xml.XML;
import com.github.xuzw.relationshipchain.model.Relationship;
import com.github.xuzw.relationshipchain.model.RelationshipChain;

/**
 * @author 徐泽威 xuzewei_2012@126.com
 * @time 2017年3月22日 下午8:13:18
 */
public class AnimateSvg {
    public static final int frames = 10;
    public static final int dur = 40;
    public static final String repeat_count = "indefinite";
    public static final String key_splines_constant_speed = "0 0 1 1";
    public static final String key_splines_speed_up = "0.5 0 1 1";
    public static final String key_splines_speed_down = "0 0 0.5 1";
    public static final String key_splines_speed_up_and_down = "0.5 0 0.5 1";
    private List<StaticSvg> staticSvgs = new ArrayList<>();

    public AnimateSvg(List<RelationshipChain> chains) {
        for (int i = 0; i < frames; i++) {
            staticSvgs.add(new StaticSvg(chains));
        }
    }

    public String toXml() {
        HarmonyColor harmonyColor = new HarmonyColor();
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        TagBuilder builder = XML.build("svg").attr("width", String.valueOf(StaticSvg.width)).attr("height", String.valueOf(StaticSvg.height)).attr("xmlns", "http://www.w3.org/2000/svg").attr("xmlns:xlink", "http://www.w3.org/1999/xlink").attr("version", "1.0");
        StaticSvg first = staticSvgs.get(0);
        for (String key : first.getElements().keySet()) {
            ElementWrapper e = first.getElements().get(key);
            builder.child("circle").attr("id", e.getValue()).attr("r", String.valueOf(e.getR())).attr("cx", String.valueOf(e.getCx())).attr("cy", String.valueOf(e.getCy())).attr("fill", harmonyColor.random().toString());
        }
        for (String chainId : first.getChains().keySet()) {
            String chainColor = harmonyColor.random().toString();
            RelationshipChain chain = first.getChains().get(chainId);
            for (int i = 0; i < chain.getRelationships().size(); i++) {
                Relationship r = chain.getRelationships().get(i);
                String aTag = r.getBegin();
                String bTag = r.getEnd();
                ElementWrapper a = first.getElements().get(StaticSvg.getElement(aTag, chain).getValue());
                ElementWrapper b = first.getElements().get(StaticSvg.getElement(bTag, chain).getValue());
                builder.child("line").attr("id", String.format("r%s-%s", i, chainId)).attr("x1", String.valueOf(a.getCx())).attr("y1", String.valueOf(a.getCy())).attr("x2", String.valueOf(b.getCx())).attr("y2", String.valueOf(b.getCy())).attr("stroke", chainColor);
            }
        }
        EndToEndSplineAnimates endToEndSplineAnimates = new EndToEndSplineAnimates();
        for (int i = 0; i < staticSvgs.size(); i++) {
            StaticSvg item = staticSvgs.get(i);
            for (String key : item.getElements().keySet()) {
                ElementWrapper e = item.getElements().get(key);
                endToEndSplineAnimates.append("#" + e.getValue(), "cx", String.valueOf(e.getCx()), key_splines_speed_up);
                endToEndSplineAnimates.append("#" + e.getValue(), "cy", String.valueOf(e.getCy()), key_splines_speed_up);
            }
            for (String chainId : item.getChains().keySet()) {
                RelationshipChain chain = item.getChains().get(chainId);
                for (int j = 0; j < chain.getRelationships().size(); j++) {
                    Relationship r = chain.getRelationships().get(j);
                    String aValue = StaticSvg.getElement(r.getBegin(), chain).getValue();
                    String bValue = StaticSvg.getElement(r.getEnd(), chain).getValue();
                    ElementWrapper a = item.getElements().get(aValue);
                    ElementWrapper b = item.getElements().get(bValue);
                    endToEndSplineAnimates.append("#" + String.format("r%s-%s", j, chainId), "x1", String.valueOf(a.getCx()), key_splines_speed_up);
                    endToEndSplineAnimates.append("#" + String.format("r%s-%s", j, chainId), "y1", String.valueOf(a.getCy()), key_splines_speed_up);
                    endToEndSplineAnimates.append("#" + String.format("r%s-%s", j, chainId), "x2", String.valueOf(b.getCx()), key_splines_speed_up);
                    endToEndSplineAnimates.append("#" + String.format("r%s-%s", j, chainId), "y2", String.valueOf(b.getCy()), key_splines_speed_up);
                }
            }
        }
        for (EndToEndSplineAnimate i : endToEndSplineAnimates.getAll()) {
            builder.child("animate").attr("xlink:href", i.getXlinkHref()).attr("attributeName", i.getAttributeName()).attr("values", i.getValues()).attr("calcMode", "spline").attr("keySplines", i.getKeySplines()).attr("dur", String.valueOf(dur)).attr("repeatCount", repeat_count);
        }
        sb.append(builder.print(XML.FOR_HUMAN));
        return sb.toString();
    }

    public static class EndToEndSplineAnimates {
        private List<EndToEndSplineAnimate> endToEndSplineAnimates = new ArrayList<>();

        public List<EndToEndSplineAnimate> getAll() {
            return endToEndSplineAnimates;
        }

        public EndToEndSplineAnimate get(String xlinkHref, String attributeName) {
            for (EndToEndSplineAnimate i : endToEndSplineAnimates) {
                if (i.getXlinkHref().equals(xlinkHref) && i.getAttributeName().equals(attributeName)) {
                    return i;
                }
            }
            endToEndSplineAnimates.add(new EndToEndSplineAnimate(xlinkHref, attributeName));
            return get(xlinkHref, attributeName);
        }

        public void append(String xlinkHref, String attributeName, String beginValue, String keySpline) {
            get(xlinkHref, attributeName).append(beginValue, keySpline);
        }
    }

    public static class EndToEndSplineAnimate {
        private String xlinkHref;
        private String attributeName;
        private List<String> values = new ArrayList<>();
        private List<String> keySplines = new ArrayList<>();

        public EndToEndSplineAnimate(String xlinkHref, String attributeName) {
            this.xlinkHref = xlinkHref;
            this.attributeName = attributeName;
        }

        public String getXlinkHref() {
            return xlinkHref;
        }

        public String getAttributeName() {
            return attributeName;
        }

        public String getValues() {
            List<String> newValues = new ArrayList<>(values);
            newValues.add(values.get(0));
            return StringUtils.join(newValues, ";");
        }

        public String getKeySplines() {
            return StringUtils.join(keySplines, ";");
        }

        public void append(String value, String keySpline) {
            this.values.add(value);
            this.keySplines.add(keySpline);
        }
    }
}
