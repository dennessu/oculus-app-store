package com.junbo.store.clientproxy.casey

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.langur.core.client.ExceptionHandler
import com.junbo.langur.core.client.MessageTranscoder
import com.junbo.langur.core.client.TypeReference
import com.junbo.store.spec.exception.casey.CaseyException

import com.ning.http.client.Response
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Required

/**
 * The CaseyExceptionHandler class.
 */
@CompileStatic
class CaseyExceptionHandler implements ExceptionHandler {

    private MessageTranscoder messageTranscoder

    @Required
    void setMessageTranscoder(MessageTranscoder messageTranscoder) {
        this.messageTranscoder = messageTranscoder
    }

    @Override
    void handleExceptionResponse(Response response) {
        String body = response.getResponseBody()
        JsonNode errorNode = null
        if (!StringUtils.isBlank(body)) {
            errorNode = messageTranscoder.decode(new TypeReference<JsonNode>() {}, body) as JsonNode
        }
        String message = "Casey_Return_Error, status=${response.statusCode}, length=${body.length()}, response:\n${body}"
        throw new CaseyException(errorNode?.get('code')?.textValue(), errorNode.get('details'), message)
    }
}