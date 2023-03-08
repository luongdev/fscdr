package com.metechvn.es;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.UUID;

@Getter
@Setter
@Document(indexName = "#{@identifier.name()}")
public abstract class DynamicIndex {
    @Id
    private UUID id;

    public DynamicIndex() {
        this.id = UUID.randomUUID();
    }
}
