/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.sewer.catalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * The SewerItem class.
 */
public class SewerItem extends com.junbo.catalog.spec.model.item.Item {

    @JsonProperty("currentRevision")
    private JsonNode currentRevisionNode;

    @JsonProperty("developer")
    private JsonNode developerNode;

    @JsonProperty("categories")
    private List<JsonNode> categoriesNode;

    @JsonProperty("genres")
    private List<JsonNode> genresNode;

    @JsonProperty("rating")
    private JsonNode ratingNode;

    public JsonNode getCurrentRevisionNode() {
        return currentRevisionNode;
    }

    public void setCurrentRevisionNode(JsonNode currentRevisionNode) {
        this.currentRevisionNode = currentRevisionNode;
    }

    public JsonNode getDeveloperNode() {
        return developerNode;
    }

    public void setDeveloperNode(JsonNode developerNode) {
        this.developerNode = developerNode;
    }

    public List<JsonNode> getCategoriesNode() {
        return categoriesNode;
    }

    public void setCategoriesNode(List<JsonNode> categoriesNode) {
        this.categoriesNode = categoriesNode;
    }

    public List<JsonNode> getGenresNode() {
        return genresNode;
    }

    public void setGenresNode(List<JsonNode> genresNode) {
        this.genresNode = genresNode;
    }

    public JsonNode getRatingNode() {
        return ratingNode;
    }

    public void setRatingNode(JsonNode ratingNode) {
        this.ratingNode = ratingNode;
    }
}
