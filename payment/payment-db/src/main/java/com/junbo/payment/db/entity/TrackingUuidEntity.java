/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.entity;


import com.junbo.payment.db.mapper.PaymentAPI;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;


/**
 * trackingUuid entity.
 */
@Entity
@Table(name = "payment_tracking_uuid")
public class TrackingUuidEntity extends GenericEntity{

    @Id
    @Column(name = "tracking_uuid_id")
    private Long id;

    @Column(name = "tracking_uuid")
    @org.hibernate.annotations.Type(type="pg-uuid")
    private UUID trackingUuid;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "api_id")
    private PaymentAPI api;

    @Column(name = "payment_instrument_id")
    private Long paymentInstrumentId;

    @Column(name = "payment_id")
    private Long paymentId;

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
        if(paymentInstrumentId != null){
            return paymentInstrumentId;
        }
        if(paymentId != null){
            return paymentId;
        }
        if(userId != null){
            return userId;
        }
        return null;
    }

    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public PaymentAPI getApi() {
        return api;
    }

    public void setApi(PaymentAPI api) {
        this.api = api;
    }

    public Long getPaymentInstrumentId() {
        return paymentInstrumentId;
    }

    public void setPaymentInstrumentId(Long paymentInstrumentId) {
        this.paymentInstrumentId = paymentInstrumentId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }


}
