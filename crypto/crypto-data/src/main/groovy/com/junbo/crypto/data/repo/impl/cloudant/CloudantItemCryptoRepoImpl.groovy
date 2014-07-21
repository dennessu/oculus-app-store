package com.junbo.crypto.data.repo.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.id.ItemCryptoId
import com.junbo.crypto.data.repo.ItemCryptoRepo
import com.junbo.crypto.spec.model.ItemCryptoRepoData
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.util.CollectionUtils

/**
 * Created by liangfu on 6/30/14.
 */
@CompileStatic
class CloudantItemCryptoRepoImpl extends CloudantClient<ItemCryptoRepoData> implements ItemCryptoRepo {

    @Override
    Promise<ItemCryptoRepoData> getByItemId(String itemId) {

        return super.queryView('by_item_id', itemId, Integer.MAX_VALUE, 0, false).then { List<ItemCryptoRepoData> list ->
            if (CollectionUtils.isEmpty(list)) {
                return Promise.pure(null)
            }

            return Promise.pure(list.get(0))
        }
    }

    @Override
    Promise<ItemCryptoRepoData> get(ItemCryptoId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<ItemCryptoRepoData> create(ItemCryptoRepoData model) {
        return cloudantPost(model)
    }

    @Override
    Promise<ItemCryptoRepoData> update(ItemCryptoRepoData model, ItemCryptoRepoData oldModel) {
        return cloudantPut(model, oldModel)
    }

    @Override
    Promise<Void> delete(ItemCryptoId id) {
        return cloudantDelete(id.toString())
    }
}
