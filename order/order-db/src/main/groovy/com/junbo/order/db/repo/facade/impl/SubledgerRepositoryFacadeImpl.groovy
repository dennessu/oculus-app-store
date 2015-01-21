package com.junbo.order.db.repo.facade.impl

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.id.*
import com.junbo.order.db.repo.SubledgerEventRepository
import com.junbo.order.db.repo.SubledgerItemRepository
import com.junbo.order.db.repo.SubledgerRepository
import com.junbo.order.db.repo.facade.SubledgerRepositoryFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.SubledgerEvent
import com.junbo.order.spec.model.SubledgerItem
import com.junbo.order.spec.model.SubledgerParam
import groovy.transform.CompileStatic
import org.hibernate.StaleObjectStateException
import org.springframework.beans.factory.annotation.Required

/**
 * Created by fzhang on 4/11/2014.
 */
@CompileStatic
class SubledgerRepositoryFacadeImpl implements SubledgerRepositoryFacade {

    private SubledgerRepository subledgerRepository;

    private SubledgerItemRepository subledgerItemRepository;

    private SubledgerEventRepository subledgerEventRepository;

    @Required
    void setSubledgerRepository(SubledgerRepository subledgerRepository) {
        this.subledgerRepository = subledgerRepository
    }

    @Required
    void setSubledgerItemRepository(SubledgerItemRepository subledgerItemRepository) {
        this.subledgerItemRepository = subledgerItemRepository
    }

    @Required
    void setSubledgerEventRepository(SubledgerEventRepository subledgerEventRepository) {
        this.subledgerEventRepository = subledgerEventRepository
    }

    @Override
    Subledger createSubledger(Subledger subledger) {
        return subledgerRepository.create(subledger).get();
    }

    @Override
    Subledger updateSubledger(Subledger subledger) {
        try {
            Subledger oldSubledger = subledgerRepository.get(subledger.getId()).get()
            return subledgerRepository.update(subledger, oldSubledger).syncRecover { Throwable ex ->
                if (ex instanceof StaleObjectStateException) {
                    throw AppErrors.INSTANCE.subledgerConcurrentUpdate().exception()
                }
                throw ex
            }.get()
        } catch (StaleObjectStateException ex) {
            throw AppErrors.INSTANCE.orderConcurrentUpdate().exception()
        }
    }

    @Override
    Subledger getSubledger(SubledgerId subledgerId) {
        return subledgerRepository.get(subledgerId).get()
    }

    @Override
    List<Subledger> getSubledgers(SubledgerParam subledgerParam, PageParam pageParam) {
        return subledgerRepository.list(subledgerParam, pageParam).get();
    }

    @Override
    List<Subledger> getSubledgersOrderBySeller(int dataCenterId, int shardId, String payOutStatus, Date startDate, Date endDate, PageParam pageParam) {
        return subledgerRepository.listOrderBySeller(dataCenterId, shardId, payOutStatus, startDate, endDate, pageParam).get()
    }

    @Override
    List<Subledger> getSubledgersByPayouId(PayoutId payoutId, PageParam pageParam) {
        return subledgerRepository.listByPayoutId(payoutId, pageParam).get()
    }

    @Override
    Subledger findSubledger(OrganizationId sellerId, String payoutStatus, OfferId offerId,
                            Date startTime, CurrencyId currency, CountryId country) {
        return subledgerRepository.find(sellerId, payoutStatus, offerId, startTime, currency, country).get()
    }

    @Override
    SubledgerItem createSubledgerItem(SubledgerItem subledgerItem) {
        return subledgerItemRepository.create(subledgerItem).get();
    }

    @Override
    SubledgerItem getSubledgerItem(SubledgerItemId subledgerItemId) {
        return subledgerItemRepository.get(subledgerItemId).get()
    }

    @Override
    List<SubledgerItem> getSubledgerItem(Integer dataCenterId, Object shardKey, String status, OfferId offerId, Date endTime, PageParam pageParam) {
        return subledgerItemRepository.getSubledgerItems(dataCenterId, shardKey, status, offerId, endTime, pageParam).get()
    }

    @Override
    List<SubledgerItem> getSubledgerItemByOrderItemId(OrderItemId orderItemId) {
        return subledgerItemRepository.getByOrderItemId(orderItemId).get()
    }

    @Override
    List<OfferId> getDistinctSubledgerItemOfferIds(Integer dataCenterId, Object shardKey, String status, PageParam pageParam) {
        return subledgerItemRepository.getDistinctOfferIds(dataCenterId, shardKey, status, pageParam).get()
    }

    @Override
    SubledgerItem updateSubledgerItem(SubledgerItem subledgerItem, SubledgerItem oldSubledgerItem) {
        return subledgerItemRepository.update(subledgerItem, oldSubledgerItem).get();
    }

    @Override
    SubledgerEvent createSubledgerEvent(SubledgerEvent subledgerEvent) {
        return subledgerEventRepository.create(subledgerEvent).get()
    }

    @Override
    List<SubledgerEvent> getSubledgerEvents(SubledgerId subledgerId) {
        return subledgerEventRepository.getBySubledgerId(subledgerId).get();
    }
}
