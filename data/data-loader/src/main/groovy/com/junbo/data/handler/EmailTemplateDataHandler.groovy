package com.junbo.data.handler

import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.QueryParam
import com.junbo.email.spec.resource.EmailTemplateResource
import com.junbo.langur.core.client.TypeReference
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

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
    void handle(String content) {
        EmailTemplate template
        try {
            template = transcoder.decode(new TypeReference<EmailTemplate>() {}, content) as EmailTemplate
        } catch (Exception e) {
            logger.warn('Error parsing EmailTemplate, skip this content:' + content, e)
            return
        }

        EmailTemplate existing
        try {
            def list = templateResource.getEmailTemplates(
                    new QueryParam(source: template.source, action: template.action, locale: template.locale)).get()
            if (list?.items?.size() != 0) {
                existing = list.items.first() as EmailTemplate
            }
        } catch (Exception e) {
            logger.debug('This content does not exist in current database', e)
        }

        if (existing != null) {
            if (alwaysOverwrite) {
                logger.debug("Overwrite EmailTemplate ${existing.name} with this content.")
                template.rev = existing.rev
                templateResource.putEmailTemplate(existing.id, template).get()
            } else {
                logger.debug("EmailTemplate ${existing.name} already exists, skipped!")
            }
        } else {
            logger.debug('Create new EmailTemplate with this content.')
            templateResource.postEmailTemplate(template).get()
        }
    }
}
