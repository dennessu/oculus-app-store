/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.AgeRating;
import com.junbo.catalog.spec.model.common.BaseRevisionModel;
import com.junbo.catalog.spec.model.common.Price;
import com.junbo.common.id.OrganizationId;
import com.junbo.common.jackson.annotation.ItemId;
import com.junbo.common.jackson.annotation.ItemRevisionId;
import com.junbo.common.jackson.annotation.XSSFreeString;
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
    private String revisionId;

    /*@ApiModelProperty(position = 2, required = true,
            value = "An ID that helps to group like items. ex. TEAM_FORTRESS, this rollup ID would be applied to" +
                    "all items that are team fortress (PC, MAC, LINUX, etc)")
    private String rollupPackageName;
*/
    @XSSFreeString
    @ApiModelProperty(position = 3, required = true, value = "Used to identify the item (app), used mainly for android")
    private String packageName;

    @XSSFreeString
    @ApiModelProperty(position = 4, required = true,
            value = " the name of the file when download from the Oculus platform, must have value when Item type is downloadable, must be empty for other types")
    private String downloadName;

    @XSSFreeString
    @JsonProperty("distributionChannel")
    @ApiModelProperty(position = 5, required = true, value = "An array of strings indicates where this item is capable of being sold",
        allowableValues = "INAPP, STORE")
    private List<String> distributionChannels;

    @ApiModelProperty(position = 6, required = true,
            value = "release notes for this version of the item, what has been changed, what bug has been fixed, this will show in the product page")
    private String revisionNotes;

    @XSSFreeString
    @ApiModelProperty(position = 15, required = true, value = "Sku")
    private String sku;

    @ItemId
    @JsonProperty("iapHostItems")
    @ApiModelProperty(position = 16, required = false, value = "The items in which the IAP item will be sold.")
    private List<String> iapHostItemIds;

    @JsonProperty("developer")
    @ApiModelProperty(position = 20, required = true, value = "Organization owner of the item revision resource")
    private OrganizationId ownerId;

    @ItemId
    @JsonProperty("item")
    @ApiModelProperty(position = 21, required = true, value = "Item of the item revision")
    private String itemId;

    @ApiModelProperty(position = 22, required = true, value = "Manufacturer's suggested retail price")
    private Price msrp;

    @ApiModelProperty(position = 23, required = true, value = "supported input devices",
            allowableValues = "KEYBOARD, MOUSE, CONTROLLER, TOUCHPAD, GAMEPAD, HEAD_LOOK")
    private List<String> requiredInputDevices;
    @XSSFreeString
    @ApiModelProperty(position = 24, required = true, value = "supported input devices",
            allowableValues = "KEYBOARD, MOUSE, CONTROLLER, TOUCHPAD, GAMEPAD, HEAD_LOOK")
    private List<String> supportedInputDevices;
    @XSSFreeString
    @ApiModelProperty(position = 25, required = true, value = "User interaction modes",
            allowableValues = "SINGLE_USER, MULTI_USER, CO_OP")
    private List<String> userInteractionModes;
    @ApiModelProperty(position = 26, required = true, value = "Download Link", allowableValues = "PC, MAC, LINUX, ANDROID")
    private Map<String, Binary> binaries;

    @XSSFreeString
    @ApiModelProperty(position = 27, required = true, value = "The platform name, for digital goods only",
            allowableValues = "PC, MAC, LINUX, ANDROID")
    private List<String> platforms;

    @ApiModelProperty(position = 28, required = true,
            value = "The content ratings given to the item by specific boards (ESRB, PEGI)")
    private Map<String, List<AgeRating>> ageRatings;
    @ApiModelProperty(position = 29, required = true,
            value = "This is the calculated value to give how accurate the localizable attributes is.",
            allowableValues = "HIGH, MEDIUM, LOW")
    private String localeAccuracy;
    @ApiModelProperty(position = 30, required = true, value = "Supported locales.")
    private Map<String, SupportedLocale> supportedLocales;

    @ApiModelProperty(position = 40, required = true, value = "Locale properties of the item revision")
    private Map<String, ItemRevisionLocaleProperties> locales;
    @ApiModelProperty(position = 41, required = true, value = "Country properties of the item revision")
    private Map<String, ItemRevisionCountryProperties> countries;
    @ApiModelProperty(position = 42, required = true,
            value = " Information used to create entitlements when an end-user purchases this Item/Item-Revision")
    private List<EntitlementDef> entitlementDefs;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDownloadName() {
        return downloadName;
    }

    public void setDownloadName(String downloadName) {
        this.downloadName = downloadName;
    }

    public String getRevisionNotes() {
        return revisionNotes;
    }

    public void setRevisionNotes(String revisionNotes) {
        this.revisionNotes = revisionNotes;
    }

    public List<String> getDistributionChannels() {
        return distributionChannels;
    }

    public void setDistributionChannels(List<String> distributionChannels) {
        this.distributionChannels = distributionChannels;
    }

    public List<String> getIapHostItemIds() {
        return iapHostItemIds;
    }

    public void setIapHostItemIds(List<String> iapHostItemIds) {
        this.iapHostItemIds = iapHostItemIds;
    }

    public String getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }

    public OrganizationId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(OrganizationId ownerId) {
        this.ownerId = ownerId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Price getMsrp() {
        return msrp;
    }

    public void setMsrp(Price msrp) {
        this.msrp = msrp;
    }

    public List<String> getRequiredInputDevices() {
        return requiredInputDevices;
    }

    public void setRequiredInputDevices(List<String> requiredInputDevices) {
        this.requiredInputDevices = requiredInputDevices;
    }

    public List<String> getSupportedInputDevices() {
        return supportedInputDevices;
    }

    public void setSupportedInputDevices(List<String> supportedInputDevices) {
        this.supportedInputDevices = supportedInputDevices;
    }

    public List<String> getUserInteractionModes() {
        return userInteractionModes;
    }

    public void setUserInteractionModes(List<String> userInteractionModes) {
        this.userInteractionModes = userInteractionModes;
    }

    public Map<String, Binary> getBinaries() {
        return binaries;
    }

    public void setBinaries(Map<String, Binary> binaries) {
        this.binaries = binaries;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<String> platforms) {
        this.platforms = platforms;
    }

    public Map<String, List<AgeRating>> getAgeRatings() {
        return ageRatings;
    }

    public void setAgeRatings(Map<String, List<AgeRating>> ageRatings) {
        this.ageRatings = ageRatings;
    }

    public String getLocaleAccuracy() {
        return localeAccuracy;
    }

    public void setLocaleAccuracy(String localeAccuracy) {
        this.localeAccuracy = localeAccuracy;
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

    public List<EntitlementDef> getEntitlementDefs() {
        return entitlementDefs;
    }

    public void setEntitlementDefs(List<EntitlementDef> entitlementDefs) {
        this.entitlementDefs = entitlementDefs;
    }

    public Map<String, SupportedLocale> getSupportedLocales() {
        return supportedLocales;
    }

    public void setSupportedLocales(Map<String, SupportedLocale> supportedLocales) {
        this.supportedLocales = supportedLocales;
    }

    @Override
    @JsonIgnore
    public String getId() {
        return revisionId;
    }

    @Override
    public void setId(String id) {
        this.revisionId = id;
    }

    @Override
    @JsonIgnore
    public String getEntityId() {
        return itemId;
    }
}
