package com.skillspace.stubapplication.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public NewTopic testTopic() {
        return new NewTopic("test-topic", 1, (short) 1);
    }

    @Bean
    public NewTopic testTopic2() {
        return new NewTopic("test-topic2", 1, (short) 1);
    }

}
