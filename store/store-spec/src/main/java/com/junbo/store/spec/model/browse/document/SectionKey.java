/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse.document;

/**
 * The SectionKey class.
 */
public class SectionKey {

    private String category;

    private String criteria;

    public SectionKey() {
    }

    public SectionKey(String category, String criteria) {
        this.category = category;
        this.criteria = criteria;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SectionKey that = (SectionKey) o;

        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        if (criteria != null ? !criteria.equals(that.criteria) : that.criteria != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = category != null ? category.hashCode() : 0;
        result = 31 * result + (criteria != null ? criteria.hashCode() : 0);
        return result;
    }
}
