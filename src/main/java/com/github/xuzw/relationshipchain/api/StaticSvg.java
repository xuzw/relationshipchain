package com.github.xuzw.relationshipchain.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.github.xuzw.relationshipchain.api.utils.xml.TagBuilder;
import com.github.xuzw.relationshipchain.api.utils.xml.XML;
import com.github.xuzw.relationshipchain.model.Element;
import com.github.xuzw.relationshipchain.model.Relationship;
import com.github.xuzw.relationshipchain.model.RelationshipChain;

/**
 * @author 徐泽威 xuzewei_2012@126.com
 * @time 2017年3月22日 下午8:12:15
 */
public class StaticSvg {
    /**
     * 宽高比例
     */
    public static final double aspectRatio = 0.75;
    /**
     * 宽度
     */
    public static final int width = 8 * 100;
    /**
     * 高度
     */
    public static final int height = (int) (width * aspectRatio);
    /**
     * 总面积
     */
    public static final int totalArea = width * height;
    /**
     * 留白比例
     */
    public static final double blankLeavingRatio = 0.9;
    /**
     * 元素总面积
     */
    public static final double totalElementArea = totalArea * (1 - blankLeavingRatio);
    /**
     * 元素等级生长系数，相对于元素半径
     */
    public static final double elementLevelGrowthRatio = 0.25;
    /**
     * 最小元素间距系数，相对于元素半径
     */
    public static final double minElementSpacingRatio = 1;
    /**
     * 最小元素半径
     */
    private final double minElementRadius;
    /**
     * 元素,key=value
     */
    private final Map<String, ElementWrapper> elements = new HashMap<>();
    /**
     * 关系,key=chainId
     */
    private final Map<String, RelationshipChain> chains = new HashMap<>();
    private List<LayoutStep> layoutSteps = new ArrayList<>();

    public StaticSvg(List<RelationshipChain> chains) {
        for (RelationshipChain chain : chains) {
            for (Element e : chain.getElements()) {
                if (elements.containsKey(e.getValue())) {
                    elements.get(e.getValue()).levelUp();
                } else {
                    elements.put(e.getValue(), new ElementWrapper(e.getValue()));
                }
            }
            this.chains.put(chain.getUuid(), chain);
        }
        double x = 0;
        for (String key : elements.keySet()) {
            double radiusRatio = _calcElementRadiusRatio(elements.get(key).getLevel());
            x += Math.PI * Math.pow(radiusRatio, 2);
        }
        minElementRadius = Math.sqrt(totalElementArea / x);
        for (String key : elements.keySet()) {
            ElementWrapper eWrapper = elements.get(key);
            eWrapper.setR((int) (_calcElementRadiusRatio(eWrapper.getLevel()) * minElementRadius));
            _layout(key, eWrapper);
        }
    }

    public String toXml() {
        HarmonyColor harmonyColor = new HarmonyColor();
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        TagBuilder builder = XML.build("svg").attr("width", String.valueOf(width)).attr("height", String.valueOf(height)).attr("xmlns", "http://www.w3.org/2000/svg").attr("xmlns:xlink", "http://www.w3.org/1999/xlink").attr("version", "1.0");
        for (String key : elements.keySet()) {
            ElementWrapper e = elements.get(key);
            builder.child("circle").attr("id", e.getValue()).attr("r", String.valueOf(e.getR())).attr("cx", String.valueOf(e.getCx())).attr("cy", String.valueOf(e.getCy())).attr("fill", harmonyColor.random().toString());
        }
        for (String chainId : chains.keySet()) {
            String chainColor = harmonyColor.random().toString();
            RelationshipChain chain = chains.get(chainId);
            for (Relationship r : chain.getRelationships()) {
                String aTag = r.getBegin();
                String bTag = r.getEnd();
                ElementWrapper a = elements.get(getElement(aTag, chain).getValue());
                ElementWrapper b = elements.get(getElement(bTag, chain).getValue());
                builder.child("line").attr("x1", String.valueOf(a.getCx())).attr("y1", String.valueOf(a.getCy())).attr("x2", String.valueOf(b.getCx())).attr("y2", String.valueOf(b.getCy())).attr("stroke", chainColor);
            }
        }
        sb.append(builder.print(XML.FOR_HUMAN));
        return sb.toString();
    }

    public static Element getElement(String tagOrValue, RelationshipChain chain) {
        for (Element e : chain.getElements()) {
            if (e.getValue().equals(tagOrValue) || (e.getTag() != null && e.getTag().equals(tagOrValue))) {
                return e;
            }
        }
        return null;
    }

    public List<LayoutStep> getLayoutSteps() {
        return layoutSteps;
    }

