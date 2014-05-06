package com.junbo.email.clientproxy.impl

import com.junbo.email.common.util.PlaceholderUtils
import com.junbo.email.common.util.Utils

import static com.ning.http.client.extra.ListenableFutureAdapter.asGuavaFuture

import com.junbo.email.spec.model.EmailTemplate
import org.springframework.util.StringUtils
import com.junbo.common.id.Id
import com.junbo.common.util.IdFormatter

import org.slf4j.Logger
import org.slf4j.LoggerFactory

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
import groovy.transform.CompileStatic

/**
 * Email Provider implement by Mandrill.
 */
@CompileStatic
class MandrillProviderImpl implements EmailProvider {
    private final static Logger LOGGER = LoggerFactory.getLogger(MandrillProviderImpl)

    private static final String TO_TYPE='to'
    private static final String VARS_NAME ='name'
    private static final String VARS_CONTENT ='content'
    private static final String CONTENT_TYPE ='Content-Type'
    private static final String APPLICATION_JSON ='application/json'

    @Autowired
    private MandrillConfiguration configuration

    @Autowired
    private final AsyncHttpClient asyncHttpClient

    MandrillProviderImpl() {
        if (asyncHttpClient == null) {
            asyncHttpClient = new AsyncHttpClient(new AsyncHttpClientConfigBean())
        }
    }

    Promise<Email> sendEmail(Email email, EmailTemplate template) {
        def requestBuilder = asyncHttpClient.preparePost(configuration.url)
        requestBuilder.addHeader(CONTENT_TYPE, APPLICATION_JSON)
        encoder(email)
        def request = populateRequest(email, template)
        requestBuilder.setBody(toJson(request))
        Promise<Response> future = Promise.wrap(asGuavaFuture(requestBuilder.execute()))

        return future.recover {
            LOGGER.error('Failed to build request')
            throw AppErrors.INSTANCE.emailSendError('Fail to build request').exception()
        } .then {
            def response = (Response)it
            if (response == null) {
                LOGGER.error('Fail to get the response')
                throw AppErrors.INSTANCE.emailSendError('Fail to get the response').exception()
            }
            try {
                return Promise.pure( transcoder(response, email))
            } catch (IOException e) {
                LOGGER.error('Fail to parse the response:', e)
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
                    returnEmail.sentTime = new Date()
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
        else {
            LOGGER.error('Failed to send email:' + response.responseBody)
            returnEmail.status = EmailStatus.FAILED.toString()
            returnEmail.statusReason = 'error'
        }

        return returnEmail
    }

    MandrillRequest populateRequest(Email email, EmailTemplate template) {
        def request = new MandrillRequest()
        request.key = configuration.key
        def toList = []
        email.recipients.each {
            def to = new To()
            to.type = TO_TYPE
            to.email = it
            toList << to
        }

        Message message = new Message()
        message.toList = toList
        if (email.replacements != null) {
            def properties = []
            email.replacements.keySet().each {
                def map = [:]
                map.put(VARS_NAME, it)
                map.put(VARS_CONTENT, email.replacements.get(it))
                properties << map
            }
            message.properties = properties
        }
        if (!StringUtils.isEmpty(template.fromAddress)) {
            message.fromEmail = template.fromAddress
        }
        if (!StringUtils.isEmpty(template.fromName)) {
            message.fromName = template.fromName
        }
        if (!StringUtils.isEmpty(template.subject)) {
            message.subject = PlaceholderUtils.replace(template.subject, email.replacements)
        }
        request.message = message
        request.templateName = template.name
        request.templateContent = [:]

        return request
    }

    static String toJson(Object object) {
        ObjectMapper mapper = new ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        mapper.writeValueAsString(object)
    }

    static void encoder(Email email) {
        if (email.replacements != null) {
            def properties = [:]
            email.replacements.each { Map.Entry<String, String> replacement ->
                def value = replacement.value
                def split = replacement.key.split(':')
                def key = split.first()
                def type = split.length == 2 ? split.last() : ''
                if (!type.isEmpty()) {
                    def canonicalName = Id.package.name + '.' + type
                    try {
                        Class c = getClass().classLoader.loadClass(canonicalName)
                        if (c.superclass ==  Id) {
                            def id = c.newInstance(Long.parseLong(replacement.value)) as Id
                            value = IdFormatter.encodeId(id)
                        }
                    }
                    catch (NumberFormatException ex) {
                        LOGGER.error('Failed to parse:' + replacement.value, ex)
                    }
                    catch (ClassNotFoundException e) {
                        LOGGER.error('Failed to reflect:' + canonicalName, e)
                    }
                }
                properties.put(key, value)
            }
            email.replacements = properties
        }
    }
}

