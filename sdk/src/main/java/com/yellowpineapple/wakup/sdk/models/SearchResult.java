package com.yellowpineapple.wakup.sdk.models;

import java.util.List;

public class SearchResult {

    private List<Company> companies;
    private List<String> tags;

    public List<Company> getCompanies() {
        return companies;
    }

    public List<String> getTags() {
        return tags;
    }
}
