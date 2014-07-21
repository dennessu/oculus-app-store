package com.junbo.email.clientproxy.impl
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.junbo.common.id.Id
import com.junbo.common.util.IdFormatter
import com.junbo.email.clientproxy.EmailProvider
import com.junbo.email.clientproxy.impl.mandrill.*
import com.junbo.email.common.util.PlaceholderUtils
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailStatus
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.langur.core.async.JunboAsyncHttpClient
import com.junbo.langur.core.async.JunboAsyncHttpClient.BoundRequestBuilder
import com.junbo.langur.core.promise.Promise
import com.ning.http.client.Response
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.util.StringUtils

import static com.ning.http.client.extra.ListenableFutureAdapter.asGuavaFuture
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

    private MandrillConfiguration configuration

    private JunboAsyncHttpClient asyncHttpClient

    void setConfiguration(MandrillConfiguration configuration) {
        this.configuration = configuration
    }

    void setAsyncHttpClient(JunboAsyncHttpClient asyncHttpClient) {
        this.asyncHttpClient = asyncHttpClient
    }

    Promise<Email> sendEmail(Email email, EmailTemplate template) {
        if (!this.configuration.enabled) {
            email.setStatusReason('Skip the email send')
            return Promise.pure(email)
        }
        def collateRecipients = email.recipients.collate(this.configuration.size)
        def requests = collateRecipients.collect { List<String> recipients ->
            email.recipients = recipients
            return this.buildRequest(email, template)
        }
        def promise = Promise.pure([])
        def iterator = requests.iterator()
        while (iterator?.hasNext()) {
            def request = iterator.next()
            promise = promise.then { List<MandrillResponse> tempResponse ->
                return this.send(request, tempResponse)
            }
        }
        return promise.then { List<MandrillResponse> finalResponses ->
            return Promise.pure(this.fillEmail(finalResponses, email))
        }
    }

    MandrillRequest populateRequest(Email email, EmailTemplate template) {
        def request = new MandrillRequest()
        request.key = configuration.key
        def toList = []
        email.recipients.each { String recipient ->
            def to = new To(
                type: TO_TYPE,
                email: recipient
            )
            toList << to
        }

        Message message = new Message()
        message.toList = toList
        if (email.replacements != null) {
            def properties = []
            email.replacements.keySet().each { String key ->
                def map = [:]
                map.put(VARS_NAME, key)
                map.put(VARS_CONTENT, email.replacements.get(key))
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
                        LOGGER.error('EMAIL_MANDRILL_ERROR. Failed to parse:' + replacement.value, ex)
                    }
                    catch (ClassNotFoundException e) {
                        LOGGER.error('EMAIL_MANDRILL_ERROR. Failed to reflect:' + canonicalName, e)
                    }
                }
                properties.put(key, value)
            }
            email.replacements = properties
        }
    }

    BoundRequestBuilder buildRequest(Email email, EmailTemplate template) {
        def requestBuilder = asyncHttpClient.preparePost(configuration.url)
        requestBuilder.addHeader(CONTENT_TYPE, APPLICATION_JSON)
        encoder(email)
        def request = populateRequest(email, template)
        requestBuilder.setBody(toJson(request))
        return requestBuilder
    }

    private List<MandrillResponse> parseResponse(Response response) {
        def mResponses = []
        if (response.statusCode == 200) {
            def mapper = new ObjectMapper()
            def type = mapper.typeFactory.constructParametricType(List, MandrillResponse)
            mResponses = mapper.readValue(response.responseBody, type) as List
        }
        else {
            LOGGER.error('EMAIL_MANDRILL_ERROR. Failed to send email:' + response.responseBody)
            MandrillResponse mResponse = new MandrillResponse()
            mResponse.setStatus(SendStatus.ERROR.name())
            mResponses << mResponse
        }
        return mResponses
    }

    private Email fillEmail(List<MandrillResponse> mResponses, Email email) {
        def statusList = mResponses.collect { MandrillResponse response ->
            response.status.toLowerCase()
        }
        email.setSentTime(new Date())
        if (statusList.contains(SendStatus.SENT.status)) {
            email.setStatus(EmailStatus.SUCCEED.name())
        }
        else if (statusList.contains(SendStatus.INVALID.status)) {
            email.setStatus(EmailStatus.FAILED.name())
            email.setStatusReason('The status of email return is invalid')
        }
        else if (statusList.contains(SendStatus.REJECTED.status)) {
            email.setStatus(EmailStatus.FAILED.name())
            email.setStatusReason('The status of email return is rejected')
        }
        else if (statusList.contains(SendStatus.QUEUED.status)) {
            email.setStatus(EmailStatus.FAILED.name())
            email.setStatusReason('The status of email return is queued')
        }
        else if (statusList.contains(SendStatus.SCHEDULED.status)) {
            email.setStatus(EmailStatus.FAILED.name())
            email.setStatusReason('The status of email return is scheduled')

        }
        else {
            email.setStatus(EmailStatus.FAILED.name())
            email.setStatusReason('An unexpected error occurred processing the request')
        }
        return email
    }

    private Promise<List<MandrillResponse>> send(BoundRequestBuilder requestBuilder, List<MandrillResponse> responses) {
        return Promise.wrap(asGuavaFuture(requestBuilder.execute())).recover { Throwable throwable ->
            LOGGER.error('EMAIL_MANDRILL_ERROR. Fail to process the request.', throwable)
            throw new RuntimeException("Send email process request failed.")
        }.then { Response response ->
            if (response == null) {
                LOGGER.error('EMAIL_MANDRILL_ERROR. Fail to get the response')
                throw new RuntimeException("Send email response is null.")
            }
            responses.addAll(parseResponse(response))
            return Promise.pure(responses)
        }
    }
}

