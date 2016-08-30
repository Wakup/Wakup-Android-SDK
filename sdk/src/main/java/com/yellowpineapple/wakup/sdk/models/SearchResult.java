package com.yellowpineapple.wakup.sdk.models;

import java.util.List;

/**
 * Created by agutierrez on 15/12/15.
 */
public class SearchResult {

    List<Company> companies;
    List<String> tags;

    public List<Company> getCompanies() {
        return companies;
    }

    public List<String> getTags() {
        return tags;
    }
}
