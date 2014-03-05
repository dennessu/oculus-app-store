/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor

import groovy.transform.CompileStatic

import javax.lang.model.element.Element
/**
 * Created by kevingu on 11/28/13.
 */
@CompileStatic
class ProcessingException extends RuntimeException {

    private final Element element

    ProcessingException(String message) {
        super(message)
    }

    ProcessingException(String message, Throwable cause) {
        super(message, cause)
    }

    ProcessingException(String message, Element element) {
        super(message)

        this.element = element
    }

    Element getElement() {
        element
    }
}

