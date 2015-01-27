package com.junbo.oauth.core.view

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.util.UriUtil
import org.springframework.beans.factory.annotation.Required

import javax.ws.rs.core.Response

/**
 * Created by haomin on 9/18/14.
 */
class RedirectToInvalidResetPasswordCodePage implements Action {

    private String pageUrl

    @Required
    void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl
    }

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)

        String realUrl = new String(pageUrl)
        if (contextWrapper.viewCountry != null) {
            realUrl = realUrl.replaceFirst('/country', '/' + contextWrapper.viewCountry)
        }

        if (contextWrapper.viewLocale != null) {
            realUrl = UriUtil.replaceLocale(realUrl, contextWrapper.viewLocale);
        }

        //append error code if needed, not need for now

        contextWrapper.responseBuilder = Response.status(Response.Status.FOUND)
                .location(URI.create("${realUrl}"))

        return Promise.pure(null)
    }
}
