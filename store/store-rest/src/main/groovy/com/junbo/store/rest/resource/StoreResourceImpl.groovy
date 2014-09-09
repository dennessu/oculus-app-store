package com.junbo.store.rest.resource.raw

import com.junbo.authorization.AuthorizeContext
import com.junbo.catalog.spec.enums.EntitlementType
import com.junbo.catalog.spec.enums.ItemType
import com.junbo.catalog.spec.model.item.*
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.model.offer.OfferRevisionGetOptions
import com.junbo.catalog.spec.model.offer.OfferRevisionLocaleProperties
import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.*
import com.junbo.common.id.util.IdUtil
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Link
import com.junbo.common.model.Results
import com.junbo.common.util.IdFormatter
import com.junbo.crypto.spec.model.ItemCryptoMessage
import com.junbo.entitlement.spec.model.EntitlementSearchParam
import com.junbo.entitlement.spec.model.PageMetadata
import com.junbo.fulfilment.spec.model.FulfilmentAction
import com.junbo.fulfilment.spec.model.FulfilmentItem
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.list.PITypeListOptions
import com.junbo.identity.spec.v1.option.list.UserCredentialListOptions
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.option.model.CurrencyGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.langur.core.client.PathParamTranscoder
import com.junbo.langur.core.promise.Promise
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.PaymentInfo
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.db.repo.ConsumptionRepository
import com.junbo.store.db.repo.TokenRepository
import com.junbo.store.rest.browse.BrowseService
import com.junbo.store.rest.challenge.ChallengeHelper
import com.junbo.store.rest.context.ErrorContext
import com.junbo.store.rest.purchase.TokenProcessor
import com.junbo.store.rest.utils.*
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.model.*
import com.junbo.store.spec.model.billing.*
import com.junbo.store.spec.model.browse.*
import com.junbo.store.spec.model.iap.*
import com.junbo.store.spec.model.identity.*
import com.junbo.store.spec.model.purchase.*
import com.junbo.store.spec.model.token.Token
import com.junbo.store.spec.resource.StoreResource
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.annotation.Resource
import javax.ws.rs.BeanParam
import javax.ws.rs.core.UriBuilder
import javax.ws.rs.ext.Provider

@Provider
@CompileStatic
@Component('defaultStoreResource')
class StoreResourceImpl implements StoreResource {

    public static final String ACTION_TYPE_GRANT_ENTITLEMENT = "GRANT_ENTITLEMENT"

    public static final String ACTION_STATUS_SUCCESS = "SUCCEED"

    public static final String FREE_PURCHASE_CURRENCY = "USD"

    private static final int PAGE_SIZE = 100

    private static final Logger LOGGER = LoggerFactory.getLogger(StoreResourceImpl)

    @Value('${store.tos.createuser}')
    private String tosCreateUser

    @Value('${store.tos.purchase}')
    private String tosPurchase

