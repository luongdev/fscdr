package com.metechvn.freeswitchcdr.mongo;

import org.bson.codecs.pojo.annotations.BsonId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Document(collection = "#{@identifier.name()}")
public class DynamicDocument extends org.bson.Document {

    @BsonId
    private UUID id;

    public DynamicDocument put(String key, Object value) {
        if (StringUtils.hasText(key)) super.put(key, value);

        return this;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public DynamicDocument() {
        this.id = UUID.randomUUID();
    }
}
