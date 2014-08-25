/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.junbo.store.spec.model.browse.document.SectionInfoNode;
import com.junbo.store.spec.model.browse.document.Tos;

import java.util.List;

/**
 * The TocResponse class.
 */
public class TocResponse {

    private Tos tos;

    private List<SectionInfoNode> sections;

    public Tos getTos() {
        return tos;
    }

    public void setTos(Tos tos) {
        this.tos = tos;
    }

    public List<SectionInfoNode> getSections() {
        return sections;
    }

    public void setSections(List<SectionInfoNode> sections) {
        this.sections = sections;
    }
}
