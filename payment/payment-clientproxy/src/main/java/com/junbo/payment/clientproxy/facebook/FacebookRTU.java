package com.junbo.payment.clientproxy.facebook;

/**
 * Created by wenzhu on 1/28/15.
 */
public class FacebookRTU {
    private String object;
    private FacebookRTUEntry[] entry;

    public FacebookRTUEntry[] getEntry() {
        return entry;
    }

    public void setEntry(FacebookRTUEntry[] entry) {
        this.entry = entry;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }
}
