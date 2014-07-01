package com.junbo.common.cloudant.client
import com.junbo.common.cloudant.CloudantEntity
import com.junbo.common.cloudant.model.CloudantQueryResult
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * CloudantClient.
 */
@CompileStatic
interface CloudantClient<T extends CloudantEntity> {
    public static final String VIEW_PATH = '/_design/views/_view/'
    public static final String SEARCH_PATH = '/_design/views/_search/'
    public static final String DEFAULT_DESIGN_ID_PREFIX = '_design/'

    Promise<T> cloudantPost(T entity)

    Promise<T> cloudantGet(String id)

    Promise<T> cloudantPut(T entity)

    Promise<Void> cloudantDelete(T entity)

    Promise<List<T>> cloudantGetAll(Integer limit, Integer skip, boolean descending)

    Promise<CloudantQueryResult> queryView(String viewName, String key, String startKey, String endKey,
                                                   Integer limit, Integer skip, boolean descending,
                                                   boolean includeDocs)

    Promise<CloudantQueryResult> search(String searchName, String queryString, Integer limit, String bookmark,
                                                boolean includeDocs)
}
