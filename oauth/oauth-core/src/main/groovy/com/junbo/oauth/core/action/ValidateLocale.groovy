package com.junbo.oauth.core.action

import com.junbo.identity.spec.v1.resource.LocaleResource
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
 * Created by haomin on 14-4-28.
 */
@CompileStatic
class ValidateLocale implements Action {
    private String defaultLocale
    private LocaleResource localeResource

    @Required
    void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale
    }

    @Required
    void setLocaleResource(LocaleResource localeResource) {
        this.localeResource = localeResource
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def parameterMap = contextWrapper.parameterMap

        String locale = parameterMap?.getFirst(OAuthParameters.LOCALE)
        if (locale == null) {
            locale = (String) context.flowScope[OAuthParameters.LOCALE]
        }
        if (locale == null) {
            locale = (String) context.requestScope[OAuthParameters.LOCALE]
        }

        if (StringUtils.hasText(locale)) {
            if (!ValidatorUtil.isValidLocale(locale, localeResource)) {
                locale = defaultLocale
            }
        }
        else {
            locale = defaultLocale
        }

        // change from en_US to en-US
        contextWrapper.viewLocale = locale.replace('_', '-')

        return Promise.pure(null)
    }
}
