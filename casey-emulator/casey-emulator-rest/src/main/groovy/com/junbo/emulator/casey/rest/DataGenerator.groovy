package com.junbo.emulator.casey.rest
import com.junbo.common.id.UserId
import com.junbo.common.util.IdFormatter
import com.junbo.store.spec.model.external.casey.CaseyAggregateRating
import com.junbo.store.spec.model.external.casey.CaseyLink
import com.junbo.store.spec.model.external.casey.CaseyReview
import groovy.transform.CompileStatic
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Component

import javax.annotation.Resource
import java.security.SecureRandom
/**
 * The DataGenerator class.
 */
@CompileStatic
@Component('caseyEmulatorDataGenerator')
class DataGenerator {

    Random random = new SecureRandom()

    @Resource(name = 'caseyEmulatorDataRepository')
    CaseyEmulatorDataRepository caseyEmulatorDataRepository

    List<CaseyAggregateRating> generateCaseyAggregateRating(String itemId) {
        List<CaseyAggregateRating> ratings = []
        if (random.nextInt(10) != 0) {
            ratings << generateCaseyAggregateRating(itemId, CaseyReview.RatingType.quality.name())
        }
        if (random.nextInt(10) != 0) {
            ratings << generateCaseyAggregateRating(itemId, CaseyReview.RatingType.comfort.name())
        }
        return ratings
    }

    CaseyAggregateRating generateCaseyAggregateRating(String itemId, String type) {
        CaseyAggregateRating aggregateRating = new CaseyAggregateRating(
                average: (random.nextInt(10001) / 100.0) as Double,
                sum: random.nextInt(100000) as long,
                count: random.nextInt(100000) as long,
                maximum: random.nextInt(100000) as int,
                minimum: random.nextInt(100000) as int,
                variance: (random.nextInt(10001) / 100.0) as Double,
                resourceType: 'item',
                resourceId: itemId,
                type: type,
                self: new CaseyLink(id: UUID.randomUUID().toString()),
        )
        aggregateRating.histogram = new Long[10]
        for (int i = 0;i < aggregateRating.histogram.length;++i) {
            aggregateRating.histogram[i] = random.nextInt(1000)
        }
        return aggregateRating
    }

    CaseyReview generateCaseyReview(String resourceType, String resourceId, UserId userId) {
        CaseyReview caseyReview = new CaseyReview(
                review: RandomStringUtils.randomAlphabetic(100),
                reviewTitle: RandomStringUtils.randomAlphabetic(100),
                resourceType: resourceType,
                resource: new CaseyLink(id : resourceId),
                postedDate: new Date(),
                user:  userId == null ? null : new CaseyLink(id : IdFormatter.encodeId(userId)),
                country: 'US',
                locale: 'en_US',
                self: new CaseyLink(id : UUID.randomUUID().toString()),
                ratings: [new CaseyReview.Rating(type: CaseyReview.RatingType.quality.name(), score: random.nextInt(101)),
                          new CaseyReview.Rating(type: CaseyReview.RatingType.comfort.name(), score: random.nextInt(101))]
        )
        return caseyReview
    }
}
