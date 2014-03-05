package com.goshop.langur.processor

import groovy.transform.CompileStatic

import javax.lang.model.element.Element

@CompileStatic
class ProcessingException extends RuntimeException {

    private final Element element

    public ProcessingException(String message) {
        super(message)
    }

    public ProcessingException(String message, Throwable cause) {
        super(message, cause)
    }

    public ProcessingException(String message, Element element) {
        super(message);

        this.element = element
    }

    public Element getElement() {
        return element
    }
}

