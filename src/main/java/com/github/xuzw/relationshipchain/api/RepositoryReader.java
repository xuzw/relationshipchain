package com.github.xuzw.relationshipchain.api;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.xuzw.relationshipchain.api.RepositoryFileFormat.LineType;
import com.github.xuzw.relationshipchain.api.RepositoryFileFormat.Metadata;
import com.github.xuzw.relationshipchain.model.RelationshipChain;

/**
 * @author 徐泽威 xuzewei_2012@126.com
 * @time 2017年3月20日 下午6:07:05
 */
public class RepositoryReader {
    private String path;
    private FileReader reader;
    private BufferedReader bReader;

    public RepositoryReader(String path) throws FileNotFoundException {
        this.path = path;
        reader = new FileReader(path);
        bReader = new BufferedReader(reader);
    }

    public String getPath() {
        return path;
    }

    public RelationshipChain read() throws IOException, RepositoryFileFormatException {
        String line = bReader.readLine();
        if (line == null) {
            return null;
        }
        JSONObject json = JSONObject.parseObject(line);
        LineType lineType = LineType.parse(json.getString("t"));
        if (lineType == LineType.metadata_of_relationshipchain) {
            Metadata metadata = JSON.toJavaObject(json, Metadata.class);
            String nextLine = bReader.readLine();
            if (!isValidSign(metadata.getSign(), nextLine)) {
                throw new RepositoryFileFormatException("invalid sign");
            }
            return JSONObject.parseObject(nextLine, RelationshipChain.class);
        } else if (lineType == LineType.relationshipchain) {
            return JSON.toJavaObject(json, RelationshipChain.class);
        } else {
            return read();
        }
    }

    public void close() {
        IOUtils.closeQuietly(bReader);
        IOUtils.closeQuietly(reader);
    }

    private static boolean isValidSign(String sign, String line) {
        return sign.equals(DigestUtils.md5Hex(line));
    }
}
