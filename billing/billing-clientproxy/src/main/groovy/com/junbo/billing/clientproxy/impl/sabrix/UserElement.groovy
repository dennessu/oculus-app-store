/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import com.thoughtworks.xstream.annotations.XStreamAlias
import groovy.transform.CompileStatic

/**
 * Custom attribute for Sabrix.
 */
@CompileStatic
@XStreamAlias('USER_ELEMENT')
class UserElement {
    @XStreamAlias('NAME')
    String name

    @XStreamAlias('VALUE')
    String value


    @Override
    public String toString() {
        return "UserElement{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
