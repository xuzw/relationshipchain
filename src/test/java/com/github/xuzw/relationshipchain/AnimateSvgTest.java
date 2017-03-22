package com.github.xuzw.relationshipchain;

import java.util.Arrays;

import com.github.xuzw.relationshipchain.api.AnimateSvg;
import com.github.xuzw.relationshipchain.api.RepositoryReader;
import com.github.xuzw.relationshipchain.model.RelationshipChain;

/**
 * @author 徐泽威 xuzewei_2012@126.com
 * @time 2017年3月22日 下午8:34:19
 */
public class AnimateSvgTest {
    public static void main(String[] args) throws Exception {
        String path = "/Users/xuzewei/tmp/test.repository";
        RepositoryReader repositoryReader = new RepositoryReader(path);
        RelationshipChain chain = repositoryReader.read();
        RelationshipChain chain1 = repositoryReader.read();
        RelationshipChain chain2 = repositoryReader.read();
        repositoryReader.close();
        AnimateSvg animateSvg = new AnimateSvg(Arrays.asList(chain, chain1, chain2));
        System.out.println(animateSvg.toXml());
    }
}
