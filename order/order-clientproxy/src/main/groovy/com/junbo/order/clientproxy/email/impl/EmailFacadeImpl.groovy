package com.junbo.order.clientproxy.email.impl

import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.common.model.Results
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.QueryParam
import com.junbo.email.spec.resource.EmailResource
import com.junbo.email.spec.resource.EmailTemplateResource
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.common.FacadeBuilder
import com.junbo.order.clientproxy.email.EmailFacade
import com.junbo.order.clientproxy.model.Offer
import com.junbo.order.spec.model.Order
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.collections.CollectionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Implementation of email facade.
 */
@CompileStatic
@TypeChecked
@Component('orderEmailFacade')
class EmailFacadeImpl implements EmailFacade {
    @Resource(name='order.emailClient')
    EmailResource emailResource

    @Resource(name = 'order.emailTemplateClient')
    EmailTemplateResource emailTemplateResource

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailFacadeImpl)

    @Override
    Promise<Email> sendOrderConfirmationEMail(Order order, User user, List<Offer> offers) {
        if (order == null || user == null || CollectionUtils.isEmpty(offers)) {
            LOGGER.info('name=Email_Info_Not_Sufficient')
            return Promise.pure(null)
        }
        return getEmailTemplates('Oculus', 'OrderConfirmation', 'en_US').recover {
            LOGGER.info('name=Get_Email_Template_Error')
            return Promise.pure(null)
        }.then { List<EmailTemplate> templates ->
            if (templates == null || CollectionUtils.isEmpty(templates)) {
                LOGGER.info('name=Get_Email_Template_Empty_Templates')
                return Promise.pure(null)
            }
            Email email = FacadeBuilder.buildOrderConfirmationEmail(order, user, offers, templates[0])
            return emailResource.postEmail(email)
        }
    }

    Promise<List<EmailTemplate>> getEmailTemplates(String source, String action, String locale) {
        QueryParam param = buildQueryParam(source, action, locale)
        return emailTemplateResource.getEmailTemplates(param).syncThen { Results<EmailTemplate> results ->
            return results == null ? Collections.emptyList() : results.items
        }
    }

    private QueryParam buildQueryParam(String source, String action, String locale) {
        QueryParam param = new QueryParam()
        param.source = source
        param.action = action
        param.locale = locale
        return param
    }
}
