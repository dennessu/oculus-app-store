/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.entity.payment;

import com.junbo.payment.db.entity.GenericEntity;
import com.junbo.payment.db.entity.JSONStringUserType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;


/**
 * payment event entity.
 */
@Entity
@Table(name = "payment_event")
@TypeDefs({@TypeDef(name = "json-string", typeClass = JSONStringUserType.class)})
public class PaymentEventEntity extends GenericEntity {

    @Id
    @Column(name = "payment_event_id")
    private Long id;

    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "payment_event_type_id")
    private Short eventTypeId;

    @Column(name = "event_status_id")
    private Short statusId;

    @Column(name = "currency_code")
    private String currency;

    @Column(name = "net_amount")
    private BigDecimal netAmount;

    @Column(name = "request")
    @Type(type = "json-string")
    private String request;

    @Column(name = "response")
    @Type(type = "json-string")
    private String response;

    @Column(name = "external_token")
    private String externalToken;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getShardMasterId() {
        return paymentId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Short getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Short eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public Short getStatusId() {
        return statusId;
    }

    public void setStatusId(Short statusId) {
        this.statusId = statusId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getExternalToken() {
        return externalToken;
    }

    public void setExternalToken(String externalToken) {
        this.externalToken = externalToken;
    }
}
