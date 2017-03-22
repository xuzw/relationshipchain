package com.github.xuzw.relationshipchain;

import com.alibaba.fastjson.JSONObject;
import com.github.xuzw.relationshipchain.api.RepositoryReader;

/**
 * @author 徐泽威 xuzewei_2012@126.com
 * @time 2017年3月20日 下午6:43:03
 */
public class ReaderTest {
    public static void main(String[] args) throws Exception {
        String path = "/Users/xuzewei/tmp/test.repository";
        RepositoryReader repositoryReader = new RepositoryReader(path);
        System.out.println(JSONObject.toJSONString(repositoryReader.read()));
        repositoryReader.close();
    }
}
