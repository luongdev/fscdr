package com.metechvn.config;

import com.metechvn.es.EsIndexIdentifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.metechvn.repositories.es")

public class ElasticsearchConfig {

    private final String defaultIndex;

    public ElasticsearchConfig(@Value("${spring.elasticsearch.default-index:default_index}") String defaultIndex) {
        this.defaultIndex = defaultIndex;
    }

    @Bean
    public EsIndexIdentifier identifier() {
        return new EsIndexIdentifier(this.defaultIndex);
    }
}
