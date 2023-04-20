package com.learn.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.kafka.entity.LibraryEvent;
import com.learn.kafka.jpa.LibraryEventsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
public class LibraryEventsService {

    @Autowired
    LibraryEventsRepository libraryEventsRepository;
    @Autowired
    ObjectMapper objectMapper;
    public void processLibraryEvent(ConsumerRecord<Integer,String> consumerRecord) throws JsonProcessingException {
       LibraryEvent libraryEvent=  objectMapper.readValue(consumerRecord.value(),LibraryEvent.class);
       log.info(" library event: {}",libraryEvent);

       switch (libraryEvent.getLibraryEventType()){
           case NEW ->{
                    save(libraryEvent);
                   break;
           }
           case UPDATE ->{
               validate(libraryEvent);
               save(libraryEvent);
               break;
           }
           default -> log.info("Invalid library event type");
        }
    }

    private void validate(LibraryEvent libraryEvent) {
        if(libraryEvent.getLibraryEventId()==null){
            throw new IllegalArgumentException("Library event id is missing");
        }
        Optional<LibraryEvent> libraryEventOptional = libraryEventsRepository.findById(libraryEvent.getLibraryEventId());
        if(!libraryEventOptional.isPresent()){
            throw new IllegalArgumentException("Not a valid library event");
        }
        log.info("validation is successful for library event:{}",libraryEventOptional.get());
    }

    private void save(LibraryEvent libraryEvent) {
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        libraryEventsRepository.save(libraryEvent);
        log.info(" successfully persisted the library event:{}",libraryEvent);
    }
}
