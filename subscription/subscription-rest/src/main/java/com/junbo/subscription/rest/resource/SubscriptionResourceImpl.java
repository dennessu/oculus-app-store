package com.junbo.subscription.rest.resource;

import com.junbo.common.id.SubscriptionId;
import com.junbo.subscription.core.SubscriptionService;
import com.junbo.subscription.spec.model.Subscription;
import com.junbo.subscription.spec.resource.SubscriptionResource;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;


public class SubscriptionResourceImpl implements SubscriptionResource {
    @Autowired
    private SubscriptionService service;

    @Override
    public Promise<Subscription> getSubscription(HttpHeaders httpHeaders, SubscriptionId subscriptionId){
        Subscription entitlement = service.getsubscription(subscriptionId.getValue());
        return Promise.pure(entitlement);
    }

}
