package com.junbo.store.rest.utils
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrderId
import com.junbo.common.util.IdFormatter
import com.junbo.langur.core.promise.Promise
import com.junbo.store.spec.model.billing.BillingProfileGetRequest
import com.junbo.store.spec.model.billing.BillingProfileUpdateRequest
import com.junbo.store.spec.model.identity.UserProfileUpdateRequest
import com.junbo.store.spec.model.login.*
import com.junbo.store.spec.model.purchase.CommitPurchaseRequest
import com.junbo.store.spec.model.purchase.MakeFreePurchaseRequest
import com.junbo.store.spec.model.purchase.PreparePurchaseRequest
import com.junbo.store.spec.model.purchase.SelectInstrumentRequest
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

import javax.annotation.Resource
/**
 * The RequestValidator class.
 */
@CompileStatic
@Component('storeRequestValidator')
class RequestValidator {

    @Resource(name = 'storeResourceContainer')
    ResourceContainer resourceContainer

    Promise validateUserNameCheckRequest(UserNameCheckRequest request) {
        if (StringUtils.isEmpty(request.username)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('username').exception()
        }
        if (StringUtils.isEmpty(request.email)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('email').exception()
        }
        return Promise.pure(null)
    }

    Promise validateCreateUserRequest(CreateUserRequest request) {
        notEmpty(request.username, 'username')
        notEmpty(request.email, 'email')
        notEmpty(request.password, 'password')
        notEmpty(request.pinCode, 'pinCode')
        notEmpty(request.dob, 'dob')
        notEmpty(request.cor, 'cor')
        notEmpty(request.preferredLocale, 'preferredLocale')
        notEmpty(request.firstName, 'firstName')
        notEmpty(request.lastName, 'lastName')
        return Promise.pure(null)
    }

    Promise validateUserSignInRequest(UserSignInRequest request) {
        notEmpty(request.username, 'username')
        notEmpty(request.userCredential, 'userCredential')
        notEmpty(request.userCredential.type, 'userCredential.type')
        notEmpty(request.userCredential.value, 'userCredential.value')
        if (!'PASSWORD'.equalsIgnoreCase(request.userCredential.type)) { //
            throw AppCommonErrors.INSTANCE.fieldInvalid('userCredential.type', 'type must be PASSWORD ').exception()
        }
        return Promise.pure(null)
    }

    Promise<Void> validateUserCredentialCheckRequest(UserCredentialCheckRequest request) {
        notEmpty(request.username, 'username')
        notEmpty(request.userCredential, 'userCredential')
        notEmpty(request.userCredential.type, 'userCredential.type')
        notEmpty(request.userCredential.value, 'userCredential.value')
        return Promise.pure(null)
    }

    Promise validateAuthTokenRequest(AuthTokenRequest request) {
        notEmpty(request.username, 'username')
        notEmpty(request.refreshToken, 'refreshToken')
        return Promise.pure(null)
    }

    Promise<Void> validateUserProfileUpdateRequest(UserProfileUpdateRequest userProfileUpdateRequest) {
        notEmpty(userProfileUpdateRequest.updateValue, 'updateValue')
        notEmpty(userProfileUpdateRequest.userId, 'userId')
        notEmpty(userProfileUpdateRequest.action, 'action')
        try {
            UserProfileUpdateRequest.UpdateAction.valueOf(userProfileUpdateRequest.action)
        } catch (IllegalArgumentException ex) {
            throw AppCommonErrors.INSTANCE.fieldInvalidEnum('action', CommonUtils.allowedEnumValues(UserProfileUpdateRequest.UpdateAction)).exception()
        }
        return Promise.pure(null)
    }

    Promise validateBillingProfileGetRequest(BillingProfileGetRequest request) {
        notEmpty(request.userId, 'userId')
        notEmpty(request.locale, 'locale')
        return Promise.pure(null)
    }

    Promise validateBillingProfileUpdateRequest(BillingProfileUpdateRequest request) {
        notEmpty(request.userId, 'userId')
        notEmpty(request.locale, 'locale')
        notEmpty(request.instrument, 'instrument')
        try {
            BillingProfileUpdateRequest.UpdateAction.valueOf(request.action)
        } catch (IllegalArgumentException ex) {
            throw AppCommonErrors.INSTANCE.fieldInvalidEnum('action', CommonUtils.allowedEnumValues(BillingProfileUpdateRequest.UpdateAction)).exception()
        }
        return Promise.pure(null)
    }

    Promise validateMakeFreePurchaseRequest(MakeFreePurchaseRequest request) {
        notEmpty(request.userId, 'userId')
        notEmpty(request.offerId, 'offerId')
        notEmpty(request.country, 'country')
        notEmpty(request.locale, 'locale')
        return Promise.pure(null)
    }

    Promise<Void> validatePreparePurchaseRequest(PreparePurchaseRequest request) {
        notEmpty(request.userId, 'userId')
        notEmpty(request.offerId, 'offerId')
        notEmpty(request.country, 'country')
        notEmpty(request.locale, 'locale')
        notEmpty(request.currency, 'currency')
        if (request.iapParams != null) {
            notEmpty(request.iapParams.packageName, 'iapParams.packageName')
            notEmpty(request.iapParams.packageSignatureHash, 'iapParams.packageSignatureHash')
            notEmpty(request.iapParams.packageVersion, 'iapParams.packageVersion')
        }
        return Promise.pure(null)
    }

    Promise validateSelectInstrumentRequest(SelectInstrumentRequest request) {
        notEmpty(request.userId, 'userId')
        notEmpty(request.instrumentId, 'instrumentId')
        notEmpty(request.purchaseToken, 'purchaseToken')
        validatePurchaseToken(request.purchaseToken, 'purchaseToken')
    }

    Promise validateCommitPurchaseRequest(CommitPurchaseRequest request) {
        notEmpty(request.userId, 'userId')
        notEmpty(request.purchaseToken, 'purchaseToken')
        if (request.challengeSolution != null) {
            notEmpty(request.challengeSolution.value, 'challengeSolution.value')
            notEmpty(request.challengeSolution.type, 'challengeSolution.type')
        }
        validatePurchaseToken(request.purchaseToken, 'purchaseToken')
    }

    void notEmpty(Object val, String fieldName) {
        if (StringUtils.isEmpty(val)) {
            throw AppCommonErrors.INSTANCE.fieldRequired(fieldName).exception()
        }
    }

    Promise validatePurchaseToken(String purchaseToken, String fieldName) {
        Promise.pure(null).then {
            resourceContainer.orderResource.getOrderByOrderId(new OrderId(IdFormatter.decodeId(OrderId, purchaseToken)))
        }.recover {
            throw AppCommonErrors.INSTANCE.fieldInvalid(fieldName).exception()
        }
    }
}
