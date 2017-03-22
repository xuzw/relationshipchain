package com.github.xuzw.relationshipchain.api;

import java.io.IOException;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;

import com.alibaba.fastjson.JSON;
import com.github.xuzw.relationshipchain.api.RepositoryFileFormat.LineType;
import com.github.xuzw.relationshipchain.api.RepositoryFileFormat.Metadata;
import com.github.xuzw.relationshipchain.api.RepositoryFileFormat.Version;
import com.github.xuzw.relationshipchain.model.RelationshipChain;

/**
 * @author 徐泽威 xuzewei_2012@126.com
 * @time 2017年3月20日 下午5:35:55
 */
public class RepositoryWriter {
    private String path;
    private FileWriterWithEncoding writer;

    public RepositoryWriter(String path) throws IOException {
        this.path = path;
        writer = new FileWriterWithEncoding(path, RepositoryFileFormat.encoding, true);
    }

    public String getPath() {
        return path;
    }

    public void append(RelationshipChain chain) throws IOException {
        String chainLine = toJsonLine(chain);
        String metadataLine = toJsonLine(buildMetadata(chainLine));
        writer.append(metadataLine + RepositoryFileFormat.line_separator + chainLine + RepositoryFileFormat.line_separator);
    }

    public void append(Version version) throws IOException {
        writer.append(toJsonLine(version) + RepositoryFileFormat.line_separator);
    }

    public void close() {
        IOUtils.closeQuietly(writer);
    }

    private static String toJsonLine(RelationshipChain chain) {
        TreeMap<String, Object> treeMap = chain.toTreeMap();
        treeMap.put("t", LineType.relationshipchain.getValue());
        return JSON.toJSONString(treeMap);
    }

    private static String toJsonLine(Metadata metadata) {
        TreeMap<String, Object> treeMap = metadata.toTreeMap();
        treeMap.put("t", LineType.metadata_of_relationshipchain.getValue());
        return JSON.toJSONString(treeMap);
    }

    private static String toJsonLine(Version version) {
        TreeMap<String, Object> treeMap = version.toTreeMap();
        treeMap.put("t", LineType.version.getValue());
        return JSON.toJSONString(treeMap);
    }

    private static Metadata buildMetadata(String line) {
        Metadata metadata = new Metadata();
        metadata.setSign(sign(line));
        return metadata;
    }

    private static String sign(String line) {
        return DigestUtils.md5Hex(line);
    }
}
