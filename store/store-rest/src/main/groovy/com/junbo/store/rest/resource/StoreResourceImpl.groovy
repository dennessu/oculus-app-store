package com.junbo.store.rest.resource.raw

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.ItemId
import com.junbo.common.id.OfferId
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.*
import com.junbo.identity.spec.v1.option.list.PITypeListOptions
import com.junbo.identity.spec.v1.option.list.UserCredentialListOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.langur.core.client.PathParamTranscoder
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.error.AppErrorUtils
import com.junbo.store.clientproxy.error.ErrorCodes
import com.junbo.store.clientproxy.error.ErrorContext
import com.junbo.store.rest.browse.BrowseService
import com.junbo.store.rest.challenge.ChallengeHelper
import com.junbo.store.rest.commerce.PurchaseService
import com.junbo.store.rest.purchase.TokenProcessor
import com.junbo.store.rest.utils.*
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.billing.*
import com.junbo.store.spec.model.browse.*
import com.junbo.store.spec.model.identity.*
import com.junbo.store.spec.model.purchase.*
import com.junbo.store.spec.resource.StoreResource
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang3.StringUtils
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

    @Resource(name = 'storePurchaseService')
    private PurchaseService purchaseService

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
                userProfile.username = ObjectMapperProvider.instanceNotStrict().treeToValue(usernameInfo.value, UserLoginName).userName
                return Promise.pure()
            }
        }.then {
            UserPersonalInfoLink emailLink = user.emails.find { UserPersonalInfoLink link -> link.isDefault }
            if (emailLink == null) {
                return Promise.pure(null)
            }

            return resourceContainer.userUserPersonalInfoResource.get(emailLink.value, new UserPersonalInfoGetOptions()).syncThen { UserPersonalInfo personalInfo ->
                def email = ObjectMapperProvider.instanceNotStrict().treeToValue(personalInfo.value, Email)

                userProfile.email = new StoreUserEmail(
                        value: email.info,
                        isValidated: personalInfo.isValidated
                )
           }
        }.then{
            return resourceContainer.userUserCredentialResource.list(user.getId(), new UserCredentialListOptions(
                    userId: user.getId(),
                    type: Constants.CredentialType.PIN,
                    active: true
            )).then { Results<UserCredential> results ->
                if (CollectionUtils.isEmpty(results.getItems())) {
                    userProfile.pin = ''
                } else {
                    userProfile.pin = '****'
                }

                return Promise.pure()
            }
        }.syncThen {
            userProfile.password = '******'
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
        requestValidator.validateRequiredApiHeaders()
        apiContextBuilder.buildApiContext().then { ApiContext apiContext ->
            return purchaseService.makeFreePurchase(request, apiContext)
        }
    }

    @Override
    Promise<PreparePurchaseResponse> preparePurchase(PreparePurchaseRequest request) {
        requestValidator.validateRequiredApiHeaders()
        apiContextBuilder.buildApiContext().then { ApiContext apiContext ->
            return purchaseService.preparePurchase(request, apiContext)
        }
    }

    @Override
    Promise<CommitPurchaseResponse> commitPurchase(CommitPurchaseRequest commitPurchaseRequest) {
        requestValidator.validateRequiredApiHeaders()
        apiContextBuilder.buildApiContext().then { ApiContext apiContext ->
            return purchaseService.commitPurchase(commitPurchaseRequest, apiContext)
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

    private void throwOnUserPasswordIncorrect(Throwable ex) {
        if (appErrorUtils.isAppError(ex, ErrorCodes.Identity.UserPasswordIncorrect)) {
            throw AppErrors.INSTANCE.invalidChallengeAnswer().exception()
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
