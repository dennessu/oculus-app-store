package com.junbo.order.mock
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemsGetOptions
import com.junbo.catalog.spec.resource.ItemResource
import com.junbo.common.id.ItemId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.core.Response
/**
 * Created by fzhang on 4/25/2014.
 */
@CompileStatic
@Component('mockCatalogItemResource')
class MockItemResource implements ItemResource {

    @Override
    Promise<Results<Item>> getItems(ItemsGetOptions options) {
        return null
    }

    @Override
    Promise<Item> getItem(String itemId) {
        def item = new Item(
                itemId: itemId,
                type: 'DIGITAL'
        )

        return Promise.pure(item)
    }

    @Override
    Promise<Item> create(Item item) {
        return null
    }

    @Override
    Promise<Item> update(String itemId, Item item) {
        return null
    }

    Promise<Response> delete(String itemId) {
        return null
    }
}
