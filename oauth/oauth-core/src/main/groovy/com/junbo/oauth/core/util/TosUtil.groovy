/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oauth.core.util

import com.junbo.common.enumid.CountryId
import com.junbo.common.id.TosId
import com.junbo.identity.spec.v1.model.Tos
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.list.TosListOptions
import com.junbo.identity.spec.v1.option.list.UserTosAgreementListOptions
import com.junbo.identity.spec.v1.resource.TosResource
import com.junbo.identity.spec.v1.resource.UserTosAgreementResource
import com.junbo.oauth.common.cache.Cache
import com.junbo.oauth.common.cache.Callable
import com.junbo.oauth.spec.model.Client
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * TosUtil.
 */
@CompileStatic
class TosUtil {
    private Cache tosCache

    private TosResource tosResource

    private UserTosAgreementResource userTosAgreementResource

    @Required
    void setTosCache(Cache tosCache) {
        this.tosCache = tosCache
    }

    @Required
    void setTosResource(TosResource tosResource) {
        this.tosResource = tosResource
    }

    @Required
    void setUserTosAgreementResource(UserTosAgreementResource userTosAgreementResource) {
        this.userTosAgreementResource = userTosAgreementResource
    }

    Set<TosId> getRequiredTos(Client client, User user) {
        Set<TosId> unacceptedTosIds = []
        if (client.requiredTos != null && !client.requiredTos.isEmpty() && user.countryOfResidence != null) {
            List<TosId> tosIds = getTosIds(client, user.countryOfResidence)
            for (TosId tosId : tosIds) {
                UserTosAgreementListOptions options = new UserTosAgreementListOptions(
                        userId: user.getId(),
                        tosId: tosId
                )

                def results = userTosAgreementResource.list(options).get()
                if (results.items.isEmpty()) {
                    unacceptedTosIds.add(tosId)
                }
            }
        }

        return unacceptedTosIds
    }

    private List<TosId> getTosIds(final Client client, final CountryId country) {
        return tosCache.get("$client.clientId:$country.value", new Callable<List<TosId>>() {
            @Override
            List<TosId> execute() {
                final List<TosId> tosId = []
                for (Client.RequiredTos requiredTos : client.requiredTos) {
                    TosListOptions options = new TosListOptions(
                            type: requiredTos.type,
                            title: requiredTos.title,
                            countryId: country,
                            state: 'APPROVED'
                    )
                    def results = tosResource.list(options).get()
                    if (!results.items.isEmpty()) {
                        Tos latest = results.items.get(0)
                        for (Tos tos : results.items) {
                            if (compareVersion(latest.version, tos.version) < 0) {
                                latest = tos
                            }
                        }
                        tosId.add(latest.getId())
                    }
                }

                return tosId
            }

            static int compareVersion(String version1, String version2) {
                double v1 = Double.parseDouble(version1)
                double v2 = Double.parseDouble(version2)
                return Double.compare(v1, v2)
            }
        })
    }
}
