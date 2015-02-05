package com.junbo.store.rest.commerce.impl
import com.junbo.authorization.AuthorizeContext
import com.junbo.catalog.spec.enums.ItemType
import com.junbo.catalog.spec.model.item.Item
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.*
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.model.Currency
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.option.model.CurrencyGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.order.spec.model.FulfillmentHistory
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.error.AppErrorUtils
import com.junbo.store.rest.challenge.ChallengeHelper
import com.junbo.store.rest.commerce.PurchaseService
import com.junbo.store.rest.purchase.TokenProcessor
import com.junbo.store.rest.utils.*
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.Challenge
import com.junbo.store.spec.model.Entitlement
import com.junbo.store.spec.model.billing.Instrument
import com.junbo.store.spec.model.iap.IAPParam
import com.junbo.store.spec.model.purchase.*
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.Assert
import org.springframework.util.CollectionUtils

import javax.annotation.Resource
/**
 * Created by fzhang on 2015/1/29.
 */
@CompileStatic
@Component('storePurchaseService')
class PurchaseServiceImpl implements PurchaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseServiceImpl)

    @Value('${store.tos.purchasetostype}')
    private String tosPurchaseType

    @Value('${store.tos.freepurchase.enable}')
    private Boolean tosFreepurchaseEnable

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'storeInstrumentUtils')
    private InstrumentUtils instrumentUtils

    @Resource(name = 'storeUtils')
    private StoreUtils storeUtils

    @Resource(name = 'storeRequestValidator')
    private RequestValidator requestValidator

    @Resource(name = 'storeIdentityUtils')
    private IdentityUtils identityUtils

    @Resource(name = 'storeFacadeContainer')
    private FacadeContainer facadeContainer

    @Resource(name = 'storeTokenProcessor')
    private TokenProcessor tokenProcessor

    @Resource(name = 'storeChallengeHelper')
    private ChallengeHelper challengeHelper

    @Resource(name = 'storeContextBuilder')
    private ApiContextBuilder apiContextBuilder
    
    @Resource(name = 'storeAppErrorUtils')
    private AppErrorUtils appErrorUtils

    @Autowired
    @Value('${store.conf.pinValidDuration}')
    private Integer pinCodeValidateDuration

    @Resource(name = 'iapValidator')
    private IAPValidator iapValidator

    @Override
    Promise<MakeFreePurchaseResponse> makeFreePurchase(MakeFreePurchaseRequest request, ApiContext apiContext) {
        User user
        Challenge challenge
        requestValidator.validateRequiredApiHeaders()
        identityUtils.getVerifiedUserFromToken().then { User u ->
            user = u
            requestValidator.validateMakeFreePurchaseRequest(request).then {
                requestValidator.validateOfferForPurchase(user.getId(), request.offer, apiContext.country.getId(), apiContext.locale.getId(), true)
            }
        }.then {
            if (tosFreepurchaseEnable) {
                return challengeHelper.checkTosChallenge(user.getId(), tosPurchaseType, apiContext.country.getId(), request.challengeAnswer, apiContext.locale.getId()).then { Challenge tosChallenge ->
                    challenge = tosChallenge
                    return Promise.pure(null)
                }
            } else {
                challenge = null
                return Promise.pure(null)
            }
        }.then {
            if (challenge != null) {
                return Promise.pure(
                        new MakeFreePurchaseResponse(
                                challenge : challenge
                        )
                )
            }

            facadeContainer.orderFacade.freePurchaseOrder(user.getId(), Collections.singletonList(request.offer), apiContext).then { Order settled ->
                MakeFreePurchaseResponse response = new MakeFreePurchaseResponse()
                response.order = settled.getId()
                getEntitlementsByOrder(settled, null, apiContext).then { List<Entitlement> entitlements ->
                    response.entitlements = entitlements
                    return Promise.pure(response)
                }
            }
        }
    }

    @Override
    Promise<PreparePurchaseResponse> preparePurchase(PreparePurchaseRequest request, ApiContext apiContext) {
        OfferId offerId = request.offer
        Item hostItem
        User user
        CurrencyId currencyId
        PurchaseState purchaseState
        IAPParam iapParam

        identityUtils.getVerifiedUserFromToken().then { User u ->
            user = u
            validatePreparePurchase(request, user, apiContext).then {
                if (request.isIAP != null) { // todo validate offer is iap & package is correct
                    iapParam = storeUtils.buildIAPParam()
                    return facadeContainer.catalogFacade.getCatalogItemByPackageName(iapParam.packageName, iapParam.packageVersion, iapParam.packageSignatureHash).then { Item item ->
                        hostItem = item
                        return iapValidator.validateInAppOffer(offerId, hostItem)
                    }
                }
                return Promise.pure(null)
            }
        }.then { // load the state
            if (request.purchaseToken == null) {
                purchaseState = new PurchaseState(
                        timestamp: new Date(),
                        user: user.getId())
                fillPurchaseState(purchaseState, request, iapParam, apiContext)
                return Promise.pure(null)
            }
            tokenProcessor.toTokenObject(request.purchaseToken, PurchaseState).then { PurchaseState ps ->
                purchaseState = ps
                fillPurchaseState(purchaseState, request, iapParam, apiContext)
                return Promise.pure(null)
            }
        }.then {
            resourceContainer.countryResource.get(apiContext.country.getId(), new CountryGetOptions()).then { Country country ->
                currencyId = country.defaultCurrency
                return Promise.pure(null)
            }
        }.then {
            return getPurchaseChallenge(user.getId(), request, apiContext).then { Challenge challenge ->
                if (challenge != null) {
                    return tokenProcessor.toTokenString(purchaseState).then { String token ->
                        return Promise.pure(
                                new PreparePurchaseResponse(
                                        challenge : challenge,
                                        purchaseToken: token
                                )
                        )
                    }
                }
                return doPreparePurchase(request, user, purchaseState, currencyId, apiContext)
            }
        }
    }

    @Override
    Promise<CommitPurchaseResponse> commitPurchase(CommitPurchaseRequest commitPurchaseRequest, ApiContext apiContext) {
        PurchaseState purchaseState
        User user
        Challenge challenge
        requestValidator.validateRequiredApiHeaders()
        return identityUtils.getVerifiedUserFromToken().then { User u ->
            user = u
            requestValidator.validateCommitPurchaseRequest(commitPurchaseRequest)
        }.then {
            tokenProcessor.toTokenObject(commitPurchaseRequest.purchaseToken, PurchaseState).then { PurchaseState e ->
                purchaseState = e
                if (purchaseState.user != AuthorizeContext.currentUserId) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('purchaseToken').exception()
                }
                if (purchaseState.order == null) {
                    throw AppErrors.INSTANCE.invalidPurchaseToken([new com.junbo.common.error.ErrorDetail(reason: 'OrderId_Invalid')] as com.junbo.common.error.ErrorDetail[]).exception()
                }

                return requestValidator.validateOrderValid(purchaseState.order)
            }
        }.then {
            challengeHelper.checkPurchasePINChallenge(user.getId(), commitPurchaseRequest?.challengeAnswer).then { Challenge e ->
                challenge = e
                return Promise.pure()
            }
        }.then {
            if (challenge != null) {
                return tokenProcessor.toTokenString(purchaseState).then { String token ->
                    return Promise.pure(new CommitPurchaseResponse(challenge : challenge, purchaseToken: token))
                }
            }
            doCommitPurchaseResponse(purchaseState, apiContext)
        }
    }

    private Promise<List<Entitlement>> getEntitlementsByOrder(Order order, ItemId hostItemId, ApiContext apiContext) {
        Assert.notNull(order)
        Set<EntitlementId> entitlementIds = [] as HashSet

        // get entitlement ids from order
        if (!CollectionUtils.isEmpty(order.orderItems)) {
            order.orderItems.each { OrderItem orderItem ->
                if (!CollectionUtils.isEmpty(orderItem.fulfillmentHistories)) {
                    orderItem.fulfillmentHistories.each { FulfillmentHistory fulfillmentHistory ->
                        if (!CollectionUtils.isEmpty(fulfillmentHistory?.entitlements)) {
                            entitlementIds.addAll(fulfillmentHistory.entitlements)
                        }
                    }
                }
            }
        }

        // get entitlements
        facadeContainer.entitlementFacade.getEntitlementsByIds(entitlementIds, true, apiContext).then { List<Entitlement> entitlements ->
            Promise.each(entitlements) { Entitlement e ->
                if (e.itemType == ItemType.ADDITIONAL_CONTENT.name()|| e.itemType == ItemType.ADDITIONAL_CONTENT.name()) { // iap
                    return storeUtils.signIAPItem(apiContext.user, e.itemDetails, hostItemId)
                }
                return Promise.pure()
            }.then {
                return Promise.pure(entitlements)
            }
        }
    }

    private Promise validateInstrumentForPreparePurchase(User user, PreparePurchaseRequest preparePurchaseRequest) {
        return resourceContainer.paymentInstrumentResource.getById(preparePurchaseRequest.getInstrument()).recover { AppErrorException e ->
            if ((int)(e.error.httpStatusCode / 100) == 4) {
                return Promise.pure(null)
            } else {
                throw e
            }
        }.then { PaymentInstrument pi ->
            if (pi == null || pi.userId != user.getId().getValue()) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('instrument').exception()
            }

            return Promise.pure(null)
        }
    }

    private Promise<PreparePurchaseResponse> doPreparePurchase(PreparePurchaseRequest request,
                                                               User user, PurchaseState purchaseState, CurrencyId currencyId, ApiContext apiContext) {
        PaymentInstrumentId selectedInstrument = null
        if (request.instrument != null) {
            selectedInstrument = request.instrument
        } else if (user.defaultPI != null) {
            selectedInstrument = user.defaultPI
        }

        PreparePurchaseResponse response = new PreparePurchaseResponse()
        Promise.pure().then {
            if (purchaseState.order == null) {
                return facadeContainer.orderFacade.createTentativeOrder(Collections.singletonList(request.offer), currencyId, selectedInstrument, apiContext)
            } else {
                return facadeContainer.orderFacade.updateTentativeOrder(purchaseState.order, Collections.singletonList(request.offer), currencyId, selectedInstrument, apiContext)
            }
        }.then { Order order ->
            return resourceContainer.currencyResource.get(currencyId, new CurrencyGetOptions()).then { Currency currency ->
                BigDecimal total = order.totalAmount
                if (!order.isTaxInclusive && order.totalTax != null) {
                    total += order.totalTax
                }
                response.formattedTotalPrice = formatPrice(total, currency)
                response.formattedTaxPrice = formatPrice(order.totalTax, currency)
                response.order = order.getId()
                purchaseState.order = order.getId()
                return tokenProcessor.toTokenString(purchaseState).then { String token ->
                    response.purchaseToken = token
                    return Promise.pure(null)
                }
            }
        }.then {
            if (selectedInstrument != null) {
                return instrumentUtils.getInstrument(user, selectedInstrument).then { Instrument instrument ->
                    response.instrument = instrument
                    return Promise.pure(response)
                }
            }
            return Promise.pure(response)
        }
    }

    Promise validatePreparePurchase(PreparePurchaseRequest request, User user, ApiContext apiContext) {
        requestValidator.validatePreparePurchaseRequest(request).then { // basic validation
            requestValidator.validateOfferForPurchase(apiContext.user, request.offer, apiContext.country.getId(), apiContext.locale.getId(), false)
        }.then {
            if (request.instrument == null) {
                return Promise.pure(null)
            }
            return validateInstrumentForPreparePurchase(user, request)
        }
    }

    Promise<CommitPurchaseResponse> doCommitPurchaseResponse(PurchaseState purchaseState, ApiContext apiContext) {
        OrderId orderId = purchaseState.order
        Item hostItem
        CommitPurchaseResponse response = new CommitPurchaseResponse()

        Promise.pure().then {
            if (purchaseState.iapPackageName == null) {
                return Promise.pure()
            }
            facadeContainer.catalogFacade.getCatalogItemByPackageName(purchaseState.iapPackageName, null, null).then { Item e ->
                hostItem = e
                return Promise.pure()
            }
        }.then {
            facadeContainer.orderFacade.settleOrder(orderId, apiContext).then { Order settled ->
                response.order = settled.getId()
                getEntitlementsByOrder(settled, hostItem?.itemId == null ? null : new ItemId(hostItem.itemId), apiContext).then { List<Entitlement> entitlements ->
                    response.entitlements = entitlements
                    return Promise.pure(response)
                }
            }
        }
    }

    private static String formatPrice(BigDecimal value, Currency currency) {
        if (value == null) {
            return null
        }
        return value.setScale(currency.numberAfterDecimal, BigDecimal.ROUND_HALF_UP) + currency.symbol
    }

    private void fillPurchaseState(PurchaseState purchaseState, PreparePurchaseRequest request, IAPParam iapParam, ApiContext apiContext) {
        if (purchaseState.user != AuthorizeContext.currentUserId) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('purchaseToken').exception()
        }
        if (purchaseState.country == null) {
            purchaseState.country = apiContext.country.getCountryCode()
        } else if (purchaseState.country != apiContext.country.getCountryCode()) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('country', 'Input country isn\'t consistent with token').exception()
        }
        if (purchaseState.locale == null) {
            purchaseState.locale = apiContext.locale.localeCode
        } else if (purchaseState.locale != apiContext.locale.localeCode) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('locale', 'Input locale isn\'t consistent with token').exception()
        }
        if (purchaseState.offer == null) {
            purchaseState.offer = request.offer.value
        } else if (purchaseState.offer != request.offer.value) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('offer', 'Input offer isn\'t consistent with token').exception()
        }
        if (purchaseState.iapPackageName == null) {
            purchaseState.iapPackageName = iapParam?.packageName
        } else if (purchaseState.iapPackageName != iapParam?.packageName) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('iapParams.packageName', 'Input packageName isn\'t consistent with token').exception()
        }
    }

    private Promise<Challenge> getPurchaseChallenge(UserId userId, PreparePurchaseRequest request, ApiContext apiContext) {
        return challengeHelper.checkTosChallenge(userId, tosPurchaseType, apiContext.country.getId(), request.challengeAnswer, apiContext.locale.getId())
    }
}
