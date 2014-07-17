package com.junbo.store.rest.resource
import com.junbo.catalog.spec.model.item.*
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.model.offer.OfferRevisionGetOptions
import com.junbo.catalog.spec.model.offer.OfferRevisionLocaleProperties
import com.junbo.catalog.spec.model.offer.OffersGetOptions
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
import com.junbo.store.rest.utils.DataConverter
import com.junbo.store.rest.utils.IAPValidator
import com.junbo.store.rest.utils.InstrumentUtils
import com.junbo.store.rest.utils.ResourceContainer
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.model.*
import com.junbo.store.spec.model.billing.*
import com.junbo.store.spec.model.iap.IAPOfferGetRequest
import com.junbo.store.spec.model.iap.IAPOfferGetResponse
import com.junbo.store.spec.model.purchase.*
import com.junbo.store.spec.resource.StoreResource
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

import javax.annotation.Resource
import javax.ws.rs.BeanParam
import javax.ws.rs.core.UriBuilder
import javax.ws.rs.ext.Provider

@Provider
@Scope('prototype')
@CompileStatic
@Component('defaultStoreResource')
class StoreResourceImpl implements StoreResource {

    public static final String ACTION_TYPE_GRANT_ENTITLEMENT = "GRANT_ENTITLEMENT";

    public static final String ACTION_STATUS_SUCCESS = "SUCCEED";

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

    private List<PIType> piTypes

    @Override
    Promise<UserProfileGetResponse> getUserProfile(@BeanParam UserProfileGetRequest userProfileGetRequest) {
        User user = null;
        com.junbo.store.spec.model.UserProfile userProfile = new com.junbo.store.spec.model.UserProfile()
        return resourceContainer.userResource.list(new UserListOptions(username: userProfileGetRequest.username)).syncThen { Results<User> users ->
            if (!users.items.isEmpty()) {
                user = users.items.iterator().next()
                userProfile.username = user.username
                userProfile.userId = user.getId()

                if (!CollectionUtils.isEmpty(user.emails)) {
                    UserPersonalInfoLink emailLink = user.emails.find { UserPersonalInfoLink link -> link.isDefault }
                    if (emailLink == null) {
                        emailLink = user.emails.iterator().next()
                    }
                    return resourceContainer.userPersonalInfoResource.get(emailLink.value, new UserPersonalInfoGetOptions(properties: 'value')).syncThen { UserPersonalInfo userPersonalInfo ->
                        userProfile.email = ObjectMapperProvider.instance().treeToValue(userPersonalInfo.value, Email).info
                    }
                } else {
                    return Promise.pure(null)
                }
            } else {
                return Promise.pure(null);
            }
        }.syncThen {
            // todo get tfa setting
            if (user == null) {
                return new UserProfileGetResponse(status: ResponseStatus.FAIL.name(), statusDetail: ResponseStatus.Detail.USER_NOT_FOUND.name())
            } else {
                return new UserProfileGetResponse(status: ResponseStatus.SUCCESS.name(), userProfile: userProfile)
            }
        }
    }

    @Override
    Promise<UserProfileUpdateResponse> updateUserProfile(@BeanParam UserProfileUpdateRequest userProfileUpdateRequest) {
        return Promise.pure(null)
    }

