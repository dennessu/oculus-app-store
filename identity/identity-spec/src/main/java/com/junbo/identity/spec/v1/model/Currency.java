/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.jackson.annotation.HateoasLink;
import com.junbo.common.model.Link;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.junbo.common.util.Identifiable;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiali_000 on 4/21/2014.
 */
public class Currency extends PropertyAssignedAwareResourceMeta implements Identifiable<CurrencyId> {

    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable]The id of the currency resource.")
    @JsonProperty("self")
    private CurrencyId id;

    @ApiModelProperty(position = 2, required = true, value = "[Nullable]The currency code of the currency " +
            "resource must be same with ID.")
    private String currencyCode;

    @ApiModelProperty(position = 3, required = false, value = "[Nullable]Countries link with default currency specified")
    @HateoasLink("/countries?defaultCurrencyId={id}")
    private Link countries;

    @ApiModelProperty(position = 4, required = true, value = "The symbol of the currency resource.")
    private String symbol;

    @ApiModelProperty(position = 5, required = true, value = "Position to put the symbol when show currency number (BEFORE, AFTER, etc).")
    private String symbolPosition;

    @ApiModelProperty(position = 6, required = true, value = "The symbol of the currency decimal.")
    private String decimalSymbol;

    @ApiModelProperty(position = 7, required = true, value = "The number of digits after decimal.")
    private Integer numberAfterDecimal;

    @ApiModelProperty(position = 8, required = true, value = "Format for the negative currency (MINUS,BRACE, etc).")
    private String negativeFormat;

    @ApiModelProperty(position = 9, required = true, value = "The symbol of the digital grouping (,)")
    private String digitGroupingSymbol;

    @ApiModelProperty(position = 10, required = true, value = "The length of digits to group together.")
    private Integer digitGroupingLength;

    @ApiModelProperty(position = 11, required = true, value = "The supported locales of the currency resource.")
    private Map<String, String> localeKeys = new HashMap<>();

    @ApiModelProperty(position = 12, required = true, value = "The future expansion of the currency resource.")
    private Map<String, JsonNode> futureExpansion = new HashMap<>();

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

    public String getDecimalSymbol() {
        return decimalSymbol;
    }

    public void setDecimalSymbol(String decimalSymbol) {
        this.decimalSymbol = decimalSymbol;
        support.setPropertyAssigned("decimalSymbol");
    }

    public Integer getNumberAfterDecimal() {
        return numberAfterDecimal;
    }

    public void setNumberAfterDecimal(Integer numberAfterDecimal) {
        this.numberAfterDecimal = numberAfterDecimal;
        support.setPropertyAssigned("numberAfterDecimal");
    }

    public String getNegativeFormat() {
        return negativeFormat;
    }

    public void setNegativeFormat(String negativeFormat) {
        this.negativeFormat = negativeFormat;
        support.setPropertyAssigned("negativeFormat");
    }

    public String getDigitGroupingSymbol() {
        return digitGroupingSymbol;
    }

    public void setDigitGroupingSymbol(String digitGroupingSymbol) {
        this.digitGroupingSymbol = digitGroupingSymbol;
        support.setPropertyAssigned("digitGroupingSymbol");
    }

    public Integer getDigitGroupingLength() {
        return digitGroupingLength;
    }

    public void setDigitGroupingLength(Integer digitGroupingLength) {
        this.digitGroupingLength = digitGroupingLength;
        support.setPropertyAssigned("digitGroupingLength");
    }

    public Map<String, JsonNode> getFutureExpansion() {
        return futureExpansion;
    }

    public void setFutureExpansion(Map<String, JsonNode> futureExpansion) {
        this.futureExpansion = futureExpansion;
        support.setPropertyAssigned("futureExpansion");
    }

    public Map<String, String> getLocaleKeys() {
        return localeKeys;
    }

    public void setLocaleKeys(Map<String, String> localeKeys) {
        this.localeKeys = localeKeys;
        support.setPropertyAssigned("localeKeys");
    }
}
