package com.metechvn.freeswitchcdr.repositories;

import com.metechvn.freeswitchcdr.mongo.DynamicDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface DynamicDocumentRepository extends MongoRepository<DynamicDocument, UUID> {

}