    @Override
    Promise<BillingProfileGetResponse> getBillingProfile(@BeanParam BillingProfileGetRequest billingProfileGetRequest) {
        BillingProfileGetResponse response = new BillingProfileGetResponse()
        resourceContainer.userResource.get(billingProfileGetRequest.userId, new UserGetOptions()).then { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFoundByUsername().exception()
            }
            return innerGetBillingProfile(user.getId(), billingProfileGetRequest.locale).syncThen { BillingProfile billingProfile ->
                response.billingProfile = billingProfile
                response.status = ResponseStatus.SUCCESS.name()
                return response
            }
        }
    }

    @Override
    Promise<BillingProfileUpdateResponse> updateBillingProfile(BillingProfileUpdateRequest billingProfileUpdateRequest) {
        return resourceContainer.userResource.get(billingProfileUpdateRequest.userId, new UserGetOptions()).then  { User user ->
            if (user == null) {
                throw AppErrors.INSTANCE.userNotFoundByUsername().exception()
            }

            Promise promise
            switch (billingProfileUpdateRequest.operation) {
                case BillingProfileUpdateRequest.Operation.ADD_PI.name():
                    promise = instrumentUtils.addInstrument(billingProfileUpdateRequest, user.getId())
                    break;
                case BillingProfileUpdateRequest.Operation.UPDATE_PI.name():
                    promise = instrumentUtils.updateInstrument(billingProfileUpdateRequest, user.getId())
                    break;
                case BillingProfileUpdateRequest.Operation.REMOVE_PI.name():
                    promise = instrumentUtils.removeInstrument(billingProfileUpdateRequest)
                    break;
                default:
                    throw AppErrors.INSTANCE.invalidBillingUpdateOperation().exception()
                    break
            }

            return promise.then {
                innerGetBillingProfile(user.getId(), billingProfileUpdateRequest.locale).then { BillingProfile billingProfile ->
                    BillingProfileUpdateResponse response = new BillingProfileUpdateResponse(
                            billingProfile: billingProfile,
                            status: ResponseStatus.SUCCESS.name()
                    )
                    return Promise.pure(response)
                }
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
    Promise<MakeFreePurchaseResponse> makeFreePurchase(MakeFreePurchaseRequest makeFreePurchaseRequest) {
        return Promise.pure(null)
    }

    @Override
    Promise<PreparePurchaseResponse> preparePurchase(PreparePurchaseRequest preparePurchaseRequest) {
        OfferId offerId = preparePurchaseRequest.offerId
        Item hostItem

        return Promise.pure(null).then { // validate offer if inapp purchase
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
                    orderItems: [new OrderItem(offer: preparePurchaseRequest.offerId, quantity: preparePurchaseRequest.quantity)]
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
        OrderId orderId = new OrderId(IdFormatter.decodeId(OrderId, commitPurchaseRequest.purchaseToken))
        Order order
        String iapPackageName = null, iapItemId = null
        CommitPurchaseResponse response = new CommitPurchaseResponse()

        resourceContainer.orderResource.getOrderByOrderId(orderId).then { Order o ->
            order = o
            iapPackageName = order.properties?.get('iap.packageName')
            iapItemId = order.properties?.get('iap.hostItemId')
            o.payments = [
                    new PaymentInfo(paymentInstrument: commitPurchaseRequest.instrumentId)
            ]
            resourceContainer.orderResource.updateOrderByOrderId(o.getId(), o).then { Order updated ->
                updated.tentative = false
                resourceContainer.orderResource.updateOrderByOrderId(updated.getId(), updated).then { Order settled ->
                    response.orderId = settled.getId()
                    response.status = ResponseStatus.SUCCESS.name()
                    return Promise.pure(null)
                }
            }
        }.then { // get iap entitlements
            if (!StringUtils.isEmpty(iapPackageName)) {
                response.iapEntitlements = []
                getEntitlementsByOrder(order, iapPackageName).then { Results<Entitlement> entitlementResults ->
                    return Promise.each(entitlementResults.items) { Entitlement entitlement ->
                        entitlement.signatureTimestamp = System.currentTimeMillis()
                        return signEntitlement(entitlement, iapItemId).then {
                            response.iapEntitlements << entitlement
                            return Promise.pure(null)
                        }
                    }
                }
            }
            return Promise.pure(response)
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
    Promise<Consumption> iapConsumeEntitlement(Consumption consumption) {
        Item hostItem = null
        return consumptionRepository.get(consumption.trackingGuid).then { Consumption existed ->
            if (existed != null) {
                return Promise.pure(existed)
            }

            return getItemByPackageName(consumption.packageName).then { Item item ->
                hostItem = item
                // todo: validate entitlement ownership & package name
                resourceContainer.entitlementResource.getEntitlement(consumption.entitlementId).then { com.junbo.entitlement.spec.model.Entitlement catalogEntitlement ->
                    return convertEntitlement(catalogEntitlement, consumption.packageName).then { Entitlement entitlement ->
                        if (!entitlement.isConsumable) {
                            throw AppErrors.INSTANCE.entitlementNotConsumable(IdFormatter.encodeId(consumption.entitlementId)).exception()
                        }
                        if (entitlement.useCount < consumption.useCountConsumed) {
                            throw AppErrors.INSTANCE.entitlementNotEnoughUsecount(IdFormatter.encodeId(consumption.entitlementId)).exception()
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
            return signConsumption(consumptionResult, hostItem.itemId)
        }
    }

    private Promise<BillingProfile> innerGetBillingProfile(UserId userId, LocaleId locale) {
        BillingProfile billingProfile = new BillingProfile()
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

    private Promise<Results<Entitlement>> getEntitlementsByOrder(Order order, String packageName) {
        List<Entitlement> result = [] as LinkedList
        Set<String> entitlementIds = [] as HashSet

        // get the entitlement ids
        def promise = resourceContainer.fulfilmentResource.getByOrderId(order.getId()).then { FulfilmentRequest fulfilmentRequest ->
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
                    return convertEntitlement(catalogEntitlement, packageName).then { Entitlement e ->
                        result << e
                        return Promise.pure(null)
                    }
                }
            }.then {
                Results<Entitlement> results = new Results<>()
                results.items = result
                return Promise.pure(results)
            }
        }
    }

    private Promise<Entitlement> convertEntitlement(com.junbo.entitlement.spec.model.Entitlement catalogEntitlement, String packageName) {
        return resourceContainer.itemResource.getItem(catalogEntitlement.itemId).then { Item item ->
            return resourceContainer.itemRevisionResource.getItemRevision(item.currentRevisionId, new ItemRevisionGetOptions()).then { ItemRevision itemRevision ->
                return Promise.pure(convertEntitlement(item, itemRevision, catalogEntitlement, packageName))
            }
        }
    }

    private Entitlement convertEntitlement(Item item, ItemRevision itemRevision,
                                           com.junbo.entitlement.spec.model.Entitlement entitlement, String packageName) {
        assert item.currentRevisionId == itemRevision.id, 'itemRevision not match'
        assert entitlement.itemId == item.id, 'item not match'

        boolean consumable = itemConsumable(itemRevision)
        return new Entitlement(
                userId: new UserId(entitlement.userId),
                entitlementId: new EntitlementId(entitlement.getId()),
                useCount: consumable ? entitlement.useCount : 1,
                sku: itemRevision.sku,
                type: entitlement.type,
                itemType: item.type,
                packageName: packageName,
                isConsumable: consumable,
                itemId: new ItemId(item.getId())
        )
    }

    private Promise<Item> getItemByPackageName(String packageName) {
        def option = new ItemsGetOptions(packageName: packageName)
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
        String jsonText = ObjectMapperProvider.instanceNoSigningSupport().writeValueAsString(entitlement)
        // todo filter out field not needed for iap
        entitlement.setIapEntitlementData(jsonText)
        return resourceContainer.itemCryptoResource.sign(itemId, new ItemCryptoMessage(message: jsonText)).then { ItemCryptoMessage itemCryptoMessage ->
            entitlement.iapSignature = itemCryptoMessage.message
            return Promise.pure(entitlement)
        }
    }

    private Promise<Consumption> signConsumption(Consumption consumption, String itemId) {
        String jsonText = ObjectMapperProvider.instanceNoSigningSupport().writeValueAsString(consumption)
        // todo filter out field not needed for iap
        consumption.iapConsumptionData =jsonText
        return resourceContainer.itemCryptoResource.sign(itemId, new ItemCryptoMessage(message: jsonText)).then { ItemCryptoMessage itemCryptoMessage ->
            consumption.iapSignature = itemCryptoMessage.message
            return Promise.pure(consumption)
        }
    }

    private Promise<Void> getOffersFromItem(Item item, ItemRevision itemRevision, IAPOfferGetRequest iapOfferGetRequest, Map<String, Offer> offers) {
        def offerOption = new OffersGetOptions(
                itemId: item.itemId,
                published: true,
                start : 0,
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

    private Promise<List<Offer>> getInAppOffers(Item hostItem, IAPOfferGetRequest iapOfferGetRequest) {
        def itemOption = new ItemsGetOptions(
                hostItemId: hostItem.itemId,
                type: iapOfferGetRequest.getType(),
                size: PAGE_SIZE,
                start: 0
        )

        Map<String, Offer> offers = new HashMap<>()
        return iteratePageRead {
            return resourceContainer.itemResource.getItems(itemOption).then { Results<Item> itemResults ->
                boolean hasMore = itemResults.items.size() >= itemOption.size
                itemOption.start += itemResults.items.size()
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

        def result = new Offer(
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
}
