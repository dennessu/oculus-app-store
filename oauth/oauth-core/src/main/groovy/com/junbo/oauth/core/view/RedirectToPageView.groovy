package com.junbo.oauth.core.view

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.model.ViewModel
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.glassfish.jersey.server.ContainerRequest
import org.springframework.beans.factory.annotation.Required
import org.springframework.web.util.UriComponentsBuilder

/**
 * Created by minhao on 5/21/14.
 */
@CompileStatic
@Deprecated
class RedirectToPageView extends AbstractView {

    private String pageUrl

    @Required
    void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl
    }

    @Override
    protected Promise<ViewModel> buildViewModel(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(pageUrl)
        builder.queryParam(OAuthParameters.CONVERSATION_ID, contextWrapper.conversationId)

        def model = new ViewModel(
                view: 'redirect',
                model: [
                        'location': builder.build().toUriString()
                ] as Map<String, Object>,
                errors: contextWrapper.errors.unique(errorComparator).asList()
        )

        return Promise.pure(model)
    }
}
