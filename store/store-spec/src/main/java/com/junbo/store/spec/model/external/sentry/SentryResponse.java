// CHECKSTYLE:OFF

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.sentry;

/**
 * Created by liangfu on 9/24/14.
 */
public class SentryResponse extends BaseSentryModel {

    private static final String BLOCK_ACCESS = "BlockAccess";

    private String restriction;
    private String fitler;
    private String policy;
    private String code;
    private String xargs;
    private String target_feature;
    private String admin_id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    private String title;
    private String body;

    public String getRestriction() {
        return restriction;
    }

    public void setRestriction(String restriction) {
        this.restriction = restriction;
    }

    public String getFitler() {
        return fitler;
    }

    public void setFitler(String fitler) {
        this.fitler = fitler;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getXargs() {
        return xargs;
    }

    public void setXargs(String xargs) {
        this.xargs = xargs;
    }

    public String getTarget_feature() {
        return target_feature;
    }

    public void setTarget_feature(String target_feature) {
        this.target_feature = target_feature;
    }

    public String getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(String admin_id) {
        this.admin_id = admin_id;
    }

    public boolean isBlockAccess() {
        return BLOCK_ACCESS.equals(restriction);
    }
}
