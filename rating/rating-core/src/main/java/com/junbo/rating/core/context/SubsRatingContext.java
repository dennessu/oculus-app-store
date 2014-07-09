/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.context;

import com.junbo.rating.core.builder.Builder;
import com.junbo.rating.spec.error.AppErrors;
import com.junbo.rating.spec.model.Currency;
import com.junbo.rating.spec.model.subscription.DurationUnit;
import com.junbo.rating.spec.model.subscription.SubsRatingRequest;
import com.junbo.rating.spec.model.subscription.SubsRatingType;

import java.math.BigDecimal;

/**
 * Created by lizwu on 5/22/14.
 */
public class SubsRatingContext extends RatingContext implements Builder<SubsRatingRequest> {
    private String offerId;
    private SubsRatingType subsRatingType;
    private int cycleCount;
    private int extensionNum;
    private DurationUnit extensionUnit;
    private BigDecimal amount;

    @Override
    public void fromRequest(SubsRatingRequest request) {
        super.setCountry(request.getCountry());
        Currency currency = Currency.findByCode(request.getCurrency());
        if (currency == null) {
            throw AppErrors.INSTANCE.currencyNotFound(request.getCurrency()).exception();
        }

        super.setCurrency(currency);

        this.offerId = request.getOfferId();
        this.subsRatingType = request.getType();
        this.cycleCount = request.getCycleCount();
        this.extensionNum = request.getExtensionNum();
        this.extensionUnit = request.getExtensionUnit();
    }

    @Override
    public SubsRatingRequest buildResult() {
        SubsRatingRequest result = new SubsRatingRequest();
        result.setOfferId(getOfferId());
        result.setCountry(getCountry());
        result.setCurrency(getCurrency().getCode());
        result.setType(getSubsRatingType());
        result.setAmount(getAmount());
        return result;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public SubsRatingType getSubsRatingType() {
        return subsRatingType;
    }

    public void setSubsRatingType(SubsRatingType subsRatingType) {
        this.subsRatingType = subsRatingType;
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
