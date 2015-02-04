package com.junbo.payment.clientproxy.facebook;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.payment.common.exception.AppServerExceptions;

/**
 * Created by wenzhu on 1/19/15.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FacebookRiskFeature {
    @JsonProperty("BrowserName")
    private String browserName;
    @JsonProperty("BrowserVersion")
    private String browserVersion;
    @JsonProperty("CurrencyPurchasing")
    private String currencyPurchasing;
    @JsonProperty("InstalledApps")
    private Integer[] installedApps;
    @JsonProperty("TimeSinceUserAccountCreatedInDays")
    private Integer timeSinceUserAccountCreatedInDays;
    @JsonProperty("SourceDATR")
    private String sourceDatr;
    @JsonProperty("PlatformName")
    private String platformName;
    @JsonProperty("PlatformVersion")
    private String platformVersion;
    @JsonProperty("SourceCountry")
    private String sourceCountry;

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this) ;
        } catch (JsonProcessingException e) {
            throw AppServerExceptions.INSTANCE.errorSerialize("FacebookRiskFeature").exception();
        }
    }

    public String toBatchString() {
        return this.toString().replace("\"", "'");
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public void setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
    }

    public String getCurrencyPurchasing() {
        return currencyPurchasing;
    }

    public void setCurrencyPurchasing(String currencyPurchasing) {
        this.currencyPurchasing = currencyPurchasing;
    }

    public Integer[] getInstalledApps() {
        return installedApps;
    }

    public void setInstalledApps(Integer[] installedApps) {
        this.installedApps = installedApps;
    }

    public Integer getTimeSinceUserAccountCreatedInDays() {
        return timeSinceUserAccountCreatedInDays;
    }

    public void setTimeSinceUserAccountCreatedInDays(Integer timeSinceUserAccountCreatedInDays) {
        this.timeSinceUserAccountCreatedInDays = timeSinceUserAccountCreatedInDays;
    }

    public String getSourceDatr() {
        return sourceDatr;
    }

    public void setSourceDatr(String sourceDatr) {
        this.sourceDatr = sourceDatr;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getPlatformVersion() {
        return platformVersion;
    }

    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }

    public String getSourceCountry() {
        return sourceCountry;
    }

    public void setSourceCountry(String sourceCountry) {
        this.sourceCountry = sourceCountry;
    }
}
