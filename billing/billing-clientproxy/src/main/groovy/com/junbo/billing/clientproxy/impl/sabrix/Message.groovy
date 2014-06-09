/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.sabrix

import com.thoughtworks.xstream.annotations.XStreamAlias
import groovy.transform.CompileStatic

/**
 * Message Type for address validation response.
 */
@CompileStatic
@XStreamAlias('MESSAGE')
class Message {
    @XStreamAlias('LOCATION')
    String location

    @XStreamAlias('CATEGORY')
    String category

    @XStreamAlias('CODE')
    String code

    @XStreamAlias('MESSAGE_TEXT')
    String messageText

    @XStreamAlias('SEVERITY')
    Integer severity


    @Override
    public String toString() {
        return "Message{" +
                "location='" + location + '\'' +
                ", category='" + category + '\'' +
                ", code='" + code + '\'' +
                ", messageText='" + messageText + '\'' +
                ", severity=" + severity +
                '}';
    }
}
