package com.metechvn.jsoncdr.repositories.es;

import com.metechvn.jsoncdr.entities.JsonCdr;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

public interface JsonCdrEsRepository extends ElasticsearchRepository<JsonCdr, UUID> {
}
