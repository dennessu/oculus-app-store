/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.csr.entity.enums;

/**
 * Created by weiyu_000 on 11/26/14.
 */
public enum SearchType {
    USERID("USERID"),
    USERNAME("USERNAME"),
    EMAIL("EMAIL"),
    FULLNAME("FULLNAME"),
    PHONENUMBER("PHONENUMBER");

    private String name;

    SearchType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
