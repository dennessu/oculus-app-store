package com.junbo.store.rest.utils

import com.junbo.common.json.ObjectMapperProvider
import com.junbo.crypto.spec.model.ItemCryptoMessage
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.spec.model.Entitlement
import com.junbo.store.spec.model.StoreApiHeader
import com.junbo.store.spec.model.browse.document.Item
import com.junbo.store.spec.model.iap.HostItemInfo
import com.junbo.store.spec.model.iap.IAPParam
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component
import org.springframework.util.Assert

import javax.annotation.Resource
/**
 * The StoreUtils class.
 */
@CompileStatic
@Component('storeUtils')
class StoreUtils {

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    Promise<Item> signIAPPurchase(Entitlement entitlement, HostItemInfo hostItemInfo) {
        Item item = entitlement.itemDetails
        Assert.notNull(item)
        Assert.notNull(item.iapDetails)

        Map<String, Object> valuesMap = new HashMap<>()
        valuesMap.put('packageName', hostItemInfo.packageName)
        valuesMap.put('sku', item.iapDetails.sku)
        valuesMap.put('type', item.itemType)
        if (entitlement.createdTime != null) {
            valuesMap.put('purchaseTime', entitlement.createdTime.time)
        }
        valuesMap.put('developerPayload', entitlement.developerPayload)
        valuesMap.put('iapPurchaseToken', entitlement.self.value)
        String jsonText = ObjectMapperProvider.instance().writeValueAsString(valuesMap)
        item.payload = jsonText

        return resourceContainer.itemCryptoResource.sign(hostItemInfo.hostItemId.value, new ItemCryptoMessage(message: jsonText)).then { ItemCryptoMessage itemCryptoMessage ->
            item.signature = itemCryptoMessage.message
            return Promise.pure(item)
        }
    }

    IAPParam buildIAPParam()  {
        IAPParam iapParam = new IAPParam()
        iapParam.packageName = CommonUtils.getHeaderString(StoreApiHeader.PACKAGE_NAME.value, true)
        iapParam.packageSignatureHash = CommonUtils.getHeaderString(StoreApiHeader.PACKAGE_SIGNATURE_HASH.value, true)
        iapParam.packageVersion = CommonUtils.getHeaderInteger(StoreApiHeader.PACKAGE_VERSION.value, true)
        return iapParam
    }
}
