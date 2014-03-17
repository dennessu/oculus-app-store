package com.junbo.email.clientproxy.impl

import static com.ning.http.client.extra.ListenableFutureAdapter.asGuavaFuture

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.junbo.email.clientproxy.EmailProvider
import com.junbo.email.clientproxy.impl.mandrill.MandrillConfiguration
import com.junbo.email.clientproxy.impl.mandrill.MandrillRequest
import com.junbo.email.clientproxy.impl.mandrill.MandrillResponse

import com.junbo.email.clientproxy.impl.mandrill.Message
import com.junbo.email.clientproxy.impl.mandrill.SendStatus
import com.junbo.email.clientproxy.impl.mandrill.To
import com.junbo.email.spec.error.AppErrors
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailStatus
import com.junbo.langur.core.promise.Promise
import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.AsyncHttpClientConfigBean
import com.ning.http.client.Response
import org.springframework.beans.factory.annotation.Autowired

/**
 * Email Provider implement by Mandrill.
 */
class MandrillProviderImpl implements EmailProvider {
    private static final String TO_TYPE='to'
    private static final String VARS_NAME ='name'
    private static final String VARS_CONTENT ='content'
    private static final String CONTENT_TYPE ='Content-Type'
    private static final String APPLICATION_JSON ='application/json'

    @Autowired
    private MandrillConfiguration configuration

    @Autowired
    final AsyncHttpClient asyncHttpClient

    MandrillProviderImpl() {
        if (asyncHttpClient == null) {
            asyncHttpClient = new AsyncHttpClient(new AsyncHttpClientConfigBean())
        }
    }

    Promise<Email> sendEmail(Email email) {
        def requestBuilder = asyncHttpClient.preparePost(configuration.url)
        requestBuilder.addHeader(CONTENT_TYPE, APPLICATION_JSON)
        def request = populateRequest(email)
        requestBuilder.setBody(toJson(request))
        Promise<Response> future = Promise.wrap(asGuavaFuture(requestBuilder.execute()))

        future.then {
            def response = (Response)it
            if (response == null) {
                throw AppErrors.INSTANCE.emailSendError('Fail to get the response').exception()
            }
            try {
                return Promise.pure( transcoder(response, email))
            } catch (IOException e) {
                throw AppErrors.INSTANCE.emailSendError('Fail to parse the response').exception()
            }
        }
    }

    static Email transcoder(Response response, Email email) {
        def returnEmail = email
        def mapper = new ObjectMapper()
        if (response.statusCode == 200) {
            JavaType type = mapper.typeFactory.constructParametricType(List, MandrillResponse)
            def mandrillResponse = ((List<MandrillResponse>) mapper.readValue(response.responseBody, type)).get(0)
            switch (mandrillResponse.status) {
                case SendStatus.SENT.status:
                    returnEmail.status = EmailStatus.SUCCEED.toString()
                    returnEmail.sentDate = new Date()
                    break
                case SendStatus.REJECTED.status:
                    returnEmail.status = EmailStatus.FAILED.toString()
                    returnEmail.statusReason = mandrillResponse.reason
                    break
                case SendStatus.INVALID.status:
                    returnEmail.status = EmailStatus.FAILED.toString()
                    returnEmail.statusReason = 'invalid'
                    break
                default:
                    returnEmail.status = EmailStatus.FAILED.toString()
                    returnEmail.statusReason = 'unknown'
                    break
            }
        }

        return returnEmail
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

    static String toJson(Object object) {
        ObjectMapper mapper = new ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        mapper.writeValueAsString(object)
    }
}
