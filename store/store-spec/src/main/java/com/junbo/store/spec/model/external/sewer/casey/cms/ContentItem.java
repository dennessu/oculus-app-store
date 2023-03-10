/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.sewer.casey.cms;

import com.junbo.store.spec.model.external.sewer.casey.CaseyLink;

import java.util.List;

/**
 * The ContentItem class.
 */
public class ContentItem {

    /**
     * The Type enum.
     */
    public enum Type {
        offer,
        item,
        string,
        offerAttribute,
        cmsPage,
        tos
    }

    private String type;
    private List<CaseyLink> links;
    private List<CaseyContentItemString> strings;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<CaseyLink> getLinks() {
        return links;
    }

    public void setLinks(List<CaseyLink> links) {
        this.links = links;
    }

    public List<CaseyContentItemString> getStrings() {
        return strings;
    }

    public void setStrings(List<CaseyContentItemString> strings) {
        this.strings = strings;
    }
}
