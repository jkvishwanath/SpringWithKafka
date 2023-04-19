package com.learn.kafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learn.kafka.domain.LibraryEvent;
import com.learn.kafka.domain.LibraryEventType;
import com.learn.kafka.producer.LibraryEventsProducer;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@Slf4j
public class LibraryEventsController {

    @Autowired
    LibraryEventsProducer libraryEventsProducer;

    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent( @RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
            log.info("before sendLibraryEvent");
            libraryEvent.setLibraryEventType(LibraryEventType.NEW);
            libraryEventsProducer.sendLibraryEvent(libraryEvent);
            log.info("after sendLibraryEvent");
            return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PostMapping("/v1/libraryevent_synchronous")
    public ResponseEntity<LibraryEvent> postLibraryEventSynchronous(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
/*          the below approach if  you want to call it in a synchronous way */

       //invoke kafka producer
        log.info("before sendLibraryEvent");
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        SendResult<Integer, String> sendResult = libraryEventsProducer.sendLibraryEventSynchronous(libraryEvent);
        log.info("SendResult is {} ", sendResult.toString());
        log.info("after sendLibraryEvent");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);


    }
    @PostMapping("/v1/libraryevent_asynchronous")
    public ResponseEntity<LibraryEvent> postLibraryEventASynchronous_Approach2(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        //invoke kafka producer
        log.info("before sendLibraryEvent");
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        libraryEventsProducer.sendLibraryEvent_Approach2(libraryEvent);
        //log.info("SendResult is {} ", sendResult.toString());
        log.info("after sendLibraryEvent");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);

    }


}
