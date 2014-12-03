package com.junbo.payment.clientproxy.facebook;

/**
 * Created by wenzhu on 12/3/14.
 */
public class FacebookCCBatchResponse {

    private Integer code;
    private FacebookBatchHeader[] headers;
    private String body;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public FacebookBatchHeader[] getHeaders() {
        return headers;
    }

    public void setHeaders(FacebookBatchHeader[] headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
