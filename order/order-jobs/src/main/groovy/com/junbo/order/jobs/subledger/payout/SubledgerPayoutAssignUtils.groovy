package com.junbo.order.jobs.subledger.payout
import com.junbo.common.id.PayoutId
import com.junbo.configuration.topo.DataCenters
import com.junbo.configuration.topo.model.DataCenter
import com.junbo.order.clientproxy.TransactionHelper
import com.junbo.order.db.repo.facade.SubledgerRepositoryFacade
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.enums.PayoutStatus
import com.junbo.order.spec.model.enums.SubledgerType
import com.junbo.sharding.IdGenerator
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

import javax.annotation.Resource
/**
 * The SubledgerPayoutAssignJob class.
 */
@Component('order.SubledgerPayoutIdAssignUtils')
class SubledgerPayoutAssignUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubledgerPayoutAssignUtils)

    private int pageSize = 100

    @Autowired
    @Qualifier('oculus48IdGenerator')
    private IdGenerator idGenerator

    @Resource(name = 'subledgerRepositoryFacade')
    SubledgerRepositoryFacade subledgerRepository

    @Resource(name = 'orderTransactionHelper')
    TransactionHelper transactionHelper

    public void execute(int dcId, Date startDate, Date endDate) {
        DataCenter dataCenter = DataCenters.instance().getDataCenter(dcId)
        for (int shardId = 0; shardId < dataCenter.numberOfShard; ++shardId) {
            execute(dcId, shardId, startDate, endDate)
        }
    }

    public void execute(int dcId, int shardId, Date startDate, Date endDate) {
        long start = System.currentTimeMillis()
        LOGGER.info('name=StartSubledgerPayoutIdAssign, dcId={}, shardId={}, startDate={}, endDate={}', dcId,
                shardId, startDate, endDate);

        int pageStart = 0
        List<Subledger> subledgersWithSameSeller = [] as ArrayList
        while (true) {
            List<Subledger> subledgers = null
            transactionHelper.executeInNewTransaction {
                subledgers = subledgerRepository.getSubledgersOrderBySeller(dcId, shardId, PayoutStatus.PENDING.name(), startDate, endDate,
                        new PageParam(start: pageStart, count: pageSize))
            }

            subledgers.each { Subledger subledger ->
                String fbPayoutOrgId = Utils.getFbPayoutOrgId(subledger)
                if (StringUtils.isEmpty(fbPayoutOrgId)) {
                    LOGGER.error('name=Missing_fbPayoutOrgId_In_Subledger, subledgerId={}', subledger.getId())
                    return
                }
                if (subledgersWithSameSeller.isEmpty() || subledgersWithSameSeller[0].seller == subledger.seller) {
                    subledgersWithSameSeller << subledger
                } else {
                    assignPayout(subledgersWithSameSeller)
                    subledgersWithSameSeller.clear()
                    subledgersWithSameSeller << subledger
                }
            }

            if (subledgers.size() == 0) {
                break
            }
            pageStart += subledgers.size()
        }
        assignPayout(subledgersWithSameSeller)

        LOGGER.info('name=EndSubledgerPayoutIdAssign, dcId={}, shardId={}, startDate={}, endDate={}, latencyInSeconds={}', dcId,
                shardId, startDate, endDate, (System.currentTimeMillis() - start) / 1000);
    }

    /**
     * Assign payout to subledgers. Subledger with same seller & fbPayoutOrgId should share the same payoutId.
     * @param subledgersWithSameSeller
     */
    void assignPayout(List<Subledger> subledgersWithSameSeller) {
        if (subledgersWithSameSeller.isEmpty()) {
            return
        }

        Map<String, PayoutId> payoutIdMap = [:] // fbPayoutOrgId -> payoutId
        subledgersWithSameSeller.each { Subledger subledger ->
            String fbPayoutOrgId = Utils.getFbPayoutOrgId(subledger)
            if (fbPayoutOrgId != null && subledger.payoutId != null && payoutIdMap[fbPayoutOrgId] == null) {
                payoutIdMap[fbPayoutOrgId] = subledger.payoutId
            }
        }

        subledgersWithSameSeller.each { Subledger subledger ->
            SubledgerType subledgerType = SubledgerType.valueOf(subledger.subledgerType)
            if (subledgerType.payoutActionType == SubledgerType.PayoutActionType.NONE) { // subledgers that don't affect payout
                subledger.payoutStatus = PayoutStatus.COMPLETED.name()
            } else if (subledger.totalPayoutAmount == BigDecimal.ZERO) { // zero payout amount subledger
                subledger.payoutStatus = PayoutStatus.COMPLETED.name()
            } else if (subledger.payoutId == null) {
                String fbPayoutOrgId = Utils.getFbPayoutOrgId(subledger)
                if (payoutIdMap[fbPayoutOrgId] == null) { // assign payout
                    payoutIdMap[fbPayoutOrgId] = new PayoutId(idGenerator.nextId(subledgersWithSameSeller[0].seller.value))
                }
                subledger.payoutId = payoutIdMap[fbPayoutOrgId]
                subledger.payoutStatus = PayoutStatus.PENDING.name()
            }
            transactionHelper.executeInNewTransaction {
                subledgerRepository.updateSubledger(subledger)
            }
        }
    }

}
