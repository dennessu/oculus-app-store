package com.junbo.payment.clientproxy.facebook;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.payment.common.exception.AppServerExceptions;

/**
 * Created by wenzhu on 12/2/14.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FacebookBatch {
    private String method;
    @JsonProperty("relative_url")
    private String relativeUrl;
    private String body;
    private String name;
    @JsonProperty("omit_response_on_success")
    private boolean omitResponse = true;

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this) ;
        } catch (JsonProcessingException e) {
            throw AppServerExceptions.INSTANCE.errorSerialize("Facebook Batch").exception();
        }
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRelativeUrl() {
        return relativeUrl;
    }

    public void setRelativeUrl(String relativeUrl) {
        this.relativeUrl = relativeUrl;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOmitResponse() {
        return omitResponse;
    }

    public void setOmitResponse(boolean omitResponse) {
        this.omitResponse = omitResponse;
    }

}
