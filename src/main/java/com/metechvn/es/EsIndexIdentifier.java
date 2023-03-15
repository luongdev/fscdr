package com.metechvn.es;

import org.springframework.util.StringUtils;

public class EsIndexIdentifier {

    private String prefix;
    private String indexName;

    public EsIndexIdentifier() {
    }

    public EsIndexIdentifier(String indexName) {
        this.indexName = indexName;
    }

    public EsIndexIdentifier(String prefix, String indexName) {
        this(indexName);
        this.prefix = prefix;
    }

    public String name() {
        return String.format("%s%s",
                !StringUtils.hasText(this.prefix) ? "" : String.format("%s_", this.prefix),
                this.indexName
        );
    }

    public EsIndexIdentifier setPrefix(String prefix) {
        this.prefix = prefix;

        return this;
    }

    public EsIndexIdentifier setIndexName(String indexName) {
        this.indexName = indexName;

        return this;
    }
}