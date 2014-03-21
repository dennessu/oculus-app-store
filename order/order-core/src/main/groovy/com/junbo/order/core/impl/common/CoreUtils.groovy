package com.junbo.order.core.impl.common
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.order.db.entity.enums.ItemType

/**
 * Created by chriszhu on 3/19/14.
 */
class CoreUtils {

    static final String GRANT_ENTITLEMENT = 'GRANT_ENTITLEMENT'
    static final String DELIVER_PHYSICAL_GOODS = 'DELIVER_PHYSICAL_GOODS'
    static final String CREDIT_WALLET = 'CREDIT_WALLET'

    static ItemType getOfferType(Offer offer) {
        Boolean isPhysical = offer.events?.any { com.junbo.catalog.spec.model.offer.Event event ->
            event.actions?.any { com.junbo.catalog.spec.model.offer.Action action ->
                action.type == DELIVER_PHYSICAL_GOODS
            }
        }
        if (isPhysical) { return ItemType.PHYSICAL }
        return ItemType.DIGITAL
    }
}
