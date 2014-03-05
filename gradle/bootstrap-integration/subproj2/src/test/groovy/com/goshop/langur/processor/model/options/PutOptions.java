package com.goshop.langur.processor.model.options;


import javax.ws.rs.QueryParam;

public class PutOptions {

    @QueryParam("body")
    private Boolean body;

    @QueryParam("expand")
    private String expand;

    public Boolean getBody() {
        return body;
    }

    public void setBody(Boolean body) {
        this.body = body;
    }

    public String getExpand() {
        return expand;
    }

    public void setExpand(String expand) {
        this.expand = expand;
    }
}
