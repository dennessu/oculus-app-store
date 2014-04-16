/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.id;

/**
 * string type token set id.
 */
@IdResourcePath("/token-sets/{0}")
public class TokenSetId extends Id {
    public TokenSetId(){

    }
    public TokenSetId(Long value){
        super(value);
    }
}
