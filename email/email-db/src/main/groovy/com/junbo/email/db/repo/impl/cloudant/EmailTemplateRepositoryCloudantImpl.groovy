/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.db.repo.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.email.db.repo.EmailTemplateRepository
import com.junbo.email.spec.model.EmailTemplate
import com.junbo.email.spec.model.Pagination
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Impl of EmailTemplate Repository(Cloudant).
 */
@CompileStatic
class EmailTemplateRepositoryCloudantImpl extends CloudantClient<EmailTemplate> implements EmailTemplateRepository {

    public Promise<EmailTemplate> getEmailTemplate(String id) {
        return super.cloudantGet(id)
    }

    public Promise<EmailTemplate> saveEmailTemplate(EmailTemplate template) {
        return super.cloudantPost(template)
    }

    public Promise<EmailTemplate> updateEmailTemplate(EmailTemplate template) {
        return cloudantGet(template.getId().value.toString()).then {EmailTemplate savedTemplate ->
            return cloudantPut(template)
        }
    }

    public Promise<EmailTemplate> getEmailTemplateByName(String name) {
        def template = super.queryView('by_name', name)
        return template.then {List<EmailTemplate> templates ->
            if (templates == null || templates.size() == 0) {
                return Promise.pure(null)
            }
            return Promise.pure(templates.first())
        }
    }

    public Promise<List<EmailTemplate>> getEmailTemplates(Map<String, String> queries, Pagination pagination) {
        def viewKey = this.buildViewKey(queries)
        if (viewKey != null) {
            def view =viewKey.keySet().getAt(0), key = viewKey.values().getAt(0)
            return super.queryView(view, key, pagination?.page, pagination?.size, false)
        }
        else {
            return super.cloudantGetAll()
        }
    }

    public Promise<Void> deleteEmailTemplate(String id) {
        return super.cloudantDelete(id)
    }

    private Map<String, String> buildViewKey(Map<String, String> queries) {
        if (queries == null || queries.size() == 0) {
            return  null
        }
        def source = queries.get('source'), action = queries.get('action'), locale = queries.get('locale')
        def viewKey = [:]
        if (source && action && locale) {
            viewKey = ['by_source_action_locale':"${source}:${action}:${locale}"]
        }
        else if (source && action && !locale) {
            viewKey = ['by_source_action':"${source}:${action}"]
        }
        else if (source && !action && locale) {
            viewKey = ['by_source_locale':"${source}:${locale}"]
        }
        else if (!source && action && locale) {
            viewKey = ['by_action_locale':"${action}:${locale}"]
        }
        else if (source && !action && !locale) {
            viewKey = ['by_source':source]
        }
        else if (!source && action && !locale) {
            viewKey = ['by_action':action]
        }
        else if (!source && !action && locale) {
            viewKey = ['by_locale':locale]
        }
        else {
            viewKey = null
        }
        return viewKey
    }

    private CloudantViews cloudantViews = new CloudantViews() {{
        Map<String, CloudantViews.CloudantView> viewMap = new HashMap<>()
        Map<String, CloudantViews.CloudantIndex> indexMap = new HashMap<>()

        def view = new CloudantViews.CloudantView()
        view.setMap("function(doc) {emit(doc.name, doc._id)}")
        view.setResultClass(String)
        viewMap.put('by_name', view)

        view = new CloudantViews.CloudantView()
        view.setMap('function(doc) {emit(doc.source, doc._id)}')
        view.setResultClass(String)
        viewMap.put('by_source', view)

        view = new CloudantViews.CloudantView()
        view.setMap('function(doc) {emit(doc.action, doc._id)}')
        view.setResultClass(String)
        viewMap.put('by_action', view)

        view = new CloudantViews.CloudantView()
        view.setMap('function(doc) {emit(doc.locale, doc._id)}')
        view.setResultClass(String)
        viewMap.put('by_locale', view)

        view = new CloudantViews.CloudantView()
        view.setMap('function(doc) {emit(doc.source + \':\' + doc.action, doc._id)}')
        view.setResultClass(String)
        viewMap.put('by_source_action', view)

        view = new CloudantViews.CloudantView()
        view.setMap('function(doc) {emit(doc.source + \':\' + doc.locale, doc._id)}')
        view.setResultClass(String)
        viewMap.put('by_source_locale', view)

        view = new CloudantViews.CloudantView()
        view.setMap('function(doc) {emit(doc.action + \':\' + doc.locale, doc._id)}')
        view.setResultClass(String)
        viewMap.put('by_action_locale', view)

        view = new CloudantViews.CloudantView()
        view.setMap('function(doc) {emit(doc.source + \':\' + doc.action +\':\' + doc.locale, doc._id)}')
        view.setResultClass(String)
        viewMap.put('by_source_action_locale', view)

        setViews(viewMap)

        def index = new CloudantViews.CloudantIndex()
        index.setResultClass(String)
        index.setIndex("function(doc) {index(\'name\', doc.name)}")
        indexMap.put("search", index)
        setIndexes(indexMap)
    }}

    @Override
    protected CloudantViews getCloudantViews() {
        return cloudantViews
    }
}
