package com.junbo.order.mock

import com.junbo.catalog.spec.model.offer.ItemEntry
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.model.offer.OfferRevisionsGetOptions
import com.junbo.catalog.spec.resource.OfferRevisionResource
import com.junbo.common.id.OfferRevisionId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.BeanParam
import javax.ws.rs.PathParam
import javax.ws.rs.core.Response

/**
* Created by LinYi on 14-2-25.
*/
@CompileStatic
@Component('mockCatalogResource')
@Scope('prototype')
class MockCatalogResource extends BaseMock implements OfferRevisionResource {

    @Override
    Promise<Results<OfferRevision>> getOfferRevisions(@BeanParam OfferRevisionsGetOptions options) {
        Results<OfferRevision> ors = new Results<>()
        ors.items = [generateOfferRevision()] as LinkedList
        return Promise.pure(ors)
    }

    @Override
    Promise<OfferRevision> getOfferRevision(@PathParam('revisionId') OfferRevisionId revisionId) {
        return Promise.pure(generateOfferRevision())
    }

    @Override
    Promise<OfferRevision> createOfferRevision(OfferRevision offerRevision) {
        return null
    }

    @Override
    Promise<OfferRevision> updateOfferRevision(
            @PathParam('revisionId') OfferRevisionId revisionId, OfferRevision offerRevision) {
        return null
    }

    @Override
    Promise<Response> delete(OfferRevisionId revisionId) {

    }

    OfferRevision generateOfferRevision() {
        OfferRevision offer = new OfferRevision(
                ownerId: generateLong(),
                revisionId: generateLong(),
                offerId: generateLong(),
                items: [new ItemEntry(
                        itemId: generateLong(),
                        quantity: 1
                )]
        )
        return offer
    }
}
