package com.junbo.email.clientproxy

import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.langur.core.promise.Promise

/**
 * Created by Wei on 3/14/14.
 */
interface EmailProvider {
    Promise<Email> sendEmail(Email email, EmailTemplate template)
}