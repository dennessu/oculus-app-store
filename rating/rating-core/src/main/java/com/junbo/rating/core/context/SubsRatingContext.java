/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.context;

import com.junbo.rating.spec.error.AppErrors;
import com.junbo.rating.spec.model.Currency;
import com.junbo.rating.spec.model.subscription.DurationUnit;
import com.junbo.rating.spec.model.subscription.SubsRatingRequest;
import com.junbo.rating.spec.model.subscription.SubsRatingType;

import java.math.BigDecimal;

/**
 * Created by lizwu on 5/22/14.
 */
public class SubsRatingContext extends RatingContext {
    private String offerId;
    private SubsRatingType type;
    private int cycleCount;
    private int extensionNum;
    private DurationUnit extensionUnit;
    private BigDecimal amount;

    public void fromRequest(SubsRatingRequest request) {
        super.setCountry(request.getCountry());
        Currency currency = Currency.findByCode(request.getCurrency());
        if (currency == null) {
            throw AppErrors.INSTANCE.currencyNotExist(request.getCurrency()).exception();
        }

        super.setCurrency(currency);

        this.offerId = request.getOfferId();
        this.type = request.getType();
        this.cycleCount = request.getCycleCount();
        this.extensionNum = request.getExtensionNum();
        this.extensionUnit = request.getExtensionUnit();
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public SubsRatingType getType() {
        return type;
    }

    public void setType(SubsRatingType type) {
        this.type = type;
    }

    public int getCycleCount() {
        return cycleCount;
    }

    public void setCycleCount(int cycleCount) {
        this.cycleCount = cycleCount;
    }

    public int getExtensionNum() {
        return extensionNum;
    }

    public void setExtensionNum(int extensionNum) {
        this.extensionNum = extensionNum;
    }

    public DurationUnit getExtensionUnit() {
        return extensionUnit;
    }

    public void setExtensionUnit(DurationUnit extensionUnit) {
        this.extensionUnit = extensionUnit;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
