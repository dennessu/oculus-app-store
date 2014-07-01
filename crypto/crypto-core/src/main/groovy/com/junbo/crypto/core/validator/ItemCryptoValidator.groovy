package com.junbo.crypto.core.validator

import com.junbo.crypto.spec.model.ItemCryptoMessage
import com.junbo.crypto.spec.model.ItemCryptoRepoData
import com.junbo.crypto.spec.model.ItemCryptoVerify
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 6/30/14.
 */
@CompileStatic
public interface ItemCryptoValidator {
    void validateForSign(String itemId, ItemCryptoMessage message)
    Promise<ItemCryptoRepoData> validateForRefresh(String itemId)
    void validateForGetPublicKey(String itemId)
    void validateForVerify(String itemId, ItemCryptoVerify message)
}
