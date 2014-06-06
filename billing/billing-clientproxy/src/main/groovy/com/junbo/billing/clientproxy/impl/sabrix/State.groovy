/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import com.thoughtworks.xstream.annotations.XStreamAlias
import groovy.transform.CompileStatic

/**
 * State type of address validation response.
 */
@CompileStatic
@XStreamAlias("STATE")
class State {
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
        return "State{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", code3='" + code3 + '\'' +
                ", isocode='" + isocode + '\'' +
                '}';
    }
}
