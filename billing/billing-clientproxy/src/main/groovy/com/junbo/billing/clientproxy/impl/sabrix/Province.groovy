/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import com.thoughtworks.xstream.annotations.XStreamAlias
import groovy.transform.CompileStatic

/**
 * Province type of address validation response.
 */
@CompileStatic
@XStreamAlias("PROVINCE")
class Province {
    @XStreamAlias('NAME')
    String name

    @XStreamAlias('CODE')
    String code

    @XStreamAlias('CODE3')
    String code3

    @XStreamAlias('ISOCODE')
    String isocode


    @Override
    public String toString() {
        return "Province{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", code3='" + code3 + '\'' +
                ", isocode='" + isocode + '\'' +
                '}';
    }
}
