package com.junbo.store.clientproxy.entitlement
import com.junbo.catalog.spec.enums.EntitlementType
import com.junbo.common.id.EntitlementId
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.utils.ItemBuilder
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.Entitlement
import com.junbo.store.spec.model.browse.Images
import com.junbo.store.spec.model.browse.document.Item
import com.junbo.store.spec.model.external.sewer.entitlement.SewerEntitlement
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.Resource
/**
 * The EntitlementFacadeImpl class.
 */
@CompileStatic
@Component('storeEntitlementFacade')
class EntitlementFacadeImpl implements EntitlementFacade {

    private static final String ENTITLEMENT_GET_EXPAND = '(item(developer,currentRevision,categories,genres,rating))'

    @Resource(name = 'storeFacadeContainer')
    private FacadeContainer facadeContainer

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'storeItemBuilder')
    private ItemBuilder itemBuilder
    @Override
    Promise<Entitlement> getDigitalEntitlement(EntitlementId entitlementId, boolean isIAP, ApiContext apiContext) {
        String type = isIAP ? EntitlementType.ALLOW_IN_APP.name() : EntitlementType.DOWNLOAD.name()
        resourceContainer.sewerEntitlementResource.getEntitlement(entitlementId, ENTITLEMENT_GET_EXPAND, apiContext.locale.getId().value).then { SewerEntitlement sewerEntitlement ->
            if (!sewerEntitlement.type.equals(type)) {
                return Promise.pure(null)
            }
            Item item = itemBuilder.buildItem(sewerEntitlement, Images.BuildType.Item_Details, apiContext)
            item.ownedByCurrentUser = (new UserId(sewerEntitlement.userId) == apiContext.user)
            Entitlement entitlement = convertDigitalEntitlement(sewerEntitlement, item)
            return Promise.pure(entitlement)
        }
    }

    private Entitlement convertDigitalEntitlement(SewerEntitlement entitlement, Item item) {
        Entitlement result = new Entitlement(
                self: new EntitlementId(entitlement.getId()),
                user: new UserId(entitlement.userId),
                entitlementType: entitlement.type,
                itemType: item.itemType,
                item: item.self,
                itemDetails: item
        )
        return result
    }
}
