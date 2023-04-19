package com.learn.kafka.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import org.apache.kafka.clients.admin.NewTopic;
@Configuration
@Profile("local")
/*Not recommended way to create topic in prod*/
public class AutoCreateConfig {
    @Bean
    public NewTopic libraryEvents(){
       return TopicBuilder.name("library-events")
                .partitions(3)
                .replicas(3)
                .build();
    }

}
