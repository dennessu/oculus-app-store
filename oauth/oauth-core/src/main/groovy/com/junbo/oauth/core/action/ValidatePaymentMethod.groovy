package com.junbo.oauth.core.action

import com.junbo.common.enumid.CountryId
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.error.AppErrorException
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.option.model.CountryGetOptions
import com.junbo.identity.spec.v1.resource.CountryResource
import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.exception.AppErrors
import com.junbo.oauth.spec.param.OAuthParameters
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.StringUtils

import java.util.regex.Pattern

/**
 * Created by Zhanxin on 5/21/2014.
 */
class ValidatePaymentMethod implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatePaymentMethod)

    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile('\\d+')
    private static final Pattern EXPIRATION_DATE_PATTERN = Pattern.compile('\\d{4}-\\d{2}')

    private CountryResource countryResource

    @Required
    void setCountryResource(CountryResource countryResource) {
        this.countryResource = countryResource
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap

        validateParameter(contextWrapper, OAuthParameters.ADDRESS1, null)
        //validateParameter(contextWrapper, OAuthParameters.CITY, null)
        //validateParameter(contextWrapper, OAuthParameters.SUB_COUNTRY, null)
        validateParameter(contextWrapper, OAuthParameters.COUNTRY, null)
        //validateParameter(contextWrapper, OAuthParameters.ZIP_CODE, null)

        validateParameter(contextWrapper, OAuthParameters.CARD_NUMBER, CARD_NUMBER_PATTERN)
        validateParameter(contextWrapper, OAuthParameters.NAME_ON_CARD, null)
        validateParameter(contextWrapper, OAuthParameters.EXPIRATION_DATE, EXPIRATION_DATE_PATTERN)

        if (!contextWrapper.errors.isEmpty()) {
            return Promise.pure(new ActionResult('error'))
        }

        String countryName = parameterMap.getFirst(OAuthParameters.COUNTRY)

        return countryResource.get(new CountryId(countryName), new CountryGetOptions()).recover { Throwable e ->
            handleException(e, contextWrapper)
            return Promise.pure(null)
        }.then { Country country ->
            if (country == null) {
                return Promise.pure(new ActionResult('error'))
            }

            // String subCountry = parameterMap.getFirst(OAuthParameters.SUB_COUNTRY)

            // todo: enable this validation when country resource setup properly, now we just disable it
            /*
            if (!country.subCountries.containsKey(subCountry)) {
                contextWrapper.errors.add(AppErrors.INSTANCE.invalidParameter(OAuthParameters.SUB_COUNTRY).error())
            }
            */


            if (!contextWrapper.errors.isEmpty()) {
                return Promise.pure(new ActionResult('error'))
            }

            return Promise.pure(new ActionResult('success'))
        }
    }

    private static validateParameter(ActionContextWrapper contextWrapper, String parameterName, Pattern pattern) {
        def parameterMap = contextWrapper.parameterMap
        String parameter = parameterMap.getFirst(parameterName)

        if (StringUtils.isEmpty(parameter)) {
            contextWrapper.errors.add(AppCommonErrors.INSTANCE.parameterRequired(parameterName).error())
        }

        if (pattern != null && parameter != null) {
            if (!pattern.matcher(parameter).find()) {
                contextWrapper.errors.add(AppCommonErrors.INSTANCE.parameterInvalid(parameterName).error())
            }
        }
    }

    private static void handleException(Throwable throwable, ActionContextWrapper contextWrapper) {
        LOGGER.error('Error calling the identity service', throwable)
        if (throwable instanceof AppErrorException) {
            contextWrapper.errors.add(((AppErrorException) throwable).error.error())
        } else {
            contextWrapper.errors.add(AppErrors.INSTANCE.errorCallingIdentity().error())
        }
    }
}
