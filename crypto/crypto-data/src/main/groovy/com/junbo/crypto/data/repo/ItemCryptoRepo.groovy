package com.junbo.crypto.data.repo

import com.junbo.common.id.ItemCryptoId
import com.junbo.crypto.spec.model.ItemCryptoRepoData
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 6/30/14.
 */
@CompileStatic
public interface ItemCryptoRepo extends BaseRepository<ItemCryptoRepoData, ItemCryptoId>{

    @ReadMethod
    Promise<ItemCryptoRepoData> getByItemId(String itemId)
}
