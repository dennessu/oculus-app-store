/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.jackson.annotation.HateoasLink;
import com.junbo.common.jackson.annotation.XSSFreeString;
import com.junbo.common.model.Link;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by xiali_000 on 4/21/2014.
 */
public class Currency extends PropertyAssignedAwareResourceMeta<CurrencyId> {

    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable] The Link to the currency resource.")
    @JsonProperty("self")
    private CurrencyId id;

    @XSSFreeString
    @ApiModelProperty(position = 2, required = true, value = "[Client Immutable] The currency code of the currency resource; always the same with self.id; " +
            "never displayed to users - use translationService(localeKeys.shortName) for that .")
    private String currencyCode;

    @ApiModelProperty(position = 3, required = false, value = "Countries link with default currency specified.")
    @HateoasLink("/countries?defaultCurrencyId={id}")
    private Link countries;

    @XSSFreeString
    @ApiModelProperty(position = 4, required = true, value = "The symbol of the currency resource.")
    private String symbol;

    @XSSFreeString
    @ApiModelProperty(position = 5, required = true, value = "Position to put the symbol when show currency number (BEFORE, AFTER, etc).")
    private String symbolPosition;

    @ApiModelProperty(position = 7, required = true, value = "The number of digits after decimal.")
    private Integer numberAfterDecimal;

    @ApiModelProperty(position = 8, required = true, value = "The Minimal Amount of money for this currency to authorize and verify the payment Instrument.")
    @JsonProperty("minimalAuthorizeAmount")
    private BigDecimal minAuthAmount;

    @ApiModelProperty(position = 11, required = true, value = "Maps locale-code to a JSON object with locale-specific info about this currency.")
    private Map<String, CurrencyLocaleKey> locales;

    @ApiModelProperty(position = 12, required = false, value = "[Client Immutable] This is the calculated value to give how accurate the localizable attributes is. " +
            "The value must be in (HIGH, MEDIUM, LOW). For a GET request without ?locale parameter, all localizable attributes are returned in all available locales," +
            " the localAccuracy value should be \"HIGH\". In a GET request with ?locale parameter, for example ?locale=\"es_ES\", " +
            "if all the localizable attributes exist in the requested locale (\"es_ES\"), the localAccuracy should be \"HIGH\". " +
            "If partial of the localizable attributes are from requested locale (\"es_ES\"), other part of the localizable attributes are from fallback locale," +
            " the localAccuracy value should be \"MEDIUM\". If all of the localizable attributes are not from request locale (\"es_ES\")," +
            " all localizable attributes are from fall back locale, the localAccracy value should be \"LOW\".")
    private String localeAccuracy;

    public CurrencyId getId() {
        return id;
    }

    public void setId(CurrencyId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        support.setPropertyAssigned("currencyCode");
    }

    public Link getCountries() {
        return countries;
    }

    public void setCountries(Link countries) {
        this.countries = countries;
        support.setPropertyAssigned("countries");
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
        support.setPropertyAssigned("symbol");
    }

    public String getSymbolPosition() {
        return symbolPosition;
    }

    public void setSymbolPosition(String symbolPosition) {
        this.symbolPosition = symbolPosition;
        support.setPropertyAssigned("symbolPosition");
    }

    public Integer getNumberAfterDecimal() {
        return numberAfterDecimal;
    }

    public void setNumberAfterDecimal(Integer numberAfterDecimal) {
        this.numberAfterDecimal = numberAfterDecimal;
        support.setPropertyAssigned("numberAfterDecimal");
    }

    public BigDecimal getMinAuthAmount() {
        return minAuthAmount;
    }

    public void setMinAuthAmount(BigDecimal minAuthAmount) {
        this.minAuthAmount = minAuthAmount;
        support.setPropertyAssigned("minAuthAmount");
        support.setPropertyAssigned("minimalAuthorizeAmount");
    }

    public Map<String, CurrencyLocaleKey> getLocales() {
        return locales;
    }

    public void setLocales(Map<String, CurrencyLocaleKey> locales) {
        this.locales = locales;
        support.setPropertyAssigned("locales");
    }

    public String getLocaleAccuracy() {
        return localeAccuracy;
    }

    public void setLocaleAccuracy(String localeAccuracy) {
        this.localeAccuracy = localeAccuracy;
        support.setPropertyAssigned("localeAccuracy");
    }
}
