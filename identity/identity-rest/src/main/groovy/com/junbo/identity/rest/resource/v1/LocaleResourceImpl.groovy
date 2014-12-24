package com.junbo.identity.rest.resource.v1

import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.core.service.filter.LocaleFilter
import com.junbo.identity.core.service.validator.LocaleValidator
import com.junbo.identity.service.LocaleService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Locale
import com.junbo.identity.spec.v1.option.list.LocaleListOptions
import com.junbo.identity.spec.v1.option.model.LocaleGetOptions
import com.junbo.identity.spec.v1.resource.LocaleResource
import com.junbo.langur.core.client.PathParamTranscoder
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import javax.ws.rs.core.Response

/**
 * Created by haomin on 14-4-25.
 */
@Transactional
@CompileStatic
class LocaleResourceImpl implements LocaleResource {

    @Autowired
    private LocaleService localeService

    @Autowired
    private LocaleFilter localeFilter

    @Autowired
    private LocaleValidator localeValidator

    @Autowired
    private PathParamTranscoder pathParamTranscoder

    @Override
    Promise<Locale> create(Locale locale) {
        if (locale == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        locale = localeFilter.filterForCreate(locale)

        return localeValidator.validateForCreate(locale).then {
            return localeService.create(locale).then { Locale newLocale ->
                Created201Marker.mark(newLocale.id)
                newLocale = localeFilter.filterForGet(newLocale, null)
                return Promise.pure(newLocale)
            }
        }
    }

    @Override
    Promise<Locale> put(LocaleId localeId, Locale locale) {
        if (localeId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }

        if (locale == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }

        return localeService.get(localeId).then { Locale oldLocale ->
            if (oldLocale == null) {
                throw AppErrors.INSTANCE.localeNotFound(localeId).exception()
            }

            locale = localeFilter.filterForPut(locale, oldLocale)

            return localeValidator.validateForUpdate(localeId, locale, oldLocale).then {
                return localeService.update(locale, oldLocale).then { Locale newLocale ->
                    newLocale = localeFilter.filterForGet(newLocale, null)
                    return Promise.pure(newLocale)
                }
            }
        }
    }

    @Override
    Promise<Locale> get(LocaleId localeId, LocaleGetOptions getOptions) {
        if (localeId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        if (localeId.value.contains('-')) {
            String oldValue = localeId.value
            localeId.value = localeId.value.replace('-', '_')
            return localeValidator.validateForGet(localeId).then { Locale newLocale ->
                String location = JunboHttpContext.requestUri.toString().replaceFirst(
                        "/${oldValue}", '/' + pathParamTranscoder.encode(newLocale.id))

                JunboHttpContext.responseStatus = 301
                JunboHttpContext.responseHeaders.add('Location', location)

                return Promise.pure(null)
            }
        } else {
            return localeValidator.validateForGet(localeId).then { Locale newLocale ->
                newLocale = localeFilter.filterForGet(newLocale, getOptions.properties?.split(',') as List<String>)
                return Promise.pure(newLocale)
            }
        }
    }

    @Override
    Promise<Results<Locale>> list(LocaleListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        return localeValidator.validateForSearch(listOptions).then {
            return search(listOptions).then { Results<Locale> localeList ->
                def result = new Results<Locale>(items: [])
                result.total = localeList.total
                localeList.items.each { Locale newLocale ->
                    newLocale = localeFilter.filterForGet(newLocale, null)

                    if (newLocale != null) {
                        result.items.add(newLocale)
                    }
                }

                return Promise.pure(result)
            }
        }
    }

    @Override
    Promise<Response> delete(LocaleId localeId) {
        if (localeId == null) {
            throw AppCommonErrors.INSTANCE.parameterRequired('id').exception()
        }

        return localeValidator.validateForGet(localeId).then {
            return localeService.delete(localeId).then {
                return Promise.pure(Response.status(204).build())
            }
        }
    }

    private Promise<Results<Locale>> search(LocaleListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }
        return localeService.searchAll(listOptions.limit, listOptions.offset)
    }
}
