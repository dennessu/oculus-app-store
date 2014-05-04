package com.junbo.order.mock

import com.junbo.catalog.spec.model.common.Price
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions
import com.junbo.catalog.spec.resource.ItemRevisionResource
import com.junbo.common.id.ItemRevisionId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.BeanParam
import javax.ws.rs.PathParam
import javax.ws.rs.core.Response

/**
 * Created by chriszhu on 4/16/14.
 */
@CompileStatic
@Component('mockItemRevisionResource')
@Scope('prototype')
class MockItemRevisionResource extends BaseMock implements ItemRevisionResource {
    @Override
    Promise<Results<ItemRevision>> getItemRevisions(@BeanParam ItemRevisionsGetOptions options) {
        return Promise.pure(new Results<ItemRevision>(
                items : [generateItemRevision()]
        ))
    }

    @Override
    Promise<ItemRevision> getItemRevision(@PathParam('revisionId') ItemRevisionId revisionId) {
        return Promise.pure(generateItemRevision())
    }

    @Override
    Promise<ItemRevision> createItemRevision(ItemRevision offerRevision) {
        return null
    }

    @Override
    Promise<ItemRevision> updateItemRevision(
            @PathParam('revisionId') ItemRevisionId revisionId, ItemRevision offerRevision) {
        return null
    }

    @Override
    Promise<Response> delete(@PathParam("revisionId") ItemRevisionId revisionId) {
        return Promise.pure(null)
    }

    private generateItemRevision() {
        return new ItemRevision(
                revisionId: generateLong(),
                ownerId: generateLong(),
                itemId: generateLong(),
                msrp: new Price(
                        priceType: 'CUSTOM',
                        prices: ['USD': 9.99G, 'CNY': 19.99G]
                )
        )
    }
}
