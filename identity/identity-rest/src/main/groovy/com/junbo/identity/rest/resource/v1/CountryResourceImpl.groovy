package com.junbo.identity.rest.resource.v1

import com.junbo.common.enumid.CountryId
import com.junbo.common.model.Results
import com.junbo.identity.core.service.Created201Marker
import com.junbo.identity.data.repository.CountryRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.option.list.CountryListOptions
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.resource.CountryResource
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
class CountryResourceImpl implements CountryResource {

    @Autowired
    private CountryRepository countryRepository

    @Autowired
    private Created201Marker created201Marker

    @Override
    Promise<Country> create(Country country) {
        if (country == null) {
            throw new IllegalArgumentException('country is null')
        }

        countryRepository.create(country).then { Country newCountry ->
            created201Marker.mark(newCountry.id)
            return Promise.pure(newCountry)
        }
    }

    @Override
    Promise<Country> put(@PathParam("countryId") CountryId countryId, Country country) {
        if (countryId == null) {
            throw new IllegalArgumentException('countryId is null')
        }

        if (country == null) {
            throw new IllegalArgumentException('country is null')
        }

        return countryRepository.get(countryId).then { Country oldCountry ->
            if (oldCountry == null) {
                throw AppErrors.INSTANCE.CountryNotFound(countryId).exception()
            }

            return countryRepository.update(country)
        }
    }

    @Override
    Promise<Country> patch(@PathParam("countryId") CountryId countryId, Country country) {
        return null
    }

    @Override
    Promise<Country> get(@PathParam("countryId") CountryId countryId, @BeanParam CountryGetOptions getOptions) {
        if (getOptions == null) {
            throw new IllegalArgumentException('getOptions is null')
        }

        return countryRepository.get(countryId)
    }

    @Override
    Promise<Results<Country>> list(@BeanParam CountryListOptions listOptions) {
        if (listOptions == null) {
            throw new IllegalArgumentException('listOptions is null')
        }

        countryRepository.search(listOptions).then { List<Country> countryList ->
            def result = new Results<Country>(items: [])

            countryList.each { Country newCountry ->
                    result.items.add(newCountry)
            }

            return Promise.pure(result)
        }
    }

    @Override
    Promise<Void> delete(@PathParam("countryId") CountryId countryId) {
        return countryRepository.delete(countryId)
    }
}
