package com.junbo.csr.core.service.impl

import com.junbo.common.id.EmailTemplateId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.csr.core.service.EmailService
import com.junbo.csr.spec.error.AppErrors
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.QueryParam
import com.junbo.email.spec.resource.EmailResource
import com.junbo.email.spec.resource.EmailTemplateResource
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 7/19/14.
 */
class EmailServiceImpl implements EmailService{
    private EmailTemplateResource emailTemplateResource
    private EmailResource emailResource

    @Required
    void setEmailTemplateResource(EmailTemplateResource emailTemplateResource) {
        this.emailTemplateResource = emailTemplateResource
    }

    @Required
    void setEmailResource(EmailResource emailResource) {
        this.emailResource = emailResource
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
                            'name': user.username,
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
}
