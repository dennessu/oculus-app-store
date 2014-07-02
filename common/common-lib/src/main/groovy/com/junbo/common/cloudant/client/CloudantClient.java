/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.cloudant.client;

import com.junbo.common.cloudant.CloudantEntity;
import com.junbo.common.cloudant.model.CloudantQueryResult;
import com.junbo.langur.core.promise.Promise;

import java.util.List;

/**
 * CloudantClient.
 */
public interface CloudantClient {
    <T extends CloudantEntity>
    Promise<T> cloudantPost(CloudantDbUri dbUri, Class<T> entityClass, T entity);

    <T extends CloudantEntity>
    Promise<T> cloudantGet(CloudantDbUri dbUri, Class<T> entityClass, String id);

    <T extends CloudantEntity>
    Promise<T> cloudantPut(CloudantDbUri dbUri, Class<T> entityClass, T entity);

    <T extends CloudantEntity>
    Promise<Void> cloudantDelete(CloudantDbUri dbUri, Class<T> entityClass, T entity);

    <T extends CloudantEntity>
    Promise<List<T>> cloudantGetAll(CloudantDbUri dbUri, Class<T> entityClass, Integer limit, Integer skip, boolean descending);

    <T extends CloudantEntity>
    Promise<CloudantQueryResult> queryView(CloudantDbUri dbUri, Class<T> entityClass, String viewName,
                                           String key, String startKey, String endKey,
                                           Integer limit, Integer skip, boolean descending, boolean includeDocs);

    <T extends CloudantEntity>
    Promise<CloudantQueryResult> search(CloudantDbUri dbUri, Class<T> entityClass, String searchName,
                                        String queryString, Integer limit, String bookmark, boolean includeDocs);
}
