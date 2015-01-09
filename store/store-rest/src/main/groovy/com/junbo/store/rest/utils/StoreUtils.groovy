package com.junbo.store.rest.utils

import com.junbo.catalog.spec.enums.ItemType
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.ItemId
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.util.IdFormatter
import com.junbo.crypto.spec.model.ItemCryptoMessage
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.spec.model.StoreApiHeader
import com.junbo.store.spec.model.browse.document.Item
import com.junbo.store.spec.model.iap.IAPParam
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component
import org.springframework.util.Assert
import org.springframework.util.StringUtils

import javax.annotation.Resource

/**
 * The StoreUtils class.
 */
@CompileStatic
@Component('storeUtils')
class StoreUtils {

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    Promise<Item> signIAPItem(UserId userId, Item item, ItemId hostItemId) {
        Assert.isTrue(item.iapDetails != null)
        Map<String, Object> valuesMap = new HashMap<>()
        valuesMap.put('userId', IdFormatter.encodeId(userId))
        valuesMap.put('useCount', item.useCount)
        valuesMap.put('sku', item.iapDetails.sku)
        valuesMap.put('type', item.itemType)
        valuesMap.put('signatureTimestamp', System.currentTimeMillis())
        String jsonText = ObjectMapperProvider.instance().writeValueAsString(valuesMap)

        item.payload = jsonText
        return resourceContainer.itemCryptoResource.sign(hostItemId.value, new ItemCryptoMessage(message: jsonText)).then { ItemCryptoMessage itemCryptoMessage ->
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
