package com.goshop.langur.processor.model.product;

import java.math.BigDecimal;
import java.util.Date;

public class Price {
    private BigDecimal price;
    private String priceType;
    private String currency;
    private Date startEffectiveDate;
    private Date endEffectiveDate;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getStartEffectiveDate() {
        return startEffectiveDate;
    }

    public void setStartEffectiveDate(Date startEffectiveDate) {
        this.startEffectiveDate = startEffectiveDate;
    }

    public Date getEndEffectiveDate() {
        return endEffectiveDate;
    }

    public void setEndEffectiveDate(Date endEffectiveDate) {
        this.endEffectiveDate = endEffectiveDate;
    }
}