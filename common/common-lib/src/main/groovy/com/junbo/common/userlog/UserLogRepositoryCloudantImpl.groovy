/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.userlog

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantSearchResult
import com.junbo.common.id.UserLogId
import com.junbo.common.model.Link
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils

/**
 * Created by x on 9/2/14.
 */
@CompileStatic
public class UserLogRepositoryCloudantImpl extends CloudantClient<UserLog> implements UserLogRepository {
    @Override
    public Promise<UserLog> get(UserLogId id) {
        return super.cloudantGet(id.value)
    }

    @Override
    public Promise<UserLog> create(UserLog model) {
        return super.cloudantPost(model)
    }

    @Override
    Results<UserLog> list(UserLogListOptions options) {
        StringBuilder sb = new StringBuilder()
        String and = ""
        if (options.getUserId() != null) {
            sb.append(and + "userId:" + longToString(options.getUserId().getValue()))
            and = " AND "
        }
        if (options.clientId != null) {
            sb.append(and + "clientId:$options.clientId")
            and = " AND "
        }
        if (options.apiName != null) {
            sb.append(and + "apiName:$options.apiName")
            and = " AND "
        }
        if (options.httpMethod != null) {
            sb.append(and + "httpMethod:$options.httpMethod")
            and = " AND "
        }
        if (options.sequenceId != null) {
            sb.append(and + "sequenceId:$options.sequenceId")
            and = " AND "
        }
        if (options.isOK != null) {
            sb.append(and + "isOK:$options.isOK")
        }

        String sort
        if (options.isDescending == null || options.isDescending == Boolean.TRUE) {
            sort = "-createdTime"
        } else {
            sort = "createdTime"
        }
        if (options.count == null || options.count > 200) {
            options.count = 200
        }

        CloudantSearchResult<UserLog> searchResult = super.searchSync("search", sb.toString(), sort, options.count, options.cursor)
        def result = new Results<UserLog>(
                items: searchResult.results,
                total: searchResult.total,
                next: new Link(
                        id: searchResult.bookmark
                )
        )
        return result
    }

    private String longToString(Long value) {
        return "'" + value + "'"
    }
}
