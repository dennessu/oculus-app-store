package com.junbo.identity.data.repository

import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.core.annotations.ReadMethod
import com.junbo.sharding.core.annotations.WriteMethod

/**
 * Created by haomin on 14-4-18.
 */
interface IdentityBaseRepository<T, K> {
    @WriteMethod
    Promise<T> create(T model)
    @WriteMethod
    Promise<T> update(T model)
    @ReadMethod
    Promise<T> get(K id)
    @WriteMethod
    Promise<Void> delete(K id)
    //Promise<Void> markDeleted(K id)
}
