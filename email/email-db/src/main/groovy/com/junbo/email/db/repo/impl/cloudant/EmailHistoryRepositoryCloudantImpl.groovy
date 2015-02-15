/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.repo.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.email.db.repo.EmailHistoryRepository
import com.junbo.email.spec.model.Email
import com.junbo.email.spec.model.EmailSearchOption
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * EmailHistory of Repository(Cloudant).
 */
@CompileStatic
class EmailHistoryRepositoryCloudantImpl extends CloudantClient<Email> implements EmailHistoryRepository {

    public Promise<Email> getEmailHistory(String id) {
        return cloudantGet(id.toString())
    }

    public Promise<Email> createEmailHistory(Email email) {
        return cloudantPost(email)
    }

    public Promise<Email> updateEmailHistory(Email email, Email oldEmail) {
        return cloudantPut(email, oldEmail)
    }

    public Promise<Void> deleteEmailHistory(String id) {
        return cloudantDelete(id)
    }

    @Override
    Promise<List<Email>> searchEmail(EmailSearchOption option) {
        return super.queryView("by_user_id", option.userId.toString());
    }
}
