package com.junbo.oauth.core.action

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppExceptions
import com.junbo.oauth.core.util.ValidatorUtil
import com.junbo.oauth.spec.param.OAuthParameters
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

/**
 * Created by minhao on 6/26/14.
 */
class ValidateCountry implements Action {
    private String defaultCountry

    @Required
    void setDefaultCountry(String defaultCountry) {
        this.defaultCountry = defaultCountry
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
            if (!ValidatorUtil.isValidCountryCode(country)) {
                throw AppExceptions.INSTANCE.invalidCountryCode().exception()
            }
        }
        else {
            country = defaultCountry
        }

        contextWrapper.viewCountry = country

        return Promise.pure(null)
    }
}
