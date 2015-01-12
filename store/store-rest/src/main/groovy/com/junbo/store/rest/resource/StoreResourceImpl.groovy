package com.junbo.store.rest.resource.raw
import com.junbo.authorization.AuthorizeContext
import com.junbo.catalog.spec.enums.ItemType
import com.junbo.catalog.spec.model.item.Item
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.*
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.list.PITypeListOptions
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.option.model.CurrencyGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.langur.core.client.PathParamTranscoder
import com.junbo.langur.core.promise.Promise
import com.junbo.order.spec.model.FulfillmentHistory
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.error.AppErrorUtils
import com.junbo.store.clientproxy.error.ErrorCodes
import com.junbo.store.clientproxy.error.ErrorContext
import com.junbo.store.db.repo.ConsumptionRepository
import com.junbo.store.db.repo.TokenRepository
import com.junbo.store.rest.browse.BrowseService
import com.junbo.store.rest.challenge.ChallengeHelper
import com.junbo.store.rest.purchase.TokenProcessor
import com.junbo.store.rest.utils.*
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.Challenge
import com.junbo.store.spec.model.Entitlement
import com.junbo.store.spec.model.billing.*
import com.junbo.store.spec.model.browse.*
import com.junbo.store.spec.model.iap.HostItemInfo
import com.junbo.store.spec.model.iap.IAPParam
import com.junbo.store.spec.model.identity.*
import com.junbo.store.spec.model.purchase.*
import com.junbo.store.spec.resource.StoreResource
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.Assert
import org.springframework.util.CollectionUtils

import javax.annotation.Resource
import javax.ws.rs.BeanParam
import javax.ws.rs.core.UriBuilder
import javax.ws.rs.ext.Provider

@Provider
@CompileStatic
@Component('defaultStoreResource')
class StoreResourceImpl implements StoreResource {

    private static final int PAGE_SIZE = 100

    private static final Logger LOGGER = LoggerFactory.getLogger(StoreResourceImpl)

    @Value('${store.browse.verifyUser}')
    private boolean verifyUserInBrowse

    @Value('${store.tos.purchasetostype}')
    private String tosPurchaseType

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

    @Resource(name = 'storeUtils')
    private StoreUtils storeUtils

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

    @Resource(name = 'iapValidator')
    private IAPValidator iapValidator

