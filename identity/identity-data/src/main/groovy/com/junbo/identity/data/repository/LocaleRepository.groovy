package com.junbo.identity.data.repository

import com.junbo.common.enumid.LocaleId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Locale
import com.junbo.identity.spec.v1.option.list.LocaleListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository

/**
 * Created by minhao on 4/24/14.
 */
public interface LocaleRepository extends BaseRepository<Locale, LocaleId> {
    @ReadMethod
    Promise<Results<Locale>> searchAll(Integer limit, Integer offset)
}