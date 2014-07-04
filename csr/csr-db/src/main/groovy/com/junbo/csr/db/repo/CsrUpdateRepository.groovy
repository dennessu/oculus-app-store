package com.junbo.csr.db.repo

import com.junbo.common.id.CsrUpdateId
import com.junbo.common.model.Results
import com.junbo.csr.spec.model.CsrUpdate
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-7-3.
 */
@CompileStatic
interface CsrUpdateRepository extends BaseRepository<CsrUpdate, CsrUpdateId> {
    @ReadMethod
    Promise<Results<CsrUpdate>> getCsrUpdateByStatus(Boolean isActive, Integer limit, Integer offset)
}
