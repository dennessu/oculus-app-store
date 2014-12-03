/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.clientproxy.facebook;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.payment.common.exception.AppServerExceptions;

/**
 * Facebook Item Description.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FacebookItemDescription {
    private FacebookPaymentEntity entity;
    private FacebookPaymentType type;
    private String[] items;

    public FacebookItemDescription(FacebookPaymentEntity entity, FacebookPaymentType type){
        this.entity = entity;
        this.type = type;
    }
    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this) ;
        } catch (JsonProcessingException e) {
            throw AppServerExceptions.INSTANCE.errorSerialize("FacebookItemDescription").exception();
        }
    }

    public FacebookPaymentEntity getEntity() {
        return entity;
    }

    public void setEntity(FacebookPaymentEntity entity) {
        this.entity = entity;
    }

    public FacebookPaymentType getType() {
        return type;
    }

    public void setType(FacebookPaymentType type) {
        this.type = type;
    }

    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        this.items = items;
    }
}
