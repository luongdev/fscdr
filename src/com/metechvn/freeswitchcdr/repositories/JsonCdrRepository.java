package com.metechvn.freeswitchcdr.repositories;

import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;

public interface JsonCdrRepository extends DynamicDocumentRepository {

    @Query("{'$and': [" +
            "{'startEpoch': {'$gte': ?1}}, " +
            "{'startEpoch': {'$lte': ?2}}," +
            "{'$or':[{'cdrId':?0},{'globalCallId':?0},{'json.variables.phone_number':?0},{'json.variables.sip_h_X-Phone-Number':?0}]}]}")
    Page<Document> findBy(
            String keyword,
            long fromDate,
            Long toDate,
            Pageable pageable);

    @Query("{'$and': [" +
            "{'startEpoch': {'$gte': ?1}}, " +
            "{'$or':[{'cdrId':?0},{'globalCallId':?0},{'json.variables.phone_number':?0},{'json.variables.sip_h_X-Phone-Number':?0}]}]}")
    Page<Document> findBy(String keyword, long fromDate, Pageable pageable);

}
