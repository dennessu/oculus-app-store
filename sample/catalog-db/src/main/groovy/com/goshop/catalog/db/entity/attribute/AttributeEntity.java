package com.goshop.catalog.db.entity.attribute;


/**
 * Created by kevingu on 11/21/13.
 */
public class AttributeEntity {
    private String key;
    private String value;
    private String country;
    private String locale;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
