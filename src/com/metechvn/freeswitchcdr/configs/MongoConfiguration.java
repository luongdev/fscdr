package com.metechvn.freeswitchcdr.configs;

import com.metechvn.freeswitchcdr.mongo.CollectionIdentifier;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Configuration
@EnableMongoRepositories("com.metechvn.*")
public class MongoConfiguration extends AbstractMongoClientConfiguration {

    private final ConnectionString connectionString;

    public MongoConfiguration(MongoProperties mongoProperties) {
        this.connectionString = new ConnectionString(mongoProperties.determineUri());
    }


    @Bean
    @NotNull
    @Override
    public MongoClient mongoClient() {
        var mongoClientSettings = MongoClientSettings
                .builder()
                .applyConnectionString(this.connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public CollectionIdentifier identifier(MongoTemplate mongoTemplate) {
        return new CollectionIdentifier(mongoTemplate);
    }

    @Bean
    MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, getDatabaseName());
    }

    @Bean
    public Lock prefixLock() {
        return new ReentrantLock();
    }

    @NotNull
    @Override
    protected String getDatabaseName() {
        return Objects.requireNonNull(connectionString.getDatabase());
    }

    @NotNull
    @Override
    protected Collection<String> getMappingBasePackages() {
        return List.of("com.metechvn.freeswitchcdr");
    }
}
