/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse.document;

import java.util.List;

/**
 * The SectionInfoNode class.
 */
public class SectionInfoNode {

    private SectionInfo sectionInfo;

    private List<SectionInfoNode> children;

    public SectionInfo getSectionInfo() {
        return sectionInfo;
    }

    public void setSectionInfo(SectionInfo sectionInfo) {
        this.sectionInfo = sectionInfo;
    }

    public List<SectionInfoNode> getChildren() {
        return children;
    }

    public void setChildren(List<SectionInfoNode> children) {
        this.children = children;
    }
}
