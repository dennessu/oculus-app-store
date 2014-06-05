/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.authorization.spec.model;

import com.junbo.common.model.ResourceMeta;
import groovy.transform.CompileStatic;

import java.util.List;
import java.util.Map;

/**
 * ApiDefinition.
 */
@CompileStatic
public class ApiDefinition extends ResourceMeta<String> {
    private String apiName;
    private Map<String, List<MatrixRow>> scopes;
    private String revision;

    @Override
    public String getId() {
        return apiName;
    }

    @Override
    public void setId(String id) {
        this.apiName = id;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public Map<String, List<MatrixRow>> getScopes() {
        return scopes;
    }

    public void setScopes(Map<String, List<MatrixRow>> scopes) {
        this.scopes = scopes;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }
}
