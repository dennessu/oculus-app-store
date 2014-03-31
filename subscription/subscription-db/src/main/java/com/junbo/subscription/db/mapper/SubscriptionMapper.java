/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.db.mapper;

import com.junbo.subscription.db.entity.*;
import com.junbo.subscription.spec.model.Subscription;
import com.junbo.subscription.spec.model.SubscriptionEvent;
import com.junbo.subscription.spec.model.SubscriptionEventAction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * subscription mapper.
 */
@Component
public class SubscriptionMapper {
    public Subscription toSubscription(SubscriptionEntity subscriptionEntity) {
        if (subscriptionEntity == null){
            return null;
        }
        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(subscriptionEntity.getSubscriptionId());
        subscription.setUserId(subscriptionEntity.getUserId());
        subscription.setStatus(subscriptionEntity.getStatusId().toString()); //subscriptionEntity.getStatusId()
        return subscription;
    }

    public SubscriptionEntity toSubscriptionEntity(Subscription subscription) {
        SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
        subscriptionEntity.setSubscriptionId(subscription.getSubscriptionId());
        subscriptionEntity.setUserId(subscription.getUserId());
        subscriptionEntity.setItemId(subscription.getOfferId().toString());
        subscriptionEntity.setStatusId(SubscriptionStatus.valueOf(subscription.getStatus()));
        return subscriptionEntity;
    }


    public List<Subscription> toSubscriptionList(List<SubscriptionEntity> subscriptionEntities) {
        List<Subscription> subscriptions = new ArrayList<Subscription>(subscriptionEntities.size());
        for (SubscriptionEntity subscriptionEntity : subscriptionEntities) {
            subscriptions.add(toSubscription(subscriptionEntity));
        }
        return subscriptions;
    }

    public SubscriptionEvent toSubsEvent(SubscriptionEventEntity eventEntity) {
        if (eventEntity == null){
            return null;
        }
        SubscriptionEvent event = new SubscriptionEvent();
        event.setSubscriptionId(eventEntity.getSubscriptionId());
        event.setSubscriptionEventId(eventEntity.getSubsEventId());
        event.setEventStatus(eventEntity.getEventStatusId().toString());
        event.setEventTypeId(eventEntity.getEventTypeId().toString());
        event.setRetryCount(eventEntity.getRetryCount());
        return event;
    }

    public SubscriptionEventEntity toSubscriptionEntity(SubscriptionEvent event) {
        SubscriptionEventEntity eventEntity = new SubscriptionEventEntity();
        eventEntity.setSubscriptionId(event.getSubscriptionId());
        eventEntity.setSubsEventId(event.getSubscriptionEventId());
        eventEntity.setEventTypeId(SubscriptionEventType.valueOf(event.getEventType()));
        eventEntity.setEventStatusId(SubscriptionStatus.valueOf(event.getEventStatus()));
        eventEntity.setRetryCount(event.getRetryCount());
        return eventEntity;
    }

    public SubscriptionEventAction toSubsEventAction(SubscriptionEventActionEntity actionEntity) {
        if (actionEntity == null){
            return null;
        }
        SubscriptionEventAction action = new SubscriptionEventAction();
        action.setSubscriptionId(actionEntity.getSubscriptionId());
        action.setSubscriptionEventId(actionEntity.getSubsEventId());
        action.setSubscriptionActionId(actionEntity.getSubsActionId());
        action.setActionStatus(actionEntity.getActionStatusId().toString());
        action.setActionType(actionEntity.getActionTypeId().toString());
        action.setRequest(actionEntity.getRequest());
        action.setResponse(actionEntity.getResponse());
        return action;
    }

    public SubscriptionEventActionEntity toSubsEventActionEntity(SubscriptionEventAction action) {
        SubscriptionEventActionEntity actionEntity = new SubscriptionEventActionEntity();
        actionEntity.setSubscriptionId(action.getSubscriptionId());
        actionEntity.setSubsEventId(action.getSubscriptionEventId());
        actionEntity.setSubsActionId(action.getSubscriptionActionId());
        actionEntity.setActionStatusId(SubscriptionStatus.valueOf(action.getActionStatus()));
        actionEntity.setActionTypeId(SubscriptionActionType.valueOf(action.getActionType()));
        actionEntity.setRequest(action.getRequest());
        actionEntity.setResponse(action.getResponse());
        return actionEntity;
    }

}
