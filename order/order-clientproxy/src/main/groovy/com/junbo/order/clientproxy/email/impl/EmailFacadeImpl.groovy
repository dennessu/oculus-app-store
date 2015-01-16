package com.junbo.order.clientproxy.email.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.QueryParam
import com.junbo.email.spec.resource.EmailResource
import com.junbo.email.spec.resource.EmailTemplateResource
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserLoginName
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
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

    @Resource(name = 'order.identityUserPersonalInfoClient')
    UserPersonalInfoResource userPersonalInfoResource

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailFacadeImpl)

    @Override
    Promise<Email> sendOrderConfirmationEMail(Order order, User user, List<Offer> offers) {
        if (order == null || user == null || user.getId() == null || CollectionUtils.isEmpty(offers)) {
            LOGGER.info('name=Email_Info_Not_Sufficient')
            return Promise.pure(null)
        }
        return getEmailTemplates('Oculus', 'OrderConfirmation_V1', user.getId()).recover {
            LOGGER.info('name=Get_Email_Template_Error')
            return Promise.pure(null)
        }.then { List<EmailTemplate> templates ->
            if (templates == null || CollectionUtils.isEmpty(templates)) {
                LOGGER.info('name=Get_Email_Template_Empty_Templates')
                return Promise.pure(null)
            }
            String username = getUserName(user).get()
            Email email = FacadeBuilder.buildOrderConfirmationEmail(order, user, username, offers, templates[0])
            return emailResource.postEmail(email)
        }
    }

    Promise<List<EmailTemplate>> getEmailTemplates(String source, String action, UserId userId) {
        QueryParam param = buildQueryParam(source, action, userId)
        return emailTemplateResource.getEmailTemplates(param).syncThen { Results<EmailTemplate> results ->
            return results == null ? Collections.emptyList() : results.items
        }
    }

    private QueryParam buildQueryParam(String source, String action, UserId userId) {
        QueryParam param = new QueryParam()
        param.source = source
        param.action = action
        param.userId = userId
        return param
    }

    private Promise<String> getUserName(User user) {
        if (user.username == null) {
            return Promise.pure(null)
        } else {
            return userPersonalInfoResource.get(user.username, new UserPersonalInfoGetOptions()).then { UserPersonalInfo userPersonalInfo ->
                UserLoginName userLoginName = (UserLoginName)jsonNodeToObj(userPersonalInfo.value, UserLoginName)
                return Promise.pure(userLoginName.userName)
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
