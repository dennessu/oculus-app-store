package com.junbo.payment.clientproxy.facebook;

/**
 * Created by wenzhu on 1/28/15.
 */
public class FacebookRTUEntry {
    private String id;
    private String time;
    private String[] changed_fields;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String[] getChanged_fields() {
        return changed_fields;
    }

    public void setChanged_fields(String[] changed_fields) {
        this.changed_fields = changed_fields;
    }
}
