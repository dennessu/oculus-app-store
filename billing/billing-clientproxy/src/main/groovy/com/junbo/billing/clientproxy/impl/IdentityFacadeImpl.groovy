/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl

import com.junbo.common.id.UserId
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.json.IdPathParamTranscoder
import com.junbo.common.json.JsonMessageTranscoder
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.json.QueryParamTranscoderImpl
import com.junbo.identity.spec.v1.model.Address
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.identity.spec.v1.resource.proxy.UserPersonalInfoResourceClientProxy
import com.junbo.identity.spec.v1.resource.proxy.UserResourceClientProxy
import com.junbo.langur.core.promise.Promise
import com.junbo.billing.clientproxy.IdentityFacade
import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.AsyncHttpClientConfigBean
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

/**
 * Created by xmchen on 14-2-20.
 */
@CompileStatic
class IdentityFacadeImpl implements IdentityFacade {

    @Autowired
    @Qualifier(value='billingAsyncHttpClient')
    private final AsyncHttpClient asyncHttpClient

    private String url

    void setUrl(String url) {
        this.url = url
    }

    IdentityFacadeImpl() {
        if (asyncHttpClient == null) {
            asyncHttpClient = new AsyncHttpClient(new AsyncHttpClientConfigBean())
        }
    }

    @Override
    Promise<User> getUser(Long userId) {
        return new UserResourceClientProxy(asyncHttpClient, new JsonMessageTranscoder(),
                new IdPathParamTranscoder(), new QueryParamTranscoderImpl(), url).get(
                new UserId(userId), new UserGetOptions())
    }

    @Override
    Promise<Address> getAddress(Long addressId) {
        return new UserPersonalInfoResourceClientProxy(asyncHttpClient, new JsonMessageTranscoder(),
                new IdPathParamTranscoder(), new QueryParamTranscoderImpl(), url).get(
                new UserPersonalInfoId(addressId), new UserPersonalInfoGetOptions()).then { UserPersonalInfo info ->
            if (info == null || !info.type.equalsIgnoreCase('address')) {
                return Promise.pure(null)
            }

            try {
                Address address = ObjectMapperProvider.instance().treeToValue(info.value, Address)
                return Promise.pure(address)
            } catch (Exception ex) {
                return Promise.pure(null)
            }
        }

    }
}