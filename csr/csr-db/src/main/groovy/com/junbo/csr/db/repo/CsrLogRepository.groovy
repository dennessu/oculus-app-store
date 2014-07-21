package com.junbo.csr.db.repo

import com.junbo.common.id.CsrLogId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.csr.spec.model.CsrLog
import com.junbo.csr.spec.option.list.CsrLogListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-7-4.
 */
@CompileStatic
interface CsrLogRepository extends BaseRepository<CsrLog, CsrLogId> {
    @ReadMethod
    Promise<Results<CsrLog>> searchByListOptions(CsrLogListOptions listOptions)
}