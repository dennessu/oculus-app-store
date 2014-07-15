/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.common.util.Identifiable;
import com.junbo.order.db.ValidationMessages;
import com.junbo.order.spec.model.enums.SubledgerItemAction;
import com.junbo.order.spec.model.enums.SubledgerItemStatus;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by chriszhu on 1/29/14.
 */
@Entity
@Table(name = "SUBLEDGER_ITEM")
public class SubledgerItemEntity extends CommonDbEntityWithDate implements Identifiable<Long> {

    private Long subledgerItemId;
    private Long originalSubledgerItemId;
    private Long subledgerId;
    private BigDecimal totalAmount;
    private BigDecimal totalPayoutAmount;
    private Long totalQuantity;
    private Long orderId;
    private Long orderItemId;
    private String offerId;
    private SubledgerItemStatus status;
    private SubledgerItemAction subledgerItemAction;

    @Id
    @Column(name = "SUBLEDGER_ITEM_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getSubledgerItemId() {
        return subledgerItemId;
    }

    public void setSubledgerItemId(Long subledgerItemId) {
        this.subledgerItemId = subledgerItemId;
    }

    @Column(name = "ORIGINAL_SUBLEDGER_ITEM_ID")
    public Long getOriginalSubledgerItemId() {
        return originalSubledgerItemId;
    }

    public void setOriginalSubledgerItemId(Long originalSubledgerItemId) {
        this.originalSubledgerItemId = originalSubledgerItemId;
    }

    @Column(name = "SUBLEDGER_ID")
    public Long getSubledgerId() {
        return subledgerId;
    }

    public void setSubledgerId(Long subledgerId) {
        this.subledgerId = subledgerId;
    }

    @Column(name = "TOTAL_AMOUNT")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Column(name = "TOTAL_PAYOUT_AMOUNT")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public BigDecimal getTotalPayoutAmount() {
        return totalPayoutAmount;
    }

    public void setTotalPayoutAmount(BigDecimal totalPayoutAmount) {
        this.totalPayoutAmount = totalPayoutAmount;
    }

    @Column(name = "TOTAL_QUANTITY")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    @Column(name = "ORDER_ITEM_ID")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    @Column(name = "OFFER_ID")
    @NotEmpty(message = ValidationMessages.MISSING_VALUE)
    @Length(max=128, message=ValidationMessages.TOO_LONG)
    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    @Column (name = "subledger_item_action")
    @NotNull (message = ValidationMessages.MISSING_VALUE)
    @Type(type = "com.junbo.order.db.entity.type.SubledgerItemActionType")
    public SubledgerItemAction getSubledgerItemAction() {
        return subledgerItemAction;
    }

    public void setSubledgerItemAction(SubledgerItemAction subledgerItemAction) {
        this.subledgerItemAction = subledgerItemAction;
    }

    @Column (name = "status_id")
    @NotNull (message = ValidationMessages.MISSING_VALUE)
    @Type(type = "com.junbo.order.db.entity.type.SubledgerItemStatusType")
    public SubledgerItemStatus getStatus() {
        return status;
    }

    public void setStatus(SubledgerItemStatus status) {
        this.status = status;
    }

    @Override
    @Transient
    public Long getShardId() {
        return subledgerItemId;
    }

    @Override
    @Transient
    public Long getId() {
        return subledgerItemId;
    }

    public void setId(Long id) {
        subledgerItemId = id;
    }
}
