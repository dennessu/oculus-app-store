package com.junbo.csr.core.service.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.EmailTemplateId
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.csr.core.service.EmailService
import com.junbo.csr.spec.error.AppErrors
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.QueryParam
import com.junbo.email.spec.resource.EmailResource
import com.junbo.email.spec.resource.EmailTemplateResource
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserName
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Created by haomin on 7/19/14.
 */
@CompileStatic
class EmailServiceImpl implements EmailService{
    private EmailTemplateResource emailTemplateResource
    private EmailResource emailResource
    private UserPersonalInfoResource userPersonalInfoResource

    @Required
    void setEmailTemplateResource(EmailTemplateResource emailTemplateResource) {
        this.emailTemplateResource = emailTemplateResource
    }

    @Required
    void setEmailResource(EmailResource emailResource) {
        this.emailResource = emailResource
    }

    @Required
    void setUserPersonalInfoResource(UserPersonalInfoResource userPersonalInfoResource) {
        this.userPersonalInfoResource = userPersonalInfoResource
    }

    @Override
    Promise<String> sendCSRInvitationEmail(QueryParam templateQueryParam, String recipient, User user, String link) {
        // todo: remove this hard coded after email template has been setup
        templateQueryParam.locale = 'en_US'

        // TODO: cache the email template for each locale.
        return emailTemplateResource.getEmailTemplates(templateQueryParam).then { Results<EmailTemplate> results ->
            if (results.items.isEmpty()) {
                throw AppErrors.INSTANCE.emailTemplateNotFound().exception()
            }
            EmailTemplate template = results.items.get(0)

            Email emailToSend = new Email(
                    userId: user.id as UserId,
                    templateId: template.id as EmailTemplateId,
                    recipients: [recipient].asList(),
                    replacements: [
                            'name': getName(user).get(),
                            'link': link
                    ]
            )

            return emailResource.postEmail(emailToSend).then { Email emailSent ->
                if (emailSent == null) {
                    throw AppErrors.INSTANCE.sendCSRInvitationEmailFailed().exception()
                }

                // Return success no matter the email has been successfully sent.
                return Promise.pure(link)
            }
        }
    }

    private Promise<String> getName(User user) {
        if (user.name == null) {
            return Promise.pure('')
        } else {
            return userPersonalInfoResource.get(user.name, new UserPersonalInfoGetOptions()).then { UserPersonalInfo userPersonalInfo ->
                if (userPersonalInfo == null) {
                    return Promise.pure('')
                }

                UserName userName = (UserName)jsonNodeToObj(userPersonalInfo.value, UserName)
                String firstName = StringUtils.isEmpty(userName.givenName) ? "" : userName.givenName
                String lastName = StringUtils.isEmpty(userName.familyName) ? "" : userName.familyName
                return Promise.pure(firstName + ' ' + lastName)
            }
        }
    }

    public static Object jsonNodeToObj(JsonNode jsonNode, Class cls) {
        try {
            return ObjectMapperProvider.instance().treeToValue(jsonNode, cls);
        } catch (Exception e) {
            throw AppCommonErrors.INSTANCE.internalServerError(new Exception('Cannot convert JsonObject to ' + cls.toString() + ' e: ' + e.getMessage())).exception()
        }
    }
}