    @Value('${store.tos.freepurchase.enable}')
    private Boolean tosFreepurchaseEnable

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'pathParamTranscoder')
    private PathParamTranscoder pathParamTranscoder

    @Resource(name = 'storeDataConverter')
    private DataConverter dataConvertor

    @Resource(name = 'storeInstrumentUtils')
    private InstrumentUtils instrumentUtils

    @Resource(name = 'iapValidator')
    private IAPValidator iapValidator

    @Resource(name = 'cloudantConsumptionRepository')
    private ConsumptionRepository consumptionRepository

    @Resource(name = 'cloudantTokenPinRepository')
    private TokenRepository tokenRepository

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

    private List<com.junbo.identity.spec.v1.model.PIType> piTypes

    @Resource(name = 'storeAppErrorUtils')
    private AppErrorUtils appErrorUtils

    @Resource(name = 'storeBrowseService')
    private BrowseService browseService

    @Autowired
    @Value('${store.conf.pinValidDuration}')
    private Integer pinCodeValidateDuration

    private static UserId getCurrentUserId() {
        UserId userId = AuthorizeContext.currentUserId
        if (userId == null || userId.value == 0) {
            throw AppErrors.INSTANCE.userNotFound().exception();
        }

        return userId
    }

    @Override
    Promise<VerifyEmailResponse> verifyEmail(VerifyEmailRequest request) {
        requestValidator.validateRequiredApiHeaders()
        return identityUtils.getActiveUserFromToken().then { User user->
            return resourceContainer.emailVerifyEndpoint.sendVerifyEmail(user.preferredLocale.value, user.countryOfResidence.value, user.getId(), null).then {
                return Promise.pure(new VerifyEmailResponse(
                        emailSent: true
                ))
            }
        }
    }

    @Override
    Promise<UserProfileGetResponse> getUserProfile() {
        requestValidator.validateRequiredApiHeaders()
        return innerGetUserProfile().syncThen { StoreUserProfile userProfile ->
            return new UserProfileGetResponse(userProfile: userProfile)
        }
    }

    @Override
    Promise<UserProfileUpdateResponse> updateUserProfile(UserProfileUpdateRequest request) {
        requestValidator.validateRequiredApiHeaders()
        return requestValidator.validateUserProfileUpdateRequest(request).then { UserProfileUpdateResponse response ->
            if (response != null) {
                return Promise.pure(response)
            }

            def userProfile = request.userProfile

            User user
            Boolean userPendingUpdate
            ErrorContext errorContext = new ErrorContext()
            return identityUtils.getVerifiedUserFromToken().syncThen { User u ->
                user = u
            }.then {
                updateUserCredential(user.getId(), request, errorContext)
            }.then {
                if (StringUtils.isEmpty(userProfile.headline)) {
                    return Promise.pure(null)
                }

                if (user.profile == null) {
                    user.profile = new UserProfile()
                }

                user.profile.headline = userProfile.headline
                userPendingUpdate = true
                return Promise.pure()
            }.then {
                if (StringUtils.isEmpty(userProfile.avatar)) {
                    return Promise.pure(null)
                }

                if (user.profile == null) {
                    user.profile = new UserProfile()
                }

                user.profile.avatar = new UserAvatar(
                        href: userProfile.avatar
                )

                userPendingUpdate = true
                return Promise.pure()
            }.then {
                if (!StringUtils.isEmpty(request?.userProfile?.nickName)) {
                    user.nickName = request?.userProfile?.nickName
                    userPendingUpdate = true
                }
                if (!userPendingUpdate) {
                    return Promise.pure()
                }
                return resourceContainer.userResource.put(user.getId(), user)
            }.then {
                return innerGetUserProfile().syncThen { StoreUserProfile userProfileResponse ->
                    return new UserProfileUpdateResponse(userProfile: userProfileResponse)
                }
            }
        }
    }

    private Promise<StoreUserProfile> innerGetUserProfile() {
        User user
        StoreUserProfile userProfile

        return identityUtils.getVerifiedUserFromToken().then { User u ->
            user = u;
            userProfile = new StoreUserProfile(
                    userId: u.getId(),
                    nickName: user.nickName,
                    headline: user.profile?.headline,
                    avatar: user.profile?.avatar?.href
            )

            return Promise.pure(null)
        }.then {
            return resourceContainer.userPersonalInfoResource.get(user.username, new UserPersonalInfoGetOptions()).then { UserPersonalInfo usernameInfo ->
                userProfile.username = ObjectMapperProvider.instance().treeToValue(usernameInfo.value, UserLoginName).userName
                return Promise.pure()
            }
        }.then {
            UserPersonalInfoLink emailLink = user.emails.find { UserPersonalInfoLink link -> link.isDefault }
            if (emailLink == null) {
                return Promise.pure(null)
            }

            return resourceContainer.userPersonalInfoResource.get(emailLink.value, new UserPersonalInfoGetOptions()).syncThen { UserPersonalInfo personalInfo ->
                def email = ObjectMapperProvider.instance().treeToValue(personalInfo.value, Email)

                userProfile.email = new StoreUserEmail(
                        value: email.info,
                        isValidated: personalInfo.isValidated
                )
           }
        }.then {
            return resourceContainer.userCredentialResource.list(user.getId(), new UserCredentialListOptions(
                    type: 'PASSWORD',
                    active: true
            )).syncThen { Results<UserCredential> results ->
                if (!results.items.isEmpty()) {
                    userProfile.password = '******'
                }
            }
        }.then {
            return resourceContainer.userCredentialResource.list(user.getId(), new UserCredentialListOptions(
                    type: 'PIN',
                    active: true
            )).syncThen { Results<UserCredential> results ->
                if (!results.items.isEmpty()) {
                    userProfile.pin = '****'
                }
            }
        }.syncThen {
            return userProfile
        }
    }

    @Override
    Promise<BillingProfileGetResponse> getBillingProfile(BillingProfileGetRequest request) {
        ApiContext apiContext
        User user
        BillingProfileGetResponse response = new BillingProfileGetResponse()
        requestValidator.validateRequiredApiHeaders()
        Promise.pure().then {
            identityUtils.getVerifiedUserFromToken().then { User u ->
                user = u
                return Promise.pure()
            }.then {
                apiContextBuilder.buildApiContext().then { ApiContext ac ->
                    apiContext = ac
                    return Promise.pure()
                }
            }
        }.then {
            return innerGetBillingProfile(user, apiContext.locale.getId(), apiContext.country.getId(), request.offer).then { BillingProfile billingProfile ->
                response.billingProfile = billingProfile
                return Promise.pure(response)
            }
        }
    }

    @Override
    Promise<InstrumentUpdateResponse> updateInstrument(InstrumentUpdateRequest request) {
        ApiContext apiContext
        User user
        requestValidator.validateRequiredApiHeaders()
        Promise.pure().then {
            requestValidator.validateInstrumentUpdateRequest(request)
        }.then {
            identityUtils.getVerifiedUserFromToken().then { User u ->
                user = u
                return Promise.pure()
            }.then {
                apiContextBuilder.buildApiContext().then { ApiContext ac ->
                    apiContext = ac
                    return Promise.pure()
                }
            }
        }.then {
            instrumentUtils.updateInstrument(user, request)
        }.then {
            innerGetBillingProfile(user, apiContext.locale.getId(), apiContext.country.getId(), null as OfferId).then { BillingProfile billingProfile ->
                InstrumentUpdateResponse response = new InstrumentUpdateResponse(
                        billingProfile: billingProfile
                )
                return Promise.pure(response)
            }
        }
    }

    @Override
    Promise<EntitlementsGetResponse> getEntitlements(PageParam pageParam) {
        requestValidator.validateRequiredApiHeaders()
        return innerGetEntitlements(new EntitlementsGetRequest(), false, pageParam).then { Results<Entitlement> entitlementResults ->
            return Promise.pure(new EntitlementsGetResponse(
                    entitlements : entitlementResults.items
            ))
        }
    }

    @Override
    Promise<IAPEntitlementGetResponse> iapGetEntitlements(IAPEntitlementGetRequest iapEntitlementGetRequest, PageParam pageParam) {
        requestValidator.validateRequiredApiHeaders()
        return identityUtils.getVerifiedUserFromToken().then {
            EntitlementsGetRequest request = new EntitlementsGetRequest(packageName: iapEntitlementGetRequest.packageName)
            return innerGetEntitlements(request, true, pageParam).then { Results<Entitlement> entitlementResults ->
                return new IAPEntitlementGetResponse(
                        entitlements : entitlementResults.items
                )
            }
        }
    }

    Promise<Results<Entitlement>> innerGetEntitlements(EntitlementsGetRequest entitlementsGetRequest, boolean isIAP, PageParam pageParam) {
        pageParam.start = pageParam.start == null ? 0 : pageParam.start
        User user
        Item hostItem = null
        List<Entitlement> entitlements = [] as LinkedList<Entitlement>
        // build entitlement search parameter
        EntitlementSearchParam entitlementSearchParam = null
        String itemType = entitlementsGetRequest.itemType == null ? ItemType.APP.name() : entitlementsGetRequest.itemType
        String entitlementType = entitlementsGetRequest.entitlementType == null ? EntitlementType.DOWNLOAD.name() : entitlementsGetRequest.entitlementType
        identityUtils.getVerifiedUserFromToken().then { User u ->
            user = u
            return Promise.pure(null)
        }.then {
            // get host item
            if (isIAP) {
                return getItemByPackageName(entitlementsGetRequest.packageName).syncThen { Item item ->
                    hostItem = item
                }
            } else {
                return Promise.pure(null)
            }
        }.then {
            entitlementSearchParam = new EntitlementSearchParam(
                    userId: user.getId(),
                    itemIds: new HashSet<ItemId>(),
                    isActive: true,
                    type: entitlementType
            )
            if (isIAP) {
                entitlementSearchParam.hostItemId = new ItemId(hostItem.getId())
            }
            return Promise.pure(null)
        }.then { // read entitlements from entitlement component
            PageMetadata entitlementPageMetaData = new PageMetadata(start: pageParam.start, count: pageParam.count != null  ? pageParam.count : PAGE_SIZE)
            CommonUtils.loop {
                return resourceContainer.entitlementResource.searchEntitlements(
                        entitlementSearchParam,
                        entitlementPageMetaData
                ).then { Results<com.junbo.entitlement.spec.model.Entitlement> results -> // get the IAP entitlements from entitlement & catalog component
                    // start page, offset, last index
                    Promise.each(results.items) { com.junbo.entitlement.spec.model.Entitlement catalogEntitlement ->
                        return convertEntitlement(catalogEntitlement, null).then { Entitlement entitlement ->
                            if ((!isIAP || entitlement.iapEntitlement.useCount > 0) && itemType == entitlement.itemType) {
                                entitlements << entitlement
                            }
                            entitlementPageMetaData.start++
                            if (pageParam.count != null && entitlements.size() > pageParam.count) {
                                return Promise.pure(Promise.BREAK)
                            } else {
                                return Promise.pure(null)
                            }
                        }
                    }.then { // check more entitlement to read
                        if ((pageParam.count != null && entitlements.size() >= pageParam.count) || results.items.size() < entitlementPageMetaData.count) {
                            return Promise.pure(Promise.BREAK)
                        } else {
                            return Promise.pure()
                        }
                    }
                }
            }
        }.then {
            Results<Entitlement> results = new Results<>()
            if (pageParam.count != null && entitlements.size() > pageParam.count) {
                results.hasNext = true;
                results.next = new Link(href: buildEntitlementNextUrl(entitlementsGetRequest, isIAP, pageParam))
                results.items = entitlements.subList(0, pageParam.count)
            } else {
                results.items = entitlements
            }

            return Promise.pure(null).then {
                if (isIAP) { // sign the iap entitlements
                    return Promise.each(results.items) { Entitlement entitlement ->
                        return signEntitlement(entitlement, hostItem.itemId)
                    }
                }
                return Promise.pure(null)
            }.then {
                return Promise.pure(results)
            }
        }
    }

    @Override
    Promise<MakeFreePurchaseResponse> makeFreePurchase(MakeFreePurchaseRequest request) {
        User user
        ApiContext apiContext
        Challenge challenge
        requestValidator.validateRequiredApiHeaders()
        identityUtils.getVerifiedUserFromToken().then { User u ->
            user = u
            apiContextBuilder.buildApiContext().then { ApiContext ac ->
                apiContext = ac
                return Promise.pure(null)
            }
        }.then {
            requestValidator.validateMakeFreePurchaseRequest(request).then {
                requestValidator.validateOfferForPurchase(user.getId(), request.offer, apiContext.country.getId(), apiContext.locale.getId(), true)
            }
        }.then {
            if (tosFreepurchaseEnable) {
                return challengeHelper.checkTosChallenge(user.getId(), tosPurchase, request.challengeAnswer).then { Challenge tosChallenge ->
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
            Order order = new Order(
                    user: user.getId(),
                    country: apiContext.country.getId(),
                    currency: new CurrencyId(FREE_PURCHASE_CURRENCY),
                    locale: apiContext.locale.getId(),
                    tentative: true,
                    orderItems: [new OrderItem(offer: request.offer, quantity: 1)]
            )

            return resourceContainer.orderResource.createOrder(order).syncThen { Order o ->
                order = o
            }.then {
                order.tentative = false
                resourceContainer.orderResource.updateOrderByOrderId(order.getId(), order).then {
                    MakeFreePurchaseResponse response = new MakeFreePurchaseResponse()
                    response.order = order.getId()
                    getEntitlementsByOrder(order, null).then { List<Entitlement> entitlements ->
                        response.entitlements = entitlements
                        expandEntitlementItem(response.entitlements, apiContext).then {
                            return Promise.pure(response)
                        }
                    }
                }
            }
        }
    }

    @Override
    Promise<PreparePurchaseResponse> preparePurchase(PreparePurchaseRequest request) {
        OfferId offerId = request.offer
        Item hostItem
        User user
        ApiContext apiContext
        CurrencyId currencyId
        PurchaseState purchaseState
        Challenge potentialChallenge
        PreparePurchaseResponse response = new PreparePurchaseResponse()
        PaymentInstrumentId selectedInstrument

        requestValidator.validateRequiredApiHeaders()
        identityUtils.getVerifiedUserFromToken().then { User u ->
            user = u
            apiContextBuilder.buildApiContext().then { ApiContext ac ->
                apiContext = ac
                return Promise.pure()
            }
        }.then {
            requestValidator.validatePreparePurchaseRequest(request).then {
                requestValidator.validateOfferForPurchase(user.getId(), request.offer, apiContext.country.getId(), apiContext.locale.getId(), false)
            }
        }.then {
            if (request.purchaseToken == null) {
                purchaseState = new PurchaseState(
                        timestamp: new Date(),
                        user: user.getId())
                return Promise.pure(null)
            }
            tokenProcessor.toTokenObject(request.purchaseToken, PurchaseState).then { PurchaseState ps ->
                purchaseState = ps
                return Promise.pure(null)
            }
        }.then {
            fillPurchaseState(purchaseState, request, apiContext)
            return Promise.pure(null)
        }.then {
            resourceContainer.countryResource.get(apiContext.country.getId(), new CountryGetOptions()).then { Country country ->
                currencyId = country.defaultCurrency
                return Promise.pure(null)
            }
        }.then { // validate offer if inapp purchase
            if (request.iapParams != null) {
                return getItemByPackageName(request.iapParams.packageName).then { Item item ->
                    hostItem = item
                    return iapValidator.validateInAppOffer(offerId, hostItem)
                }
            }
            return Promise.pure(null)
        }.then {
            if (request.instrument == null) {
                return Promise.pure(null)
            }
            return validateInstrumentForPreparePurchase(user, request)
        }.then {
            return getPurchaseChallenge(user.getId(), request).then { Challenge challenge ->
                potentialChallenge = challenge

                return Promise.pure(null)
            }
        }.then {
            if (potentialChallenge != null) {
                return tokenProcessor.toTokenString(purchaseState).then { String token ->
                    return Promise.pure(
                            new PreparePurchaseResponse(
                                    challenge : potentialChallenge,
                                    purchaseToken: token
                            )
                    )
                }
            }

            Order order = new Order(
                    user: user.getId(),
                    country: apiContext.country.getId(),
                    currency: currencyId,
                    locale: apiContext.locale.getId(),
                    tentative: true,
                    orderItems: [new OrderItem(offer: request.offer, quantity: 1)]
            )

            if (request.instrument != null) {
                order.payments = [new PaymentInfo(paymentInstrument : request.instrument)]
                selectedInstrument = request.instrument
            } else if (user.defaultPI != null) {
                order.payments = [new PaymentInfo(paymentInstrument : user.defaultPI)]
                selectedInstrument = user.defaultPI
            }
            if (request.iapParams != null) {
                order.properties = [:]
                order.properties.put('iap.packageName', request.iapParams.packageName)
                order.properties.put('iap.hostItemId', hostItem.itemId)
            }

            return resourceContainer.orderResource.createOrder(order).then { Order createOrder ->

                return resourceContainer.currencyResource.get(currencyId, new CurrencyGetOptions()).then { Currency currency ->
                    response.formattedTotalPrice = order.totalAmount + currency.symbol
                    purchaseState.order = order.getId()
                    return tokenProcessor.toTokenString(purchaseState).then { String token ->
                        response.purchaseToken = token
                        return Promise.pure(null)
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
        }
    }

    @Override
    Promise<CommitPurchaseResponse> commitPurchase(CommitPurchaseRequest commitPurchaseRequest) {
        PurchaseState purchaseState
        User user
        ApiContext apiContext
        boolean askChallenge = false
        requestValidator.validateRequiredApiHeaders()
        return identityUtils.getVerifiedUserFromToken().then { User u ->
            user = u
            apiContextBuilder.buildApiContext().then { ApiContext ac ->
                apiContext = ac
                return Promise.pure(null)
            }
            return Promise.pure()
        }.then {
            requestValidator.validateCommitPurchaseRequest(commitPurchaseRequest)
        }.then {
            return isPurchasePINChallengeNeeded(user.getId(), null).then { Boolean purchaseChallengeNeeded ->
                askChallenge = purchaseChallengeNeeded
                return Promise.pure(null)
            }
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
            if (askChallenge) {
                return tokenProcessor.toTokenString(purchaseState).then { String token ->
                    return Promise.pure(
                            new CommitPurchaseResponse(
                                    challenge : new Challenge(type: Constants.ChallengeType.PIN),
                                    purchaseToken: token
                            )
                    )
                }
            }
            OrderId orderId = purchaseState.order
            String iapPackageName = null, iapItemId = null
            CommitPurchaseResponse response = new CommitPurchaseResponse()
            boolean inappPurchase
            resourceContainer.orderResource.getOrderByOrderId(orderId).then { Order order ->
                iapPackageName = order.properties?.get('iap.packageName')
                iapItemId = order.properties?.get('iap.hostItemId')
                inappPurchase = !StringUtils.isEmpty(iapPackageName)
                order.tentative = false
                resourceContainer.orderResource.updateOrderByOrderId(order.getId(), order).then { Order settled ->
                    response.order = settled.getId()
                    return Promise.pure(null)
                }.then { // get entitlements
                    getEntitlementsByOrder(order, iapPackageName).then { List<Entitlement> entitlements ->
                        response.entitlements = entitlements
                        return Promise.pure(null)
                    }
                }.then { // sign the inapp entitlements
                    if (!inappPurchase) {
                        return Promise.pure(null)
                    }
                    return Promise.each(response.entitlements) { Entitlement entitlement ->
                        signEntitlement(entitlement, iapItemId)
                    }
                }
            }.then {
                expandEntitlementItem(response.entitlements, apiContext).then {
                    return Promise.pure(response)
                }
            }
        }
    }

    @Override
    Promise<IAPOfferGetResponse> iapGetOffers(IAPOfferGetRequest iapOfferGetRequest) {
        requestValidator.validateRequiredApiHeaders()
        return identityUtils.getVerifiedUserFromToken().then {
            return getItemByPackageName(iapOfferGetRequest.packageName).then { Item hostItem ->
                return getInAppOffers(hostItem, iapOfferGetRequest).then { List<Offer> offers ->
                    return Promise.pure(
                            new IAPOfferGetResponse(
                                    offers: new Results<Offer>(items: offers)
                            )
                    )
                }
            }
        }
    }

    @Override
    Promise<IAPEntitlementConsumeResponse> iapConsumeEntitlement(IAPEntitlementConsumeRequest iapEntitlementConsumeRequest) {
        return Promise.pure(null)
        /* Item hostItem = null
         return consumptionRepository.get(iapEntitlementConsumeRequest.trackingGuid).then { Consumption existed ->
             if (existed != null) {
                 return Promise.pure(new IAPEntitlementConsumeResponse(consumption: existed, status: ResponseStatus.SUCCESS.name()))
             }
 
             Consumption consumption = new Consumption(
                     userId: iapEntitlementConsumeRequest.userId,
                     entitlementId : iapEntitlementConsumeRequest.entitlementId,
                     useCountConsumed : iapEntitlementConsumeRequest.useCountConsumed,
                     trackingGuid: iapEntitlementConsumeRequest.trackingGuid,
                     packageName: iapEntitlementConsumeRequest.packageName
             )
 
             return getItemByPackageName(iapEntitlementConsumeRequest.packageName).then { Item item ->
                 hostItem = item
                 // todo: validate entitlement ownership & package name
                 resourceContainer.entitlementResource.getEntitlement(consumption.entitlementId).then { com.junbo.entitlement.spec.model.Entitlement catalogEntitlement ->
                     return convertEntitlement(catalogEntitlement, consumption.packageName).then { Entitlement entitlement ->
                         if (!entitlement.isConsumable) {
                             throw AppErrors.INSTANCE.entitlementNotConsumable(IdFormatter.encodeId(consumption.entitlementId)).exception()
                         }
                         if (entitlement.useCount < consumption.useCountConsumed) {
                             throw AppErrors.INSTANCE.entitlementNotEnoughUseCount(IdFormatter.encodeId(consumption.entitlementId)).exception()
                         }
 
                         consumption.packageName = entitlement.packageName
                         consumption.sku = entitlement.sku
                         consumption.type = entitlement.type
 
                         // consume via update the use count
                         catalogEntitlement.useCount -= consumption.useCountConsumed
                         return resourceContainer.entitlementResource.updateEntitlement(new EntitlementId(catalogEntitlement.getId()), catalogEntitlement).then {
                             return consumptionRepository.create(consumption)
                         }
                     }
                 }
             }
 
         }.then { Consumption consumptionResult ->
             consumptionResult.signatureTimestamp = System.currentTimeMillis()
             return signConsumption(consumptionResult, hostItem.itemId).then {
                 return Promise.pure(new IAPEntitlementConsumeResponse(consumption: consumptionResult, status: ResponseStatus.SUCCESS.name()))
             }
         }*/
    }

    @Override
    Promise<TocResponse> getToc() {
        requestValidator.validateRequiredApiHeaders()
        prepareBrowse().then { ApiContext apiContext ->
            return browseService.getToc(apiContext)
        }
    }

    @Override
    Promise<AcceptTosResponse> acceptTos(AcceptTosRequest request) {
        User user
        requestValidator.validateRequiredApiHeaders().validateAcceptTosRequest(request)
        return identityUtils.getVerifiedUserFromToken().then { User u ->
            user = u
            return Promise.pure()
        }.then {
            resourceContainer.userTosAgreementResource.create(new UserTosAgreement(userId: user.getId(), tosId: request.tosId, agreementTime: new Date())).then { UserTosAgreement userTosAgreement ->
                return Promise.pure(new AcceptTosResponse(
                    tos: userTosAgreement.tosId,
                    acceptedTime: userTosAgreement.agreementTime
                ))
            }
        }
    }

    @Override
    Promise<SectionLayoutResponse> getSectionLayout(@BeanParam SectionLayoutRequest request) {
        requestValidator.validateRequiredApiHeaders()
        prepareBrowse().then { ApiContext apiContext ->
            return browseService.getSectionLayout(request, apiContext)
        }
    }

    @Override
    Promise<ListResponse> getList(ListRequest request) {
        requestValidator.validateRequiredApiHeaders()
        prepareBrowse().then { ApiContext apiContext ->
            return browseService.getList(request, apiContext)
        }
    }

    @Override
    Promise<LibraryResponse> getLibrary() {
        requestValidator.validateRequiredApiHeaders()
        prepareBrowse().then { ApiContext apiContext ->
            return browseService.getLibrary(apiContext)
        }
    }

    @Override
    Promise<DetailsResponse> getDetails(DetailsRequest request) {
        requestValidator.validateRequiredApiHeaders().validateDetailsRequest(request)
        prepareBrowse().then { ApiContext apiContext ->
            return browseService.getItem(request.itemId, true, apiContext).then { com.junbo.store.spec.model.browse.document.Item item ->
                return Promise.pure(new DetailsResponse(item: item))
            }
        }
    }

    @Override
    Promise<ReviewsResponse> getReviews(ReviewsRequest request) {
        requestValidator.validateRequiredApiHeaders().validateReviewsRequest(request)
        prepareBrowse().then { ApiContext apiContext ->
            return browseService.getReviews(request, apiContext)
        }
    }

    @Override
    Promise<AddReviewResponse> addReview(AddReviewRequest request) {
        requestValidator.validateRequiredApiHeaders().validateAddReviewRequest(request)
        prepareBrowse().then { ApiContext apiContext ->
            return browseService.addReview(request, apiContext)
        }
    }

    @Override
    Promise<DeliveryResponse> getDelivery(DeliveryRequest request) {
        requestValidator.validateRequiredApiHeaders().validateDeliveryRequest(request)
        prepareBrowse().then { ApiContext apiContext ->
            return browseService.getDelivery(request, apiContext)
        }
    }

    private Promise<BillingProfile> innerGetBillingProfile(User user, LocaleId locale, CountryId country, OfferId offerId) {
        com.junbo.store.spec.model.catalog.Offer offer
        Promise.pure(null).then {
            if (offerId != null) {
                return facadeContainer.catalogFacade.getOffer(offerId.value, locale).then { com.junbo.store.spec.model.catalog.Offer of ->
                    offer = of
                    return Promise.pure(null)
                }
            }
            return Promise.pure(null)
        }.then {
            innerGetBillingProfile(user, locale, country, offer)
        }
    }

    private Promise<BillingProfile> innerGetBillingProfile(User user, LocaleId locale, CountryId country, com.junbo.store.spec.model.catalog.Offer offer) {
        BillingProfile billingProfile = new BillingProfile()
        billingProfile.instruments = []

        instrumentUtils.getInstruments(user).then { List<Instrument> lists ->
            billingProfile.instruments = lists
            return Promise.pure(null)
        }.then {
            billingProfile.paymentOptions = []
            loadData().then {
                piTypes.each { com.junbo.identity.spec.v1.model.PIType piType ->
                    PaymentOption paymentOption = dataConvertor.toPaymentOption(piType, locale)
                    billingProfile.paymentOptions << paymentOption
                }
                return Promise.pure(null)
            }
        }.then {
            if (offer != null && offer.hasStoreValueItem) {
                billingProfile.instruments.removeAll {Instrument instrument -> instrument.type == com.junbo.common.id.PIType.STOREDVALUE.name()}
                billingProfile.paymentOptions.removeAll {PaymentOption paymentOption -> paymentOption.type == com.junbo.common.id.PIType.STOREDVALUE.name()}
            }
            billingProfile.instruments.each { Instrument instrument ->
                instrument.isDefault = instrument.self == user.defaultPI
            }
            return Promise.pure(null)
        }.then {
            return Promise.pure(billingProfile)
        }
    }

    private Promise loadData() {
        if (piTypes == null) {
            resourceContainer.piTypeResource.list(new PITypeListOptions()).then { Results<com.junbo.identity.spec.v1.model.PIType> typeResults ->
                piTypes = []
                piTypes.addAll(typeResults.items.findAll {com.junbo.identity.spec.v1.model.PIType piType -> piType.typeCode == com.junbo.common.id.PIType.CREDITCARD.name() || piType.typeCode == com.junbo.common.id.PIType.STOREDVALUE.name()  })
                return Promise.pure(null)
            }
        } else {
            return Promise.pure(null)
        }
    }

    private Promise<User> getByUserName(String username) {
        resourceContainer.userResource.list(new UserListOptions(username: username)).syncThen { Results<User> results ->
            return results.items.isEmpty() ? null : results.items[0]
        }
    }



    private static boolean itemConsumable(ItemRevision itemRevision) {
        if (itemRevision.entitlementDefs != null) {
            return itemRevision.entitlementDefs.any { EntitlementDef entitlementDef ->
                return entitlementDef.consumable
            }
        }
        return false
    }

    private String buildEntitlementNextUrl(EntitlementsGetRequest entitlementsGetRequest, Boolean isIAP, PageParam pageParam) {
        UriBuilder builder = UriBuilder.fromPath(IdUtil.getResourcePathPrefix()).path('storeapi')
        if (isIAP) {
            builder = builder.path('iap').path('entitlements')
            addQuery(builder, 'packageName', entitlementsGetRequest.packageName)
        } else {
            builder = builder.path('entitlements')
        }
        addQuery(builder, 'itemType', entitlementsGetRequest.itemType)
        addQuery(builder, 'entitlementType', entitlementsGetRequest.entitlementType)
        builder.queryParam("count", pageParam.count);
        builder.queryParam("start", pageParam.start);
        return builder.toTemplate();
    }

    private void addQuery(UriBuilder builder, String name, Object value) {
        if (value != null) {
            builder.queryParam(name, value)
        }
    }

    private Promise<List<Entitlement>> getEntitlementsByOrder(Order order, String hostPackageName) {
        List<Entitlement> result = [] as LinkedList
        Set<String> entitlementIds = [] as HashSet

        // get the entitlement ids
        Promise promise = resourceContainer.fulfilmentResource.getByOrderId(order.getId()).then { FulfilmentRequest fulfilmentRequest ->
            if (fulfilmentRequest.items != null) {
                fulfilmentRequest.items.each { FulfilmentItem fulfilmentItem ->
                    if (fulfilmentItem.actions == null) {
                        return
                    }
                    fulfilmentItem.actions.each { FulfilmentAction fulfilmentAction ->
                        if (ACTION_TYPE_GRANT_ENTITLEMENT.equalsIgnoreCase(fulfilmentAction.type) &&
                                ACTION_STATUS_SUCCESS.equalsIgnoreCase(fulfilmentAction.status) &&
                                fulfilmentAction.result != null && fulfilmentAction.result.entitlementIds != null) {

                            fulfilmentAction.result.entitlementIds.each { String id ->
                                entitlementIds.add(id)
                            }
                        }
                    }
                }
            }
            return Promise.pure(null)

        }

        // get entitlements
        return promise.then {
            return Promise.each(entitlementIds) { String entitlementId ->
                return resourceContainer.entitlementResource.getEntitlement(new EntitlementId(entitlementId)).then { com.junbo.entitlement.spec.model.Entitlement catalogEntitlement ->
                    if (StringUtils.isEmpty(hostPackageName) && catalogEntitlement.type != EntitlementType.DOWNLOAD.name()) {
                        return Promise.pure(null)
                    }
                    return convertEntitlement(catalogEntitlement, hostPackageName).then { Entitlement e ->
                        result << e
                        return Promise.pure(null)
                    }
                }
            }.then {
                return Promise.pure(result)
            }
        }
    }

    private Promise<Entitlement> convertEntitlement(com.junbo.entitlement.spec.model.Entitlement catalogEntitlement, String hostPackageName) {
        return resourceContainer.itemResource.getItem(catalogEntitlement.itemId).then { Item item ->
            return resourceContainer.itemRevisionResource.getItemRevision(item.currentRevisionId, new ItemRevisionGetOptions()).then { ItemRevision itemRevision ->
                return Promise.pure(convertEntitlement(item, itemRevision, catalogEntitlement, hostPackageName))
            }
        }
    }

    private Entitlement convertEntitlement(Item item, ItemRevision itemRevision,
                                           com.junbo.entitlement.spec.model.Entitlement entitlement, String hostPackageName) {
        assert item.currentRevisionId == itemRevision.id, 'itemRevision not match'
        assert entitlement.itemId == item.id, 'item not match'

        boolean consumable = item.type == ItemType.CONSUMABLE_UNLOCK.name()
        Entitlement result = new Entitlement(
                self: new EntitlementId(entitlement.getId()),
                user: new UserId(entitlement.userId),
                entitlementType: entitlement.type,
                itemType: item.type,
                item: new ItemId(item.getId())
        )

        if (item.type == ItemType.CONSUMABLE_UNLOCK.name() || item.type == ItemType.PERMANENT_UNLOCK.name()) {
            result.iapEntitlement = new IAPEntitlement(
                    useCount: consumable ? entitlement.useCount : 1,
                    sku: itemRevision.sku,
                    isConsumable: consumable,
                    packageName: hostPackageName != null ? hostPackageName : itemRevision.packageName
            )
        }
        return result
    }

    private Promise<Item> getItemByPackageName(String packageName) {
        ItemsGetOptions option = new ItemsGetOptions(packageName: packageName)
        return resourceContainer.itemResource.getItems(option).then { Results<Item> itemResults ->
            if (itemResults.items.isEmpty()) {
                throw AppErrors.INSTANCE.itemNotFoundWithPackageName().exception()
            }
            if (itemResults.items.size() > 1) {
                LOGGER.warn('name=IAP_Multiple_ItemsFound_With_PackgeName, packageName={}', packageName)
            }
            return Promise.pure(itemResults.items[0])
        }
    }

    private Promise<Entitlement> signEntitlement(Entitlement entitlement, String itemId) {
        Map<String, Object> valuesMap = new HashMap<>()
        valuesMap.put('userId', IdFormatter.encodeId(entitlement.user))
        valuesMap.put('entitlementId', IdFormatter.encodeId(entitlement.self))
        valuesMap.put('useCount', entitlement.iapEntitlement.useCount)
        valuesMap.put('sku', entitlement.iapEntitlement.sku)
        valuesMap.put('type', entitlement.itemType)
        valuesMap.put('isConsumable', entitlement.iapEntitlement.isConsumable)
        valuesMap.put('packageName', entitlement.iapEntitlement.packageName)
        valuesMap.put('signatureTimestamp', System.currentTimeMillis())
        String jsonText = ObjectMapperProvider.instance().writeValueAsString(valuesMap)

        entitlement.iapEntitlement.payload = jsonText
        return resourceContainer.itemCryptoResource.sign(itemId, new ItemCryptoMessage(message: jsonText)).then { ItemCryptoMessage itemCryptoMessage ->
            entitlement.iapEntitlement.signature = itemCryptoMessage.message
            return Promise.pure(entitlement)
        }
    }

    private Promise<Consumption> signConsumption(Consumption consumption, String itemId) {
        /*Map<String, Object> valuesMap = new HashMap<>()
        valuesMap.put('userId', IdFormatter.encodeId(consumption.userId))
        valuesMap.put('entitlementId', IdFormatter.encodeId(consumption.getEntitlementId()))
        valuesMap.put('useCountConsumed', consumption.useCountConsumed)
        valuesMap.put('sku', consumption.sku)
        valuesMap.put('type', consumption.type)
        valuesMap.put('trackingGuid', consumption.trackingGuid)
        valuesMap.put('packageName', consumption.packageName)
        String jsonText = ObjectMapperProvider.instance().writeValueAsString(valuesMap)

        consumption.iapConsumptionData =jsonText
        return resourceContainer.itemCryptoResource.sign(itemId, new ItemCryptoMessage(message: jsonText)).then { ItemCryptoMessage itemCryptoMessage ->
            consumption.iapSignature = itemCryptoMessage.message
            return Promise.pure(consumption)
        }*/
    }

    private Promise<Void> getOffersFromItem(Item item, ItemRevision itemRevision, IAPOfferGetRequest iapOfferGetRequest, Map<String, Offer> offers) {
        OffersGetOptions offerOption = new OffersGetOptions(
                itemId: item.itemId,
                published: true,
                cursor : String.valueOf(0),
                size : PAGE_SIZE
        )

        CurrencyId currencyId
        resourceContainer.countryResource.get(iapOfferGetRequest.country, new CountryGetOptions()).then { Country c ->
            currencyId = c.defaultCurrency
            return Promise.pure(null)
        }.then {
            resourceContainer.offerResource.getOffers(offerOption).then { Results<com.junbo.catalog.spec.model.offer.Offer> catalogOffers ->
                if (catalogOffers.items.size() >= PAGE_SIZE) {
                    LOGGER.warn('name=IAP_TooManyOffers_Return, itemId={}, fetch first {}', item.id, PAGE_SIZE)
                }

                return Promise.each(catalogOffers.items) { com.junbo.catalog.spec.model.offer.Offer cOffer ->
                    if (offers.containsKey(cOffer.offerId)) {
                        return Promise.pure(null)
                    }
                    return resourceContainer.offerRevisionResource.getOfferRevision(cOffer.currentRevisionId, new OfferRevisionGetOptions()).then { OfferRevision offerRevision ->
                        offers.put(cOffer.id, convertOffer(cOffer, offerRevision, item, itemRevision, iapOfferGetRequest.locale.value, iapOfferGetRequest.country.value, currencyId.value))
                        return Promise.pure(null)
                    }
                }
            }
        }

    }

    private Promise<List<Offer>> getInAppOffers(Item hostItem, IAPOfferGetRequest iapOfferGetRequest) {
        ItemsGetOptions itemOption = new ItemsGetOptions(
                hostItemId: hostItem.itemId,
                // type: iapOfferGetRequest.getType(), todo :  how to get all the iap offers without given the type
                size: PAGE_SIZE,
                cursor: String.valueOf(0)
        )

        Map<String, Offer> offers = new HashMap<>()
        return CommonUtils.loop {
            return resourceContainer.itemResource.getItems(itemOption).then { Results<Item> itemResults ->
                boolean hasMore = itemResults.items.size() >= itemOption.size
                itemOption.cursor = String.valueOf(Integer.valueOf(itemOption.cursor) + itemResults.items.size())
                return Promise.each(itemResults.items) { Item item ->
                    return resourceContainer.itemRevisionResource.getItemRevision(item.currentRevisionId, new ItemRevisionGetOptions()).then { ItemRevision itemRevision ->
                        return getOffersFromItem(item, itemRevision, iapOfferGetRequest, offers).then {
                            return Promise.pure(hasMore ? null : Promise.BREAK)
                        }
                    }
                }.then {
                    return Promise.pure(hasMore ? null : Promise.BREAK)
                }
            }
        }.syncThen {
            return new ArrayList<Offer>(offers.values())
        }
    }

    private static Offer convertOffer(com.junbo.catalog.spec.model.offer.Offer offer, OfferRevision offerRevision,
                                      Item item, ItemRevision itemRevision, String locale, String country, String currency) {

        Offer result = new Offer(
                self: new OfferId(offer.getId()),
                sku: itemRevision.sku
        )
        OfferRevisionLocaleProperties offerRevisionLocaleProperties = offerRevision.locales.get(locale)

        result.description = offerRevisionLocaleProperties?.shortDescription
        result.title = offerRevisionLocaleProperties?.name
        result.price = offerRevision.price?.prices?.get(country)?.get(currency)

        return result
    }

    private AppDeliveryData buildAppDeliveryData(com.junbo.entitlement.spec.model.Entitlement entitlement, ItemRevision itemRevision) {
        AppDeliveryData result = new AppDeliveryData()
        String platform = getPlatformName()
        result.downloadUrl = entitlement.binaries[platform]

        if (StringUtils.isEmpty(result.downloadUrl)) {
            LOGGER.warn('name=Store_DownloadUrl_Empty, entitlementId={}, platform={}', entitlement.getId(), platform)
        }

        Binary binary = itemRevision.binaries[platform]
        if (binary == null) {
            LOGGER.warn('name=Store_Binary_Not_Found, entitlementId={}, platform={}', entitlement.getId(), platform)
        }

        result.downloadSize = binary?.size
        result.md5 = binary?.md5
        result.version = binary?.version
        return result
    }

    private Promise updateUserCredential(UserId userId, UserProfileUpdateRequest request, ErrorContext errorContext) {
        return Promise.pure().then {
            if (StringUtils.isEmpty(request.userProfile.password)) {
                return Promise.pure()
            }
            errorContext.fieldName = 'userProfile.password'
            return resourceContainer.userCredentialResource.create(userId, new UserCredential(
                    type: 'PASSWORD',
                    currentPassword: request.challengeAnswer.password,
                    value: request.userProfile.password
            )).recover { Throwable ex ->
                throwOnUserPasswordIncorrect(ex)
                appErrorUtils.throwOnFieldInvalidError(errorContext, ex)
                throw ex
            }
        }.then {
            if (StringUtils.isEmpty(request.userProfile.pin)) {
                return Promise.pure()
            }
            errorContext.fieldName = 'userProfile.pin'
            return resourceContainer.userCredentialResource.create(userId, new UserCredential(
                    type: 'PIN',
                    currentPassword: request.challengeAnswer.password,
                    value: request.userProfile.pin
            )).recover { Throwable ex ->
                throwOnUserPasswordIncorrect(ex)
                appErrorUtils.throwOnFieldInvalidError(errorContext, ex)
                throw ex
            }
        }
    }

    private String getPlatformName() {
        return 'ANDROID'
    }

    private Promise validateInstrumentForPreparePurchase(User user, PreparePurchaseRequest preparePurchaseRequest) {
        return resourceContainer.paymentInstrumentResource.getById(preparePurchaseRequest.getInstrument()).then { PaymentInstrument pi ->
            if (pi == null || pi.userId != user.getId().getValue()) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('instrument').exception()
            }

            return Promise.pure(null)
        }
    }

    private void fillPurchaseState(PurchaseState purchaseState, PreparePurchaseRequest request, ApiContext apiContext) {
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
    }

    private void throwOnUserPasswordIncorrect(Throwable ex) {
        if (appErrorUtils.isAppError(ex, ErrorCodes.Identity.UserPasswordIncorrect)) {
            throw AppErrors.INSTANCE.invalidChallengeAnswer().exception()
        }
    }

    private Promise<Boolean> isPurchasePINChallengeNeeded(UserId userId, String pinCode) {
        if (userId == null) {
            throw new IllegalArgumentException('userId can\'t be null')
        }

        def askChallenge = false
        if (pinCode == null) {
            return tokenRepository.searchByUserIdAndType(userId, Constants.ChallengeType.PIN, 1, 0).then { List<Token> tokenList ->
                if (CollectionUtils.isEmpty(tokenList)) {
                    askChallenge = true
                    return Promise.pure(askChallenge)
                }

                Token validToken = tokenList.find { Token token ->
                    return token.expireTime.after(new Date())
                }

                askChallenge = validToken == null
                return Promise.pure(askChallenge)
            }.then {
                return Promise.pure(askChallenge)
            }
        } else {
            return resourceContainer.userCredentialVerifyAttemptResource.create(
                    new UserCredentialVerifyAttempt(
                            userId: userId,
                            type: Constants.ChallengeType.PIN,
                            value: pinCode
                    )
            ).recover { Throwable t ->
                if (appErrorUtils.isAppError(t, ErrorCodes.Identity.InvalidPin)) {
                    throw AppErrors.INSTANCE.invalidChallengeAnswer().exception()
                }

                if (appErrorUtils.isAppError(t, ErrorCodes.Identity.InvalidField)) {
                    AppErrorException appError = (AppErrorException)t
                    if (!CollectionUtils.isEmpty(appError.error.error().getDetails())
                     && !org.springframework.util.StringUtils.isEmpty(appError.error.error().getDetails().get(0).getReason())
                     && appError.error.error().getDetails().get(0).getReason().contains('User reaches maximum allowed retry count')) {
                        throw AppErrors.INSTANCE.invalidChallengeAnswer().exception()
                    }
                }

                appErrorUtils.throwUnknownError('purchase', t)
            }.then {
                Token token = new Token(
                    userId: userId,
                    type: Constants.ChallengeType.PIN,
                    expireTime: DateUtils.addSeconds(new Date(), pinCodeValidateDuration)
                )
                return tokenRepository.create(token).then {
                    return Promise.pure(askChallenge)
                }
            }
        }
    }

    /**
    private Promise<Boolean> isPurchaseTosChallengeNeeded(UserId userId, PreparePurchaseRequest request) {
        if (request?.challengeAnswer?.type != null && request?.challengeAnswer?.type != Constants.ChallengeType.TOS_ACCEPTANCE) {
            return Promise.pure(true)
        }

        return resourceContainer.tosResource.list(new TosListOptions(title: PURCHASE_TOS_TITLE)).then { Results<Tos> toses ->
            if (toses == null || CollectionUtils.isEmpty(toses.items)) {
                if (request?.challengeAnswer?.type == Constants.ChallengeType.TOS_ACCEPTANCE && request?.challengeAnswer?.tosAcceptable) {
                    return Promise.pure(false)
                }
                return Promise.pure(true)
            }

            Tos tos = toses.items.get(0)
            return resourceContainer.userTosAgreementResource.list(new UserTosAgreementListOptions(
                    userId: userId,
                    tosId: tos.getId()
            )).then { Results<UserTosAgreement> tosAgreementResults ->
                if (tosAgreementResults == null || CollectionUtils.isEmpty(tosAgreementResults.items)) {
                    if (request?.challengeAnswer?.type == Constants.ChallengeType.TOS_ACCEPTANCE && request?.challengeAnswer?.tosAcceptable) {
                        return resourceContainer.userTosAgreementResource.create(new UserTosAgreement(
                                userId: userId,
                                tosId: tos.getId(),
                                agreementTime: new Date()
                        )).then {
                            return Promise.pure(false)
                        }
                    }

                    return Promise.pure(true)
                }

                return Promise.pure(false)
            }
        }
    }*/

    private Promise<Challenge> getPurchaseChallenge(UserId userId, PreparePurchaseRequest request) {
        return isPurchasePINChallengeNeeded(userId, request?.challengeAnswer?.pin).then { Boolean pinChallengeNeeded ->
            if (pinChallengeNeeded) {
                return Promise.pure(new Challenge(type:  Constants.ChallengeType.PIN))
            }
            return challengeHelper.checkTosChallenge(userId, tosPurchase, request.challengeAnswer)
        }
    }

    private Promise<ApiContext> prepareBrowse() {
        identityUtils.getVerifiedUserFromToken().then {
            return apiContextBuilder.buildApiContext()
        }
    }

    private Promise expandEntitlementItem(List<Entitlement> entitlements, ApiContext apiContext) {
        Promise.each(entitlements) { Entitlement entitlement ->
            browseService.getItem(entitlement.item, false, apiContext).then { com.junbo.store.spec.model.browse.document.Item item ->
                entitlement.itemDetails = item
                entitlement.itemDetails.ownedByCurrentUser = true
                return Promise.pure()
            }
        }
    }
}
