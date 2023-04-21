package com.metechvn.freeswitchcdr.configs;

import com.metechvn.freeswitchcdr.mongo.CollectionIdentifier;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.metechvn")
public class MongoConfiguration { // extends AbstractMongoClientConfiguration {

    private final MongoProperties mongoProperties;

    public MongoConfiguration(MongoProperties mongoProperties) {
        this.mongoProperties = mongoProperties;
    }


//    @Override
//    public MongoClient mongoClient() {
//        var mongoClientSettings = MongoClientSettings.builder().applyConnectionString(this.connectionString).build();
//
//        return MongoClients.create(mongoClientSettings);
//    }
//
//    @Override
//    @SuppressWarnings("all")
//    public Collection getMappingBasePackages() {
//        return Collections.singleton("com.metechvn");
//    }

    @Bean
    public CollectionIdentifier identifier(MongoDatabase mongoDatabase) {
        return new CollectionIdentifier(mongoDatabase);
    }

    @Bean
    MongoDatabase mongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(mongoProperties.getDatabase());
    }
}
