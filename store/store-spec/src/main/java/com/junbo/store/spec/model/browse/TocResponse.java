/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.browse;

import com.junbo.store.spec.model.Challenge;
import com.junbo.store.spec.model.browse.document.SectionInfoNode;

import java.util.List;

/**
 * The TocResponse class.
 */
public class TocResponse {

    private Challenge challenge;

    private List<SectionInfoNode> sections;

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public List<SectionInfoNode> getSections() {
        return sections;
    }

    public void setSections(List<SectionInfoNode> sections) {
        this.sections = sections;
    }
}
