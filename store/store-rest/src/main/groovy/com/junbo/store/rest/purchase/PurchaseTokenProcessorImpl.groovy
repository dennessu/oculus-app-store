package com.junbo.store.rest.purchase

import com.junbo.common.json.ObjectMapperProvider
import com.junbo.crypto.spec.model.CryptoMessage
import com.junbo.langur.core.promise.Promise
import com.junbo.store.rest.utils.ResourceContainer
import com.junbo.store.spec.model.purchase.PurchaseState
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * The PurchaseTokenProcessorImpl class.
 */
@CompileStatic
@Component('storePurchaseTokenProcessor')
class PurchaseTokenProcessorImpl implements PurchaseTokenProcessor {

    @Resource(name = 'storeResourceContainer')
    ResourceContainer resourceContainer

    @Override
    Promise<String> toPurchaseToken(PurchaseState state) {
        if (state == null) {
            throw new IllegalArgumentException('state is null')
        }

        String result = ObjectMapperProvider.instanceNoIndent().writeValueAsString(state)
        return encode(result)
    }

    @Override
    Promise<PurchaseState> toPurchaseState(String purchaseToken) {
        if (purchaseToken == null) {
            throw new IllegalArgumentException('purchaseToken is null')
        }

        return decode(purchaseToken).then { String decoded ->
            PurchaseState purchaseState = ObjectMapperProvider.instanceNoIndent().readValue(decoded, PurchaseState)
            return Promise.pure(purchaseState)
        }
    }

    private Promise<String> decode(String val) {
        resourceContainer.cryptoResource.decrypt(new CryptoMessage(value: val)).then { CryptoMessage result ->
            return Promise.pure(result.value)
        }
    }

    private Promise<String> encode(String val) {
        resourceContainer.cryptoResource.encrypt(new CryptoMessage(value: val)).then { CryptoMessage result ->
            return Promise.pure(result.value)
        }
    }
}
