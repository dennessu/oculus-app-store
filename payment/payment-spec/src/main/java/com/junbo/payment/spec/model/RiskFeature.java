package com.junbo.payment.spec.model;

/**
 * Created by wenzhu on 1/19/15.
 */
public class RiskFeature {
    private String browserName;
    private String browserVersion;
    private String currencyPurchasing;
    private Integer[] installedApps;
    private Integer timeSinceUserAccountCreatedInDays;
    private String sourceDatr;
    private String platformName;
    private String platformVersion;
    private String sourceCountry;

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
