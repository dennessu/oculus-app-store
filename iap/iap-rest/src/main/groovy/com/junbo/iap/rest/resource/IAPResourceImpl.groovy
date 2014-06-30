package com.junbo.iap.rest.resource

import com.junbo.catalog.spec.model.item.EntitlementDef
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.catalog.spec.model.item.ItemsGetOptions
import com.junbo.catalog.spec.model.item.ItemRevisionGetOptions
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.catalog.spec.model.offer.OfferRevisionGetOptions
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.EntitlementId
import com.junbo.common.id.ItemId
import com.junbo.common.id.OfferId
import com.junbo.common.id.PIType
import com.junbo.common.id.PaymentInstrumentId
import com.junbo.common.id.UserId
import com.junbo.common.id.util.IdUtil
import com.junbo.common.model.Link
import com.junbo.common.model.Results
import com.junbo.common.util.IdFormatter
import com.junbo.entitlement.spec.model.EntitlementSearchParam
import com.junbo.entitlement.spec.model.PageMetadata
import com.junbo.fulfilment.spec.model.FulfilmentAction
import com.junbo.fulfilment.spec.model.FulfilmentItem
import com.junbo.fulfilment.spec.model.FulfilmentRequest
import com.junbo.iap.db.repo.ConsumptionRepository
import com.junbo.iap.rest.utils.IAPValidator
import com.junbo.iap.rest.utils.ResourceContainer
import com.junbo.iap.spec.error.AppErrors
import com.junbo.iap.spec.model.*
import com.junbo.iap.spec.resource.IAPResource
import com.junbo.langur.core.client.PathParamTranscoder
import com.junbo.langur.core.promise.Promise
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.PaymentInfo
import com.junbo.payment.spec.model.PageMetaData
import com.junbo.payment.spec.model.PaymentInstrument
import com.junbo.payment.spec.model.PaymentInstrumentSearchParam
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.Resource
import javax.ws.rs.BeanParam
import javax.ws.rs.QueryParam
import javax.ws.rs.core.UriBuilder
import javax.ws.rs.ext.Provider

/**
 * The wrapper api for IAP (In-App Purchase).
 */
@Provider
@Scope('prototype')
@CompileStatic
@Component('defaultIAPResource')
class IAPResourceImpl implements IAPResource {

    public static final String ACTION_TYPE_GRANT_ENTITLEMENT = "GRANT_ENTITLEMENT";

    public static final String ACTION_STATUS_SUCCESS = "SUCCEED";

    private static final Logger LOGGER = LoggerFactory.getLogger(IAPResourceImpl)

    private static final int PAGE_SIZE = 100

    @Resource(name = 'cloudantConsumptionRepository')
    private ConsumptionRepository consumptionRepository

