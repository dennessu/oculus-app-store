package com.junbo.order.mock

import com.junbo.common.id.CsrLogId
import com.junbo.common.model.Results
import com.junbo.csr.spec.model.CsrLog
import com.junbo.csr.spec.option.list.CsrLogListOptions
import com.junbo.csr.spec.option.model.CsrLogGetOptions
import com.junbo.csr.spec.resource.CsrLogResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.ws.rs.BeanParam

/**
 * Created by haomin on 14-7-24.
 */
@CompileStatic
@Component('mockCsrLogResource')
class MockCsrLogResource extends BaseMock implements CsrLogResource {
    @Override
    Promise<CsrLog> create(CsrLog csrLog) {
        return Promise.pure(new CsrLog())
    }

    @Override
    Promise<CsrLog> get(CsrLogId csrLogId, CsrLogGetOptions getOptions) {
        return null
    }

    @Override
    Promise<Results<CsrLog>> list(CsrLogListOptions listOptions) {
        return null
    }
}
