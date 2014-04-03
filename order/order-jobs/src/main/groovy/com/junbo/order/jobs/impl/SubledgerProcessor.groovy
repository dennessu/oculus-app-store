package com.junbo.order.jobs.impl

import com.junbo.common.id.UserId
import com.junbo.order.clientproxy.catalog.CatalogFacade
import com.junbo.order.core.SubledgerService
import com.junbo.order.core.impl.common.TransactionHelper
import com.junbo.order.db.entity.enums.SubledgerItemAction
import com.junbo.order.db.entity.enums.SubledgerItemStatus
import com.junbo.order.db.entity.enums.SubledgerStatus
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.SubledgerItem
import com.junbo.order.spec.model.SubledgerParam
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by fzhang on 4/2/2014.
 */
class SubledgerProcessor {

    private final static Logger LOGGER = LoggerFactory.getLogger(SubledgerProcessor)

    TransactionHelper transactionHelper

    CatalogFacade catalogFacade

    SubledgerService subledgerService

    private int processNumLimit

    void setProcessNumLimit(int processNumLimit) {
        this.processNumLimit = processNumLimit
    }

    void processSubledgerItem() {
/*
  get subleger item by item id
     get matching subleger (create one if not exsted, by develper id, item created time, item currency)
     aggregate subleger
     save subleger

* */
    }

    void aggregateToSubledger(Subledger subledger, List<SubledgerItem> items) {
        items.each { SubledgerItem item ->

            if (item.subledgerItemAction == SubledgerItemAction.CHARGE.name()) {
                subledger.totalAmount += item.totalAmount
            }
            if (item.subledgerItemAction == SubledgerItemAction.REFUND.name()) {
                subledger.totalAmount -= item.totalAmount
            }

            item.subledgerId = subledger.subledgerId
            item.status = SubledgerItemStatus.PROCESSED
            subledgerService.updateSubledgerItem(item)
        }

        subledgerService.updateSubledger(subledger)
    }

    Subledger lookupMatchingSubledger(SubledgerItem subledgerItem, Long ownerId) {
        // get
        // catalogFacade.getOffer()
        /*def offer = catalogFacade.getOffer(item.offerId.value).wrapped().get()
        if (offer == null) {  // todo: need also get the retired offer
            LOGGER.error('name=Subledger_Offer_Not_Found, orderItemId={}, offerId={}', item.orderItemId.value,
                    item.offerId.value)
            return
        }

        offer.catalogOffer.ownerId*/
        SubledgerParam param = new SubledgerParam(
              sellerId: new UserId(ownerId),
              status: SubledgerStatus.PENDING,

        )
        return null
    }

    Subledger createSubledger(SubledgerItem subledgerItem, Long ownerId) {

        return null
    }
}
