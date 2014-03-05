package com.junbo.subscription.core.service;

import com.junbo.subscription.core.SubscriptionService;
import com.junbo.subscription.db.dao.SubscriptionDao;
import com.junbo.subscription.db.entity.SubscriptionEntity;
import com.junbo.subscription.spec.model.Subscription;
import com.junbo.subscription.db.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public class SubscriptionServiceImpl implements SubscriptionService {
    public static final String NOT_START = "NOT_START";
    public static final String EXPIRED = "EXPIRED";
    @Autowired
    private SubscriptionDao subscriptionDao;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Override
    public Subscription getsubscription(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.get(subscriptionId);
        if (subscription == null) {
            //throw new AppErrorException();
        }
        return subscription;
    }

    @Override
    @Transactional
    public Long addsubscription(SubscriptionEntity subscriptionEntity) {
        //TODO: check userId and itemId

        if (subscriptionEntity.getType() == null) {
            subscriptionEntity.setType("FREE_MONTHLY");
        }

        Date currentDate = new Date();

        subscriptionEntity.setCreatedBy("DEFAULT");
        subscriptionEntity.setCreatedTime(currentDate);
        subscriptionEntity.setModifiedBy("DEFAULT");
        subscriptionEntity.setModifiedTime(currentDate);

        return subscriptionDao.insert(subscriptionEntity);
    }

}
