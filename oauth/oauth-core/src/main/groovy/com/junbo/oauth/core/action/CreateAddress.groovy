package com.junbo.oauth.core.action

import com.junbo.common.enumid.CountryId
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.identity.spec.v1.model.Address
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink
import com.junbo.identity.spec.v1.resource.UserPersonalInfoResource
import com.junbo.identity.spec.v1.resource.UserResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.error.AppErrors
import com.junbo.oauth.spec.param.OAuthParameters
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * Created by Zhanxin on 5/21/2014.
 */
class CreateAddress implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateUserPii)

    private UserPersonalInfoResource userPersonalInfoResource

    private UserResource userResource

    @Required
    void setUserPersonalInfoResource(UserPersonalInfoResource userPersonalInfoResource) {
        this.userPersonalInfoResource = userPersonalInfoResource
    }

    @Required
    void setUserResource(UserResource userResource) {
        this.userResource = userResource
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap
        def user = contextWrapper.user
        def loginState = contextWrapper.loginState

        Assert.notNull(user, 'user is null')
        Assert.notNull(loginState, 'loginState is null')
        user.id = new UserId(loginState.userId)

        String address1 = parameterMap.getFirst(OAuthParameters.ADDRESS1)
        String address2 = parameterMap.getFirst(OAuthParameters.ADDRESS2)
        String city = parameterMap.getFirst(OAuthParameters.CITY)
        String subCountry = parameterMap.getFirst(OAuthParameters.SUB_COUNTRY)
        String country = parameterMap.getFirst(OAuthParameters.COUNTRY)
        String zipCode = parameterMap.getFirst(OAuthParameters.ZIP_CODE)

        Address address = new Address(
                street1: address1,
                street2: address2,
                city: city,
                subCountry: subCountry,
                countryId: new CountryId(country),
                postalCode: zipCode
        )

        // check the address already created or not
        if (user.addresses != null && user.addresses.get(0) != null && user.addresses.get(0) == address) {
            return Promise.pure(new ActionResult('success'))
        }

        UserPersonalInfo addressPii = new UserPersonalInfo(
                userId: user.id as UserId,
                type: 'ADDRESS',
                value: ObjectMapperProvider.instance().valueToTree(address)
        )

        return userPersonalInfoResource.create(addressPii).recover { Throwable e ->
            handleException(e, contextWrapper)
            return Promise.pure(null)
        }.then { UserPersonalInfo pii ->
            if (pii == null) {
                return Promise.pure(new ActionResult('error'))
            }

            user.addresses = [new UserPersonalInfoLink(
                    isDefault: true,
                    value: pii.id as UserPersonalInfoId
            )]

            return Promise.pure(new ActionResult('next'))
        }.then { ActionResult result ->
            if (result.id == 'error') {
                return Promise.pure(result)
            }

            return userResource.put(user.id as UserId, user).recover { Throwable e ->
                handleException(e, contextWrapper)
                return Promise.pure(null)
            }.then { User updatedUser ->
                if (updatedUser == null) {
                    return Promise.pure(new ActionResult('error'))
                }

                contextWrapper.user = updatedUser

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
