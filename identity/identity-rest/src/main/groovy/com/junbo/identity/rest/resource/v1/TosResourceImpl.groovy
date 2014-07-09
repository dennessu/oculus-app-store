/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource.v1

import com.junbo.authorization.AuthorizeContext
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.TosId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
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
    private static final String IDENTITY_ADMIN_SCOPE = 'identity.admin'

    @Autowired
    private TosRepository tosRepository

    @Autowired
    private TosFilter tosFilter

    @Autowired
    private TosValidator tosValidator

    @Override
    Promise<Tos> create(Tos tos) {
        if (!AuthorizeContext.hasScopes(IDENTITY_ADMIN_SCOPE)) {
            throw AppCommonErrors.INSTANCE.forbidden().exception()
        }

        tos = tosFilter.filterForCreate(tos)

        return tosValidator.validateForCreate(tos).then {
            return tosRepository.create(tos).then { Tos newTos ->
                Created201Marker.mark(newTos.getId())

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
            return tosRepository.get(tosId).then { Tos newTos ->
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
        return tosValidator.validateForSearch(listOptions).then {
            def resultList = new Results<Tos>(items: [])
            return search(listOptions).then { List<Tos> newToses ->
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

                return Promise.pure(resultList)
            }
        }
    }

    @Override
    Promise<Tos> patch(TosId tosId, Tos tos) {
        if (tosId == null) {
            throw new IllegalArgumentException('tosId is null')
        }

        if (tos == null) {
            throw new IllegalArgumentException('tos is null')
        }

        if (!AuthorizeContext.hasScopes(IDENTITY_ADMIN_SCOPE)) {
            throw AppCommonErrors.INSTANCE.forbidden().exception()
        }

        return tosRepository.get(tosId).then { Tos oldTos ->
            if (oldTos == null) {
                throw AppErrors.INSTANCE.tosNotFound(tosId).exception()
            }

            tos = tosFilter.filterForPatch(tos, oldTos)

            return tosValidator.validateForUpdate(tosId, tos, oldTos).then {
                return tosRepository.update(tos).then { Tos newTos ->
                    newTos = tosFilter.filterForGet(newTos, null)
                    return Promise.pure(newTos)
                }
            }
        }
    }

    @Override
    Promise<Void> delete(TosId tosId) {
        if (!AuthorizeContext.hasScopes(IDENTITY_ADMIN_SCOPE)) {
            throw AppCommonErrors.INSTANCE.forbidden().exception()
        }

        return tosValidator.validateForGet(tosId).then {
            return tosRepository.delete(tosId)
        }
    }

    private Promise<List<Tos>> search(TosListOptions listOptions) {
        return tosRepository.searchAll(listOptions.limit, listOptions.offset)
    }
}
