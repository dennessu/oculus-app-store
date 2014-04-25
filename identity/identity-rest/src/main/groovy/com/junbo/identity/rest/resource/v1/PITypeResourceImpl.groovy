package com.junbo.identity.rest.resource.v1

import com.junbo.common.enumid.PITypeId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.data.repository.PITypeRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.PIType
import com.junbo.identity.spec.v1.option.list.PITypeListOptions
import com.junbo.identity.spec.v1.option.model.PITypeGetOptions
import com.junbo.identity.spec.v1.resource.PITypeResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by haomin on 14-4-25.
 */
@Transactional
@CompileStatic
class PITypeResourceImpl implements PITypeResource {

    @Autowired
    private PITypeRepository piTypeRepository

    @Autowired
    private Created201Marker created201Marker

    @Override
    Promise<PIType> create(PIType piType) {
        if (piType == null) {
            throw new IllegalArgumentException('piType is null')
        }

        piTypeRepository.create(piType).then { PIType newPIType ->
            created201Marker.mark(newPIType.id)
            return Promise.pure(newPIType)
        }
    }

    @Override
    Promise<PIType> put(PITypeId piTypeId, PIType piType) {
        if (piTypeId == null) {
            throw new IllegalArgumentException('piTypeId is null')
        }

        if (piType == null) {
            throw new IllegalArgumentException('piType is null')
        }

        return piTypeRepository.get(piTypeId).then { PIType oldPIType ->
            if (oldPIType == null) {
                throw AppErrors.INSTANCE.piTypeNotFound(piTypeId).exception()
            }

            piTypeRepository.update(piType).then { PIType newPIType ->
                return Promise.pure(newPIType)
            }
        }
    }

    @Override
    Promise<PIType> get(PITypeId piTypeId, PITypeGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return piTypeRepository.get(piTypeId)
    }

    @Override
    Promise<Results<PIType>> list(PITypeListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        piTypeRepository.search(listOptions).then { List<PIType> piTypeList ->
            def result = new Results<PIType>(items: [])

            piTypeList.each { PIType newPItype ->
                result.items.add(newPItype)
            }

            return Promise.pure(result)
        }
    }

    @Override
    Promise<Void> delete(PITypeId piTypeId) {
        return piTypeRepository.delete(piTypeId)
    }
}
