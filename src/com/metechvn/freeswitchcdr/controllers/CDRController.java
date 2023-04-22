package com.metechvn.freeswitchcdr.controllers;

import com.metechvn.freeswitchcdr.dtos.CDRListDto;
import com.metechvn.freeswitchcdr.dtos.PagedResult;
import com.metechvn.freeswitchcdr.mongo.CollectionIdentifier;
import com.metechvn.freeswitchcdr.repositories.JsonCdrRepository;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.metechvn.freeswitchcdr.utils.PrefixUtils.formatCollectionPrefix;

@RestController
@RequestMapping("/api/v1/cdr")
public class CDRController {

    private final CollectionIdentifier identifier;
    private final JsonCdrRepository jsonCdrRepository;

    public CDRController(CollectionIdentifier identifier, JsonCdrRepository jsonCdrRepository) {
        this.identifier = identifier;
        this.jsonCdrRepository = jsonCdrRepository;
    }

    @GetMapping({"", "/"})
    public PagedResult get(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("fromDate") long fromDate,
            @RequestParam(name = "toDate", required = false) Long toDate,
            @RequestParam(name = "keyword", required = false) String keyword) {
        identifier.prefix(formatCollectionPrefix(fromDate)).collectionName("json_cdr");

        var pageable = PageRequest.of(page, size, Sort.by("startEpoch").ascending());
        Page<Document> pagedResult;
        if (toDate == null || toDate < fromDate) {
            pagedResult = jsonCdrRepository.findBy(keyword, fromDate, pageable);
        } else {
            pagedResult = jsonCdrRepository.findBy(keyword, fromDate, toDate, pageable);
        }

        return PagedResult.of(
                pagedResult.stream().map(CDRListDto::of).toList(),
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages()
        );
    }
}
