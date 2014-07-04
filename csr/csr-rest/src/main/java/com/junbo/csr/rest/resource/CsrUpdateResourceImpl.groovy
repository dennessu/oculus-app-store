package com.junbo.csr.rest.resource

import com.junbo.common.id.CsrUpdateId
import com.junbo.common.model.Results
import com.junbo.csr.spec.model.CsrUpdate
import com.junbo.csr.spec.option.list.CsrUpdateListOptions
import com.junbo.csr.spec.option.model.CsrUpdateGetOptions
import com.junbo.csr.spec.resource.CsrUpdateResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-7-4.
 */
@CompileStatic
class CsrUpdateResourceImpl implements CsrUpdateResource {
    @Override
    Promise<CsrUpdate> create(CsrUpdate csrUpdate) {
        return null
    }

    @Override
    Promise<CsrUpdate> put(CsrUpdateId csrUpdateId, CsrUpdate csrUpdate) {
        return null
    }

    @Override
    Promise<CsrUpdate> patch(CsrUpdateId csrUpdateId, CsrUpdate csrUpdate) {
        return null
    }

    @Override
    Promise<CsrUpdate> get(CsrUpdateId csrUpdateId, CsrUpdateGetOptions getOptions) {
        return null
    }

    @Override
    Promise<Results<CsrUpdate>> list(CsrUpdateListOptions listOptions) {
        return null
    }

    @Override
    Promise<Void> delete(CsrUpdateId csrUpdateId) {
        return null
    }
}
