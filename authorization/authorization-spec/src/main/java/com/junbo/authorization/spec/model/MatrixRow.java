/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.authorization.spec.model;

import groovy.transform.CompileStatic;

import java.util.List;

/**
 * MatrixRow.
 */
@CompileStatic
public class MatrixRow {
    private String scriptType;
    private String precondition;
    private Boolean breakOnMatch;
    private List<String> rights;

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }

    public String getPrecondition() {
        return precondition;
    }

    public void setPrecondition(String precondition) {
        this.precondition = precondition;
    }

    public Boolean getBreakOnMatch() {
        return breakOnMatch;
    }

    public void setBreakOnMatch(Boolean breakOnMatch) {
        this.breakOnMatch = breakOnMatch;
    }

    public List<String> getRights() {
        return rights;
    }

    public void setRights(List<String> rights) {
        this.rights = rights;
    }
}
