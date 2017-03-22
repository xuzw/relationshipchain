package com.github.xuzw.relationshipchain.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import com.github.xuzw.relationshipchain.model.Element;
import com.github.xuzw.relationshipchain.model.ElementBuilder;
import com.github.xuzw.relationshipchain.model.Relationship;
import com.github.xuzw.relationshipchain.model.RelationshipBuilder;
import com.github.xuzw.relationshipchain.model.RelationshipChain;
import com.github.xuzw.relationshipchain.model.RelationshipChainBuilder;

/**
 * @author 徐泽威 xuzewei_2012@126.com
 * @time 2017年3月21日 上午12:08:20
 */
public class SearchSpace {
    public static final Label label_element = Label.label("element");
    public static final String node_prop_key_value = "value";
    public static final String relationship_prop_key_atag = "atag";
    public static final String relationship_prop_key_btag = "btag";
    public static final String relationship_prop_key_chain = "chain";
    private String workPath;
    private GraphDatabaseService graphDb;

    public SearchSpace(String workPath) {
        this.workPath = workPath;
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File(workPath));
    }

    public String getWorkPath() {
        return workPath;
    }

    public GraphDatabaseService getGraphDb() {
        return graphDb;
    }

    private boolean _existRelationshipChain(RelationshipChain chain) {
        return null != graphDb.findNode(Label.label(chain.getUuid()), node_prop_key_value, chain.getElements().get(0).getValue());
    }

    private Node _findNode(Element element) {
        return _findNode(element.getValue());
    }

    private Node _findNode(String value) {
        return graphDb.findNode(label_element, node_prop_key_value, value);
    }

    public List<RelationshipChain> search(String string) {
        Map<String, RelationshipChain> map = new HashMap<>();
        String[] keywords = string.trim().split("\\s+");
        try (Transaction tx = graphDb.beginTx()) {
            Node node = _findNode(keywords[0]);
            for (org.neo4j.graphdb.Relationship r : node.getRelationships()) {
                String chainId = (String) r.getProperty(relationship_prop_key_chain);
                map.put(chainId, _findRelationshipChain(chainId));
            }
            tx.success();
        }
        return new ArrayList<>(map.values());
    }

    private RelationshipChain _findRelationshipChain(String chainId) {
        RelationshipChainBuilder builder = new RelationshipChainBuilder().uuid(chainId);
        ResourceIterator<Node> iterator = graphDb.findNodes(Label.label(chainId));
        while (iterator.hasNext()) {
            Node node = iterator.next();
            for (org.neo4j.graphdb.Relationship r : node.getRelationships(Direction.OUTGOING)) {
                String begin = (String) r.getProperty(relationship_prop_key_atag);
                String end = (String) r.getProperty(relationship_prop_key_btag);
                Element a = new ElementBuilder().tag(begin).value((String) node.getProperty(node_prop_key_value)).build();
                Element b = new ElementBuilder().tag(end).value((String) r.getOtherNode(node).getProperty(node_prop_key_value)).build();
                Relationship relationship = new RelationshipBuilder().begin(begin).end(end).value(r.getType().name()).build();
                builder.element(a).element(b).relationship(relationship);
            }
        }
        return builder.build();
    }

    public void loadRelationshipChain(RelationshipChain chain) {
        try (Transaction tx = graphDb.beginTx()) {
            if (_existRelationshipChain(chain)) {
                return;
            }
            Map<String, Node> map = new HashMap<>();
            for (Element element : chain.getElements()) {
                Node node = _findNode(element);
                if (node == null) {
                    node = graphDb.createNode(label_element, Label.label(chain.getUuid()));
                    node.setProperty(node_prop_key_value, element.getValue());
                }
                if (StringUtils.isNotBlank(element.getTag())) {
                    if (!node.hasLabel(Label.label(element.getTag()))) {
                        node.addLabel(Label.label(element.getTag()));
                    }
                    map.put(element.getTag(), node);
                } else {
                    map.put(element.getValue(), node);
                }
            }
            for (Relationship relationship : chain.getRelationships()) {
                Node begin = map.get(relationship.getBegin());
                Node end = map.get(relationship.getEnd());
                org.neo4j.graphdb.Relationship r = begin.createRelationshipTo(end, RelationshipType.withName(relationship.getValue()));
                r.setProperty(relationship_prop_key_atag, relationship.getBegin());
                r.setProperty(relationship_prop_key_btag, relationship.getEnd());
                r.setProperty(relationship_prop_key_chain, chain.getUuid());
            }
            tx.success();
        }
    }

    public void unloadRelationshipChain(RelationshipChain chain) {
        try (Transaction tx = graphDb.beginTx()) {
            if (!_existRelationshipChain(chain)) {
                return;
            }
            for (Element element : chain.getElements()) {
                Node node = _findNode(element);
                for (org.neo4j.graphdb.Relationship r : node.getRelationships(Direction.OUTGOING)) {
                    if (r.getProperty(relationship_prop_key_chain).equals(chain.getUuid())) {
                        r.delete();
                    }
                }
                node.removeLabel(Label.label(chain.getUuid()));
                if (StringUtils.isNotBlank(element.getTag())) {
                    node.removeLabel(Label.label(element.getTag()));
                }
            }
            tx.success();
        }
    }

    public void loadRepository(String repositoryPath) throws IOException, RepositoryFileFormatException {
        RepositoryReader repositoryReader = new RepositoryReader(repositoryPath);
        RelationshipChain chain = null;
        while ((chain = repositoryReader.read()) != null) {
            loadRelationshipChain(chain);
        }
        repositoryReader.close();
    }

    public void unloadRepository(String repositoryPath) throws IOException, RepositoryFileFormatException {
        RepositoryReader repositoryReader = new RepositoryReader(repositoryPath);
        RelationshipChain chain = null;
        while ((chain = repositoryReader.read()) != null) {
            unloadRelationshipChain(chain);
        }
        repositoryReader.close();

    }

    public void shutdown() {
        graphDb.shutdown();
    }
}
