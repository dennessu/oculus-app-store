/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.entity.payment;

import com.junbo.payment.db.entity.GenericEntity;
import com.junbo.payment.db.entity.JSONStringUserType;
import com.junbo.payment.spec.enums.PaymentEventType;
import com.junbo.payment.spec.enums.PaymentStatus;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import java.math.BigDecimal;

import javax.persistence.*;


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
    private PaymentEventType type;

    @Column(name = "event_status_id")
    private PaymentStatus status;

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

    public PaymentEventType getType() {
        return type;
    }

    public void setType(PaymentEventType type) {
        this.type = type;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
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
}
