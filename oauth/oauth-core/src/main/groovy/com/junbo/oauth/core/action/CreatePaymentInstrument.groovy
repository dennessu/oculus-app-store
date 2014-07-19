package com.junbo.oauth.core.action

import com.junbo.common.error.AppErrorException
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppErrors
import com.junbo.oauth.spec.param.OAuthParameters
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.payment.spec.model.TypeSpecificDetails
import com.junbo.payment.spec.resource.PaymentInstrumentResource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.Assert

/**
 * Created by Zhanxin on 5/22/2014.
 */
class CreatePaymentInstrument implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreatePaymentInstrument)

    private PaymentInstrumentResource paymentInstrumentResource

    @Required
    void setPaymentInstrumentResource(PaymentInstrumentResource paymentInstrumentResource) {
        this.paymentInstrumentResource = paymentInstrumentResource
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

        String cardNumber = parameterMap.getFirst(OAuthParameters.CARD_NUMBER)
        String nameOnCard = parameterMap.getFirst(OAuthParameters.NAME_ON_CARD)
        String expirationDate = parameterMap.getFirst(OAuthParameters.EXPIRATION_DATE)
        String cvv = parameterMap.getFirst(OAuthParameters.CVV)

        PaymentInstrument paymentInstrument = new PaymentInstrument(
                accountName: nameOnCard,
                accountNum: cardNumber,
                isValidated: false,
                type: 0L,
                trackingUuid: UUID.randomUUID(),
                userId: user.id.value,
                billingAddressId: user.addresses.get(0).value.value,
                typeSpecificDetails: new TypeSpecificDetails(
                        expireDate: expirationDate,
                        encryptedCvmCode: cvv
                )
        )

        return paymentInstrumentResource.postPaymentInstrument(paymentInstrument).recover { Throwable e ->
            handleException(e, contextWrapper)
            return Promise.pure(null)
        }.then { PaymentInstrument newPi ->
            if (newPi == null) {
                return Promise.pure(new ActionResult('error'))
            }

            return Promise.pure(new ActionResult('success'))
        }
    }

    private static void handleException(Throwable throwable, ActionContextWrapper contextWrapper) {
        LOGGER.error('Error calling the payment service', throwable)
        if (throwable instanceof AppErrorException) {
            contextWrapper.errors.add(((AppErrorException) throwable).error.error())
        } else {
            contextWrapper.errors.add(AppErrors.INSTANCE.errorCallingPayment().error())
        }
    }
}
