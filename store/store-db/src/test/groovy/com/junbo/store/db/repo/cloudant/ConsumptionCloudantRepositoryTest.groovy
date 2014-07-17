package com.junbo.store.db.repo.cloudant

import com.junbo.common.id.EntitlementId
import com.junbo.common.id.UserId
import com.junbo.store.db.repo.ConsumptionRepository
import com.junbo.store.spec.model.Consumption
import org.apache.commons.lang.RandomStringUtils
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

import javax.annotation.Resource
import java.security.SecureRandom

/**
 * Created by fzhang on 6/20/2014.
 */
@Test
@ContextConfiguration(locations = ["classpath:spring/context-test.xml"])
class ConsumptionCloudantRepositoryTest extends AbstractTestNGSpringContextTests {

    @Resource(name = 'cloudantConsumptionRepository')
    private ConsumptionRepository consumptionRepository

    private def rand = new SecureRandom()

    @Test
    public void testAddAndGet() {
        Consumption consumption = new Consumption(
                userId: new UserId(System.currentTimeMillis()),
                entitlementId: new EntitlementId(RandomStringUtils.randomAlphabetic(10)),
                useCountConsumed: rand.nextInt(),
                sku : RandomStringUtils.randomAlphabetic(10),
                type: RandomStringUtils.randomAlphabetic(5),
                trackingGuid: UUID.randomUUID().toString(),
                packageName: RandomStringUtils.randomAlphabetic(10),
                signatureTimestamp: System.currentTimeMillis(),
        )

        def created = consumptionRepository.create(consumption).get();
        validate(consumption, created)

        def read = consumptionRepository.get(consumption.trackingGuid).get();
        validate(consumption, read)
        assert read.signatureTimestamp == null

    }

    private static void validate(Consumption c1, Consumption c2) {
        assert c1.trackingGuid == c2.trackingGuid
        assert c1.entitlementId == c2.entitlementId
        assert c1.packageName == c2.packageName
        assert c1.useCountConsumed == c2.useCountConsumed
    }
}
