package com.junbo.csr.rest.resource

import com.junbo.common.id.CsrLogId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.csr.core.validator.CsrLogValidator
import com.junbo.csr.db.repo.CsrLogRepository
import com.junbo.csr.spec.model.CsrLog
import com.junbo.csr.spec.option.list.CsrLogListOptions
import com.junbo.csr.spec.option.model.CsrLogGetOptions
import com.junbo.csr.spec.resource.CsrLogResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-7-4.
 */
@CompileStatic
class CsrLogResourceImpl implements CsrLogResource {
    @Autowired
    private CsrLogRepository csrLogRepository

    @Autowired
    private CsrLogValidator csrLogValidator

    @Override
    Promise<CsrLog> create(CsrLog csrLog) {
        return csrLogValidator.validateForCreate(csrLog).then {
            return csrLogRepository.create(csrLog).then { CsrLog newCsrLog ->
                Created201Marker.mark(newCsrLog.getId())
                return Promise.pure(newCsrLog)
            }
        }
    }

    @Override
    Promise<CsrLog> get(CsrLogId csrLogId, CsrLogGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return csrLogValidator.validateForGet(csrLogId).then {
            return csrLogRepository.get(csrLogId).then { CsrLog csrLog ->
                return Promise.pure(csrLog)
            }
        }
    }

    @Override
    Promise<Results<CsrLog>> list(CsrLogListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        return csrLogValidator.validateForSearch(listOptions).then {
            return csrLogRepository.searchByListOptions(listOptions).then { Results<CsrLog> results ->
                return Promise.pure(results)
            }
        }
    }
}
