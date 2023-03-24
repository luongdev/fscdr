package com.metechvn.freeswitchcdr.mongo;

import org.bson.codecs.pojo.annotations.BsonId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Document(collection = "#{@identifier.name()}")
public class DynamicDocument extends org.bson.Document {

    @Id
    @BsonId
    private UUID id;

    public DynamicDocument() {
        super("_id", UUID.randomUUID());

        this.id = get("_id", UUID.class);
    }

    public DynamicDocument put(String key, Object value) {
        if (StringUtils.hasText(key)) {
            if (key.contains(".")) {
                key = key.replace(".", "_dot_");
            }
            super.put(key, value);
        }

        return this;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}
