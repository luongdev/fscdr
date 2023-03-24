package com.metechvn.freeswitchcdr.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;

public final class TopicUtils {

    private TopicUtils() {}

    public static NewTopic getMetadata(String topic, int defaultPartition, int defaultReplicas) {

        if (StringUtils.isEmpty(topic)) return null;

        var topicParts = topic.split(":");

        var topicBuilder = TopicBuilder.name(topicParts[0]);

        try {
            topicBuilder.partitions(Integer.parseInt(topicParts[1]));
        } catch (Exception ignored) {
            topicBuilder.partitions(defaultPartition);
        }

        try {
            topicBuilder.replicas(Integer.parseInt(topicParts[2]));
        } catch (Exception ignored) {
            topicBuilder.replicas(defaultReplicas);
        }

        return topicBuilder.build();
    }
}
