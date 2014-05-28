/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import groovy.transform.CompileStatic

/**
 * Province type of address validation response.
 */
@CompileStatic
@XStreamAlias("PROVINCE")
class Province {
    @XStreamAsAttribute
    @XStreamAlias('NAME')
    String name

    @XStreamAsAttribute
    @XStreamAlias('CODE')
    String code


    @Override
    public String toString() {
        return "Province{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
