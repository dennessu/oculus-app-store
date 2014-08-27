package com.junbo.store.rest.purchase

import com.junbo.common.json.ObjectMapperProvider
import com.junbo.crypto.spec.model.CryptoMessage
import com.junbo.langur.core.promise.Promise
import com.junbo.store.rest.utils.ResourceContainer
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

import javax.annotation.Resource

/**
 * The PurchaseTokenProcessorImpl class.
 */
@CompileStatic
@Component('storeTokenProcessor')
class TokenProcessorImpl implements TokenProcessor {

    @Resource(name = 'storeResourceContainer')
    ResourceContainer resourceContainer

    @Override
    Promise<String> toTokenString(Object object) {
        if (object == null) {
            throw new IllegalArgumentException('object is null')
        }

        String result = ObjectMapperProvider.instanceNoIndent().writeValueAsString(object)
        return encode(result)
    }

    @Override
    Promise<Object> toTokenObject(String tokenString, Class cls) {
        if (StringUtils.isEmpty(tokenString)) {
            throw new IllegalArgumentException('token is null')
        }

        return decode(tokenString).then { String decoded ->
            Object obj = ObjectMapperProvider.instanceNoIndent().readValue(decoded, cls)
            return Promise.pure(obj)
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
