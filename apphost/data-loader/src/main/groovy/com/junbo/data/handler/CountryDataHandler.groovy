package com.junbo.data.handler

import com.junbo.common.enumid.CountryId
import com.junbo.common.error.AppErrorException
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.resource.CountryResource
import com.junbo.langur.core.client.TypeReference
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-6-3.
 */
@CompileStatic
class CountryDataHandler extends BaseDataHandler {
    private CountryResource countryResource

    @Required
    void setCountryResource(CountryResource countryResource) {
        this.countryResource = countryResource
    }

    @Override
    void handle(String content) {
        Country country
        try {
            country = transcoder.decode(new TypeReference<Country>() {}, content) as Country
        } catch (Exception e) {
            logger.error("Error parsing country $content", e)
            exit()
        }

        logger.info("loading country $country.countryCode")

        Country existing = null
        try {
            existing = countryResource.get(new CountryId(country.countryCode), new CountryGetOptions()).get()
        } catch (AppErrorException e) {
            logger.debug('This content does not exist in current database', e)
        }

        if (existing != null) {
            if (alwaysOverwrite) {
                logger.debug("Overwrite Country $country.countryCode with this content.")
                country.id = (CountryId) existing.id
                country.rev = existing.rev
                try {
                    countryResource.patch(new CountryId(country.countryCode), country).get()
                } catch (Exception e) {
                    logger.error("Error updating country $country.countryCode.", e)
                }
            } else {
                logger.debug("$country.countryCode already exists, skipped!")
            }
        } else {
            logger.debug('Create new country with this content')
            try {
                countryResource.create(country).get()
            } catch (Exception e) {
                logger.error("Error creating country $country.countryCode.", e)
            }
        }
    }
}
