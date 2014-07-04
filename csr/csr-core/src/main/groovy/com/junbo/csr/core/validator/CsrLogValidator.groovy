package com.junbo.csr.core.validator

import com.junbo.common.enumid.CountryId
import com.junbo.common.id.CsrLogId
import com.junbo.csr.spec.model.CsrLog
import com.junbo.csr.spec.option.list.CsrLogListOptions
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.option.list.CountryListOptions
import com.junbo.langur.core.promise.Promise

/**
 * Created by haomin on 14-7-4.
 */
public interface CsrLogValidator {
    Promise<Void> validateForSearch(CsrLogListOptions options)
    Promise<Void> validateForCreate(CsrLog csrLog)
}