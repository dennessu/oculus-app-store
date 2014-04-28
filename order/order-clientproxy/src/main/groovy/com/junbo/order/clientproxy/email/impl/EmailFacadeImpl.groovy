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
import com.junbo.order.spec.model.Order
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.apache.commons.collections.CollectionUtils
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

    @Override
    Promise<Email> sendOrderConfirmationEMail(Order order, User user, List<OfferRevision> offers) {
        if (order == null || user == null || CollectionUtils.isEmpty(offers)) {
            return Promise.pure(null)
        }
        return getEmailTemplates('SilkCloud', 'OrderConfirmation', 'en_US').then { List<EmailTemplate> templates ->
            if (templates == null || CollectionUtils.isEmpty(templates)) {
                return Promise.pure(null)
            }
            Email email = FacadeBuilder.buildOrderConfirmationEmail(order, user, offers, templates[0].id)
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
