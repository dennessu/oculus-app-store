package com.junbo.order.db.repo.facade.impl

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.id.*
import com.junbo.langur.core.promise.SyncModeScope
import com.junbo.order.db.repo.SubledgerItemRepository
import com.junbo.order.db.repo.SubledgerRepository
import com.junbo.order.db.repo.facade.SubledgerRepositoryFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.Subledger
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

    @Required
    void setSubledgerRepository(SubledgerRepository subledgerRepository) {
        this.subledgerRepository = subledgerRepository
    }

    @Required
    void setSubledgerItemRepository(SubledgerItemRepository subledgerItemRepository) {
        this.subledgerItemRepository = subledgerItemRepository
    }

    @Override
    Subledger createSubledger(Subledger subledger) {
        return SyncModeScope.with {
            return subledgerRepository.create(subledger).syncGet();
        }
    }

    @Override
    Subledger updateSubledger(Subledger subledger) {
        return SyncModeScope.with {
            try {
                Subledger oldSubledger = subledgerRepository.get(subledger.getId()).syncGet()
                return subledgerRepository.update(subledger, oldSubledger).syncRecover { Throwable ex ->
                    if (ex instanceof StaleObjectStateException) {
                        throw AppErrors.INSTANCE.subledgerConcurrentUpdate().exception()
                    }
                    throw ex
                }.syncGet()
            } catch (StaleObjectStateException ex) {
                throw AppErrors.INSTANCE.orderConcurrentUpdate().exception()
            }
        }
    }

    @Override
    Subledger getSubledger(SubledgerId subledgerId) {
        return SyncModeScope.with {
            return subledgerRepository.get(subledgerId).syncGet();
        }
    }

    @Override
    List<Subledger> getSubledgers(SubledgerParam subledgerParam, PageParam pageParam) {
        return SyncModeScope.with {
            return subledgerRepository.list(subledgerParam, pageParam).syncGet();
        }
    }

    @Override
    Subledger findSubledger(OrganizationId sellerId, String payoutStatus, OfferId offerId,
                            Date startTime, CurrencyId currency, CountryId country) {
        return SyncModeScope.with {
            return subledgerRepository.find(sellerId, payoutStatus, offerId, startTime, currency, country).syncGet();
        }
    }

    @Override
    SubledgerItem createSubledgerItem(SubledgerItem subledgerItem) {
        return SyncModeScope.with {
            return subledgerItemRepository.create(subledgerItem).syncGet();
        }
    }

    @Override
    SubledgerItem getSubledgerItem(SubledgerItemId subledgerItemId) {
        return SyncModeScope.with {
            return subledgerItemRepository.get(subledgerItemId).syncGet()
        }
    }

    @Override
    List<SubledgerItem> getSubledgerItem(Integer dataCenterId, Object shardKey, String status, PageParam pageParam) {
        return SyncModeScope.with {
            return subledgerItemRepository.getByStatus(dataCenterId, shardKey, status, pageParam).syncGet()
        }
    }

    @Override
    List<SubledgerItem> getSubledgerItemByOrderItemId(OrderItemId orderItemId) {
        return SyncModeScope.with {
            return subledgerItemRepository.getByOrderItemId(orderItemId).syncGet()
        }
    }

    @Override
    SubledgerItem updateSubledgerItem(SubledgerItem subledgerItem, SubledgerItem oldSubledgerItem) {
        return SyncModeScope.with {
            return subledgerItemRepository.update(subledgerItem, oldSubledgerItem).syncGet();
        }
    }
}
