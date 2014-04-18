/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.*;
import com.junbo.common.jackson.annotation.ItemRevisionId;
import com.junbo.common.jackson.annotation.OfferId;
import com.junbo.common.jackson.annotation.UserId;
import com.wordnik.swagger.annotations.ApiModelProperty;

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

    @ApiModelProperty(position = 2, required = true, value = "Item type",
            allowableValues = "PHYSICAL, DIGITAL, WALLET, SUBSCRIPTION")
    private String type;

    @UserId
    @JsonProperty("developer")
    @ApiModelProperty(position = 20, required = true, value = "Developer of the item revision resource")
    private Long ownerId;

    @OfferId
    @JsonProperty("item")
    @ApiModelProperty(position = 21, required = true, value = "Item of the item revision")
    private Long itemId;

    @ApiModelProperty(position = 22, required = true, value = "msrp")
    private Price msrp;

    @ApiModelProperty(position = 24, required = true, value = "Item version")
    private String version;
    @ApiModelProperty(position = 25, required = true, value = "Download Link")
    private Binaries binaries;
    @ApiModelProperty(position = 26, required = false, value = "Website for the item revision resource")
    private String website;
    @ApiModelProperty(position = 27, required = false, value = "Support email")
    private String supportEmail;
    @ApiModelProperty(position = 28, required = false, value = "Support phone")
    private String supportPhone;
    @ApiModelProperty(position = 29, required = false, value = "Images to describe the item revision resource")
    private Images images;
    @ApiModelProperty(position = 30, required = false, value = "Videos to describe the item revision resource")
    private List<Video> videos;
    @ApiModelProperty(position = 31, required = false, value = "Manual document for the item revision resource")
    private String manualDocument;
    @ApiModelProperty(position = 32, required = false, value = "Community forum link of the item revision resource")
    private String communityForumLink;
    @ApiModelProperty(position = 33, required = true, value = "The platform name, for digital goods only",
            allowableValues = "PC, MAC, LINUX, ANDROID")
    private List<String> platforms;
    @ApiModelProperty(position = 34, required = true, value = "supported input devices",
            allowableValues = "KEYBOARD, MOUSE")
    List<String> supportedInputDevices;
    @ApiModelProperty(position = 35, required = true, value = "game modes",
            allowableValues = "SINGLE_PLAYER, MULTI_PLAYER, CO_OP")
    String gameModes;
    //@ApiModelProperty(position = 33, required = true, value = "Age ratings for the item revision resource")
    //private Map<String, AgeRatingId> ageRatings;
    @ApiModelProperty(position = 36, required = true,
            value = "The content rating given to the item by a specific board (ESRB, PEGI)")
    private List<AgeRating> ageRatings;
    @ApiModelProperty(position = 37, required = true, value = "Wallet currency type",
            allowableValues = "REAL_CURRENCY, VIRTUAL_CURRENCY")
    private String walletCurrencyType;
    @ApiModelProperty(position = 38, required = true, value = "Wallet currency")
    private String walletCurrency;
    @ApiModelProperty(position = 39, required = true, value = "Wallet amount")
    private String walletAmount;

    @ApiModelProperty(position = 40, required = true, value = "Locale properties of the item revision")
    private Map<String, ItemRevisionLocaleProperties> locales;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Price getMsrp() {
        return msrp;
    }

    public void setMsrp(Price msrp) {
        this.msrp = msrp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Binaries getBinaries() {
        return binaries;
    }

    public void setBinaries(Binaries binaries) {
        this.binaries = binaries;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
    }

    public String getSupportPhone() {
        return supportPhone;
    }

    public void setSupportPhone(String supportPhone) {
        this.supportPhone = supportPhone;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public String getManualDocument() {
        return manualDocument;
    }

    public void setManualDocument(String manualDocument) {
        this.manualDocument = manualDocument;
    }

    public String getCommunityForumLink() {
        return communityForumLink;
    }

    public void setCommunityForumLink(String communityForumLink) {
        this.communityForumLink = communityForumLink;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        this.platforms = platforms;
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

    public List<AgeRating> getAgeRatings() {
        return ageRatings;
    }

    public void setAgeRatings(List<AgeRating> ageRatings) {
        this.ageRatings = ageRatings;
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

    public String getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(String walletAmount) {
        this.walletAmount = walletAmount;
    }

    public Map<String, ItemRevisionLocaleProperties> getLocales() {
        return locales;
    }

    public void setLocales(Map<String, ItemRevisionLocaleProperties> locales) {
        this.locales = locales;
    }

    @Override
    @JsonIgnore
    public Long getEntityId() {
        return itemId;
    }
}
