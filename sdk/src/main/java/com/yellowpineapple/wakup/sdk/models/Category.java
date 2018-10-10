package com.yellowpineapple.wakup.sdk.models;

import java.util.List;

public class Category {

    private int id;
    private String name;
    private List<Company> companies;
    private List<String> tags;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public List<String> getTags() {
        return tags;
    }
}
