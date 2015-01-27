package com.junbo.data.handler

import com.junbo.common.enumid.CountryId
import com.junbo.common.error.AppErrorException
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.resource.CountryResource
import com.junbo.langur.core.client.TypeReference
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.core.io.Resource

/**
 * Created by haomin on 14-6-3.
 */
@CompileStatic
class CountryDataHandler extends BaseDataHandler {
    private CountryResource countryResource
    private boolean countryOverride

    @Required
    void setCountryResource(CountryResource countryResource) {
        this.countryResource = countryResource
    }

    @Required
    void setCountryOverride(boolean countryOverride) {
        this.countryOverride = countryOverride
    }

    @Override
    Resource[] resolveDependencies(Resource[] resources) {
        return resources
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

        Country existing = null
        try {
            existing = countryResource.get(new CountryId(country.countryCode), new CountryGetOptions()).get()
        } catch (AppErrorException e) {
            logger.debug('This content does not exist in current database', e)
        }

        if (existing != null) {
            if (countryOverride) {
                logger.debug("Update the existed country $country.countryCode.")
                try {
                    country.createdTime = existing.createdTime
                    country.updatedTime = existing.updatedTime
                    country.adminInfo = existing.adminInfo
                    country.rev = existing.rev
                    country.id = existing.getId()

                    countryResource.put(new CountryId(country.countryCode), country).get()
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
