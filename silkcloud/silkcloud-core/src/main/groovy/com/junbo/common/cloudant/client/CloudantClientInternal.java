/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.cloudant.client;

import com.junbo.common.cloudant.CloudantEntity;
import com.junbo.common.cloudant.model.CloudantQueryResult;
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
    Promise<CloudantQueryResult> cloudantGetAll(CloudantDbUri dbUri, Class<T> entityClass, Integer limit, Integer skip, boolean descending, boolean includeDocs);

    <T extends CloudantEntity>
    Promise<CloudantQueryResult> queryView(CloudantDbUri dbUri, Class<T> entityClass, String viewName,
                                           String key, Integer limit, Integer skip, boolean descending, boolean includeDocs);


    <T extends CloudantEntity>
    Promise<CloudantQueryResult> queryView(CloudantDbUri dbUri, Class<T> entityClass, String viewName, String startKey, String endKey,
                                           Integer limit, Integer skip, boolean descending, boolean includeDocs);


    <T extends CloudantEntity>
    Promise<CloudantQueryResult> queryView(CloudantDbUri dbUri, Class<T> entityClass, String viewName, Object[] startKey, Object[] endKey,
                                           boolean withHighKey, Integer limit, Integer skip, boolean descending, boolean includeDocs);


    <T extends CloudantEntity>
    Promise<CloudantQueryResult> search(CloudantDbUri dbUri, Class<T> entityClass, String searchName,
                                        String queryString, String sort, Integer limit, String bookmark, boolean includeDocs);

    Promise<Integer> queryViewTotal(CloudantDbUri dbUri, String key, String viewName);

    Promise<Integer> queryViewTotal(CloudantDbUri dbUri, String viewName, Object[] startKey, Object[] endKey, boolean withHighKey, boolean descending);

    <T extends CloudantEntity>
    Promise<Integer> queryViewCount(CloudantDbUri dbUri, Class<T> entityClass, Object[] startKey, Object[] endKey, String viewName, boolean withHighKey,
                                    boolean descending, Integer limit, Integer skip);
}
