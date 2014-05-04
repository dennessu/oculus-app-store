/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.view

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.Action
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.langur.core.webflow.action.ActionResult
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.spec.model.ViewModel
import groovy.transform.CompileStatic

import javax.ws.rs.core.Response

/**
 * RedirectToPage.
 */
@CompileStatic
abstract class AbstractView implements Action {

    protected abstract Promise<ViewModel> buildViewModel(ActionContext context)

    @Override
    Promise<ActionResult> execute(ActionContext context) {
        return buildViewModel(context).then { ViewModel model ->

            def responseBuilder = Response.status(Response.Status.OK)
            responseBuilder.entity(model)

            def contextWrapper = new ActionContextWrapper(context)
            contextWrapper.responseBuilder = responseBuilder

            return Promise.pure(null)
        }
    }

    private static class ErrorComparator implements Comparator<com.junbo.common.error.Error> {

        @Override
        int compare(com.junbo.common.error.Error o1, com.junbo.common.error.Error o2) {
            return o1.code <=> o2.code
        }
    }

    protected static Comparator<com.junbo.common.error.Error> getErrorComparator() {
        return new ErrorComparator()
    }
}
