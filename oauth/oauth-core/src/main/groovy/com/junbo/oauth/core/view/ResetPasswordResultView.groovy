package com.junbo.oauth.core.view

import com.junbo.langur.core.promise.Promise
import com.junbo.langur.core.webflow.action.ActionContext
import com.junbo.oauth.core.context.ActionContextWrapper
import com.junbo.oauth.core.util.CookieUtil
import com.junbo.oauth.db.repo.LoginStateRepository
import com.junbo.oauth.spec.model.LoginState
import com.junbo.oauth.spec.model.ResetPasswordCode
import com.junbo.oauth.spec.model.ViewModel
import com.junbo.oauth.spec.param.OAuthParameters
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by minhao on 5/1/14.
 */
@CompileStatic
class ResetPasswordResultView extends AbstractView {
    private LoginStateRepository loginStateRepository

    @Required
    void setLoginStateRepository(LoginStateRepository loginStateRepository) {
        this.loginStateRepository = loginStateRepository
    }

    @Override
    protected Promise<ViewModel> buildViewModel(ActionContext context) {
        def contextWrapper = new ActionContextWrapper(context)
        def modelMap = [
                'reset_password_success': contextWrapper.errors.isEmpty() ? 'true' : 'false',
                'locale': contextWrapper.viewLocale
        ]

        if (contextWrapper.errors.isEmpty()) {
            ResetPasswordCode resetPasswordCode = contextWrapper.resetPasswordCode
            LoginState loginState = new LoginState(
                    userId: resetPasswordCode.userId,
                    lastAuthDate: new Date()
            )

            loginStateRepository.save(loginState)
            CookieUtil.setCookie(context, OAuthParameters.COOKIE_LOGIN_STATE, loginState.loginStateId, -1)
            CookieUtil.setCookie(context, OAuthParameters.COOKIE_SESSION_STATE, loginState.sessionId, -1, false)
        }

        def model = new ViewModel(
                view: 'reset_password_result',
                model: modelMap as Map<String, Object>,
                errors: contextWrapper.errors.unique(errorComparator).asList()
        )

        return Promise.pure(model)
    }
}
