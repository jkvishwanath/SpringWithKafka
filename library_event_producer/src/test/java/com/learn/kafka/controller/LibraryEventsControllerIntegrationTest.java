package com.learn.kafka.controller;

import com.learn.kafka.domain.Book;
import com.learn.kafka.domain.LibraryEvent;
import com.learn.kafka.domain.LibraryEventType;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;//import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
@EmbeddedKafka(topics = {"library-events"}, partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
public class LibraryEventsControllerIntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;


    private Consumer<Integer,String> consumer;

    @BeforeEach
    void setUp() {
        Map<String,Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    @Timeout(5)
    void postLibraryEvent(){
        //given
        Book book = Book.builder()
                    .bookId(123)
                    .bookAuthor("VIswa")
                    .bookName("Spring with Kafka")
                    .build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
                                    .libraryEventId(1)
                                    .book(book)
                                    .build();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent,httpHeaders);

        //when
            ResponseEntity<LibraryEvent> responseEntity = testRestTemplate.exchange("/v1/libraryevent_asynchronous", HttpMethod.POST,request,LibraryEvent.class);
         //then
            assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());

        ConsumerRecord<Integer, String> consumerRecord =  KafkaTestUtils.getSingleRecord(consumer,"library-events");
        //Thread.sleep(3000);
        String expectedRecord ="{\"libraryEventId\":1,\"libraryEventType\":\"NEW\",\"book\":{\"bookId\":123,\"bookName\":\"Spring with Kafka\",\"bookAuthor\":\"VIswa\"}}";
        String value = consumerRecord.value();
        assertEquals(expectedRecord, value);
    }

}
