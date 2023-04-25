package com.metechvn.freeswitchcdr.repositories;

import com.metechvn.freeswitchcdr.mongo.CollectionIdentifier;
import com.metechvn.freeswitchcdr.projection.CDRListProjection;
import com.metechvn.freeswitchcdr.utils.PrefixUtils;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class MongoTemplateRepository {

    private final String collectionName;
    private final CollectionIdentifier identifier;

    public MongoTemplateRepository(CollectionIdentifier identifier) {
        this.identifier = identifier;
        this.collectionName = "json_cdr";
    }

    public Page<CDRListProjection> query(String keyword, long fromDate, Long toDate, Pageable pageable) {
        var criteria = Criteria.where("startEpoch").gte(fromDate);
        if (toDate != null && toDate > fromDate) criteria.lt(toDate);

        if (StringUtils.isNotEmpty(keyword)) criteria.orOperator(
                Criteria.where("phoneNumber").is(keyword.trim()),
                Criteria.where("dialedNumber").is(keyword.trim()),
                Criteria.where("cdrId").is(keyword.trim()),
                Criteria.where("globalCallId").is(keyword.trim())
        );

        var aggregation = Aggregation.newAggregation(
                new MatchOperation(criteria),
                new SortOperation(pageable.getSort()),
                new SkipOperation(pageable.getOffset()),
                new LimitOperation(pageable.getPageSize()),
                new ProjectionOperation(CDRListProjection.class)
        );

        var res = identifier
                .prefix(PrefixUtils.formatCollectionPrefix(fromDate))
                .collectionName(collectionName)
                .template()
                .aggregate(aggregation, identifier.name(), CDRListProjection.class);

        var count = identifier
                .prefix(PrefixUtils.formatCollectionPrefix(fromDate))
                .collectionName(collectionName)
                .template()
                .count(new Query(criteria), identifier.name());

        return new PageImpl<>(res.getMappedResults(), pageable, count);
    }
}
