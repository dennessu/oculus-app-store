package com.junbo.order.clientproxy.email.impl

import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.resource.EmailResource
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
 * Created by LinYi on 14-3-14.
 */
@CompileStatic
@TypeChecked
@Component('orderEmailFacade')
class EmailFacadeImpl implements EmailFacade {
    @Resource(name='order.emailClient')
    EmailResource emailResource


    @Override
    Promise<Email> sendOrderConfirmationEMail(Order order, User user, List<OfferRevision> offers) {
        if (order == null || user == null || CollectionUtils.isEmpty(offers)) {
            return Promise.pure(null)
        }
        Email email = FacadeBuilder.buildOrderConfirmationEmail(order, user, offers)
        return emailResource.postEmail(email)
    }
}
