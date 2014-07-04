package com.junbo.csr.core.validator

import com.junbo.common.id.CsrUpdateId
import com.junbo.csr.spec.model.CsrUpdate
import com.junbo.csr.spec.option.list.CsrUpdateListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-7-4.
 */
@CompileStatic
public interface CsrUpdateValidator {
    Promise<CsrUpdate> validateForGet(CsrUpdateId csrUpdateId)
    Promise<Void> validateForSearch(CsrUpdateListOptions options)
    Promise<Void> validateForCreate(CsrUpdate csrUpdate)
    Promise<Void> validateForUpdate(CsrUpdateId csrUpdateId, CsrUpdate csrUpdate, CsrUpdate oldcsrUpdate)
}