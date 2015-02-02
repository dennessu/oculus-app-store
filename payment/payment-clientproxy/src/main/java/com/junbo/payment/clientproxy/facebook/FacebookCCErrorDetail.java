package com.junbo.payment.clientproxy.facebook;

/**
 * Created by wenzhumac on 1/31/15.
 */
public class FacebookCCErrorDetail {
    private String message;
    private String type;
    private String code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
