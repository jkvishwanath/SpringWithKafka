package com.learn.kafka.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Book {
    private Integer bookId;
    private String bookName;
    private String bookAuthor;
}
