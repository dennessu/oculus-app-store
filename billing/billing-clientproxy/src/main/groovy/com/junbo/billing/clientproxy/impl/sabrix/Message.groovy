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
 * Message Type for address validation response.
 */
@CompileStatic
@XStreamAlias("MESSAGE")
class Message {
    @XStreamAsAttribute
    @XStreamAlias('LOCATION')
    String location

    @XStreamAsAttribute
    @XStreamAlias('CATEGORY')
    String category

    @XStreamAsAttribute
    @XStreamAlias('CODE')
    String code

    @XStreamAsAttribute
    @XStreamAlias('MESSAGE_TEXT')
    String messageText

    @XStreamAsAttribute
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
