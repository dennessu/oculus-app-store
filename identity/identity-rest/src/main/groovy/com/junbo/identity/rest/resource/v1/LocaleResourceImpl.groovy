package com.junbo.identity.rest.resource.v1

import com.junbo.common.enumid.LocaleId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
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

import javax.ws.rs.BeanParam
import javax.ws.rs.PathParam

/**
 * Created by haomin on 14-4-25.
 */
@Transactional
@CompileStatic
class LocaleResourceImpl implements LocaleResource {
    @Autowired
    private LocaleRepository localeRepository

    @Autowired
    private Created201Marker created201Marker


    @Override
    Promise<Locale> create(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException('locale is null')
        }

        localeRepository.create(locale).then { Locale newLocale ->
            created201Marker.mark(newLocale.id)
            return Promise.pure(newLocale)
        }
    }

    @Override
    Promise<Locale> put(@PathParam("localeId") LocaleId localeId, Locale locale) {
        if (localeId == null) {
            throw new IllegalArgumentException('localeId is null')
        }

        if (locale == null) {
            throw new IllegalArgumentException('locale is null')
        }

        return localeRepository.get(localeId).then { Locale oldLocale ->
            if (oldLocale == null) {
                throw AppErrors.INSTANCE.LocaleNotFound(localeId).exception()
            }

            localeRepository.update(locale).then { Locale newLocale ->
                return Promise.pure(newLocale)
            }
        }
    }

    @Override
    Promise<Locale> patch(@PathParam("localeId") LocaleId localeId, Locale locale) {
        return null
    }

    @Override
    Promise<Locale> get(@PathParam("LocaleId") LocaleId localeId, @BeanParam LocaleGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return localeRepository.get(localeId)
    }

    @Override
    Promise<Results<Locale>> list(@BeanParam LocaleListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        localeRepository.search(listOptions).then { List<Locale> localeList ->
            def result = new Results<Locale>(items: [])

            localeList.each { Locale newLocale ->
                result.items.add(newLocale)
            }

            return Promise.pure(result)
        }
    }

    @Override
    Promise<Void> delete(LocaleId localeId) {
        return localeRepository.delete(localeId)
    }
}
