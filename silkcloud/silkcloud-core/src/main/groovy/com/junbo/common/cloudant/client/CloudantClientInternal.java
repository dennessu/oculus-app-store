/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.cloudant.client;

import com.junbo.common.cloudant.CloudantEntity;
import com.junbo.common.cloudant.model.CloudantQueryResult;
import com.junbo.common.cloudant.model.CloudantViewQueryOptions;
import com.junbo.langur.core.promise.Promise;

/**
 * CloudantClient.
 */
public interface CloudantClientInternal {
    <T extends CloudantEntity>
    Promise<T> cloudantPost(CloudantDbUri dbUri, Class<T> entityClass, T entity, boolean noOverrideWrites);

    <T extends CloudantEntity>
    Promise<T> cloudantGet(CloudantDbUri dbUri, Class<T> entityClass, String id);

    <T extends CloudantEntity>
    Promise<T> cloudantPut(CloudantDbUri dbUri, Class<T> entityClass, T entity, boolean noOverrideWrites);

    <T extends CloudantEntity>
    Promise<Void> cloudantDelete(CloudantDbUri dbUri, Class<T> entityClass, T entity, boolean noOverrideWrites);

    <T extends CloudantEntity>
    Promise<CloudantQueryResult> cloudantGetAll(CloudantDbUri dbUri, Class<T> entityClass, CloudantViewQueryOptions options);

    <T extends CloudantEntity>
    Promise<CloudantQueryResult> queryView(CloudantDbUri dbUri, Class<T> entityClass, String viewName, CloudantViewQueryOptions options);

    Promise<Integer> queryViewTotal(CloudantDbUri dbUri, String viewName, CloudantViewQueryOptions options);

    <T extends CloudantEntity>
    Promise<CloudantQueryResult> search(CloudantDbUri dbUri, Class<T> entityClass, String searchName,
                                        String queryString, String sort, Integer limit, String bookmark, boolean includeDocs);
}
