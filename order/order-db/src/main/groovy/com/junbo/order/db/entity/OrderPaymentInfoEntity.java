/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Created by chriszhu on 1/28/14.
 */

@Entity
@Table(name = "ORDER_PAYMENT_INFO")
public class OrderPaymentInfoEntity extends CommonDbEntityWithDate {
    private Long id;
    private Long orderId;
    private String paymentInstrumentId;
    private String paymentInstrumentType;

    @Id
    @Column(name = "ORDER_PAYMENT_INFO_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getOrderPaymentId() {
        return id;
    }

    public void setOrderPaymentId(Long orderPaymentId) {
        this.id = orderPaymentId;
    }

    @Column(name = "ORDER_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Column(name = "PAYMENT_METHOD_ID")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    @Length(max=128, message=ValidationMessages.TOO_LONG)
    public String getPaymentInstrumentId() {
        return paymentInstrumentId;
    }

    public void setPaymentInstrumentId(String paymentInstrumentId) {
        this.paymentInstrumentId = paymentInstrumentId;
    }

    @Column(name = "PAYMENT_METHOD_TYPE")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    @Length(max=128, message=ValidationMessages.TOO_LONG)
    public String getPaymentInstrumentType() {
        return paymentInstrumentType;
    }

    public void setPaymentInstrumentType(String paymentInstrumentType) {
        this.paymentInstrumentType = paymentInstrumentType;
    }
}
