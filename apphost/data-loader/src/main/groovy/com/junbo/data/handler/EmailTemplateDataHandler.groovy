package com.junbo.data.handler

import com.junbo.data.model.EmailTemplateData
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.QueryParam
import com.junbo.email.spec.resource.EmailTemplateResource
import com.junbo.langur.core.client.TypeReference
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.core.io.Resource

/**
 * Created by Wei on 6/12/14.
 */
@CompileStatic
class EmailTemplateDataHandler extends BaseDataHandler {
    private EmailTemplateResource templateResource

    @Required
    void setTemplateResource(EmailTemplateResource templateResource) {
        this.templateResource = templateResource
    }

    @Override
    Resource[] resolveDependencies(Resource[] resources) {
        return resources
    }

    @Override
    void handle(String content) {
        EmailTemplateData emailTemplateData
        try {
            emailTemplateData = transcoder.decode(new TypeReference<EmailTemplateData>() {}, content) as EmailTemplateData
        } catch (Exception e) {
            logger.error("Error parsing emailTemplate $content", e)
        }

        for (String locale : emailTemplateData.locales) {
            EmailTemplate template = new EmailTemplate(
                    source: emailTemplateData.source,
                    action: emailTemplateData.action,
                    locale: locale,
                    providerName: emailTemplateData.providerName,
                    placeholderNames: emailTemplateData.placeholderNames
            )

            EmailTemplate existing
            try {
                def list = templateResource.getEmailTemplates(
                        new QueryParam(source: template.source, action: template.action, locale: template.locale)).get()
                if (list?.items?.size() != 0) {
                    existing = list.items.first() as EmailTemplate
                    if (existing.locale != locale) {
                        // This is to avoid locale fallback search
                        existing = null
                    }
                }
            } catch (Exception e) {
                logger.debug('This content does not exist in current database', e)
            }

            if (existing != null) {
                logger.debug("EmailTemplate ${existing.action} already exists, skipped!")
            } else {
                logger.debug('Create new EmailTemplate with this content.')
                try {
                    templateResource.postEmailTemplate(template).get()
                } catch (Exception e) {
                    logger.error("Error creating emailTemplate $template.action", e)
                }
            }
        }
    }
}
