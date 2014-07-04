package com.junbo.csr.rest.resource

import com.junbo.common.id.CsrLogId
import com.junbo.common.model.Results
import com.junbo.csr.spec.model.CsrLog
import com.junbo.csr.spec.option.list.CsrLogListOptions
import com.junbo.csr.spec.option.model.CsrLogGetOptions
import com.junbo.csr.spec.resource.CsrLogResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-7-4.
 */
@CompileStatic
class CsrLogResourceImpl implements CsrLogResource {
    @Override
    Promise<CsrLog> create(CsrLog csrLog) {
        return null
    }

    @Override
    Promise<CsrLog> put(CsrLogId csrLogId, CsrLog csrLog) {
        return null
    }

    @Override
    Promise<CsrLog> patch(CsrLogId csrLogId, CsrLog csrLog) {
        return null
    }

    @Override
    Promise<CsrLog> get(CsrLogId csrLogId, CsrLogGetOptions getOptions) {
        return null
    }

    @Override
    Promise<Results<CsrLog>> list(CsrLogListOptions listOptions) {
        return null
    }

    @Override
    Promise<Void> delete(CsrLogId csrLogId) {
        return null
    }
}
