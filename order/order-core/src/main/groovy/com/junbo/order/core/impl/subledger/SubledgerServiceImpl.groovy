package com.junbo.order.core.impl.subledger

import com.google.common.base.Joiner
import com.junbo.authorization.AuthorizeCallback
import com.junbo.authorization.AuthorizeContext
import com.junbo.authorization.AuthorizeService
import com.junbo.authorization.RightsScope
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.OrderItemId
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.SubledgerId
import com.junbo.common.util.IdFormatter
import com.junbo.order.auth.SubledgerAuthorizeCallbackFactory
import com.junbo.order.core.SubledgerService
import com.junbo.order.core.impl.common.OrderValidator
import com.junbo.order.core.impl.common.ParamUtils
import com.junbo.order.core.impl.common.SubledgerUtils
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.db.repo.facade.SubledgerRepositoryFacade
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.FBPayoutStatusChangeRequest
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.SubledgerAmount
import com.junbo.order.spec.model.SubledgerItem
import com.junbo.order.spec.model.SubledgerParam
import com.junbo.order.spec.model.enums.PayoutStatus
import com.junbo.order.spec.model.enums.SubledgerType
import com.junbo.order.spec.model.enums.SubledgerItemStatus
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.annotation.Resource
import javax.transaction.Transactional
/**
 * Created by fzhang on 4/2/2014.
 */
