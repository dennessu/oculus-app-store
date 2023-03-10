package com.junbo.store.rest.utils

import com.junbo.common.id.ItemId
import com.junbo.common.id.UserId
import com.junbo.common.util.IdFormatter
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
import org.apache.commons.codec.binary.Base64
import org.springframework.stereotype.Component
import org.springframework.util.Assert

import javax.annotation.Resource
import java.nio.charset.Charset

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
        String jsonText = ObjectMapperProvider.instanceNotStrict().writeValueAsString(valuesMap)

        item.payload = jsonText
        return resourceContainer.itemCryptoResource.sign(hostItemId.value, new
                ItemCryptoMessage(message: Base64.encodeBase64String(jsonText.getBytes(Charset.forName('UTF-8')))), false).then { ItemCryptoMessage itemCryptoMessage ->
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
