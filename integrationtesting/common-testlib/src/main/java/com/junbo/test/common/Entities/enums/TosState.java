/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.Entities.enums;

/**
 * Enum for Tos state.
 *
 * @author Jason
 * Created on 5/14/2014.
 */
public enum TosState {

    DRAFT("DRAFT"),
    APPROVED("APPROVED"),
    OBSOLETE("OBSOLETE");

    private String type;

    private TosState(String type) {
        this.type = type;
    }

    public String getState() {
        return type;
    }

}