@Component('orderSubledgerService')
@CompileStatic
class SubledgerServiceImpl implements SubledgerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubledgerServiceImpl)

    private static final int PAGE_SIZE = 100

    private static final Set<SubledgerType> REVERSE_SUBLEDGER_TYPES = [
            SubledgerType.REFUND,
            SubledgerType.DECLINE,
            SubledgerType.CHARGE_BACK,
            SubledgerType.CHARGE_BACK_OTW,
            SubledgerType.CHARGE_BACK_REFUNDED

    ] as Set

    @Resource(name = 'subledgerRepositoryFacade')
    SubledgerRepositoryFacade subledgerRepository

    @Resource(name = 'orderRepositoryFacade')
    OrderRepositoryFacade orderRepository

    @Resource(name = 'orderValidator')
    OrderValidator orderValidator

    @Resource(name = 'orderSubledgerBuilder')
    SubledgerBuilder subledgerBuilder

    @Resource(name = 'orderSubledgerHelper')
    SubledgerHelper subledgerHelper

    @Resource
    AuthorizeService authorizeService

    @Resource
    SubledgerAuthorizeCallbackFactory authorizeCallbackFactory

    @Override
    @Transactional
    Subledger createSubledger(Subledger subledger) {
        subledger.payoutStatus = PayoutStatus.PENDING.name()
        subledger.totalAmount = 0
        subledger.totalPayoutAmount = 0
        subledger.totalQuantity = 0
        subledger.taxAmount = 0
        return subledgerRepository.createSubledger(subledger)
    }

    @Override
    @Transactional
    Subledger updateSubledger(Subledger subledger) {
        orderValidator.notNull(subledger.id, 'subledgerId')

        def persisted = getSubledger(subledger.getId())
        if (persisted == null) {
            throw AppErrors.INSTANCE.subledgerNotFound().exception()
        }

        def callback = authorizeCallbackFactory.create(persisted)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('update')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            if (subledger.payoutStatus != null) {
                persisted.payoutStatus = subledger.payoutStatus
            }
            if (subledger.payoutId != null) {
                persisted.payoutId = subledger.payoutId
            }

            Subledger result = subledgerRepository.updateSubledger(persisted)
            return result
        }
    }

    @Override
    @Transactional
    Subledger getSubledger(SubledgerId subledgerId) {
        orderValidator.notNull(subledgerId, 'subledgerId')
        Subledger subledger = subledgerRepository.getSubledger(subledgerId)
        if (subledger == null) {
            throw AppErrors.INSTANCE.subledgerNotFound().exception()
        }

        def callback = authorizeCallbackFactory.create(subledger)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('read')) {
                throw AppErrors.INSTANCE.subledgerNotFound().exception()
            }
            return subledger
        }
    }

    @Override
    @Transactional
    List<Subledger> getSubledgers(SubledgerParam subledgerParam, PageParam pageParam) {
        def param = ParamUtils.processSubledgerParam(subledgerParam)
        def callback = authorizeCallbackFactory.create(param.sellerId as OrganizationId)
        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('read')) {
                return []
            }

            return subledgerRepository.getSubledgers(param, ParamUtils.processPageParam(pageParam))
        }
    }

    @Override
    @Transactional
    SubledgerItem createSubledgerItem(SubledgerItem subledgerItem) {
        orderValidator.notNull(subledgerItem.totalAmount, 'totalAmount')
        orderValidator.notNull(subledgerItem.orderItem, 'orderItem')
        orderValidator.notNull(subledgerItem.offer, 'offer')

        AuthorizeCallback callback
        if (subledgerItem.subledger != null) {
            callback = authorizeCallbackFactory.create(subledgerItem.subledger as SubledgerId)
        } else {
            callback = authorizeCallbackFactory.create()
        }

        return RightsScope.with(authorizeService.authorize(callback)) {
            if (!AuthorizeContext.hasRights('create-item')) {
                throw AppCommonErrors.INSTANCE.forbidden().exception()
            }

            return innerCreateSubledgerItem(subledgerItem)
        }
    }

    @Override
    @Transactional
    List<SubledgerItem> getSubledgerItemsByOrderItemId(OrderItemId orderItemId) {
        return subledgerRepository.getSubledgerItemByOrderItemId(orderItemId)
    }

    @Override
    @Transactional
    void aggregateSubledgerItem(List<SubledgerItem> subledgerItems) {
        subledgerItems.each { SubledgerItem item ->
            if (item.subledgerCriteria == null) {
                throw new RuntimeException('subledgerCriteria in subleger item could not be null')
            }
            if (!item.subledgerCriteria.equals(subledgerItems[0].subledgerCriteria)) {
                throw new RuntimeException('subledger items should should have same key')
            }
        }

        def subledger = subledgerHelper.getMatchingSubledger(subledgerItems[0].subledgerCriteria)

        if (subledger == null) {
            subledger = subledgerHelper.createSubledger(subledgerItems[0].subledgerCriteria, subledgerItems[0].offer, subledgerItems[0].subledgerKeyInfo)
            subledger = createSubledger(subledger)
            LOGGER.info('name=Subledger_Created, id={}', subledger.getId())
        }

        subledgerItems.each { SubledgerItem subledgerItem ->
            subledgerItem.subledger = subledger.getId()
            subledgerItem.status = SubledgerItemStatus.PROCESSED
            subledger.totalAmount += subledgerItem.totalAmount
            subledger.totalQuantity += subledgerItem.totalQuantity
            subledger.totalPayoutAmount += subledgerItem.totalPayoutAmount
            subledger.taxAmount += subledgerItem.taxAmount

            SubledgerItem oldSubledgerItem = subledgerRepository.getSubledgerItem(subledgerItem.getId())
            subledgerRepository.updateSubledgerItem(subledgerItem, oldSubledgerItem)
        }

        subledgerRepository.updateSubledger(subledger)
    }

    @Override
    @Transactional
    void updateStatusOnFacebookPayoutStatusChange(FBPayoutStatusChangeRequest fbPayoutStatusChangeRequest) {
        long start = System.currentTimeMillis()
        FBPayoutStatusChangeRequest.FBPayoutStatus payoutStatus = null
        try {
            payoutStatus = FBPayoutStatusChangeRequest.FBPayoutStatus.valueOf(fbPayoutStatusChangeRequest.status)
        } catch (IllegalArgumentException) {
            throw AppCommonErrors.INSTANCE.fieldInvalidEnum('status', Joiner.on(',').join(FBPayoutStatusChangeRequest.FBPayoutStatus.values())).exception()
        }

        int totalRead = 0;
        while (true) {
            List<Subledger> subledgers = subledgerRepository.getSubledgersByPayouId(fbPayoutStatusChangeRequest.payoutId, new PageParam(start: totalRead, count: PAGE_SIZE))
            totalRead += subledgers.size()
            subledgers.each { Subledger subledger ->
                updateSubledgerStatus(subledger, payoutStatus, fbPayoutStatusChangeRequest)
            }
            if (subledgers.isEmpty()) {
                break;
            }
        }

        if (totalRead == 0) {
            throw AppErrors.INSTANCE.subledgerNotFoundByPayoutId(fbPayoutStatusChangeRequest.payoutId).exception();
        }
        LOGGER.info('name=UpdateStatusOnFBPayoutChange, latencyInMs={}, numOfSubledgerUpdated={}', System.currentTimeMillis() - start, totalRead);
    }

    @Override
    @Transactional
    List<SubledgerItem> createReverseSubledgerItem(SubledgerType subledgerType, OrderItem orderItem) {
        if (!REVERSE_SUBLEDGER_TYPES.contains(subledgerType)) {
            throw new IllegalArgumentException("invalid subledger type")
        }

        List<SubledgerItem> subledgerItems = getSubledgerItemsByOrderItemId(orderItem.getId())
        List<SubledgerItem> payoutSubledgerItems = subledgerItems.findAll { SubledgerItem item -> item.subledgerType == SubledgerType.PAYOUT.name() }.asList()
        if (payoutSubledgerItems.isEmpty()) {
            LOGGER.error('name=Empty_PayoutSubledgerItems,orderItemId={}', orderItem.getId())
            return []
        }

        List<SubledgerItem> existingReverseSubledgerItems = subledgerItems.findAll {
            SubledgerItem item -> SubledgerUtils.getSubledgerType(item).payoutActionType == SubledgerType.PayoutActionType.DEDUCT
        }.asList()

        SubledgerItem reverseSubledgerItem = buildReverseSubledgerItem(payoutSubledgerItems[0], subledgerType, existingReverseSubledgerItems, orderItem)
        SubledgerAmount amount = SubledgerUtils.getSubledgerAmount(reverseSubledgerItem)
        if (!amount.anyPositive()) {
            return []
        }

        SubledgerItem created = innerCreateSubledgerItem(reverseSubledgerItem)
            LOGGER.info('name=CreateReverseSubledgerItem, orderItemId={}, quantity={}',
                    reverseSubledgerItem.orderItem, reverseSubledgerItem.totalQuantity)
        return [created]
    }

    @Override
    @Transactional
    List<SubledgerItem> createChargebackReverseSubledgerItem(SubledgerType subledgerType, OrderItem orderItem) {
        if (subledgerType != SubledgerType.CHARGE_BACK_REVERSAL && subledgerType != SubledgerType.CHARGE_BACK_REVERSAL_OTW) {
            throw new IllegalArgumentException("invalid subledger type")
        }
        SubledgerType chargeBackSubledgerType = (subledgerType == SubledgerType.CHARGE_BACK_REVERSAL) ? SubledgerType.CHARGE_BACK : SubledgerType.CHARGE_BACK_OTW

        List<SubledgerItem> subledgerItems = getSubledgerItemsByOrderItemId(orderItem.getId())
        List<SubledgerItem> payoutSubledgerItems = subledgerItems.findAll { SubledgerItem item -> item.subledgerType == SubledgerType.PAYOUT.name() }.asList()
        if (payoutSubledgerItems.isEmpty()) {
            LOGGER.error('name=Empty_PayoutSubledgerItems,orderItemId={}', orderItem.getId())
            return []
        }

        boolean hasChargeBackReverseItem = subledgerItems.any { SubledgerItem item ->
            return item.subledgerType == SubledgerType.CHARGE_BACK_REVERSAL.name() || item.subledgerType == SubledgerType.CHARGE_BACK_REVERSAL_OTW.name()
        }
        if (hasChargeBackReverseItem) {
            LOGGER.error("name=ReverseChargeBack_Subledger_Item_Already_Exists, orderId={}, orderItem={}", orderItem.orderId, orderItem.getId())
            return []
        }

        List<SubledgerItem> chargeBackSubledgerItems = subledgerItems.findAll { SubledgerItem item -> item.subledgerType == chargeBackSubledgerType.name() }.asList()
        if (chargeBackSubledgerItems.isEmpty()) {
            LOGGER.error("name=ChargeBack_Items_NotFound, orderId={}, orderItem={}, chargeBackType={}", orderItem.orderId, orderItem.getId(), chargeBackSubledgerType.name())
            return []
        }
        SubledgerAmount amount = new SubledgerAmount()
        chargeBackSubledgerItems.each { SubledgerItem chargeBackSubledgerItem ->
            amount = amount.add(SubledgerUtils.getSubledgerAmount(chargeBackSubledgerItem))
        }
        if (!amount.anyPositive()) {
            return []
        }

        SubledgerItem subledgerItem = SubledgerUtils.buildSubledgerItem(payoutSubledgerItems[0], amount)
        subledgerItem.subledgerType = subledgerType.name()
        SubledgerItem created = innerCreateSubledgerItem(subledgerItem)
        LOGGER.info('name=CreateSubledgerChargeBackReverseItem, orderItemId={}, quantity={}',
                subledgerItem.orderItem, subledgerItem.totalQuantity)
        return [created]
    }

    private SubledgerItem buildReverseSubledgerItem(SubledgerItem payoutSubledgerItem, SubledgerType subledgerType, List<SubledgerItem> existingReverseSubledgerItems, OrderItem reverseOrderItem) {
        SubledgerItem subledgerItem = SubledgerUtils.buildSubledgerItem(payoutSubledgerItem, null)
        subledgerItem.subledgerType = subledgerType.name()

        SubledgerAmount payoutAmount = SubledgerUtils.getSubledgerAmount(payoutSubledgerItem)
        SubledgerAmount reversedAmount = new SubledgerAmount()
        existingReverseSubledgerItems.each { SubledgerItem e ->
            reversedAmount = reversedAmount.add(SubledgerUtils.getSubledgerAmount(e))
        }
        SubledgerAmount remainedAmount = payoutAmount.substract(reversedAmount)
        SubledgerAmount zero = new SubledgerAmount()

        SubledgerAmount finalAmount = getReverseAmountFromOrderItem(reverseOrderItem, payoutSubledgerItem)
        if (subledgerType.payoutActionType == SubledgerType.PayoutActionType.DEDUCT) {
            finalAmount = finalAmount.min(remainedAmount).max(zero)
        }
        SubledgerUtils.updateSubledgerAmount(finalAmount, subledgerItem)
        return subledgerItem
    }

    private SubledgerAmount getReverseAmountFromOrderItem(OrderItem reverseOrderItem, SubledgerItem payoutSubledgerItem) {
        SubledgerAmount amount = new SubledgerAmount()
        amount.totalQuantity = reverseOrderItem.quantity
        amount.totalAmount = reverseOrderItem.totalAmount
        amount.taxAmount = (reverseOrderItem.totalTax == null) ? 0 : reverseOrderItem.totalTax
        amount.totalPayoutAmount = reverseOrderItem.totalAmount * payoutSubledgerItem.totalPayoutAmount / payoutSubledgerItem.totalAmount
        return amount
    }

    private void updateSubledgerStatus(Subledger subledger, FBPayoutStatusChangeRequest.FBPayoutStatus fbPayoutStatus, FBPayoutStatusChangeRequest fbPayoutStatusChangeRequest) {
        LOGGER.info('name=UpdateSubledgerStatus, subledgerId={}', IdFormatter.encodeId(subledger.getId()))
        subledger.payoutStatus = subledgerBuilder.buildSubledgerPayoutStatus(fbPayoutStatus).name()
        subledger = subledgerRepository.updateSubledger(subledger)
        def event = subledgerBuilder.buildSubledgerStatusUpdateEvent(subledger, fbPayoutStatusChangeRequest)
        subledgerRepository.createSubledgerEvent(event)
    }

    private SubledgerItem innerCreateSubledgerItem(SubledgerItem subledgerItem) {
        def start = System.currentTimeMillis()
        def result = subledgerRepository.createSubledgerItem(subledgerItem)
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info('name=CreateSubledgerItem, subledgerItemId={}, orderItemId={}, offerId={}, latency={}',
                    result.id, result.orderItem, result.offer, System.currentTimeMillis() - start)
        }
        return result
    }

}
