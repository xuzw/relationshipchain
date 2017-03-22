package com.github.xuzw.relationshipchain;

import java.util.UUID;

import com.github.xuzw.relationshipchain.api.RepositoryWriter;
import com.github.xuzw.relationshipchain.model.ElementBuilder;
import com.github.xuzw.relationshipchain.model.RelationshipBuilder;
import com.github.xuzw.relationshipchain.model.RelationshipChainBuilder;

/**
 * @author 徐泽威 xuzewei_2012@126.com
 * @time 2017年3月20日 下午6:43:03
 */
public class WriterTest {
    public static void main(String[] args) throws Exception {
        String path = "/Users/xuzewei/tmp/test.repository";
        RepositoryWriter repositoryWriter = new RepositoryWriter(path);
        RelationshipChainBuilder chainBuilder = new RelationshipChainBuilder();
        chainBuilder.element(new ElementBuilder().value("A").build());
        chainBuilder.element(new ElementBuilder().value("B2").build());
        chainBuilder.element(new ElementBuilder().value("C2").build());
        chainBuilder.relationship(new RelationshipBuilder().begin("A").value("link").end("B2").build());
        chainBuilder.relationship(new RelationshipBuilder().begin("A").value("link").end("C2").build());
        repositoryWriter.append(chainBuilder.uuid(UUID.randomUUID().toString()).timestamp(System.currentTimeMillis()).build());
        repositoryWriter.close();
    }
}
