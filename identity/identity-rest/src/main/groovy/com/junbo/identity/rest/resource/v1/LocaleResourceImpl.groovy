package com.junbo.identity.rest.resource.v1

import com.junbo.common.enumid.LocaleId
import com.junbo.common.model.Results
import com.junbo.common.rs.Created201Marker
import com.junbo.identity.core.service.filter.LocaleFilter
import com.junbo.identity.core.service.validator.LocaleValidator
import com.junbo.identity.data.repository.LocaleRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Locale
import com.junbo.identity.spec.v1.option.list.LocaleListOptions
import com.junbo.identity.spec.v1.option.model.LocaleGetOptions
import com.junbo.identity.spec.v1.resource.LocaleResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by haomin on 14-4-25.
 */
@Transactional
@CompileStatic
class LocaleResourceImpl implements LocaleResource {
    @Autowired
    private LocaleRepository localeRepository

    @Autowired
    private LocaleFilter localeFilter

    @Autowired
    private LocaleValidator localeValidator


    @Override
    Promise<Locale> create(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException('locale is null')
        }

        locale = localeFilter.filterForCreate(locale)

        return localeValidator.validateForCreate(locale).then {
            return localeRepository.create(locale).then { Locale newLocale ->
                Created201Marker.mark(newLocale.id)
                newLocale = localeFilter.filterForGet(newLocale, null)
                return Promise.pure(newLocale)
            }
        }
    }

    @Override
    Promise<Locale> put(LocaleId localeId, Locale locale) {
        if (localeId == null) {
            throw new IllegalArgumentException('localeId is null')
        }

        if (locale == null) {
            throw new IllegalArgumentException('locale is null')
        }

        return localeRepository.get(localeId).then { Locale oldLocale ->
            if (oldLocale == null) {
                throw AppErrors.INSTANCE.localeNotFound(localeId).exception()
            }

            locale = localeFilter.filterForPut(locale, oldLocale)

            return localeValidator.validateForUpdate(localeId, locale, oldLocale).then {
                return localeRepository.update(locale).then { Locale newLocale ->
                    newLocale = localeFilter.filterForGet(newLocale, null)
                    return Promise.pure(newLocale)
                }
            }
        }
    }

    @Override
    Promise<Locale> patch(LocaleId localeId, Locale locale) {
        if (localeId == null) {
            throw new IllegalArgumentException('localeId is null')
        }

        if (locale == null) {
            throw new IllegalArgumentException('locale is null')
        }

        return localeRepository.get(localeId).then { Locale oldLocale ->
            if (oldLocale == null) {
                throw AppErrors.INSTANCE.localeNotFound(localeId).exception()
            }

            locale = localeFilter.filterForPatch(locale, oldLocale)

            return localeValidator.validateForUpdate(localeId, locale, oldLocale).then {
                return localeRepository.update(locale).then { Locale newLocale ->
                    newLocale = localeFilter.filterForGet(newLocale, null)
                    return Promise.pure(newLocale)
                }
            }
        }
    }

    @Override
    Promise<Locale> get(LocaleId localeId, LocaleGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return localeValidator.validateForGet(localeId).then {
            return localeRepository.get(localeId).then { Locale newLocale ->
                if (newLocale == null) {
                    throw AppErrors.INSTANCE.localeNotFound(localeId).exception()
                }

                newLocale = localeFilter.filterForGet(newLocale, null)
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
            return search(listOptions).then { List<Locale> localeList ->
                def result = new Results<Locale>(items: [])

                localeList.each { Locale newLocale ->
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
    Promise<Void> delete(LocaleId localeId) {
        if (localeId == null) {
            throw new IllegalArgumentException('localeId is null')
        }

        return localeValidator.validateForGet(localeId).then {
            return localeRepository.delete(localeId)
        }
    }

    private Promise<List<Locale>> search(LocaleListOptions listOptions) {
        return localeRepository.searchAll(listOptions.limit, listOptions.offset)
    }
}
