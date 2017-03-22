package com.github.xuzw.relationshipchain.api;

import java.util.TreeMap;

/**
 * @author 徐泽威 xuzewei_2012@126.com
 * @time 2017年3月20日 下午6:19:20
 */
public class RepositoryFileFormat {
    public static final String line_separator = "\n";
    public static final String encoding = "utf-8";

    public static class Version {
        private String code;
        private long timestamp;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public TreeMap<String, Object> toTreeMap() {
            TreeMap<String, Object> treeMap = new TreeMap<>();
            treeMap.put("code", code);
            treeMap.put("timestamp", new Long(timestamp));
            return treeMap;
        }
    }

    public static class Metadata {
        private String sign;

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public TreeMap<String, Object> toTreeMap() {
            TreeMap<String, Object> treeMap = new TreeMap<>();
            treeMap.put("sign", sign);
            return treeMap;
        }
    }

    public static enum LineType {
        relationshipchain("c"), version("version"), metadata_of_relationshipchain("mc");

        private String value;

        private LineType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static LineType parse(String value) {
            for (LineType t : values()) {
                if (t.value.equals(value)) {
                    return t;
                }
            }
            return null;
        }
    }
}
