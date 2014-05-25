package com.junbo.order.db.repo.facade.impl
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.id.OfferId
import com.junbo.common.id.SubledgerId
import com.junbo.common.id.UserId
import com.junbo.order.db.repo.SubledgerItemRepository
import com.junbo.order.db.repo.SubledgerRepository
import com.junbo.order.db.repo.facade.SubledgerRepositoryFacade
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.SubledgerItem
import com.junbo.order.spec.model.SubledgerParam
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
/**
 * Created by fzhang on 4/11/2014.
 */
@CompileStatic
@Component('subledgerRepositoryFacade')
class SubledgerRepositoryFacadeImpl implements SubledgerRepositoryFacade {

    @Autowired
    @Qualifier('subledgerRepository')
    private SubledgerRepository subledgerRepository;

    @Autowired
    @Qualifier('subledgerItemRepository')
    private SubledgerItemRepository subledgerItemRepository;

    @Override
    Subledger createSubledger(Subledger subledger) {
        return subledgerRepository.create(subledger).get();
    }

    @Override
    Subledger updateSubledger(Subledger subledger) {
        return subledgerRepository.update(subledger).get();
    }

    @Override
    Subledger getSubledger(SubledgerId subledgerId) {
        return subledgerRepository.get(subledgerId).get();
    }

    @Override
    List<Subledger> getSubledgers(SubledgerParam subledgerParam, PageParam pageParam) {
        return subledgerRepository.list(subledgerParam, pageParam).get();
    }

    @Override
    Subledger findSubledger(UserId sellerId, String payoutStatus, OfferId offerId,
                            Date startTime, CurrencyId currency, CountryId country) {
        return subledgerRepository.find(sellerId, payoutStatus, offerId, startTime, currency, country).get();
    }

    @Override
    SubledgerItem createSubledgerItem(SubledgerItem subledgerItem) {
        return subledgerItemRepository.create(subledgerItem).get();
    }

    @Override
    List<SubledgerItem> getSubledgerItem(Object shardKey, String status, PageParam pageParam) {
        return subledgerItemRepository.getByStatus(shardKey, status, pageParam).get();
    }

    @Override
    SubledgerItem updateSubledgerItem(SubledgerItem subledgerItem) {
        return subledgerItemRepository.update(subledgerItem).get();
    }
}
