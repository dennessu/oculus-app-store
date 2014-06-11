/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.repo.impl.cloudant

import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.id.EmailId
import com.junbo.email.db.repo.EmailHistoryRepository
import com.junbo.email.spec.model.Email
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * EmailHistory of Repository(Cloudant).
 */
@CompileStatic
class EmailHistoryRepositoryCloudantImpl extends EmailBaseRepository<Email> implements EmailHistoryRepository {
    private IdGenerator idGenerator

    @Required
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator
    }

    public Promise<Email> getEmailHistory(Long id) {
        return super.cloudantGet(id.toString())
    }

    public Promise<Email> createEmailHistory(Email email) {
        email.setId(new EmailId(this.getId(email?.userId?.value)))

        return super.cloudantPost(email)
    }

    public Promise<Email> updateEmailHistory(Email email) {

        return super.cloudantPut(email)
    }

    private Long getId(Long userId) {
        if(userId != null) {
            return idGenerator.nextId(userId)
        }
        return idGenerator.nextId()
    }

    private CloudantViews cloudantViews = new CloudantViews() {{
        Map<String, CloudantViews.CloudantView> viewMap = new HashMap<>()

        def view = new CloudantViews.CloudantView()
        view.setMap('function(doc) {emit(doc.id, doc._id)}')
        view.setResultClass(String.class)
        viewMap.put('by_emailId', view)

        setViews(viewMap)
    }}

    @Override
    protected CloudantViews getCloudantViews() {
        return cloudantViews
    }
}
