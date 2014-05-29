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
 * Postcode type of address validation response.
 */
@CompileStatic
@XStreamAlias("POSTCODE")
class Postcode {
    @XStreamAsAttribute
    @XStreamAlias('NAME')
    String name

    @Override
    public String toString() {
        return "Postcode{" +
                "name='" + name + '\'' +
                '}';
    }
}