package com.junbo.payment.clientproxy.facebook;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by wenzhu on 2/4/15.
 */
public class FacebookRiskResult {
    @JsonProperty("fraud_status")
    private FacebookFraudStatus fraudStatus;

    public FacebookFraudStatus getFraudStatus() {
        return fraudStatus;
    }

    public void setFraudStatus(FacebookFraudStatus fraudStatus) {
        this.fraudStatus = fraudStatus;
    }
}
