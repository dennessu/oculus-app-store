/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import com.junbo.common.id.SubledgerEventId;
import com.junbo.common.id.SubledgerId;
import com.junbo.common.jackson.annotation.XSSFreeString;
import com.junbo.common.model.ResourceMetaForDualWrite;

import java.util.Map;

/**
 * Created by fzhang on 2015/1/18.
 */
public class SubledgerEvent extends ResourceMetaForDualWrite<SubledgerEventId> {

    private SubledgerEventId id;

    private SubledgerId subledger;

    @XSSFreeString
    private String action;

    @XSSFreeString
    private String status;

    private Map<String, String> properties;

    @Override
    public SubledgerEventId getId() {
        return id;
    }

    @Override
    public void setId(SubledgerEventId id) {
        this.id = id;
    }

    public SubledgerId getSubledger() {
        return subledger;
    }

    public void setSubledger(SubledgerId subledger) {
        this.subledger = subledger;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
