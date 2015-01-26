/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.order.spec.model;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by acer on 2015/1/25.
 */
public class SubledgerKeyInfo {

    private String fbPayoutOrgId;

    private boolean isPhysical;

    private boolean isMixTxn;

    public String getSubledgerKey() {
        return StringUtils.join(Arrays.asList(
                fbPayoutOrgId == null ? "" : fbPayoutOrgId,
                String.valueOf(isPhysical ? 1 : 0),
                String.valueOf(isMixTxn ? 1 : 0)), ';');
    }

    public String getFbPayoutOrgId() {
        return fbPayoutOrgId;
    }

    public void setFbPayoutOrgId(String fbPayoutOrgId) {
        this.fbPayoutOrgId = fbPayoutOrgId;
    }

    public boolean isPhysical() {
        return isPhysical;
    }

    public void setPhysical(boolean isPhysical) {
        this.isPhysical = isPhysical;
    }

    public boolean isMixTxn() {
        return isMixTxn;
    }

    public void setMixTxn(boolean isMixTxn) {
        this.isMixTxn = isMixTxn;
    }

    public Map<String, String> toProperties() {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("fbPayoutOrgId", fbPayoutOrgId);
        properties.put("mixTxn", String.valueOf(isMixTxn));
        properties.put("'physical'", String.valueOf(isPhysical));
        return properties;
    }

    public static SubledgerKeyInfo fromProperties(Map<String, String> properties) {
        if (properties == null) {
            return null;
        }
        SubledgerKeyInfo subledgerKeyInfo = new SubledgerKeyInfo();
        subledgerKeyInfo.fbPayoutOrgId = properties.get("fbPayoutOrgId");
        subledgerKeyInfo.isMixTxn = Boolean.valueOf(properties.get("mixTxn"));
        subledgerKeyInfo.isPhysical = Boolean.valueOf(properties.get("isPhysical"));
        return subledgerKeyInfo;
    }
}
