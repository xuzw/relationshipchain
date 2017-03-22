package com.github.xuzw.relationshipchain;

import com.alibaba.fastjson.JSON;
import com.github.xuzw.relationshipchain.api.SearchSpace;

/**
 * @author 徐泽威 xuzewei_2012@126.com
 * @time 2017年3月21日 下午12:03:53
 */
public class SearchSpaceTest {
    public static void main(String[] args) throws Exception {
        SearchSpace searchSpace = new SearchSpace("/Users/xuzewei/tmp/test.repository.graphdb/");
        // searchSpace.loadRepository("/Users/xuzewei/tmp/test.repository");
        System.out.println(JSON.toJSONString(searchSpace.search("xuzewei2017")));
        searchSpace.shutdown();
    }
}
