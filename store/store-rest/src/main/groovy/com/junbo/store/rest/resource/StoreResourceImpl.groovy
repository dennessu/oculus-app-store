package com.junbo.store.rest.resource

import com.junbo.catalog.spec.enums.EntitlementType
import com.junbo.catalog.spec.model.item.*
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.model.offer.OfferRevisionGetOptions
import com.junbo.catalog.spec.model.offer.OfferRevisionLocaleProperties
import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.enumid.LocaleId
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
import com.junbo.identity.spec.v1.option.list.UserListOptions
import com.junbo.identity.spec.v1.option.model.CurrencyGetOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.langur.core.client.PathParamTranscoder
import com.junbo.langur.core.promise.Promise
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.PaymentInfo
import com.junbo.payment.spec.model.PageMetaData
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.payment.spec.model.PaymentInstrumentSearchParam
import com.junbo.store.db.repo.ConsumptionRepository
import com.junbo.store.rest.utils.*
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.model.*
import com.junbo.store.spec.model.billing.*
import com.junbo.store.spec.model.browse.*
import com.junbo.store.spec.model.iap.*
import com.junbo.store.spec.model.identity.*
import com.junbo.store.spec.model.purchase.*
import com.junbo.store.spec.resource.StoreResource
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

    @Resource(name = 'storeRequestValidator')
    private RequestValidator requestValidator

    @Resource(name = 'storeIdentityUtils')
    private IdentityUtils identityUtils

    private List<PIType> piTypes

    @Override
    Promise<UserProfileGetResponse> getUserProfile(@BeanParam UserProfileGetRequest userProfileGetRequest) {
        return innerGetUserProfile(userProfileGetRequest.userId).syncThen { StoreUserProfile userProfile ->
            return new UserProfileGetResponse(status: ResponseStatus.SUCCESS.name(), userProfile: userProfile)
        }
    }

    @Override
    Promise<UserProfileUpdateResponse> updateUserProfile(UserProfileUpdateRequest userProfileUpdateRequest) {
        User user = null
        Promise.pure(null).then {
            requestValidator.validateUserProfileUpdateRequest(userProfileUpdateRequest).then {
                resourceContainer.userResource.get(userProfileUpdateRequest.userId, new UserGetOptions()).syncThen { User u ->
                    user = u
                    user.emails = user.emails == null ? new ArrayList<UserPersonalInfoLink>() : user.emails
                    user.phones = user.phones == null ? new ArrayList<UserPersonalInfoLink>() : user.phones
                    user.addresses = user.addresses == null ? new ArrayList<UserPersonalInfoLink>() : user.addresses
                }
            }
        }.then {
            PersonalInfo updateValue = ObjectMapperProvider.instance().treeToValue(userProfileUpdateRequest.updateValue, PersonalInfo)
            updateValue.isDefault = updateValue.isDefault == null ? false : updateValue.isDefault
            UserProfileUpdateRequest.UpdateAction action = UserProfileUpdateRequest.UpdateAction.valueOf(userProfileUpdateRequest.action)
            if (action.requirePersonalInfoId) {
                requestValidator.notEmpty(updateValue.userPersonalInfoId, 'updateValue.userPersonalInfoId')
            }
            switch (action) {
                case UserProfileUpdateRequest.UpdateAction.REMOVE_EMAIL:
                    identityUtils.removeFromUserPersonalInfoLinkList(user.emails, updateValue.userPersonalInfoId, true)
                    return Promise.pure(null)

                case UserProfileUpdateRequest.UpdateAction.ADD_EMAIL:
                    return identityUtils.addPersonalInfo(user.getId(), Constants.PersonalInfoType.EMAIL, updateValue, user.emails)

                case UserProfileUpdateRequest.UpdateAction.UPDATE_DEFAULT_EMAIL:
                    identityUtils.setDefaultUserPersonalInfo(user.emails, updateValue.userPersonalInfoId)
                    return Promise.pure(null)

                case UserProfileUpdateRequest.UpdateAction.REMOVE_PHONE:
                    identityUtils.removeFromUserPersonalInfoLinkList(user.phones, updateValue.userPersonalInfoId, true)
                    return Promise.pure(null)

                case UserProfileUpdateRequest.UpdateAction.ADD_PHONE:
                    return identityUtils.addPersonalInfo(user.getId(), Constants.PersonalInfoType.PHONE, updateValue, user.phones)

                case UserProfileUpdateRequest.UpdateAction.UPDATE_DEFAULT_PHONE:
                    identityUtils.setDefaultUserPersonalInfo(user.phones, updateValue.userPersonalInfoId)
                    return Promise.pure(null)

                case UserProfileUpdateRequest.UpdateAction.UPDATE_NAME:
                    return resourceContainer.userPersonalInfoResource.create(new UserPersonalInfo(
                            userId: user.getId(), type: Constants.PersonalInfoType.NAME, value: updateValue.value
                    )).then { UserPersonalInfo added ->
                        user.name = new UserPersonalInfoLink(value: added.getId())
                        return Promise.pure(null)
                    }
            }
        }.then {
            resourceContainer.userResource.put(user.getId(), user);
        }.then {
            return innerGetUserProfile(user.getId()).syncThen { StoreUserProfile userProfile ->
               return new UserProfileUpdateResponse(status: ResponseStatus.SUCCESS.name(), userProfile: userProfile)
            }
        }
    }

    @Override
    Promise<BillingProfileGetResponse> getBillingProfile(@BeanParam BillingProfileGetRequest billingProfileGetRequest) {
        BillingProfileGetResponse response = new BillingProfileGetResponse()
        Promise.pure(null).then {
            requestValidator.validateBillingProfileGetRequest(billingProfileGetRequest)
        }.then {
            resourceContainer.userResource.get(billingProfileGetRequest.userId, new UserGetOptions()).then {
                return innerGetBillingProfile(billingProfileGetRequest.userId, billingProfileGetRequest.locale).syncThen { BillingProfile billingProfile ->
                    response.billingProfile = billingProfile
                    response.status = ResponseStatus.SUCCESS.name()
                    return response
                }
            }
        }
    }

    @Override
    Promise<BillingProfileUpdateResponse> updateBillingProfile(BillingProfileUpdateRequest billingProfileUpdateRequest) {
        User user
        Promise.pure(null).then {
            requestValidator.validateBillingProfileUpdateRequest(billingProfileUpdateRequest)
        }.then {
            resourceContainer.userResource.get(billingProfileUpdateRequest.userId, new UserGetOptions()).syncThen { User u ->
                user = u
            }
        }.then {
                switch (billingProfileUpdateRequest.action) {
                    case BillingProfileUpdateRequest.UpdateAction.ADD_PI.name():
                        return instrumentUtils.addInstrument(billingProfileUpdateRequest, user.getId())
                    case BillingProfileUpdateRequest.UpdateAction.UPDATE_PI.name():
                        return instrumentUtils.updateInstrument(billingProfileUpdateRequest, user.getId())
                    case BillingProfileUpdateRequest.UpdateAction.REMOVE_PI.name():
                        return instrumentUtils.removeInstrument(billingProfileUpdateRequest)
                }
        }.then {
                innerGetBillingProfile(user.getId(), billingProfileUpdateRequest.locale).then { BillingProfile billingProfile ->
                    BillingProfileUpdateResponse response = new BillingProfileUpdateResponse(
                            billingProfile: billingProfile,
                            status: ResponseStatus.SUCCESS.name()
                    )
                    return Promise.pure(response)
                }
        }
    }

    @Override
    Promise<EntitlementsGetResponse> getEntitlements(
            @BeanParam EntitlementsGetRequest entitlementsGetRequest, @BeanParam PageParam pageParam) {
        pageParam.start = pageParam.start == null ? 0 : pageParam.start
        pageParam.count = pageParam.count == null ? PAGE_SIZE : pageParam.count
        User user
        Item hostItem = null
        List<Entitlement> entitlements = [] as LinkedList<Entitlement>

        // get user
        Promise promise = resourceContainer.userResource.get(entitlementsGetRequest.userId, new UserGetOptions()).syncThen { User u ->
            user = u
        }

        // get host item
        if (entitlementsGetRequest.isIAP) {
            promise = promise.then {
                getItemByPackageName(entitlementsGetRequest.packageName).syncThen { Item item ->
                    hostItem = item
                }
            }
        }

        // build entitlement search parameter
        EntitlementSearchParam entitlementSearchParam = null
        promise.syncThen {
            entitlementSearchParam = new EntitlementSearchParam(
                    userId: user.getId(),
                    itemIds: new HashSet<ItemId>(),
                    isActive: entitlementsGetRequest.isActive == null ? true : entitlementsGetRequest.isActive,
                    type: entitlementsGetRequest.entitlementType
            )
            if (entitlementsGetRequest.isIAP) {
                entitlementSearchParam.hostItemId = new ItemId(hostItem.getId())
            }

        }.then { // read entitlements from entitlement component
            iteratePageRead {
                resourceContainer.entitlementResource.searchEntitlements(
                        entitlementSearchParam,
                        new PageMetadata(
                                start: pageParam.start,
                                count: pageParam.count
                        )
                ).then { Results<com.junbo.entitlement.spec.model.Entitlement> results -> // get the IAP entitlements from entitlement & catalog component
                    // start page, offset, last index
                    Promise.each(results.items) { com.junbo.entitlement.spec.model.Entitlement catalogEntitlement ->
                        return convertEntitlement(catalogEntitlement, entitlementsGetRequest.packageName).then { Entitlement entitlement ->
                            if (!entitlementsGetRequest.isIAP || entitlement.useCount > 0) {
                                entitlements << entitlement
                            }
                            pageParam.start++
                            if (entitlements.size() > pageParam.count) {
                                return Promise.pure(Promise.BREAK)
                            } else {
                                return Promise.pure(null)
                            }
                        }
                    }.then { // check more entitlement to read
                        if (entitlements.size() >= pageParam.count || results.items.size() < pageParam.count) {
                            return Promise.pure(false)
                        } else {
                            return Promise.pure(true)
                        }
                    }
                }
            }
        }.then {
            Results<Entitlement> results = new Results<>()
            if (entitlements.size() > pageParam.count) {
                results.hasNext = true;
                results.next = new Link(href: buildEntitlementNextUrl(entitlementsGetRequest, pageParam))
                results.items = entitlements.subList(0, pageParam.count)
            } else {
                results.items = entitlements
            }

            return Promise.pure(null).then {
                if (entitlementsGetRequest.isIAP) { // sign the iap entitlements
                    return Promise.each(results.items) { Entitlement entitlement ->
                        entitlement.setSignatureTimestamp(System.currentTimeMillis())
                        return signEntitlement(entitlement, hostItem.itemId)
                    }
                }
                return Promise.pure(null)
            }.then {
                EntitlementsGetResponse response = new EntitlementsGetResponse(
                        status: ResponseStatus.SUCCESS.name(),
                        entitlements: results
                )
                return Promise.pure(response)
            }
        }
    }

    @Override
    Promise<SelectInstrumentResponse> selectInstrumentForPurchase(SelectInstrumentRequest selectInstrumentRequest) {
        Promise.pure(null).then {
            requestValidator.validateSelectInstrumentRequest(selectInstrumentRequest)
        }.then {
            OrderId orderId = new OrderId(IdFormatter.decodeId(OrderId, selectInstrumentRequest.purchaseToken))
            resourceContainer.orderResource.getOrderByOrderId(orderId).then { Order o ->
                o.payments = [
                        new PaymentInfo(paymentInstrument: selectInstrumentRequest.instrumentId)
                ]
                resourceContainer.orderResource.updateOrderByOrderId(o.getId(), o).then { Order updated ->
                    return Promise.pure(new SelectInstrumentResponse(status: ResponseStatus.SUCCESS.name()))
                }
            }
        }
    }

    @Override
    Promise<MakeFreePurchaseResponse> makeFreePurchase(MakeFreePurchaseRequest makeFreePurchaseRequest) {
        Promise.pure(null).then {
            requestValidator.validateMakeFreePurchaseRequest(makeFreePurchaseRequest)
        }.then {
            Order order = new Order(
                    user: makeFreePurchaseRequest.userId,
                    country: makeFreePurchaseRequest.country,
                    currency: new CurrencyId(FREE_PURCHASE_CURRENCY),
                    locale: makeFreePurchaseRequest.locale,
                    tentative: true,
                    orderItems: [new OrderItem(offer: makeFreePurchaseRequest.offerId, quantity: 1)]
            )

            return resourceContainer.orderResource.createOrder(order).syncThen { Order o ->
                order = o
            }.then {
                order.tentative = false
                resourceContainer.orderResource.updateOrderByOrderId(order.getId(), order).then {
                    MakeFreePurchaseResponse response = new MakeFreePurchaseResponse()
                    response.status = ResponseStatus.SUCCESS.name()
                    response.orderId = order.getId()
                    getEntitlementsByOrder(order, null).then { List<Entitlement> entitlements ->
                        response.entitlements = entitlements
                        return Promise.pure(response)
                    }
                }
            }
        }
    }

    @Override
    Promise<PreparePurchaseResponse> preparePurchase(PreparePurchaseRequest preparePurchaseRequest) {
        OfferId offerId = preparePurchaseRequest.offerId
        Item hostItem

        return Promise.pure(null).then {
            requestValidator.validatePreparePurchaseRequest(preparePurchaseRequest)
        }
        .then { // validate offer if inapp purchase
            if (preparePurchaseRequest.iapParams != null) {
                return getItemByPackageName(preparePurchaseRequest.iapParams.packageName).then { Item item ->
                    hostItem = item
                    return iapValidator.validateInAppOffer(offerId, hostItem)
                }
            }
            return Promise.pure(null)
        }.then {
            Order order = new Order(
                    user: preparePurchaseRequest.userId,
                    country: preparePurchaseRequest.country,
                    currency: preparePurchaseRequest.currency,
                    locale: preparePurchaseRequest.locale,
                    tentative: true,
                    orderItems: [new OrderItem(offer: preparePurchaseRequest.offerId, quantity: 1)]
            )

            if (preparePurchaseRequest.iapParams != null) {
                order.properties = [:]
                order.properties.put('iap.packageName', preparePurchaseRequest.iapParams.packageName)
                order.properties.put('iap.hostItemId', hostItem.itemId)
            }

            return resourceContainer.orderResource.createOrder(order).then { Order createOrder ->
                PreparePurchaseResponse response = new PreparePurchaseResponse()
                response.status = ResponseStatus.SUCCESS.name()
                resourceContainer.currencyResource.get(preparePurchaseRequest.currency, new CurrencyGetOptions()).then { Currency currency ->
                    response.formattedTotalPrice = order.totalAmount + currency.symbol
                    response.purchaseToken = IdFormatter.encodeId(createOrder.getId())
                    return Promise.pure(null)
                }.syncThen {
                    response.status = ResponseStatus.SUCCESS.name()
                    return response
                }
            }
        }
    }

    @Override
    Promise<CommitPurchaseResponse> commitPurchase(CommitPurchaseRequest commitPurchaseRequest) {
        Promise.pure(null).then {
            requestValidator.validateCommitPurchaseRequest(commitPurchaseRequest)
        }.then {
            OrderId orderId = new OrderId(IdFormatter.decodeId(OrderId, commitPurchaseRequest.purchaseToken))
            String iapPackageName = null, iapItemId = null
            CommitPurchaseResponse response = new CommitPurchaseResponse()
            boolean inappPurchase
            resourceContainer.orderResource.getOrderByOrderId(orderId).then { Order order ->
                inappPurchase = !StringUtils.isEmpty(iapPackageName)
                iapPackageName = order.properties?.get('iap.packageName')
                iapItemId = order.properties?.get('iap.hostItemId')
                order.tentative = false
                resourceContainer.orderResource.updateOrderByOrderId(order.getId(), order).then { Order settled ->
                    response.orderId = settled.getId()
                    response.status = ResponseStatus.SUCCESS.name()
                    return Promise.pure(null)
                }.then { // get entitlements
                    getEntitlementsByOrder(order, iapPackageName).then { List<Entitlement> entitlements ->
                        response.entitlements = entitlements
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
                return Promise.pure(response)
            }
        }
    }

    @Override
    Promise<IAPOfferGetResponse> iapGetOffers(IAPOfferGetRequest iapOfferGetRequest) {
        return getItemByPackageName(iapOfferGetRequest.packageName).then { Item hostItem ->
            return getInAppOffers(hostItem, iapOfferGetRequest).then { List<Offer> offers ->
                return Promise.pure(
                        new IAPOfferGetResponse(
                                status: ResponseStatus.SUCCESS.name(),
                                offers: new Results<Offer>(items: offers)
                        )
                )
            }
        }
    }

    @Override
    Promise<IAPEntitlementConsumeResponse> iapConsumeEntitlement(IAPEntitlementConsumeRequest iapEntitlementConsumeRequest) {
        Item hostItem = null
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
        }
    }

    @Override
    Promise<GetTocResponse> getToc(GetTocRequest getTocRequest) {
        return null
    }

    @Override
    Promise<GetSectionResponse> getSection(@BeanParam GetSectionRequest getSectionRequest) {
        return null
    }

    @Override
    Promise<GetListResponse> getList(@BeanParam GetListRequest getListRequest) {
        return null
    }

    @Override
    Promise<GetDetailsResponse> getDetails(@BeanParam GetDetailsRequest getDetailsRequest) {
        return null
    }

    private Promise<BillingProfile> innerGetBillingProfile(UserId userId, LocaleId locale) {
        BillingProfile billingProfile = new BillingProfile(userId: userId)
        billingProfile.instruments = []
        PageMetaData pageMetaData = new PageMetaData(start: 0, count: PAGE_SIZE)

        iteratePageRead {
            resourceContainer.paymentInstrumentResource.searchPaymentInstrument(new PaymentInstrumentSearchParam(userId: userId), pageMetaData).then { Results<PaymentInstrument> paymentInstrumentResults ->
                Promise.each(paymentInstrumentResults.items) { PaymentInstrument pi ->
                    Instrument storePi = new Instrument()
                    dataConvertor.toInstrument(pi, storePi)
                    return instrumentUtils.loadInstrumentPersonalInfo(storePi, pi).syncThen {
                        billingProfile.instruments << dataConvertor.toInstrument(pi, storePi)
                    }
                }.then {
                    pageMetaData.start += PAGE_SIZE
                    return Promise.pure(paymentInstrumentResults.items.size() == PAGE_SIZE)
                }
            }
        }.then { // todo set the default pi
            billingProfile.paymentOptions = []
            loadData().then {
                piTypes.each { com.junbo.identity.spec.v1.model.PIType piType ->
                    PaymentOption paymentOption = dataConvertor.toPaymentOption(piType, locale)
                    billingProfile.paymentOptions << paymentOption
                }
                return Promise.pure(null)
            }
        }.then {
            return Promise.pure(billingProfile)
        }
    }

    private Promise loadData() {
        if (piTypes == null) {
            resourceContainer.piTypeResource.list(new PITypeListOptions()).then { Results<PIType> typeResults ->
                piTypes = []
                piTypes.addAll(typeResults.items)
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

    private Promise<Void> iteratePageRead(Closure<Promise<Boolean>> pageReadFunc) {
        pageReadFunc.call().then { Boolean moreItem ->
            if (!moreItem) {
                return Promise.pure(null)
            }
            return iteratePageRead(pageReadFunc)
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

    private String buildEntitlementNextUrl(EntitlementsGetRequest entitlementsGetRequest, PageParam pageParam) {
        UriBuilder builder = UriBuilder.fromPath(IdUtil.getResourcePathPrefix()).path("iap").path("entitlements");
        addQuery(builder, 'packageName', entitlementsGetRequest.packageName)
        addQuery(builder, 'userId', IdFormatter.encodeId(entitlementsGetRequest.userId))
        addQuery(builder, 'itemType', entitlementsGetRequest.itemType)
        addQuery(builder, 'entitlementType', entitlementsGetRequest.entitlementType)
        addQuery(builder, 'isActive', entitlementsGetRequest.isActive)
        builder.queryParam("isIAP", entitlementsGetRequest.isIAP);
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

        boolean consumable = itemConsumable(itemRevision)
        Entitlement result = new Entitlement(
                userId: new UserId(entitlement.userId),
                entitlementId: new EntitlementId(entitlement.getId()),
                useCount: consumable ? entitlement.useCount : 1,
                sku: itemRevision.sku,
                type: entitlement.type,
                itemType: item.type,
                isConsumable: consumable,
                packageName: hostPackageName != null ? hostPackageName : itemRevision.packageName,
                itemId: new ItemId(item.getId())
        )

        if (entitlement.type == EntitlementType.DOWNLOAD.name()) {
            result.appDeliveryData = new AppDeliveryData()
            String platform = getPlatformName()
            result.appDeliveryData.downloadUrl = entitlement.binaries[platform]
            if (StringUtils.isEmpty(result.appDeliveryData.downloadUrl)) {
                LOGGER.warn('name=Store_DownloadUrl_Empty, entitlementId={}, platform={}', entitlement.getId(), platform)
            }
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
        String jsonText = ObjectMapperProvider.instance().writeValueAsString(entitlement)
        // todo filter out field not needed for iap
        entitlement.setIapEntitlementData(jsonText)
        return resourceContainer.itemCryptoResource.sign(itemId, new ItemCryptoMessage(message: jsonText)).then { ItemCryptoMessage itemCryptoMessage ->
            entitlement.iapSignature = itemCryptoMessage.message
            return Promise.pure(entitlement)
        }
    }

    private Promise<Consumption> signConsumption(Consumption consumption, String itemId) {
        String jsonText = ObjectMapperProvider.instance().writeValueAsString(consumption)
        // todo filter out field not needed for iap
        consumption.iapConsumptionData =jsonText
        return resourceContainer.itemCryptoResource.sign(itemId, new ItemCryptoMessage(message: jsonText)).then { ItemCryptoMessage itemCryptoMessage ->
            consumption.iapSignature = itemCryptoMessage.message
            return Promise.pure(consumption)
        }
    }

    private Promise<Void> getOffersFromItem(Item item, ItemRevision itemRevision, IAPOfferGetRequest iapOfferGetRequest, Map<String, Offer> offers) {
        OffersGetOptions offerOption = new OffersGetOptions(
                itemId: item.itemId,
                published: true,
                cursor : String.valueOf(0),
                size : PAGE_SIZE
        )

        resourceContainer.offerResource.getOffers(offerOption).then { Results<com.junbo.catalog.spec.model.offer.Offer> catalogOffers ->
            if (catalogOffers.items.size() >= PAGE_SIZE) {
                LOGGER.warn('name=IAP_TooManyOffers_Return, itemId={}, fetch first {}', item.id, PAGE_SIZE)
            }

            return Promise.each(catalogOffers.items) { com.junbo.catalog.spec.model.offer.Offer cOffer ->
                if (offers.containsKey(cOffer.offerId)) {
                    return Promise.pure(null)
                }
                return resourceContainer.offerRevisionResource.getOfferRevision(cOffer.currentRevisionId, new OfferRevisionGetOptions()).then { OfferRevision offerRevision ->
                    offers.put(cOffer.id, convertOffer(cOffer, offerRevision, item, itemRevision, iapOfferGetRequest.locale.value, iapOfferGetRequest.country.value, iapOfferGetRequest.currency.value))
                    return Promise.pure(null)
                }
            }
        }
    }

    Promise<StoreUserProfile> innerGetUserProfile(UserId userId) {
        User user
        StoreUserProfile userProfile = new StoreUserProfile()
        resourceContainer.userResource.get(userId, new UserGetOptions()).syncThen { User u ->
            user = u;
        }.syncThen {
            userProfile.username = user.username
            userProfile.idUserProfile = user.profile
            userProfile.userId = userId
        }.then {
            identityUtils.expandPersonalInfo(user.emails).syncThen { List<PersonalInfo> personalInfos ->
                userProfile.emails = personalInfos
            }
        }.then {
            identityUtils.expandPersonalInfo(user.phones).syncThen { List<PersonalInfo> personalInfos ->
                userProfile.phones = personalInfos
            }
        }.then {
            identityUtils.expandPersonalInfo(user.addresses).syncThen { List<PersonalInfo> personalInfos ->
                userProfile.addresses = personalInfos
            }
        }.then {
            if (user.name != null) {
                return resourceContainer.userPersonalInfoResource.get(user.name.value, new UserPersonalInfoGetOptions()).syncThen { UserPersonalInfo info ->
                    userProfile.name = dataConvertor.toStorePersonalInfo(info, user.name)
                }
            }
        }.syncThen {
            return userProfile
        }
    }

    private Promise<List<Offer>> getInAppOffers(Item hostItem, IAPOfferGetRequest iapOfferGetRequest) {
        ItemsGetOptions itemOption = new ItemsGetOptions(
                hostItemId: hostItem.itemId,
                type: iapOfferGetRequest.getType(),
                size: PAGE_SIZE,
                cursor: String.valueOf(0)
        )

        Map<String, Offer> offers = new HashMap<>()
        return iteratePageRead {
            return resourceContainer.itemResource.getItems(itemOption).then { Results<Item> itemResults ->
                boolean hasMore = itemResults.items.size() >= itemOption.size
                itemOption.cursor = String.valueOf(Integer.valueOf(itemOption.cursor) + itemResults.items.size())
                return Promise.each(itemResults.items) { Item item ->
                    return resourceContainer.itemRevisionResource.getItemRevision(item.currentRevisionId, new ItemRevisionGetOptions()).then { ItemRevision itemRevision ->
                        return getOffersFromItem(item, itemRevision, iapOfferGetRequest, offers).then {
                            return Promise.pure(hasMore)
                        }
                    }
                }.then {
                    return Promise.pure(hasMore)
                }
            }
        }.syncThen {
            return new ArrayList<Offer>(offers.values())
        }
    }

    private static Offer convertOffer(com.junbo.catalog.spec.model.offer.Offer offer, OfferRevision offerRevision,
                                      Item item, ItemRevision itemRevision, String locale, String country, String currency) {

        Offer result = new Offer(
                offerId: new OfferId(offer.getId()),
                type: item.type,
                sku: itemRevision.sku,
                isConsumable: itemConsumable(itemRevision)
        )
        OfferRevisionLocaleProperties offerRevisionLocaleProperties = offerRevision.locales.get(locale)

        result.description = offerRevisionLocaleProperties?.shortDescription
        result.title = offerRevisionLocaleProperties?.name
        result.price = offerRevision.price?.prices?.get(country)?.get(currency)

        return result
    }

    private String getPlatformName() {
        return 'ANDROID'
    }
}
