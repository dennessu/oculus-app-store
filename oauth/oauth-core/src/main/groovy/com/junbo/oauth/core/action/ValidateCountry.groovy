package com.junbo.oauth.core.action

import com.junbo.common.error.AppCommonErrors
import com.junbo.identity.spec.v1.resource.CountryResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.util.ValidatorUtil
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Created by minhao on 6/26/14.
 */
@CompileStatic
class ValidateCountry implements Action {
    private String defaultCountry
    private CountryResource countryResource

    @Required
    void setDefaultCountry(String defaultCountry) {
        this.defaultCountry = defaultCountry
    }

    @Required
    void setCountryResource(CountryResource countryResource) {
        this.countryResource = countryResource
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap

        String country = parameterMap?.getFirst(OAuthParameters.COUNTRY)
        if (country == null) {
            country = (String) context.flowScope[OAuthParameters.COUNTRY]
        }
        if (country == null) {
            country = (String) context.requestScope[OAuthParameters.COUNTRY]
        }

        if (StringUtils.hasText(country)) {
            if (!ValidatorUtil.isValidCountryCode(country, countryResource)) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('country', 'Invalid CountryCode.').exception()
            }
        }
        else {
            country = defaultCountry
        }

        contextWrapper.viewCountry = country

        return Promise.pure(null)
    }
}
