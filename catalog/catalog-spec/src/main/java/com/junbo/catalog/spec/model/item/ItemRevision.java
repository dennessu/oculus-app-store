/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.*;
import com.junbo.common.jackson.annotation.CurrencyId;
import com.junbo.common.jackson.annotation.ItemRevisionId;
import com.junbo.common.jackson.annotation.OfferId;
import com.junbo.common.jackson.annotation.UserId;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * Item revision.
 */
public class ItemRevision extends BaseRevisionModel {
    @ItemRevisionId
    @JsonProperty("self")
    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable] The id of item revision resource")
    private Long revisionId;

    @UserId
    @JsonProperty("developer")
    @ApiModelProperty(position = 20, required = true, value = "Developer of the item revision resource")
    private Long ownerId;

    @OfferId
    @JsonProperty("item")
    @ApiModelProperty(position = 21, required = true, value = "Item of the item revision")
    private Long itemId;

    @ApiModelProperty(position = 22, required = true, value = "Manufacturer's suggested retail price")
    private Price msrp;

    @ApiModelProperty(position = 23, required = false, value = "Website for the item revision resource")
    private String website;

    @ApiModelProperty(position = 24, required = true, value = "supported input devices",
            allowableValues = "KEYBOARD, MOUSE")
    List<String> supportedInputDevices;
    @ApiModelProperty(position = 25, required = true, value = "game modes",
            allowableValues = "SINGLE_PLAYER, MULTI_PLAYER, CO_OP")
    String gameModes;
    @ApiModelProperty(position = 26, required = true, value = "Download Link", allowableValues = "PC, MAC, LINUX")
    private Map<String, Binary> binaries;

    @ApiModelProperty(position = 30, required = true, value = "Wallet currency type",
            allowableValues = "REAL_CURRENCY, VIRTUAL_CURRENCY")
    private String walletCurrencyType;
    @CurrencyId
    @ApiModelProperty(position = 31, required = true, value = "Wallet currency")
    private String walletCurrency;
    @ApiModelProperty(position = 32, required = true, value = "Wallet amount")
    private BigDecimal walletAmount;

    @ApiModelProperty(position = 40, required = true, value = "Locale properties of the item revision")
    private Map<String, ItemRevisionLocaleProperties> locales;
    @ApiModelProperty(position = 41, required = true, value = "Country properties of the item revision")
    private Map<String, ItemRevisionCountryProperties> countries;

    public Long getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(Long revisionId) {
        this.revisionId = revisionId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Price getMsrp() {
        return msrp;
    }

    public void setMsrp(Price msrp) {
        this.msrp = msrp;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public List<String> getSupportedInputDevices() {
        return supportedInputDevices;
    }

    public void setSupportedInputDevices(List<String> supportedInputDevices) {
        this.supportedInputDevices = supportedInputDevices;
    }

    public String getGameModes() {
        return gameModes;
    }

    public void setGameModes(String gameModes) {
        this.gameModes = gameModes;
    }

    public Map<String, Binary> getBinaries() {
        return binaries;
    }

    public void setBinaries(Map<String, Binary> binaries) {
        this.binaries = binaries;
    }

    public String getWalletCurrencyType() {
        return walletCurrencyType;
    }

    public void setWalletCurrencyType(String walletCurrencyType) {
        this.walletCurrencyType = walletCurrencyType;
    }

    public String getWalletCurrency() {
        return walletCurrency;
    }

    public void setWalletCurrency(String walletCurrency) {
        this.walletCurrency = walletCurrency;
    }

    public BigDecimal getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(BigDecimal walletAmount) {
        this.walletAmount = walletAmount;
    }

    public Map<String, ItemRevisionLocaleProperties> getLocales() {
        return locales;
    }

    public void setLocales(Map<String, ItemRevisionLocaleProperties> locales) {
        this.locales = locales;
    }

    public Map<String, ItemRevisionCountryProperties> getCountries() {
        return countries;
    }

    public void setCountries(Map<String, ItemRevisionCountryProperties> countries) {
        this.countries = countries;
    }

    @Override
    @JsonIgnore
    public Long getEntityId() {
        return itemId;
    }
}
