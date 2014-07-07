package com.junbo.csr.rest.resource

import com.junbo.common.id.CsrUpdateId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.csr.core.validator.CsrUpdateValidator
import com.junbo.csr.db.repo.CsrUpdateRepository
import com.junbo.csr.spec.model.CsrLog
import com.junbo.csr.spec.model.CsrUpdate
import com.junbo.csr.spec.option.list.CsrUpdateListOptions
import com.junbo.csr.spec.option.model.CsrUpdateGetOptions
import com.junbo.csr.spec.resource.CsrUpdateResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-7-4.
 */
@CompileStatic
class CsrUpdateResourceImpl implements CsrUpdateResource {
    @Autowired
    private CsrUpdateRepository csrUpdateRepository

    @Autowired
    private CsrUpdateValidator csrUpdateValidator

    @Override
    Promise<CsrUpdate> create(CsrUpdate csrUpdate) {
        return csrUpdateValidator.validateForCreate(csrUpdate).then {
            return csrUpdateRepository.create(csrUpdate).then { CsrUpdate newCsrUpdate ->
                Created201Marker.mark(newCsrUpdate.getId())
                return Promise.pure(newCsrUpdate)
            }
        }
    }

    @Override
    Promise<CsrUpdate> put(CsrUpdateId csrUpdateId, CsrUpdate csrUpdate) {
        return csrUpdateValidator.validateForGet(csrUpdateId).then { CsrUpdate oldCsrUpdate ->
            return csrUpdateValidator.validateForUpdate(csrUpdateId, csrUpdate, oldCsrUpdate).then {
                return csrUpdateRepository.update(csrUpdate).then { CsrUpdate newCsrUpdate ->
                    return Promise.pure(newCsrUpdate)
                }
            }
        }
    }

    @Override
    Promise<CsrUpdate> patch(CsrUpdateId csrUpdateId, CsrUpdate csrUpdate) {
        return csrUpdateValidator.validateForGet(csrUpdateId).then { CsrUpdate oldCsrUpdate ->
            return csrUpdateValidator.validateForUpdate(csrUpdateId, csrUpdate, oldCsrUpdate).then {
                return csrUpdateRepository.update(csrUpdate).then { CsrUpdate newCsrUpdate ->
                    return Promise.pure(newCsrUpdate)
                }
            }
        }
    }

    @Override
    Promise<CsrUpdate> get(CsrUpdateId csrUpdateId, CsrUpdateGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return csrUpdateValidator.validateForGet(csrUpdateId).then {
            return csrUpdateRepository.get(csrUpdateId).then { CsrUpdate csrUpdate ->
                return Promise.pure(csrUpdate)
            }
        }
    }

    @Override
    Promise<Results<CsrUpdate>> list(CsrUpdateListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        return csrUpdateValidator.validateForSearch(listOptions).then {
            return csrUpdateRepository.searchByListOptions(listOptions).then { Results<CsrUpdate> results ->
                return Promise.pure(results)
            }
        }
    }

    @Override
    Promise<Void> delete(CsrUpdateId csrUpdateId) {
        return csrUpdateValidator.validateForGet(csrUpdateId).then { CsrUpdate existing ->
            return csrUpdateRepository.delete(csrUpdateId)
        }
    }
}
