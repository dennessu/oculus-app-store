/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.clientproxy.impl


import static com.ning.http.client.extra.ListenableFutureAdapter.asGuavaFuture

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.junbo.email.clientproxy.MandrillFacade
import com.junbo.email.clientproxy.impl.mandrill.*
import com.junbo.email.spec.error.AppErrors
import com.junbo.email.spec.model.Email
import com.junbo.langur.core.promise.Promise
import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.AsyncHttpClientConfigBean
import com.ning.http.client.Response
import org.springframework.beans.factory.annotation.Autowired
import groovy.transform.CompileStatic

/**
 * Impl of MandrillFacade.
 */
@CompileStatic
class MandrillFacadeImpl implements MandrillFacade {
    private static final String TO_TYPE='to'
    private static final String VARS_NAME ='name'
    private static final String VARS_CONTENT ='content'
    private static final String CONTENT_TYPE ='Content-Type'
    private static final String APPLICATION_JSON ='application/json'

    @Autowired
    private MandrillConfiguration configuration

    @Autowired
    final AsyncHttpClient asyncHttpClient

    MandrillFacadeImpl() {
        if (asyncHttpClient == null) {
           asyncHttpClient = new AsyncHttpClient(new AsyncHttpClientConfigBean())
        }
    }

    Promise<MandrillResponse> send(Email email) {
        def requestBuilder = asyncHttpClient.preparePost(configuration.url)
        requestBuilder.addHeader(CONTENT_TYPE, APPLICATION_JSON)
        def request = populateRequest(email)
        requestBuilder.setBody(toJson(request))
        Promise<Response> future = Promise.wrap(asGuavaFuture(requestBuilder.execute()))
        return future.then( new Promise.Func<Response, Promise<MandrillResponse> > () {
            @Override
            Promise<MandrillResponse> apply (Response response) {
                try {
                    return Promise.pure( transcoder(response))
                } catch (IOException ex) {
                    throw AppErrors.INSTANCE.emailSendError('Fail to parse the response').exception()
                }
            }
        } )
    }

    MandrillRequest populateRequest(Email email) {
        def request = new MandrillRequest()
        request.key = configuration.key
        def toList = []
        def to = new To()
        to.type = TO_TYPE
        to.email = email.recipient
        toList << to

        Message message = new Message()
        message.toList = toList
        if (email.properties != null) {
            def properties = []
            email.properties.keySet().each {
                def map = new HashMap()
                map.put(VARS_NAME, it)
                map.put(VARS_CONTENT, email.properties.get(it))
                properties << map
            }
            message.properties = properties
        }
        request.message = message
        String templateName = String.format('%s.%s.%s', email.source, email.action, email.locale)
        request.templateName = templateName
        request.templateContent = new HashMap<>()

        return request
    }

    MandrillResponse transcoder(Response response) {
        ObjectMapper mapper = new ObjectMapper()
        def mandrillResponse = new MandrillResponse()
        if (response.statusCode == 200) {
            JavaType type = mapper.typeFactory.constructParametricType(List, MandrillResponse)
            def parseResponse = ((List<MandrillResponse>) mapper.readValue(response.responseBody, type)).get(0)
            mandrillResponse.email = parseResponse.email
            mandrillResponse.status = parseResponse.status
            mandrillResponse.id = parseResponse.id
            mandrillResponse.reason = parseResponse.reason
        }
        mandrillResponse.code = response.statusCode
        mandrillResponse.body = response.responseBody

        return mandrillResponse
    }

    String toJson(Object object) {
        ObjectMapper mapper = new ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        mapper.writeValueAsString(object)
    }
}