    private void _layout(String elementKey, ElementWrapper elementWrapper) {
        int r = elementWrapper.getR();
        int xRandom = r + new Random().nextInt(width - 2 * r);
        int yRandom = r + new Random().nextInt(height - 2 * r);
        boolean valid = true;
        for (String key : elements.keySet()) {
            ElementWrapper anther = elements.get(key);
            if (key.equals(elementKey) || (anther.getCx() == 0 && anther.getCy() == 0)) {
                continue;
            }
            int antherCx = anther.getCx();
            int antherCy = anther.getCy();
            int antherR = anther.getR();
            double distance = _calcDistance(xRandom, yRandom, antherCx, antherCy);
            if (distance - r - antherR < minElementSpacingRatio * minElementRadius) {
                valid = false;
            }
            // ----
            LayoutStep layoutStep = new LayoutStep();
            layoutStep.setElementKey(elementKey);
            layoutStep.setR(r);
            layoutStep.setxRandom(xRandom);
            layoutStep.setyRandom(yRandom);
            layoutStep.setAnotherElementKey(key);
            layoutStep.setAnotherElementR(antherR);
            layoutStep.setAnotherElementCx(antherCx);
            layoutStep.setAnotherElementCy(antherCy);
            layoutStep.setDistance(distance);
            layoutStep.setValid(valid);
            layoutSteps.add(layoutStep);
            // ----
        }
        if (valid) {
            elementWrapper.setCx(xRandom);
            elementWrapper.setCy(yRandom);
        } else {
            _layout(elementKey, elementWrapper);
        }
    }

    private static double _calcDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * 相对于最小元素半径
     * 
     * @param level
     * @return
     */
    private double _calcElementRadiusRatio(int level) {
        return 1 + (level - 1) * elementLevelGrowthRatio;
    }

    public Map<String, ElementWrapper> getElements() {
        return elements;
    }

    public Map<String, RelationshipChain> getChains() {
        return chains;
    }

    public static class LayoutStep {
        private String elementKey;
        private int r;
        private int xRandom;
        private int yRandom;
        private boolean valid;
        private String anotherElementKey;
        private int anotherElementCx;
        private int anotherElementCy;
        private int anotherElementR;
        private double distance;

        public String getElementKey() {
            return elementKey;
        }

        public void setElementKey(String elementKey) {
            this.elementKey = elementKey;
        }

        public int getR() {
            return r;
        }

        public void setR(int r) {
            this.r = r;
        }

        public int getxRandom() {
            return xRandom;
        }

        public void setxRandom(int xRandom) {
            this.xRandom = xRandom;
        }

        public int getyRandom() {
            return yRandom;
        }

        public void setyRandom(int yRandom) {
            this.yRandom = yRandom;
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getAnotherElementKey() {
            return anotherElementKey;
        }

        public void setAnotherElementKey(String anotherElementKey) {
            this.anotherElementKey = anotherElementKey;
        }

        public int getAnotherElementCx() {
            return anotherElementCx;
        }

        public void setAnotherElementCx(int anotherElementCx) {
            this.anotherElementCx = anotherElementCx;
        }

        public int getAnotherElementCy() {
            return anotherElementCy;
        }

        public void setAnotherElementCy(int anotherElementCy) {
            this.anotherElementCy = anotherElementCy;
        }

        public int getAnotherElementR() {
            return anotherElementR;
        }

        public void setAnotherElementR(int anotherElementR) {
            this.anotherElementR = anotherElementR;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }
    }

    public static class ElementWrapper {
        private int r;
        private int cx;
        private int cy;
        /**
         * 从1开始
         */
        private int level = 1;
        private String value;

        public ElementWrapper(String value) {
            this.value = value;
        }

        public void levelUp() {
            this.level++;
        }

        public int getR() {
            return r;
        }

        public void setR(int r) {
            this.r = r;
        }

        public int getCx() {
            return cx;
        }

        public void setCx(int cx) {
            this.cx = cx;
        }

        public int getCy() {
            return cy;
        }

        public void setCy(int cy) {
            this.cy = cy;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class HarmonyColor {
        private List<Integer> points = new ArrayList<>();

        public HarmonyColor() {
            points.add(42);
            points.add(84);
            complement();
        }

        public HarmonyColor(Set<Integer> points) {
            this.points.addAll(points);
            complement();
        }

        private void complement() {
            List<Integer> cpoints = new ArrayList<>();
            for (Integer p : points) {
                cpoints.add(255 - p);
            }
            for (Integer p : cpoints) {
                if (!points.contains(p)) {
                    points.add(p);
                }
            }
        }

        public Color random() {
            int size = points.size();
            return new Color(points.get(new Random().nextInt(size)), points.get(new Random().nextInt(size)), points.get(new Random().nextInt(size)));
        }
    }

    public static class Color {
        private static final String format = "rgb(%s,%s,%s)";
        private int r;
        private int g;
        private int b;

        public Color(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        @Override
        public String toString() {
            return String.format(format, r, g, b);
        }
    }
}
