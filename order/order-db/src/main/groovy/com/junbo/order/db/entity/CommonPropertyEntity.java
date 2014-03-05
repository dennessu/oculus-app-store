/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.order.db.ValidationMessages;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * Created by chriszhu on 1/27/14.
 */
public class CommonPropertyEntity extends CommonDbEntityWithDate {
    private Long propertyId;
    private Short propertyType;
    private String propertyValue;

    @Id
    @Column(name = "PROPERTY_ID")
    public Long getPropertyIdropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    @Column(name = "PROPERTY_TYPE")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Short getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Short propertyType) {
        this.propertyType = propertyType;
    }

    @Column(name = "PROPERTY_VALUE")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }
}
