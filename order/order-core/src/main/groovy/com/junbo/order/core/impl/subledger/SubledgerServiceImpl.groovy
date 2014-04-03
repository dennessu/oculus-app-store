package com.junbo.order.core.impl.subledger
import com.junbo.common.id.SubledgerId
import com.junbo.order.core.SubledgerService
import com.junbo.order.core.impl.common.OrderValidator
import com.junbo.order.core.impl.common.ParamUtils
import com.junbo.order.db.entity.enums.SubledgerItemAction
import com.junbo.order.db.entity.enums.SubledgerItemStatus
import com.junbo.order.db.entity.enums.SubledgerStatus
import com.junbo.order.db.repo.SubledgerRepository
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.SubledgerItem
import com.junbo.order.spec.model.SubledgerParam
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component
/**
 * Created by fzhang on 4/2/2014.
 */
@Component('orderSubledgerService')
@CompileStatic
class SubledgerServiceImpl implements SubledgerService {

    SubledgerRepository subledgerRepository

    OrderValidator orderValidator

    @Override
    Subledger createSubledger(Subledger subledger) {
        subledger.status = SubledgerStatus.PENDING.name()
        return subledgerRepository.createSubledger(subledger)
    }

    @Override
    Subledger updateSubledger(Subledger subledger) {
        orderValidator.notNull(subledger.subledgerId, 'subledgerId')

        def persisted = getSubledger(subledger.subledgerId)
        if (persisted == null) {
            throw AppErrors.INSTANCE.subledgerNotFound().exception()
        }

        persisted.totalAmount = subledger.totalAmount
        persisted.status = subledger.status

        Subledger result = subledgerRepository.updateSubledger(persisted)
        return result;
    }

    @Override
    Subledger getSubledger(SubledgerId subledgerId) {
        orderValidator.notNull(subledgerId, 'subledgerId')
        return subledgerRepository.getSubledger(subledgerId)
    }

    @Override
    List<Subledger> getSubledgers(SubledgerParam subledgerParam, PageParam pageParam) {
        return subledgerRepository.getSubledgers(ParamUtils.processSubledgerParam(subledgerParam),
            ParamUtils.processPageParam(pageParam))
    }

    @Override
    SubledgerItem createSubledgerItem(SubledgerItem subledgerItem) {
        orderValidator.notNull(subledgerItem.totalAmount, 'totalAmount')
        orderValidator.notNull(subledgerItem.orderItemId, 'orderItemId')
        orderValidator.notNull(subledgerItem.offerId, 'offerId')
        orderValidator.notNull(subledgerItem.subledgerItemAction, 'subledgerItemAction').
                validEnumString(subledgerItem.subledgerItemAction, 'subledgerItemAction', SubledgerItemAction)
        orderValidator.validEnumString(subledgerItem.status, 'status', SubledgerItemStatus)

        return subledgerRepository.createSubledgerItem(subledgerItem)
    }
}
