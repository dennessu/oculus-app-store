package com.junbo.identity.data.repository

import com.junbo.langur.core.promise.Promise

/**
 * Created by haomin on 14-4-18.
 */
interface IdentityBaseRepository<T, K> {
    Promise<T> create(T model)
    Promise<T> update(T model)
    Promise<T> get(K id)
    Promise<Void> delete(K id)
    //Promise<Void> markDeleted(K id)
}
