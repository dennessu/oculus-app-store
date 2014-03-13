/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.clientproxy.impl;

import com.junbo.common.id.UserId;
import com.junbo.entitlement.spec.model.Entitlement;
import com.junbo.entitlement.spec.model.EntitlementSearchParam;
import com.junbo.entitlement.spec.model.PageMetadata;
import com.junbo.entitlement.spec.resource.EntitlementResource;
import com.junbo.rating.clientproxy.EntitlementGateway;
import com.junbo.rating.common.util.Constants;
import com.junbo.rating.spec.error.AppErrors;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by lizwu on 2/27/14.
 */
public class EntitlementGatewayImpl implements EntitlementGateway {
    @Autowired
    private EntitlementResource entitlementResource;

    @Override
    public Set<String> getEntitlements(Long userId, Set<String> groups) {
        return format(retrieve(userId, groups));
    }

    public List<Entitlement> retrieve(Long userId, Set<String> groups) {
        EntitlementSearchParam param = new EntitlementSearchParam();
        param.setGroups(groups);

        PageMetadata pagingOption = new PageMetadata();
        pagingOption.setStart(Constants.DEFAULT_PAGE_START);
        pagingOption.setCount(Constants.DEFAULT_PAGE_SIZE);

        List<Entitlement> results = new ArrayList<Entitlement>();
        while(true) {
            List<Entitlement> entitlements = new ArrayList<Entitlement>();
            try {
                entitlements.addAll(
                        entitlementResource.getEntitlements(
                                new UserId(userId), param, pagingOption).wrapped().get().getCriteria());
            } catch (Exception e) {
                throw AppErrors.INSTANCE.entitlementGatewayError().exception();
            }
            results.addAll(entitlements);
            pagingOption.setStart(pagingOption.getStart() + Constants.DEFAULT_PAGE_SIZE);
            if (entitlements.size() < Constants.DEFAULT_PAGE_SIZE) {
                break;
            }
        }

        return results;
    }

    public Set<String> format(List<Entitlement> entitlements) {
        Set<String> results = new HashSet<String>();
        for (Entitlement entitlement : entitlements) {
            //TODO: implement after entitlement model support to config group & tag
        }

        return results;
    }
}
