/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.spec.model.subscription;

import com.junbo.common.jackson.annotation.CurrencyId;
import com.junbo.common.jackson.annotation.OfferId;

import javax.validation.constraints.Null;
import java.math.BigDecimal;

/**
 * Created by lizwu on 5/21/14.
 */
public class SubsRatingRequest {
    private SubsRatingType type;

    @OfferId
    private Long offerId;

    @CurrencyId
    private String currency;

    @Null
    private BigDecimal amount;

    public SubsRatingType getType() {
        return type;
    }

    public void setType(SubsRatingType type) {
        this.type = type;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
