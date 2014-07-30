/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse.annotation;

/**
 * The Annotations class.
 */
public class Annotations {

    private SectionMetadata sectionCrossSell;
    private SectionMetadata sectionRelated;
    private SectionMetadata moreByAuthor;

    public SectionMetadata getSectionCrossSell() {
        return sectionCrossSell;
    }

    public void setSectionCrossSell(SectionMetadata sectionCrossSell) {
        this.sectionCrossSell = sectionCrossSell;
    }

    public SectionMetadata getSectionRelated() {
        return sectionRelated;
    }

    public void setSectionRelated(SectionMetadata sectionRelated) {
        this.sectionRelated = sectionRelated;
    }

    public SectionMetadata getMoreByAuthor() {
        return moreByAuthor;
    }

    public void setMoreByAuthor(SectionMetadata moreByAuthor) {
        this.moreByAuthor = moreByAuthor;
    }
}
