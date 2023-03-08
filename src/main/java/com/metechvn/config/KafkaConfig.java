package com.metechvn.config;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

@Configuration
public class KafkaConfig {

    private final Map<String, Object> producerProperties;
    private final Map<String, Object> consumerProperties;

    public KafkaConfig(KafkaProperties kafkaProperties) {
        this.producerProperties = kafkaProperties.buildProducerProperties();
        this.consumerProperties = kafkaProperties.buildConsumerProperties();
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<Object, Object> objProducerFactory() {
        final var properties = this.producerProperties;
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());

        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public KafkaTemplate<Object, Object> objKafkaTemplate(ProducerFactory<Object, Object> objProducerFactory) {
        return new KafkaTemplate<>(objProducerFactory);
    }

    @Bean(name = "objConsumerFactory")
    public ConsumerFactory<Object, Object> objConsumerFactory() {
        final var properties = this.consumerProperties;
        properties.put(KEY_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
        properties.put(VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());

        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean(name = "objListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<Object, Object> objListenerContainerFactory(
            ConsumerFactory<Object, Object> objConsumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(objConsumerFactory);

        return factory;
    }
}
