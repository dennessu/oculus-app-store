/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.casey.cms;

import com.junbo.store.spec.model.external.casey.CaseyLink;

import java.util.Map;

/**
 * The CmsSchedule class.
 */
public class CmsSchedule {

    private CaseyLink self;

    private Map<String, CmsScheduleContent> slots;

    public CaseyLink getSelf() {
        return self;
    }

    public void setSelf(CaseyLink self) {
        this.self = self;
    }

    public Map<String, CmsScheduleContent> getSlots() {
        return slots;
    }

    public void setSlots(Map<String, CmsScheduleContent> slots) {
        this.slots = slots;
    }
}
