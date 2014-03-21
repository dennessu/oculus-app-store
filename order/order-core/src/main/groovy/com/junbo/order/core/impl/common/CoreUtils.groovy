package com.junbo.order.core.impl.common
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.order.db.entity.enums.ItemType
/**
 * Created by chriszhu on 3/19/14.
 */
class CoreUtils {

    static final String OFFER_ITEM_TYPE_PHYSICAL = 'PHYSICAL'

    static ItemType getOfferType(Offer offer) {
        // TODO support bundle type
        Boolean hasPhysical = offer.items?.any { Item item ->
            item.type == OFFER_ITEM_TYPE_PHYSICAL
        }
        if (hasPhysical) { return ItemType.PHYSICAL }
        return ItemType.DIGITAL
    }
}
