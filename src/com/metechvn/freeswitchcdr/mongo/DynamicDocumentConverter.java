package com.metechvn.freeswitchcdr.mongo;

import org.bson.json.Converter;
import org.bson.json.StrictJsonWriter;

public class DynamicDocumentConverter implements Converter<DynamicDocument> {
    @Override
    public void convert(DynamicDocument doc, StrictJsonWriter writer) {
        writer.writeStartObject();


        writer.writeEndObject();
    }
}
