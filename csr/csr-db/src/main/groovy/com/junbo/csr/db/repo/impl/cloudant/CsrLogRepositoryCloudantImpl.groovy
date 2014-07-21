package com.junbo.csr.db.repo.impl.cloudant

import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantQueryResult
import com.junbo.common.id.CsrLogId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.csr.db.repo.CsrLogRepository
import com.junbo.csr.spec.model.CsrLog
import com.junbo.csr.spec.option.list.CsrLogListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-7-4.
 */
@CompileStatic
class CsrLogRepositoryCloudantImpl extends CloudantClient<CsrLog> implements CsrLogRepository {

    @Override
    Promise<Results<CsrLog>> searchByListOptions(CsrLogListOptions listOptions) {
        if (listOptions.lastHours != null) {
            return searchByLastHours(listOptions.lastHours, listOptions.userId, listOptions.action, listOptions.limit, listOptions.offset)
        }
        else {
            return searchByDateRange(listOptions.from, listOptions.to, listOptions.userId, listOptions.action, listOptions.limit, listOptions.offset)
        }
    }

    @Override
    Promise<CsrLog> get(CsrLogId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<CsrLog> create(CsrLog model) {
        return cloudantPost(model)
    }

    @Override
    Promise<CsrLog> update(CsrLog model, CsrLog oldModel) {
        return cloudantPut(model, oldModel)
    }

    @Override
    Promise<Void> delete(CsrLogId id) {
        return cloudantDelete(id.toString())
    }

    private Promise<Results<CsrLog>> searchByLastHours(Integer lastHours, UserId userId, String action, Integer limit, Integer offset) {
        Date utcTo = new Date()
        Calendar calendar = Calendar.instance
        calendar.setTime(utcTo)
        calendar.add(Calendar.HOUR, -lastHours)
        Date utcFrom = calendar.time
        ISO8601DateFormat formatter = new ISO8601DateFormat();

        return searchByDateRange(formatter.format(utcFrom), formatter.format(utcTo), userId, action, limit, offset)
    }

    private Promise<Results<CsrLog>> searchByDateRange(String utcFrom, String utcTo, UserId userId, String action, Integer limit, Integer offset) {

        if (userId != null && action != null) {
            def startKey = [userId.toString(), action, utcFrom]
            def endKey = [userId.toString(), action, utcTo]
            return internalSearch('by_user_action_time', startKey.toArray(new String()), endKey.toArray(new String()), limit, offset)
        }
        else if (userId != null) {
            def startKey = [userId.toString(), utcFrom]
            def endKey = [userId.toString(), utcTo]
            return internalSearch('by_user_time', startKey.toArray(new String()), endKey.toArray(new String()), limit, offset)
        }
        else if (action != null) {
            def startKey = [action, utcFrom]
            def endKey = [action, utcTo]
            return internalSearch('by_action_time', startKey.toArray(new String()), endKey.toArray(new String()), limit, offset)
        }
        else {
            def startKey = [utcFrom]
            def endKey = [utcTo]
            return internalSearch('by_time', startKey.toArray(new String()), endKey.toArray(new String()), limit, offset)
        }
    }

    private Promise<Results<CsrLog>> internalSearch(String view, Object[] startKey, Object[] endKey, Integer limit, Integer offset) {
        def resultList = new Results<CsrLog>(items: [])
        CloudantQueryResult cloudantQueryResult = queryViewSync(view, startKey, endKey, false, null, null, false, false)
        if (cloudantQueryResult.rows != null) {
            resultList.total = cloudantQueryResult.rows.size()
        }
        else {
            resultList.total = 0L
        }

        if (resultList.total > 0) {
            return queryView(view, startKey, endKey, false, limit, offset, false, true).then { CloudantQueryResult results ->
                if (results == null || results.rows == null || results.rows.size() == 0) {
                    return Promise.pure(resultList)
                }

                if (results.rows != null) {
                    for(CloudantQueryResult.ResultObject result in results.rows) {
                        resultList.items.add((CsrLog)(result.doc))
                    }

                    return Promise.pure(resultList)
                }
            }
        }

        return Promise.pure(resultList)

    }
}
