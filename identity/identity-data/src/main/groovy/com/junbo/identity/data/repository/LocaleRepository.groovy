package com.junbo.identity.data.repository

import com.junbo.common.enumid.LocaleId
import com.junbo.identity.spec.v1.model.Locale
import com.junbo.identity.spec.v1.option.list.LocaleListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.core.annotations.ReadMethod

/**
 * Created by minhao on 4/24/14.
 */
public interface LocaleRepository extends IdentityBaseRepository<Locale, LocaleId> {
    @ReadMethod
    Promise<List<Locale>> search(LocaleListOptions options)
}