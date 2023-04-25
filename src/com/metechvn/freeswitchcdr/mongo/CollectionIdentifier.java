package com.metechvn.freeswitchcdr.mongo;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.StringUtils;

public class CollectionIdentifier {

    private String prefix;
    private String collectionName;

    private final MongoTemplate mongoTemplate;

    public CollectionIdentifier(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public MongoCollection<Document> collection() {
        return mongoTemplate.getCollection(name());
    }

    public MongoTemplate template() {
        return mongoTemplate;
    }

    public String name() {
        return String
                .format("%s%s",
                        !StringUtils.hasText(this.prefix) ? "" : String.format("%s_", this.prefix),
                        this.collectionName
                ).toLowerCase();
    }

    public CollectionIdentifier prefix(String prefix) {
        this.prefix = prefix;

        return this;
    }

    public CollectionIdentifier collectionName(String collectionName) {
        this.collectionName = collectionName;

        return this;
    }

}
