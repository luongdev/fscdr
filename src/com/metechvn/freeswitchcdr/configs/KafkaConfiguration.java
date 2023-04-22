package com.metechvn.freeswitchcdr.configs;

import com.metechvn.freeswitchcdr.utils.TopicUtils;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer;
import org.apache.avro.JsonProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

@Configuration
@ConditionalOnProperty(value = "app.kafka.enabled", havingValue = "true")
public class KafkaConfiguration {

    private final List<NewTopic> queueTopics;
    private final KafkaProperties kafkaProperties;
    private final Map<String, Object> producerProperties;
    private final Map<String, Object> consumerProperties;
    private final String schemaRegistryUrl;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public KafkaConfiguration(
            KafkaProperties kafkaProperties,
            @Value("${app.json-cdr.topic:json_cdr}") String jsonCdrTopic,
            @Value("${spring.kafka.properties.schema.registry.url:}") String schemaRegistryUrl,
            @Value("${spring.kafka.properties.default-topic-partitions:2}") int topicPartitions,
            @Value("${spring.kafka.properties.default-topic-replications:1}") int topicReplications) {
        this.kafkaProperties = kafkaProperties;
        this.queueTopics = new ArrayList<>();
        this.schemaRegistryUrl = schemaRegistryUrl;

        if (StringUtils.isEmpty(kafkaProperties.getSsl().getTrustStoreCertificates())) {
            kafkaProperties.getSsl().setTrustStoreCertificates(null);
        }
        if (kafkaProperties.getSsl().getTrustStoreLocation() == null) {
            kafkaProperties.getSsl().setTrustStoreLocation(null);
        }
        if (StringUtils.isEmpty(kafkaProperties.getSsl().getTrustStoreType())) {
            kafkaProperties.getSsl().setTrustStoreType(null);
        }
        if (StringUtils.isEmpty(kafkaProperties.getSsl().getTrustStorePassword())) {
            kafkaProperties.getSsl().setTrustStorePassword(null);
        }

        if (StringUtils.isEmpty(kafkaProperties.getSsl().getKeyStoreCertificateChain())) {
            kafkaProperties.getSsl().setKeyStoreCertificateChain(null);
        }
        if (StringUtils.isEmpty(kafkaProperties.getSsl().getKeyStoreKey())) {
            kafkaProperties.getSsl().setKeyStoreKey(null);
        }
        if (kafkaProperties.getSsl().getKeyStoreLocation() == null) {
            kafkaProperties.getSsl().setKeyStoreLocation(null);
        }
        if (StringUtils.isEmpty(kafkaProperties.getSsl().getKeyStoreType())) {
            kafkaProperties.getSsl().setKeyStoreType(null);
        }
        if (StringUtils.isEmpty(kafkaProperties.getSsl().getKeyStorePassword())) {
            kafkaProperties.getSsl().setKeyStorePassword(null);
        }

        if (StringUtils.isNotEmpty(jsonCdrTopic))
            this.queueTopics.add(TopicUtils.getMetadata(jsonCdrTopic, topicPartitions, topicReplications));

        this.producerProperties = this.kafkaProperties.buildProducerProperties();
        this.consumerProperties = this.kafkaProperties.buildConsumerProperties();

        if (StringUtils.isEmpty(schemaRegistryUrl)) {
            producerProperties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            producerProperties.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
            consumerProperties.put(KEY_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
            consumerProperties.put(VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
        } else {
            producerProperties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            producerProperties.put(VALUE_SERIALIZER_CLASS_CONFIG, KafkaJsonSchemaDeserializer.class.getName());
            consumerProperties.put(KEY_DESERIALIZER_CLASS_CONFIG, KafkaJsonSchemaDeserializer.class.getName());
            consumerProperties.put(VALUE_DESERIALIZER_CLASS_CONFIG, KafkaJsonSchemaDeserializer.class.getName());
        }
    }

    @Bean("adminClient")
    public AdminClient adminClient() throws ExecutionException, InterruptedException {
        var adminClient = KafkaAdminClient.create(kafkaProperties.buildAdminProperties());
        var topics = adminClient.listTopics();
        Map<String, Boolean> existTopics = topics.names().get().stream().collect(Collectors.toMap(x -> x, x -> true));
        for (var topic : queueTopics) {
            if (!existTopics.containsKey(topic.name())) {
                adminClient.createTopics(Collections.singleton(topic)).all().get();
                existTopics.put(topic.name(), true);

                log.info(
                        "Create topic {}[{} partitions, {} replicas] successfully!",
                        topic.name(),
                        topic.numPartitions(),
                        topic.numPartitions()
                );
            }
        }

        return adminClient;
    }

    @Bean
    public ProducerFactory<JsonProperties.Null, Object> nullKeyProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    @Bean
    public KafkaTemplate<JsonProperties.Null, Object> nullKeyKafkaTemplate(
            ProducerFactory<JsonProperties.Null, Object> nullKeyProducerFactory) {
        return new KafkaTemplate<>(nullKeyProducerFactory);
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
        var producerProperties = this.producerProperties;
        if (StringUtils.isEmpty(schemaRegistryUrl)) {
            producerProperties.put(KEY_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
            producerProperties.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        } else {
            producerProperties.put(KEY_SERIALIZER_CLASS_CONFIG, KafkaJsonSchemaSerializer.class.getName());
            producerProperties.put(VALUE_SERIALIZER_CLASS_CONFIG, KafkaJsonSchemaSerializer.class.getName());
        }

        return new DefaultKafkaProducerFactory<>(producerProperties);
    }

    @Bean
    public KafkaTemplate<Object, Object> objKafkaTemplate(ProducerFactory<Object, Object> objProducerFactory) {
        return new KafkaTemplate<>(objProducerFactory);
    }

    @Bean(name = "objConsumerFactory")
    public ConsumerFactory<Object, Object> objConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProperties);
    }

    @Bean(name = "objListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<Object, Object> objListenerContainerFactory(
            ConsumerFactory<Object, Object> objConsumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(objConsumerFactory);

        return factory;
    }

}
