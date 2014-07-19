package com.junbo.csr.core.service

import com.junbo.email.spec.model.QueryParam
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise

/**
 * Created by haomin on 7/19/14.
 */
public interface EmailService {
    Promise<String> sendCSRInvitationEmail(QueryParam templateQueryParam, String recipients, User user, String link)
}