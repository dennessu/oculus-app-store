package com.junbo.crypto.data.repo

import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.core.annotations.ReadMethod
import com.junbo.sharding.core.annotations.WriteMethod
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/12/14.
 */
@CompileStatic
interface CryptoBaseRepository<T, K> {
    @WriteMethod
    Promise<T> create(T model)
    @WriteMethod
    Promise<T> update(T model)
    @ReadMethod
    Promise<T> get(K id)
    @WriteMethod
    Promise<Void> delete(K id)
}
