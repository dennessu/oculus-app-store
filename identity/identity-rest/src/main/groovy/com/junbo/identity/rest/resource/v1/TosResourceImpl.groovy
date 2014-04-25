/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource.v1

import com.junbo.common.id.Id
import com.junbo.common.id.TosId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.core.service.filter.TosFilter
import com.junbo.identity.core.service.validator.TosValidator
import com.junbo.identity.data.repository.TosRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Tos
import com.junbo.identity.spec.v1.option.list.TosListOptions
import com.junbo.identity.spec.v1.option.model.TosGetOptions
import com.junbo.identity.spec.v1.resource.TosResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by liangfu on 4/9/14.
 */
@Transactional
@CompileStatic
class TosResourceImpl implements TosResource {

    @Autowired
    private TosRepository tosRepository

    @Autowired
    private Created201Marker created201Marker

    @Autowired
    private TosFilter tosFilter

    @Autowired
    private TosValidator tosValidator

    @Override
    Promise<Tos> create(Tos tos) {
        tos = tosFilter.filterForCreate(tos)

        return tosValidator.validateForCreate(tos).then {
            tosRepository.create(tos).then { Tos newTos ->
                created201Marker.mark((Id) newTos.id)

                newTos = tosFilter.filterForGet(newTos, null)
                return Promise.pure(newTos)
            }
        }
    }

    @Override
    Promise<Tos> get(TosId tosId, TosGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return tosValidator.validateForGet(tosId).then {
            tosRepository.get(tosId).then { Tos newTos ->
                if (newTos == null) {
                    throw AppErrors.INSTANCE.tosNotFound(tosId).exception()
                }

                newTos = tosFilter.filterForGet(newTos, getOptions.properties?.split(',') as List<String>)
                return Promise.pure(newTos)
            }
        }
    }

    @Override
    Promise<Results<Tos>> list(TosListOptions listOptions) {
        tosValidator.validateForSearch(listOptions).then {
            def resultList = new Results<Tos>(items: [])
            tosRepository.search(listOptions).then { List<Tos> newToses ->
                if (newToses == null) {
                    return Promise.pure(resultList)
                }
                newToses.each { Tos newTos ->
                    if (newTos != null) {
                        newTos = tosFilter.filterForGet(newTos, listOptions.properties?.split(',') as List<String>)
                    }

                    if (newTos != null) {
                        resultList.items.add(newTos)
                    }
                }
            }

            return Promise.pure(resultList)
        }
    }

    @Override
    Promise<Tos> put(TosId tosId, Tos tos) {
        if (tosId == null) {
            throw new IllegalArgumentException('tosId is null')
        }

        if (tos == null) {
            throw new IllegalArgumentException('tos is null')
        }

        return tosRepository.get(tosId).then { Tos oldTos ->
            if (oldTos == null) {
                throw AppErrors.INSTANCE.tosNotFound(tosId).exception()
            }

            tos = tosFilter.filterForPut(tos, oldTos)

            tosRepository.update(tos).then { Tos newTos ->
                newTos = tosFilter.filterForGet(newTos, null)
                return Promise.pure(newTos)
            }
        }
    }

    @Override
    Promise<Void> delete(TosId tosId) {
        tosValidator.validateForGet(tosId).then {
            return tosRepository.delete(tosId)
        }
    }
}
