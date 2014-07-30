/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.token.model.enums;

/**
 * Created by weiyu_000 on 7/30/14.
 */
public enum  ItemStatus {
    ACTIVATED("ACTIVATED"),
    DEACTIVATED("DEACTIVATED"),
    BLACKLISTED("BLACKLISTED"),
    USED("USED");

    String status;

    private ItemStatus(String status){
        this.status= status;
    }

    @Override
    public String toString(){
        return status;
    }

}
