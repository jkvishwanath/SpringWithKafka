package com.learn.kafka.domain;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LibraryEvent {
    private Integer libraryEventId;
    private LibraryEventType libraryEventType;
    @NotNull
    @Valid
    private Book book;

}
