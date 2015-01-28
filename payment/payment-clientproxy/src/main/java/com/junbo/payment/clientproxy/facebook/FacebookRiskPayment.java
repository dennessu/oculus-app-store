package com.junbo.payment.clientproxy.facebook;

/**
 * Created by wenzhu on 1/28/15.
 */
public class FacebookRiskPayment {
    public String getFraud_status() {
        return fraud_status;
    }

    public void setFraud_status(String fraud_status) {
        this.fraud_status = fraud_status;
    }

    private String fraud_status;
}
