package com.metechvn.freeswitchcdr.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.util.StringUtils;

public class CollectionIdentifier {

    private String prefix;
    private String collectionName;

    private final MongoDatabase mongoDatabase;

    public CollectionIdentifier(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public MongoCollection<Document> collection() {
        return mongoDatabase.getCollection(name());
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
