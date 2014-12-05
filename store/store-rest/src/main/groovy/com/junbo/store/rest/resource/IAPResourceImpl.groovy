package com.junbo.store.rest.resource

import com.junbo.catalog.spec.enums.ItemType
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemsGetOptions
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.EntitlementId
import com.junbo.common.id.ItemId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.error.AppErrorUtils
import com.junbo.store.clientproxy.error.ErrorCodes
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.db.repo.ConsumptionRepository
import com.junbo.store.rest.browse.BrowseService
import com.junbo.store.rest.utils.ApiContextBuilder
import com.junbo.store.rest.utils.IdentityUtils
import com.junbo.store.rest.utils.RequestValidator
import com.junbo.store.rest.utils.StoreUtils
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.browse.DetailsResponse
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

    @Resource(name = 'cloudantConsumptionRepository')
    private ConsumptionRepository consumptionRepository

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

    @Resource(name = 'storeAppErrorUtils')
    private AppErrorUtils appErrorUtils

    @Override
    Promise<LibraryResponse> getLibrary() {
        requestValidator.validateRequiredApiHeaders()
        IAPParam iapParam = storeUtils.buildIAPParam()
        identityUtils.getVerifiedUserFromToken().then {
            return apiContextBuilder.buildApiContext()
        }.then { ApiContext apiContext ->
            facadeContainer.catalogFacade.getCatalogItemByPackageName(iapParam.packageName, iapParam.packageVersion, iapParam.packageSignatureHash).then { Item catalogItem ->
                browseService.getLibrary(true, new HostItemInfo(hostItemId: new ItemId(catalogItem.itemId), packageName: iapParam.packageName), apiContext)
            }
        }
    }

    @Override
    Promise<IAPItemsResponse> getItems(IAPGetItemsParam iapGetItemsParam) {
        requestValidator.validateRequiredApiHeaders()
        IAPParam iapParam = storeUtils.buildIAPParam()
        if (CollectionUtils.isEmpty(iapGetItemsParam?.skus)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('sku').exception()
        }

        identityUtils.getVerifiedUserFromToken().then {
            return apiContextBuilder.buildApiContext()
        }.then { ApiContext apiContext ->
            facadeContainer.catalogFacade.getCatalogItemByPackageName(iapParam.packageName, iapParam.packageVersion, iapParam.packageSignatureHash).then { Item catalogItem ->
                return browseService.getIAPItems(new HostItemInfo(hostItemId: new ItemId(catalogItem.getId()), packageName: iapParam.packageName),
                        iapGetItemsParam.skus, apiContext).then { List<com.junbo.store.spec.model.browse.document.Item> itemList ->
                    return Promise.pure(new IAPItemsResponse(items: itemList))
                }
            }
        }
    }

    @Override
    Promise<IAPConsumeItemResponse> consumePurchase(IAPConsumeItemRequest request) {
        requestValidator.validateRequiredApiHeaders().validateIAPConsumeItemRequest(request)
        IAPParam iapParam = storeUtils.buildIAPParam()
        Item hostItem
        ApiContext apiContext

        com.junbo.store.spec.model.Entitlement entitlement
        identityUtils.getVerifiedUserFromToken().then {
            return apiContextBuilder.buildApiContext().then { ApiContext ac ->
                apiContext = ac
                facadeContainer.catalogFacade.getCatalogItemByPackageName(iapParam.packageName, iapParam.packageVersion, iapParam.packageSignatureHash).then { Item catalogItem ->
                    hostItem = catalogItem
                    return Promise.pure()
                }
            }.then {
                facadeContainer.entitlementFacade.getEntitlementsByIds(Collections.singleton(new EntitlementId(request.iapPurchaseToken)), false, apiContext).recover { Throwable ex ->
                    if (appErrorUtils.isAppError(ex, ErrorCodes.Entitlement.EntitlementNotFound)) {
                        throw AppErrors.INSTANCE.invalidIAPPurchaseToken().exception()
                    }
                    throw ex
                }.then { List<com.junbo.store.spec.model.Entitlement> entitlements ->
                    if (CollectionUtils.isEmpty(entitlements)) {
                        throw AppErrors.INSTANCE.invalidIAPPurchaseToken().exception()
                    }
                    entitlement = entitlements[0]
                    return Promise.pure()
                }
            }.then {
                validateConsumeEntitlement(entitlement, new ItemId(hostItem.getId()), apiContext)
            }.then {
                facadeContainer.entitlementFacade.consumeEntitlement(entitlement.self).then {
                    return Promise.pure(new IAPConsumeItemResponse(iapPurchaseToken: request.iapPurchaseToken))
                }
            }
        }
    }

    Promise validateConsumeEntitlement(com.junbo.store.spec.model.Entitlement entitlement, ItemId hostItemId, ApiContext apiContext) {
        if (entitlement.user != apiContext.user) {
            throw AppErrors.INSTANCE.invalidIAPPurchaseToken().exception()
        }
        if (entitlement.itemType != ItemType.CONSUMABLE_UNLOCK.name()) {
            throw AppErrors.INSTANCE.iapPurchaseNotConsumable().exception()
        }
        facadeContainer.catalogFacade.checkHostItem(entitlement.item, hostItemId).then { Boolean hosted ->
            if (!hosted) {
                throw AppErrors.INSTANCE.invalidIAPPurchaseToken().exception()
            }
            return Promise.pure()
        }
    }
}
