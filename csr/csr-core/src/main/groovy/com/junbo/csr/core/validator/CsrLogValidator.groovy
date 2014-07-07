package com.junbo.csr.core.validator

import com.junbo.common.id.CsrLogId
import com.junbo.csr.spec.model.CsrLog
import com.junbo.csr.spec.option.list.CsrLogListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-7-4.
 */
@CompileStatic
public interface CsrLogValidator {
    Promise<CsrLog> validateForGet(CsrLogId csrLogId)
    Promise<Void> validateForSearch(CsrLogListOptions options)
    Promise<Void> validateForCreate(CsrLog csrLog)
}