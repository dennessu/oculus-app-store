package com.junbo.payment.clientproxy.facebook;

/**
 * Created by wenzhumac on 1/31/15.
 */
public class FacebookCCBatchError {
    public FacebookCCErrorDetail getError() {
        return error;
    }

    public void setError(FacebookCCErrorDetail error) {
        this.error = error;
    }

    private FacebookCCErrorDetail error;

}
