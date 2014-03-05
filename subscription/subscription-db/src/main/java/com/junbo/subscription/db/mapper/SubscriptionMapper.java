package com.junbo.subscription.db.mapper;

import com.junbo.subscription.db.entity.Entity;
import com.junbo.subscription.db.entity.SubscriptionEntity;
import com.junbo.subscription.spec.model.Subscription;
import com.junbo.subscription.spec.model.Model;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SubscriptionMapper {
    public Subscription toSubscription(SubscriptionEntity subscriptionEntity) {
        if (subscriptionEntity == null) return null;
        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(subscriptionEntity.getSubscriptionId());
        subscription.setUserId(subscriptionEntity.getUserId());
        subscription.setStatus(subscriptionEntity.getStatus());
        toModel(subscriptionEntity, subscription);
        return subscription;
    }

    public SubscriptionEntity toSubscriptionEntity(Subscription subscription) {
        SubscriptionEntity subscriptionEntity = new SubscriptionEntity();
        subscriptionEntity.setSubscriptionId(subscription.getSubscriptionId());
        subscriptionEntity.setUserId(subscription.getUserId());

        toEntity(subscription, subscriptionEntity);
        return subscriptionEntity;
    }


    public List<Subscription> toSubscriptionList(List<SubscriptionEntity> subscriptionEntities) {
        List<Subscription> subscriptions = new ArrayList<Subscription>(subscriptionEntities.size());
        for (SubscriptionEntity subscriptionEntity : subscriptionEntities) {
            subscriptions.add(toSubscription(subscriptionEntity));
        }
        return subscriptions;
    }

    private void toModel(Entity entity, Model model) {
        model.setCreatedBy(entity.getCreatedBy());
        model.setCreatedTime(entity.getCreatedTime());
        model.setModifiedBy(entity.getModifiedBy());
        model.setModifiedTime(entity.getModifiedTime());
    }

    private void toEntity(Model model, Entity entity) {
        entity.setCreatedBy(model.getCreatedBy());
        entity.setCreatedTime(model.getCreatedTime());
        entity.setModifiedBy(model.getModifiedBy());
        entity.setModifiedTime(model.getModifiedTime());
    }


}
