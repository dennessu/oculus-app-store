package com.junbo.payment.clientproxy.facebook;

/**
 * Created by wenzhu on 12/2/14.
 */
public class FacebookCreditCardTokenRequest {
    private String creditCardNumber;
    private String csc;

    @Override
    public String toString() {
        return "creditCardNumber=" + this.creditCardNumber + "&csc=" + this.csc;
    }

    public String getCsc() {
        return csc;
    }

    public void setCsc(String csc) {
        this.csc = csc;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }
}
