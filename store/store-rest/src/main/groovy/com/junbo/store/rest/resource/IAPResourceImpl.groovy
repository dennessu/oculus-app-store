package com.junbo.store.rest.resource

import com.junbo.catalog.spec.model.item.EntitlementDef
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.catalog.spec.model.item.ItemsGetOptions
import com.junbo.common.id.ItemId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.rest.browse.BrowseService
import com.junbo.store.rest.utils.ApiContextBuilder
import com.junbo.store.rest.utils.IdentityUtils
import com.junbo.store.rest.utils.RequestValidator
import com.junbo.store.rest.utils.StoreUtils
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.browse.LibraryResponse
import com.junbo.store.spec.model.iap.*
import com.junbo.store.spec.resource.IAPResource
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

import javax.annotation.Resource
import javax.ws.rs.ext.Provider

/**
 * The IAPResourceImpl class.
 */
@Provider
@CompileStatic
@Component('defaultIAPResource')
class IAPResourceImpl implements IAPResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(IAPResourceImpl)

    private static final int PAGE_SIZE = 100

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'storeUtils')
    private StoreUtils storeUtils

    @Resource(name = 'storeRequestValidator')
    private RequestValidator requestValidator

    @Resource(name = 'storeIdentityUtils')
    private IdentityUtils identityUtils

    @Resource(name = 'storeFacadeContainer')
    private FacadeContainer facadeContainer

    @Resource(name = 'storeContextBuilder')
    private ApiContextBuilder apiContextBuilder

    @Resource(name = 'storeBrowseService')
    private BrowseService browseService

    @Override
    Promise<LibraryResponse> getLibrary() {
        requestValidator.validateRequiredApiHeaders()
        IAPParam iapParam = storeUtils.buildIAPParam()
        identityUtils.getVerifiedUserFromToken().then {
            return apiContextBuilder.buildApiContext()
        }.then { ApiContext apiContext ->
            facadeContainer.catalogFacade.getCatalogItemByPackageName(iapParam.packageName, iapParam.packageVersion, iapParam.packageSignatureHash).then { Item catalogItem ->
                browseService.getLibrary(true, new ItemId(catalogItem.itemId), apiContext).then { LibraryResponse libraryResponse ->
                    Promise.each(libraryResponse.items) { com.junbo.store.spec.model.browse.document.Item item ->
                        storeUtils.signIAPItem(apiContext.user, item, new ItemId(catalogItem.itemId))
                    }.then {
                        return Promise.pure(libraryResponse)
                    }
                }
            }
        }
    }

    @Override
    Promise<IAPItemsResponse> getItems() {
        requestValidator.validateRequiredApiHeaders()
        IAPParam iapParam = storeUtils.buildIAPParam()
        identityUtils.getVerifiedUserFromToken().then {
            return apiContextBuilder.buildApiContext()
        }.then { ApiContext apiContext ->
            facadeContainer.catalogFacade.getCatalogItemByPackageName(iapParam.packageName, iapParam.packageVersion, iapParam.packageSignatureHash).then { Item catalogItem ->
                getInAppItems(new ItemId(catalogItem.getId()), null, apiContext)
            }
        }
    }

    @Override
    Promise<IAPConsumeItemResponse> consumeItem(IAPConsumeItemRequest request) {
        requestValidator.validateRequiredApiHeaders().validateIAPConsumeItemRequest(request)
        IAPParam iapParam = storeUtils.buildIAPParam()

        identityUtils.getVerifiedUserFromToken().then {
            return apiContextBuilder.buildApiContext()
        }.then { ApiContext apiContext ->
            facadeContainer.catalogFacade.getCatalogItemByPackageName(iapParam.packageName, iapParam.packageVersion, iapParam.packageSignatureHash).then { Item catalogItem ->
                getInAppItems(new ItemId(catalogItem.getId()), null, apiContext)
            }
        }

        /*
        *  ApiContext apiContext
        Item hostItem = null
        return identityUtils.getVerifiedUserFromToken().then {
            apiContextBuilder.buildApiContext().then { ApiContext e ->
                apiContext = e
                return Promise.pure()
            }
        }.then {
            return consumptionRepository.get(iapEntitlementConsumeRequest.trackingGuid).then { Consumption existed ->
                if (existed != null) {
                    return Promise.pure(existed)
                }

                Consumption consumption = new Consumption(
                        user: apiContext.user,
                        entitlement : iapEntitlementConsumeRequest.entitlement,
                        useCountConsumed : iapEntitlementConsumeRequest.useCountConsumed,
                        trackingGuid: iapEntitlementConsumeRequest.trackingGuid,
                        packageName: iapEntitlementConsumeRequest.packageName
                )

                return getItemByPackageName(iapEntitlementConsumeRequest.packageName).then { Item item ->
                    hostItem = item
                    // todo: validate entitlement ownership & package name
                    facadeContainer.entitlementFacade.getEntitlementsByIds(apiContext.user, Collections.singleton(iapEntitlementConsumeRequest.entitlement)).then { List<Entitlement> entitlements ->
                        if (CollectionUtils.isEmpty(entitlements)) {
                            throw AppErrors.INSTANCE.entitlementNotConsumable(iapEntitlementConsumeRequest.entitlement.value).exception()
                        }
                    }
                    resourceContainer.entitlementResource.getEntitlement(consumption.entitlement).then { com.junbo.entitlement.spec.model.Entitlement catalogEntitlement ->
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
        * */
    }

    private Promise<IAPItemsResponse> getInAppItems(ItemId hostItemId, Set<String> skus, ApiContext apiContext) {
        IAPItemsResponse response = new IAPItemsResponse(items : [] as List)
        ItemsGetOptions itemOption = new ItemsGetOptions(
                hostItemId: hostItemId?.value,
                size: PAGE_SIZE,
        )

        return CommonUtils.loop {
            return resourceContainer.itemResource.getItems(itemOption).then { Results<Item> itemResults ->
                itemOption.cursor = CommonUtils.getQueryParam(itemResults?.next?.href, 'cursor')
                return Promise.each(itemResults.items) { Item catalogItem ->
                    browseService.getItem(new ItemId(catalogItem.getId()), false, apiContext).then { com.junbo.store.spec.model.browse.document.Item item ->
                        if (item == null) {
                            return Promise.pure()
                        }

                        String itemSku = item.iapDetails?.sku
                        if (skus != null && (itemSku == null && !skus.contains(itemSku))) {
                            return Promise.pure()
                        }

                        response.items << item
                        return Promise.pure()
                    }
                }.then {
                    if (CollectionUtils.isEmpty(itemResults?.items) || StringUtils.isBlank(itemOption.cursor)) {
                        return Promise.pure(Promise.BREAK)
                    }
                    return Promise.pure()
                }
            }
        }.then {
            return Promise.pure(response)
        }
    }

    private Promise<Consumption> signConsumption(Consumption consumption, String packageName) {
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

    private static boolean itemConsumable(ItemRevision itemRevision) {
        if (itemRevision.entitlementDefs != null) {
            return itemRevision.entitlementDefs.any { EntitlementDef entitlementDef ->
                return entitlementDef.consumable
            }
        }
        return false
    }
}
