package com.junbo.order.mock
import com.junbo.catalog.spec.model.offer.ItemEntry
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.model.offer.OfferRevisionGetOptions
import com.junbo.catalog.spec.model.offer.OfferRevisionsGetOptions
import com.junbo.catalog.spec.resource.OfferRevisionResource
import com.junbo.common.id.OrganizationId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.BeanParam
import javax.ws.rs.PathParam
import javax.ws.rs.core.Response
/**
 * Created by chriszhu on 7/3/14.
 */
@CompileStatic
@Component('mockOfferRevisionResource')
@Scope('prototype')
class MockOfferRevisionResource extends BaseMock implements OfferRevisionResource {
    @Override
    Promise<Results<OfferRevision>> getOfferRevisions(@BeanParam OfferRevisionsGetOptions options) {
        Results<OfferRevision> ors = new Results<>()
        ors.items = [generateOfferRevision(options.offerIds[0])] as LinkedList
        return Promise.pure(ors)
    }

    @Override
    Promise<OfferRevision> getOfferRevision(@PathParam('revisionId') String revisionId,
                                            @BeanParam OfferRevisionGetOptions options) {
        return Promise.pure(generateOfferRevision(revisionId))
    }

    @Override
    Promise<OfferRevision> createOfferRevision(OfferRevision offerRevision) {
        return null
    }

    @Override
    Promise<OfferRevision> updateOfferRevision(
            @PathParam('revisionId') String revisionId, OfferRevision offerRevision) {
        return null
    }

    @Override
    Promise<Response> delete(String revisionId) {

    }

    OfferRevision generateOfferRevision(String revisionId) {
        OfferRevision offer = new OfferRevision(
                ownerId: new OrganizationId(generateLong()),
                revisionId: revisionId,
                offerId: revisionId,
                items: [new ItemEntry(
                        itemId: generateString(),
                        quantity: 1
                )]
        )
        return offer
    }
}
