/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.rest.resource.v1

import com.junbo.common.enumid.CountryId
import com.junbo.common.id.TosId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.core.service.filter.TosFilter
import com.junbo.identity.core.service.validator.TosValidator
import com.junbo.identity.service.TosService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Tos
import com.junbo.identity.spec.v1.option.list.TosListOptions
import com.junbo.identity.spec.v1.option.model.TosGetOptions
import com.junbo.identity.spec.v1.resource.TosResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils

import javax.ws.rs.core.Response

/**
 * Created by liangfu on 4/9/14.
 */
@Transactional
@CompileStatic
class TosResourceImpl implements TosResource {
    @Autowired
    private TosService tosService

    @Autowired
    private TosFilter tosFilter

    @Autowired
    private TosValidator tosValidator

    @Value('${identity.tos.defaultCountry}')
    private String defaultCountryCode

    @Override
    Promise<Tos> create(Tos tos) {
        if (tos == null) {
            throw new IllegalArgumentException('tos is null')
        }
        tos = tosFilter.filterForCreate(tos)

        return tosValidator.validateForCreate(tos).then {
            return tosService.create(tos).then { Tos newTos ->
                Created201Marker.mark(newTos.getId())

                newTos = tosFilter.filterForGet(newTos, null)
                return Promise.pure(newTos)
            }
        }
    }

    @Override
    Promise<Tos> get(TosId tosId, TosGetOptions getOptions) {
        if (tosId == null) {
            throw new IllegalArgumentException('tosId is null')
        }
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return tosValidator.validateForGet(tosId).then {
            return tosService.get(tosId).then { Tos newTos ->
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

        return tosService.get(tosId).then { Tos oldTos ->
            if (oldTos == null) {
                throw AppErrors.INSTANCE.tosNotFound(tosId).exception()
            }

            tos = tosFilter.filterForPatch(tos, oldTos)

            return tosValidator.validateForUpdate(tosId, tos, oldTos).then {
                return tosService.update(tos, oldTos).then { Tos newTos ->
                    newTos = tosFilter.filterForGet(newTos, null)
                    return Promise.pure(newTos)
                }
            }
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

        return tosService.get(tosId).then { Tos oldTos ->
            if (oldTos == null) {
                throw AppErrors.INSTANCE.tosNotFound(tosId).exception()
            }

            tos = tosFilter.filterForPut(tos, oldTos)

            return tosValidator.validateForUpdate(tosId, tos, oldTos).then {
                return tosService.update(tos, oldTos).then { Tos newTos ->
                    newTos = tosFilter.filterForGet(newTos, null)
                    return Promise.pure(newTos)
                }
            }
        }
    }

    @Override
    Promise<Response> delete(TosId tosId) {
        return tosValidator.validateForGet(tosId).then {
            return tosService.delete(tosId).then {
                return Promise.pure(Response.status(204).build())
            }
        }
    }

    private Promise<List<Tos>> search(TosListOptions listOptions) {
        Promise.pure().then {
            if (!StringUtils.isEmpty(listOptions.title) && !StringUtils.isEmpty(listOptions.state) && !StringUtils.isEmpty(listOptions.type) && listOptions.countryId != null) {
                return tosService.searchByTitleAndTypeAndStateAndCountry(listOptions.title, listOptions.type, listOptions.state,
                        listOptions.countryId, listOptions.limit, listOptions.offset)
            } else if (!StringUtils.isEmpty(listOptions.title) && !StringUtils.isEmpty(listOptions.state) && !StringUtils.isEmpty(listOptions.type)) {
                return tosService.searchByTitleAndTypeAndState(listOptions.title, listOptions.type, listOptions.state, listOptions.limit, listOptions.offset)
            } else if (!StringUtils.isEmpty(listOptions.title) && !StringUtils.isEmpty(listOptions.state) && listOptions.countryId != null) {
                return tosService.searchByTitleAndStateAndCountry(listOptions.title, listOptions.state, listOptions.countryId, listOptions.limit, listOptions.offset)
            } else if (!StringUtils.isEmpty(listOptions.title) && !StringUtils.isEmpty(listOptions.type) && listOptions.countryId != null) {
                return tosService.searchByTitleAndTypeAndCountry(listOptions.title, listOptions.type, listOptions.countryId, listOptions.limit, listOptions.offset)
            } else if (!StringUtils.isEmpty(listOptions.type) && !StringUtils.isEmpty(listOptions.state) && listOptions.countryId != null) {
                return tosService.searchByTypeAndStateAndCountry(listOptions.type, listOptions.state, listOptions.countryId, listOptions.limit, listOptions.offset)
            } else if (!StringUtils.isEmpty(listOptions.title) && !StringUtils.isEmpty(listOptions.type)) {
                return tosService.searchByTitleAndType(listOptions.title, listOptions.type, listOptions.limit, listOptions.offset)
            } else if (!StringUtils.isEmpty(listOptions.title) && !StringUtils.isEmpty(listOptions.state)) {
                return tosService.searchByTitleAndState(listOptions.title, listOptions.state, listOptions.limit, listOptions.offset)
            } else if (!StringUtils.isEmpty(listOptions.title) && listOptions.countryId != null) {
                return tosService.searchByTitleAndCountry(listOptions.title, listOptions.countryId, listOptions.limit, listOptions.offset)
            } else if (!StringUtils.isEmpty(listOptions.type) && !StringUtils.isEmpty(listOptions.state)) {
                return tosService.searchByTypeAndState(listOptions.type, listOptions.state, listOptions.limit, listOptions.offset)
            } else if (!StringUtils.isEmpty(listOptions.type) && listOptions.countryId != null) {
                return tosService.searchByTypeAndCountry(listOptions.type, listOptions.countryId, listOptions.limit, listOptions.offset)
            } else if (!StringUtils.isEmpty(listOptions.state) && listOptions.countryId != null) {
                return tosService.searchByStateAndCountry(listOptions.state, listOptions.countryId, listOptions.limit, listOptions.offset)
            } else if (!StringUtils.isEmpty(listOptions.title)) {
                return tosService.searchByTitle(listOptions.title, listOptions.limit, listOptions.offset)
            } else if (!StringUtils.isEmpty(listOptions.type)) {
                return tosService.searchByType(listOptions.type, listOptions.limit, listOptions.offset)
            } else if (!StringUtils.isEmpty(listOptions.state)) {
                return tosService.searchByState(listOptions.state, listOptions.limit, listOptions.offset)
            } else if (listOptions.countryId != null) {
                return tosService.searchByCountry(listOptions.countryId, listOptions.limit, listOptions.offset)
            } else {
                return tosService.searchAll(listOptions.limit, listOptions.offset)
            }
        }.then { List<Tos> tosList ->
            if (listOptions.countryId != null && !listOptions.countryId.getValue().equalsIgnoreCase(defaultCountryCode) && CollectionUtils.isEmpty(tosList)) {
                listOptions.setCountryId(new CountryId(defaultCountryCode))
                return search(listOptions)
            }

            return Promise.pure(tosList)
        }
    }
}
