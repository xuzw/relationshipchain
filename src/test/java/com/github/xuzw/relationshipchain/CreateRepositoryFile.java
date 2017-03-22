package com.github.xuzw.relationshipchain;

import com.github.xuzw.relationshipchain.api.RepositoryFileFormat.Version;
import com.github.xuzw.relationshipchain.api.RepositoryWriter;

/**
 * @author 徐泽威 xuzewei_2012@126.com
 * @time 2017年3月20日 下午6:43:03
 */
public class CreateRepositoryFile {
    public static void main(String[] args) throws Exception {
        String path = "/Users/xuzewei/tmp/test.repository";
        RepositoryWriter repositoryWriter = new RepositoryWriter(path);
        Version version = new Version();
        version.setCode("perspective");
        version.setTimestamp(System.currentTimeMillis());
        repositoryWriter.append(version);
        repositoryWriter.close();
    }
}
