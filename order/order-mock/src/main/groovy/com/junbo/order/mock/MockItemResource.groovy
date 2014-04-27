package com.junbo.order.mock
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemsGetOptions
import com.junbo.catalog.spec.resource.ItemResource
import com.junbo.common.id.ItemId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.core.Response
/**
 * Created by fzhang on 4/25/2014.
 */
@Component('mockCatalogItemResource')
@Scope('prototype')
class MockItemResource implements ItemResource {

    @Override
    Promise<Results<Item>> getItems(ItemsGetOptions options) {
        return null
    }

    @Override
    Promise<Item> getItem(ItemId itemId) {
        def item = new Item(
                itemId: itemId.value,
                type: 'DIGITAL'
        )

        return Promise.pure(item)
    }

    @Override
    Promise<Item> create(Item item) {
        return null
    }

    @Override
    Promise<Item> update(ItemId itemId, Item item) {
        return null
    }

    Promise<Response> delete(ItemId itemId) {
        return null
    }
}
