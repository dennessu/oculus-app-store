/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.store.spec.model;

/**
 * The ResponseStatus enum.
 */
public enum ResponseStatus {

    SUCCESS,
    FAIL;

    /**
     * The Detail enum.
     */
    public enum Detail {
        USERNAME_ALREADY_USED,
        USER_CREDENTIAL_INVALID,
        USER_NOT_FOUND
    }

}
