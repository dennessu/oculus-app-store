package com.junbo.order.clientproxy.email.impl

import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.resource.EmailResource
import com.junbo.identity.spec.model.user.User
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.common.FacadeBuilder
import com.junbo.order.clientproxy.email.EmailFacade
import com.junbo.order.spec.model.Order
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * Created by LinYi on 14-3-14.
 */
@CompileStatic
@Component('emailFacade')
@TypeChecked
class EmailFacadeImpl implements EmailFacade {
    @Resource(name='emailClient')
    EmailResource emailResource

    @Override
    Promise<Email> sendOrderConfirmationEMail(Order order, User user, List<Offer> offers) {
        Email email = FacadeBuilder.buildOrderConfirmationEmail(order, user, offers)
        return emailResource.postEmail(email)
    }
}
