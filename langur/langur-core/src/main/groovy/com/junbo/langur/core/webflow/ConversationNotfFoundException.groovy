package com.junbo.langur.core.webflow

import groovy.transform.CompileStatic

/**
 * Created by kg on 2/26/14.
 */
@CompileStatic
class ConversationNotfFoundException extends FlowException {

    ConversationNotfFoundException() {
    }

    ConversationNotfFoundException(String s) {
        super(s)
    }

    ConversationNotfFoundException(String s, Throwable throwable) {
        super(s, throwable)
    }

    ConversationNotfFoundException(Throwable throwable) {
        super(throwable)
    }

}