    @Resource(name = 'iapResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'pathParamTranscoder')
    private PathParamTranscoder pathParamTranscoder

    @Resource(name = 'iapValidator')
    private IAPValidator iapValidator

    @Override
    Promise<Results<Offer>> getOffers(@QueryParam("packageName") String packageName, @QueryParam("type") String type) {
        // todo validation

        return getItemByPackageName(packageName).then { Item hostItem ->
            return getInAppOffers(hostItem, type).then { List<Offer> offers ->
                return Promise.pure(new Results<Offer>(items: offers))
            }
        }

    }

    @Override
    Promise<Results<Entitlement>> getEntitlements(@QueryParam("packageName") String packageName,
                                                  @QueryParam("userId") UserId userId,
                                                  @QueryParam("type") String type,
                                                  @BeanParam PageParam pageParam) {
        // todo validation

        pageParam.start = pageParam.start == null ? 0 : pageParam.start
        pageParam.count = pageParam.count == null ? PAGE_SIZE : pageParam.count

        List<Entitlement> entitlements = [] as LinkedList<Entitlement>
        return getItemByPackageName(packageName).then { Item hostItem ->
            iteratePageRead {
                resourceContainer.entitlementResource.searchEntitlements(
                        new EntitlementSearchParam(
                                // todo: how to set the type ?
                                userId: userId,
                                // isActive: true,
                                hostItemId: new ItemId(hostItem.id),
                                itemIds: new HashSet<ItemId>()
                        ),
                        new PageMetadata(
                                start: pageParam.start,
                                count: pageParam.count
                        )
                ).then { Results<com.junbo.entitlement.spec.model.Entitlement> results -> // get the IAP entitlements from entitlement & catalog component
                    // start page, offset, last index
                    Promise.each(results.items) { com.junbo.entitlement.spec.model.Entitlement catalogEntitlement ->
                        return convertEntitlement(catalogEntitlement, packageName).then { Entitlement entitlement ->
                            if (entitlement.useCount > 0 && catalogEntitlement.isActive) {
                                entitlement.setSignatureTimestamp(System.currentTimeMillis())
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
                results.next = new Link(href: buildEntitlementNextUrl(packageName, userId, type, pageParam))
                results.items = entitlements.subList(0, pageParam.count)
            } else {
                results.items = entitlements
            }
            return Promise.pure(results)
        }
    }

    @Override
    Promise<Results<Entitlement>> postPurchase(Purchase purchase) {
         // todo some basic validate
        def promise = getItemByPackageName(purchase.packageName)

        PaymentInstrument piUsed = null
        UserId userId = new UserId(IdFormatter.decodeId(UserId, purchase.userId))

        // validate offer and select pi
        promise = promise.then { Item hostItem ->
            return iapValidator.validateInAppOffer(new OfferId(purchase.offerId), hostItem).then {
                return selectPaymentInstrument(userId).then { PaymentInstrument paymentInstrument ->
                    piUsed = paymentInstrument
                    return Promise.pure(null)
                }
            }
        }

        // post order with tentative = false & get entitlements
        return promise.then {
            Order order = new Order(
                    user: userId,
                    payments: [new PaymentInfo(paymentInstrument : new PaymentInstrumentId(piUsed.getId()))],
                    country: new CountryId(purchase.country),
                    currency: new CurrencyId(purchase.currency),
                    locale: new LocaleId(purchase.locale),
                    tentative: true,
                    orderItems: [new OrderItem(offer: new OfferId(purchase.offerId), quantity: 1)]
            )
            return resourceContainer.orderResource.createOrder(order).then { Order tentativeOrder ->
                // todo verify the order status
                tentativeOrder.tentative = false
                return resourceContainer.orderResource.createOrder(tentativeOrder).then { Order settledOrder ->
                    return getEntitlementsByOrder(settledOrder, purchase.packageName)
                }
            }
        }
    }

    @Override
    Promise<Consumption> postConsumption(Consumption consumption) {
        consumptionRepository.get(consumption.trackingGuid).then { Consumption existed ->
            if (existed != null) {
                return Promise.pure(existed)
            }

            // todo: validate entitlement ownership & package name
            resourceContainer.entitlementResource.getEntitlement(new EntitlementId(consumption.entitlementId)).then { com.junbo.entitlement.spec.model.Entitlement catalogEntitlement ->
                return convertEntitlement(catalogEntitlement, consumption.packageName).then { Entitlement entitlement ->
                    if (!entitlement.isConsumable) {
                        throw AppErrors.INSTANCE.entitlementNotConsumable(consumption.entitlementId).exception()
                    }
                    if (entitlement.useCount < consumption.useCountConsumed) {
                        throw AppErrors.INSTANCE.entitlementNotEnoughUsecount(consumption.entitlementId).exception()
                    }

                    consumption.packageName = entitlement.packageName
                    consumption.sku = entitlement.sku
                    consumption.type = entitlement.type
                    consumption.signatureTimestamp = System.currentTimeMillis()

                    // consume via update the use count
                    catalogEntitlement.useCount -= consumption.useCountConsumed
                    return resourceContainer.entitlementResource.updateEntitlement(new EntitlementId(catalogEntitlement.getId()), catalogEntitlement).then {
                        return consumptionRepository.create(consumption)
                    }
                }
            }
        }
    }

    Promise<Item> getItemByPackageName(String packageName) {
        // todo : use search function on catalog
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


    Promise<List<Offer>> getInAppOffers(Item hostItem, String type) {
        def itemOption = new ItemsGetOptions(
                hostItemId: hostItem.id,
                type: type,
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
                        return getOffersFromItem(item, itemRevision, offers).then {
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

    private Promise<Void> iteratePageRead(Closure<Promise<Boolean>> pageReadFunc) {
        pageReadFunc.call().then { Boolean moreItem ->
            if (!moreItem) {
                return Promise.pure(null)
            }
            return iteratePageRead(pageReadFunc)
        }
    }



    private Promise<Void> getOffersFromItem(Item item, ItemRevision itemRevision, Map<String, Offer> offers) {
        def offerOption = new OffersGetOptions(
                itemId: item.id,
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
                    offers.put(cOffer.id, convertOffer(cOffer, offerRevision, item, itemRevision))
                    return Promise.pure(null)
                }
            }

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
                        e.setSignatureTimestamp(System.currentTimeMillis())
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
                userId: pathParamTranscoder.encode(new UserId(entitlement.userId)),
                entitlementId: entitlement.getId(),
                useCount: consumable ? entitlement.useCount : 1,
                sku: itemRevision.sku,
                type: item.type,
                packageName: packageName,
                isConsumable: consumable
        )
    }

    private static Offer convertOffer(com.junbo.catalog.spec.model.offer.Offer offer, OfferRevision offerRevision,
                                        Item item, ItemRevision itemRevision) {
        def result = new Offer(
                offerId: offer.id,
                price: offerRevision.price,
                type: item.type,
                sku: itemRevision.sku,
                offerLocales: offerRevision.locales,
                isConsumable: itemConsumable(itemRevision)
        )

        return result
    }

    private static boolean itemConsumable(ItemRevision itemRevision) {
        if (itemRevision.entitlementDefs != null) {
            return itemRevision.entitlementDefs.any { EntitlementDef entitlementDef ->
                return entitlementDef.consumable
            }
        }
        return false
    }

    private String buildEntitlementNextUrl(String packageName, UserId userId, String type, PageParam pageParam) {
        UriBuilder builder = UriBuilder.fromPath(IdUtil.getResourcePathPrefix()).path("iap").path("entitlements");
        builder.queryParam("packageName", packageName);
        builder.queryParam("userId", pathParamTranscoder.encode(userId));
        builder.queryParam("type", type);
        builder.queryParam("count", pageParam.count);
        builder.queryParam("start", pageParam.start);

        return builder.toTemplate();
    }

    private Promise<PaymentInstrument> selectPaymentInstrument(UserId userId) {
        resourceContainer.paymentInstrumentResource.searchPaymentInstrument(
                new PaymentInstrumentSearchParam(
                        userId: userId,
                        type: PIType.STOREDVALUE.name()
                ),
                new PageMetaData(
                        count: 1,
                        start: 0
                )).then { Results<PaymentInstrument> paymentInstrumentResults ->
            return Promise.pure(paymentInstrumentResults.items.isEmpty() ? null : paymentInstrumentResults.items[0])
        }
    }
}
