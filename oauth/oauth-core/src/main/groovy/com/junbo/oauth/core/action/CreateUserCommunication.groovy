package com.junbo.oauth.core.action

import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.CommunicationId
import com.junbo.common.id.UniversalId
import com.junbo.common.id.UserId
import com.junbo.common.id.util.IdUtil
import com.junbo.common.model.Link
import com.junbo.identity.spec.v1.model.Communication
import com.junbo.identity.spec.v1.model.UserCommunication
import com.junbo.identity.spec.v1.option.model.CommunicationGetOptions
import com.junbo.identity.spec.v1.resource.CommunicationResource
import com.junbo.identity.spec.v1.resource.UserCommunicationResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.error.AppErrors
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert
import org.springframework.util.StringUtils

/**
 * Created by haomin on 9/15/14.
 */
@CompileStatic
class CreateUserCommunication implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserTosAgreement)
    private CommunicationResource communicationResource
    private UserCommunicationResource userCommunicationResource

    @Required
    void setCommunicationResource(CommunicationResource communicationResource) {
        this.communicationResource = communicationResource
    }

    @Required
    void setUserCommunicationResource(UserCommunicationResource userCommunicationResource) {
        this.userCommunicationResource = userCommunicationResource
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        ActionContextWrapper contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap
        String generalCommunicationIdStr = parameterMap.getFirst(OAuthParameters.GENERAL_COMMUNICATION)
        String newReleaseCommunicationIdStr = parameterMap.getFirst(OAuthParameters.NEW_RELEASE_COMMUNICATION)

        def user = contextWrapper.user
        Assert.notNull(user, 'user is null')

        if (StringUtils.isEmpty(generalCommunicationIdStr) && StringUtils.isEmpty(newReleaseCommunicationIdStr)) {
            return Promise.pure(new ActionResult('success'))
        }

        return this.createUserCommunication(generalCommunicationIdStr, contextWrapper).then {
            return this.createUserCommunication(newReleaseCommunicationIdStr, contextWrapper)
        }
    }

    private Promise<ActionResult> createUserCommunication(String communicationUri, ActionContextWrapper contextWrapper) {
        if (StringUtils.isEmpty(communicationUri)){
            return Promise.pure(new ActionResult('success'))
        }

        def user = contextWrapper.user

        UniversalId communicationId = IdUtil.fromLink(new Link(href: communicationUri))
        if (communicationId == null || !(communicationId instanceof CommunicationId)) {
            contextWrapper.errors.add(AppCommonErrors.INSTANCE.parameterInvalid('communication').error())
            return Promise.pure(new ActionResult('error'))
        }

        return communicationResource.get(communicationId as CommunicationId, new CommunicationGetOptions()).recover { Throwable e ->
            handleException(e, contextWrapper)
            return Promise.pure(null)
        }.then { Communication communication ->
            if (communication == null) {
                contextWrapper.errors.add(AppCommonErrors.INSTANCE.parameterInvalid('communication').error())
                return Promise.pure(new ActionResult('error'))
            }

            UserCommunication userCommunication = new UserCommunication(
                    userId: user.id as UserId,
                    communicationId: communicationId as CommunicationId
            )

            return userCommunicationResource.create(userCommunication).recover { Throwable e ->
                handleException(e, contextWrapper)
                return Promise.pure(null)
            }.then { UserCommunication newUserCommunication ->
                if (newUserCommunication == null) {
                    return Promise.pure(new ActionResult('error'))
                }

                return Promise.pure(new ActionResult('success'))
            }
        }
    }

    private static void handleException(Throwable throwable, ActionContextWrapper contextWrapper) {
        LOGGER.error('Error calling the identity service', throwable)
        if (throwable instanceof AppErrorException) {
            contextWrapper.errors.add(((AppErrorException) throwable).error.error())
        } else {
            contextWrapper.errors.add(AppErrors.INSTANCE.errorCallingIdentity().error())
        }
    }
}
