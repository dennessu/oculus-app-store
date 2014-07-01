package com.junbo.crypto.core.validator.impl

import com.junbo.crypto.core.validator.ItemCryptoValidator
import com.junbo.crypto.data.repo.ItemCryptoRepo
import com.junbo.crypto.spec.error.AppErrors
import com.junbo.crypto.spec.model.ItemCryptoMessage
import com.junbo.crypto.spec.model.ItemCryptoRepoData
import com.junbo.crypto.spec.model.ItemCryptoVerify
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Created by liangfu on 6/30/14.
 */
@CompileStatic
class ItemCryptoValidatorImpl implements ItemCryptoValidator {

    private ItemCryptoRepo itemCryptoRepo

    @Required
    void setItemCryptoRepo(ItemCryptoRepo itemCryptoRepo) {
        this.itemCryptoRepo = itemCryptoRepo
    }

    @Override
    void validateForSign(String itemId, ItemCryptoMessage message) {
        if (StringUtils.isEmpty(itemId)) {
            throw new IllegalArgumentException('itemId is null')
        }
        if (message == null) {
            throw new IllegalArgumentException('message is null')
        }

        if (StringUtils.isEmpty(message.message)) {
            throw new IllegalArgumentException('message to sign is empty or null')
        }
    }

    @Override
    void validateForGetPublicKey(String itemId) {
        if (StringUtils.isEmpty(itemId)) {
            throw new IllegalArgumentException('itemId is null')
        }
    }

    @Override
    Promise<ItemCryptoRepoData> validateForRefresh(String itemId) {
        if (StringUtils.isEmpty(itemId)) {
            throw new IllegalArgumentException('itemId is null')
        }
        return itemCryptoRepo.getByItemId(itemId).then { ItemCryptoRepoData data ->
            if (data == null) {
                throw AppErrors.INSTANCE.itemKeyNotFound(itemId).exception()
            }

            return Promise.pure(data)
        }
    }

    @Override
    void validateForVerify(String itemId, ItemCryptoVerify message) {
        if (StringUtils.isEmpty(itemId)) {
            throw new IllegalArgumentException('itemId is null')
        }
        if (message == null) {
            throw new IllegalArgumentException('message is null')
        }

        if (StringUtils.isEmpty(message.rawMessage)) {
            throw new IllegalArgumentException('message to verify is empty or null')
        }

        if (StringUtils.isEmpty(message.messageSigned)) {
            throw new IllegalArgumentException('messageSigned is empty or null')
        }
    }
}