    @Override
    Promise<VerifyEmailResponse> verifyEmail() {
        requestValidator.validateRequiredApiHeaders()
        return identityUtils.getActiveUserFromToken().then { User user->
            apiContextBuilder.buildApiContext().then { ApiContext apiContext ->
                LocaleId locale = user.preferredLocale != null ? user.preferredLocale : apiContext.locale.getId()
                CountryId country = user.countryOfResidence != null ? user.countryOfResidence : apiContext.country.getId()
                return facadeContainer.oAuthFacade.sendVerifyEmail(locale.value, country.value, user.getId(), null).then {
                    return Promise.pure(new VerifyEmailResponse(
                            emailSent: true
                    ))
                }
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
        ApiContext apiContext
        requestValidator.validateRequiredApiHeaders()
        apiContextBuilder.buildApiContext().then { ApiContext ac ->
            apiContext = ac
            return requestValidator.validateUserProfileUpdateRequest(request, apiContext)
        }.then { UserProfileUpdateResponse response ->
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
                return resourceContainer.userUserResource.put(user.getId(), user)
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
            return resourceContainer.userUserPersonalInfoResource.get(user.username, new UserPersonalInfoGetOptions()).then { UserPersonalInfo usernameInfo ->
                userProfile.username = ObjectMapperProvider.instance().treeToValue(usernameInfo.value, UserLoginName).userName
                return Promise.pure()
            }
        }.then {
            UserPersonalInfoLink emailLink = user.emails.find { UserPersonalInfoLink link -> link.isDefault }
            if (emailLink == null) {
                return Promise.pure(null)
            }

            return resourceContainer.userUserPersonalInfoResource.get(emailLink.value, new UserPersonalInfoGetOptions()).syncThen { UserPersonalInfo personalInfo ->
                def email = ObjectMapperProvider.instance().treeToValue(personalInfo.value, Email)

                userProfile.email = new StoreUserEmail(
                        value: email.info,
                        isValidated: personalInfo.isValidated
                )
           }
        }.syncThen {
            userProfile.password = '******'
            userProfile.pin = '****'
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
            return innerGetBillingProfile(user, apiContext.locale.getId(), apiContext.country.getId(), request.offer, null).then { BillingProfile billingProfile ->
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
        }.then { PaymentInstrumentId paymentInstrumentId ->
            innerGetBillingProfile(user, apiContext.locale.getId(), apiContext.country.getId(), null as OfferId, paymentInstrumentId).then { BillingProfile billingProfile ->
                InstrumentUpdateResponse response = new InstrumentUpdateResponse(
                        billingProfile: billingProfile
                )
                response.updatedInstrument = paymentInstrumentId
                return Promise.pure(response)
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
                getEntitlementsByOrder(settled, null, null, apiContext).then { List<Entitlement> entitlements ->
                    response.entitlements = entitlements
                    return Promise.pure(response)
                }
            }
        }
    }

    @Override
    Promise<PreparePurchaseResponse> preparePurchase(PreparePurchaseRequest request) {
        Item hostItem
        User user
        ApiContext apiContext
        CurrencyId currencyId
        PurchaseState purchaseState
        Challenge potentialChallenge
        PreparePurchaseResponse response = new PreparePurchaseResponse()
        PaymentInstrumentId selectedInstrument
        IAPParam iapParam

        requestValidator.validateRequiredApiHeaders()
        identityUtils.getVerifiedUserFromToken().then { User u ->
            user = u
            apiContextBuilder.buildApiContext().then { ApiContext ac ->
                apiContext = ac
                return Promise.pure()
            }
        }.then {
            requestValidator.validatePreparePurchaseRequest(request).then {
                if (!request.isIAP) {
                    return requestValidator.validateOfferForPurchase(user.getId(), request.offer, apiContext.country.getId(), apiContext.locale.getId(), false)
                } else {
                    iapParam = storeUtils.buildIAPParam()
                    return facadeContainer.catalogFacade.getCatalogItemByPackageName(iapParam.packageName, iapParam.packageVersion, iapParam.packageSignatureHash).then { Item item ->
                        hostItem = item
                        browseService.getIAPItems(new HostItemInfo(packageName: iapParam.packageName, hostItemId: new ItemId(hostItem.itemId)),
                                Collections.singleton(request.sku), apiContext).then { List<com.junbo.store.spec.model.browse.document.Item> iapItems ->
                            if (CollectionUtils.isEmpty(iapItems)) {
                                throw AppErrors.INSTANCE.iapItemNotFoundWithSku().exception()
                            }
                            request.offer = iapItems[0].offer.self
                            return Promise.pure()
                        }
                    }
                }
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
            fillPurchaseState(purchaseState, request, iapParam, apiContext)
            return Promise.pure(null)
        }.then {
            resourceContainer.countryResource.get(apiContext.country.getId(), new CountryGetOptions()).then { Country country ->
                currencyId = country.defaultCurrency
                return Promise.pure(null)
            }
        }.then {
            if (request.instrument == null) {
                return Promise.pure(null)
            }
            return validateInstrumentForPreparePurchase(user, request)
        }.then {
            return getPurchaseChallenge(user.getId(), request, apiContext).then { Challenge challenge ->
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

            if (request.instrument != null) {
                selectedInstrument = request.instrument
            } else if (user.defaultPI != null) {
                selectedInstrument = user.defaultPI
            }
            return facadeContainer.orderFacade.createTentativeOrder(user.getId(), Collections.singletonList(request.offer), currencyId, selectedInstrument, apiContext).then { Order createdOrder ->
                return resourceContainer.currencyResource.get(currencyId, new CurrencyGetOptions()).then { Currency currency ->
                    response.formattedTotalPrice = createdOrder.totalAmount.setScale(currency.numberAfterDecimal, BigDecimal.ROUND_HALF_UP) + currency.symbol
                    purchaseState.order = createdOrder.getId()
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
                    response.order = createdOrder.getId()
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
        Challenge challenge
        requestValidator.validateRequiredApiHeaders()
        return identityUtils.getVerifiedUserFromToken().then { User u ->
            user = u
            apiContextBuilder.buildApiContext().then { ApiContext ac ->
                apiContext = ac
                return Promise.pure(null)
            }
        }.then {
            requestValidator.validateCommitPurchaseRequest(commitPurchaseRequest)
        }.then {
            tokenProcessor.toTokenObject(commitPurchaseRequest.purchaseToken, PurchaseState).then { PurchaseState e ->
                purchaseState = e
                if (purchaseState.user != apiContext.user) {
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
                    userTosAgreementId: userTosAgreement.getId(),
                    tos: userTosAgreement.tosId,
                    acceptedTime: userTosAgreement.agreementTime
                ))
            }
        }
    }

    @Override
    Promise<InitialDownloadItemsResponse> getInitialDownloadItems(Integer version) {
        requestValidator.validateRequiredApiHeaders()
        prepareBrowse(false).then { ApiContext apiContext ->
            return browseService.getInitialDownloadItems(version, apiContext)
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
            return browseService.getLibrary(false, null, apiContext)
        }
    }

    @Override
    Promise<DetailsResponse> getItemDetails(DetailsRequest request) {
        requestValidator.validateRequiredApiHeaders().validateDetailsRequest(request)
        prepareBrowse().then { ApiContext apiContext ->
            return browseService.getItem(request.itemId, true, apiContext)
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
        requestValidator.validateRequiredApiHeaders().validateRequestBody(request)
        prepareBrowse().then { ApiContext apiContext ->
            return browseService.addReview(request, apiContext)
        }
    }

    @Override
    Promise<DeliveryResponse> getDelivery(DeliveryRequest request) {
        requestValidator.validateRequiredApiHeaders().validateDeliveryRequest(request, false)
        prepareBrowse().then { ApiContext apiContext ->
            return browseService.getDelivery(request, apiContext)
        }
    }

    @Override
    Promise<List<DeliveryResponse>> getDeliveryFromOffer(DeliveryRequest request) {
        List<DeliveryResponse> responses = [] as List
        requestValidator.validateRequiredApiHeaders().validateDeliveryRequest(request, true)
        OfferId offerId = request.offerId
        prepareBrowse().then { ApiContext apiContext ->
            facadeContainer.catalogFacade.getItemsInOffer(offerId.value).then { List<ItemId> itemIds ->
                Promise.each(itemIds) { ItemId itemId ->
                    browseService.getDelivery(new DeliveryRequest(itemId: itemId), apiContext).then { DeliveryResponse response ->
                        responses << response
                        return Promise.pure()
                    }
                }
            }.then {
                return Promise.pure(responses)
            }
        }
    }

    private Promise<BillingProfile> innerGetBillingProfile(User user, LocaleId locale, CountryId country, OfferId offerId,
                                                           PaymentInstrumentId paymentInstrumentId) {
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
            innerGetBillingProfile(user, locale, country, offer, paymentInstrumentId)
        }
    }

    private Promise<BillingProfile> innerGetBillingProfile(User user, LocaleId locale, CountryId country,
                                                           com.junbo.store.spec.model.catalog.Offer offer, PaymentInstrumentId paymentInstrumentId) {
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

    private void addQuery(UriBuilder builder, String name, Object value) {
        if (value != null) {
            builder.queryParam(name, value)
        }
    }

    private Promise<List<Entitlement>> getEntitlementsByOrder(Order order, HostItemInfo hostItemInfo, String developerPayload, ApiContext apiContext) {
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

    private Promise updateUserCredential(UserId userId, UserProfileUpdateRequest request, ErrorContext errorContext) {
        return Promise.pure().then {
            if (StringUtils.isEmpty(request.userProfile.password)) {
                return Promise.pure()
            }
            errorContext.fieldName = 'userProfile.password'
            return resourceContainer.userUserCredentialResource.create(userId, new UserCredential(
                    type: 'PASSWORD',
                    currentPassword: request.challengeAnswer.password,
                    value: request.userProfile.password
            )).recover { Throwable ex ->
                throwOnUserPasswordIncorrect(ex)
                appErrorUtils.throwOnFieldInvalidError(errorContext, ex, ErrorCodes.Identity.majorCode)
                throw ex
            }
        }.then {
            if (StringUtils.isEmpty(request.userProfile.pin)) {
                return Promise.pure()
            }
            errorContext.fieldName = 'userProfile.pin'
            return resourceContainer.userUserCredentialResource.create(userId, new UserCredential(
                    type: 'PIN',
                    currentPassword: request.challengeAnswer.password,
                    value: request.userProfile.pin
            )).recover { Throwable ex ->
                throwOnUserPasswordIncorrect(ex)
                appErrorUtils.throwOnFieldInvalidError(errorContext, ex, ErrorCodes.Identity.majorCode)
                throw ex
            }
        }
    }

    private String getPlatformName() {
        return 'ANDROID'
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

    Promise<CommitPurchaseResponse> doCommitPurchaseResponse(PurchaseState purchaseState, ApiContext apiContext) {
        OrderId orderId = purchaseState.order
        HostItemInfo hostItemInfo
        Item hostItem
        CommitPurchaseResponse response = new CommitPurchaseResponse()

        Promise.pure().then {
            if (purchaseState.iapPackageName == null) {
                return Promise.pure()
            }
            facadeContainer.catalogFacade.getCatalogItemByPackageName(purchaseState.iapPackageName, null, null).then { Item e ->
                hostItemInfo = new HostItemInfo(packageName: purchaseState.iapPackageName, hostItemId: new ItemId(e.itemId))
                hostItem = e
                return Promise.pure()
            }
        }.then {
            resourceContainer.orderResource.getOrderByOrderId(orderId).then { Order order ->
                order.tentative = false
                resourceContainer.orderResource.updateOrderByOrderId(order.getId(), order).then { Order settled ->
                    response.order = settled.getId()
                    getEntitlementsByOrder(settled, hostItemInfo, purchaseState.developerPayload, apiContext).then { List<Entitlement> entitlements ->
                        response.entitlements = entitlements
                        return Promise.pure(response)
                    }
                }
            }
        }
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
        if (purchaseState.developerPayload == null) {
            purchaseState.developerPayload = request.developerPayload
        } else if (purchaseState.developerPayload != request?.developerPayload) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('developerPayload', 'Input developerPayload isn\'t consistent with token').exception()
        }
    }

    private void throwOnUserPasswordIncorrect(Throwable ex) {
        if (appErrorUtils.isAppError(ex, ErrorCodes.Identity.UserPasswordIncorrect)) {
            throw AppErrors.INSTANCE.invalidChallengeAnswer().exception()
        }
    }

    private Promise<Challenge> getPurchaseChallenge(UserId userId, PreparePurchaseRequest request, ApiContext apiContext) {
        return challengeHelper.checkPurchasePINChallenge(userId, request?.challengeAnswer).then { Challenge challenge ->
            if (challenge != null) {
                return Promise.pure(challenge)
            }
            return challengeHelper.checkTosChallenge(userId, tosPurchaseType, apiContext.country.getId(), request.challengeAnswer, apiContext.locale.getId())
        }
    }

    private Promise<ApiContext> prepareBrowse() {
        return prepareBrowse(true)
    }

    private Promise<ApiContext> prepareBrowse(boolean needEmailVerify) {
        if (verifyUserInBrowse) {
            if (needEmailVerify) {
                return identityUtils.getVerifiedUserFromToken().then {
                    return apiContextBuilder.buildApiContext()
                }
            } else {
                return identityUtils.getActiveUserFromToken().then {
                    return apiContextBuilder.buildApiContext()
                }
            }
        } else {
            return apiContextBuilder.buildApiContext()
        }
    }
}
