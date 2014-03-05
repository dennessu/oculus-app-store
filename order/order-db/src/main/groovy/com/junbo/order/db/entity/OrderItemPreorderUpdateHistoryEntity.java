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
import java.util.Date;

/**
 * Created by chriszhu on 1/28/14.
 */

@Entity
@Table(name = "ORDER_ITEM_PREORDER_UPDATE_HISTORY")
public class OrderItemPreorderUpdateHistoryEntity {
    private Long orderItemPreorderUpdateHistoryId;
    private Long orderItemId;
    private Short updateTypeId;
    private String updateColumn;
    private String updateBeforeValue;
    private String updateAfterValue;
    private Date updatedTime;
    private String updatedBy;

    @Id
    @Column(name = "ORDER_ITEM_PREORDER_UPDATE_HISTORY_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getOrderItemPreorderUpdateHistoryId() {
        return orderItemPreorderUpdateHistoryId;
    }

    public void setOrderItemPreorderUpdateHistoryId(Long orderItemPreorderUpdateHistoryId) {
        this.orderItemPreorderUpdateHistoryId = orderItemPreorderUpdateHistoryId;
    }

    @Column(name = "ORDER_ITEM_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    @Column(name = "UPDATE_TYPE_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Short getUpdateTypeId() {
        return updateTypeId;
    }

    public void setUpdateTypeId(Short updateTypeId) {
        this.updateTypeId = updateTypeId;
    }

    @Column(name = "UPDATE_COLUMN")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    @Length(max=128, message=ValidationMessages.TOO_LONG)
    public String getUpdateColumn() {
        return updateColumn;
    }

    public void setUpdateColumn(String updateColumn) {
        this.updateColumn = updateColumn;
    }

    @Column(name = "UPDATE_BEFORE_VALUE")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    @Length(max=128, message=ValidationMessages.TOO_LONG)
    public String getUpdateBeforeValue() {
        return updateBeforeValue;
    }

    public void setUpdateBeforeValue(String updateBeforeValue) {
        this.updateBeforeValue = updateBeforeValue;
    }

    @Column(name = "UPDATE_AFTER_VALUE")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    @Length(max=128, message=ValidationMessages.TOO_LONG)
    public String getUpdateAfterValue() {
        return updateAfterValue;
    }

    public void setUpdateAfterValue(String updateAfterValue) {
        this.updateAfterValue = updateAfterValue;
    }

    @Column(name = "UPDATED_TIME")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Column(name = "UPDATED_BY")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    @Length(max=128, message=ValidationMessages.TOO_LONG)
    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
