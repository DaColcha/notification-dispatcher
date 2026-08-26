package dev.dacolcha.eventproducer.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;
    @Value("${kafka.topic.email}")
    private String emailTopic;
    @Value("${kafka.topic.discord}")
    private String discordTopic;
    @Value("${kafka.topic.slack}")
    private String slackTopic;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic emailTopic() {
        return TopicBuilder.name(emailTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic discordTopic() {
        return TopicBuilder.name(discordTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic slackTopic() {
        return TopicBuilder.name(slackTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
