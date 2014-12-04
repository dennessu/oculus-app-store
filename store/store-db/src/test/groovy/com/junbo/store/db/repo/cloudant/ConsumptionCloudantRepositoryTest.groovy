package com.junbo.store.db.repo.cloudant

import com.junbo.common.id.ItemId
import com.junbo.common.id.UserId
import com.junbo.store.db.repo.ConsumptionRepository
import com.junbo.store.spec.model.iap.Consumption
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
                user: new UserId(System.currentTimeMillis()),
                useCountConsumed: rand.nextInt(),
                sku : RandomStringUtils.randomAlphabetic(10),
                trackingGuid: UUID.randomUUID().toString(),
                hostItem: new ItemId(RandomStringUtils.randomAlphabetic(10)),
                signatureTimestamp: System.currentTimeMillis(),
        )

        def created = consumptionRepository.create(consumption).get();
        validate(consumption, created)

        def read = consumptionRepository.get(consumption.hostItem, consumption.trackingGuid).get()
        validate(consumption, read)
        assert read.signatureTimestamp == null

        consumptionRepository.delete(consumption.hostItem, consumption.trackingGuid).get()
        read = consumptionRepository.get(consumption.hostItem, consumption.trackingGuid).get()
        assert read == null
    }

    private static void validate(Consumption c1, Consumption c2) {
        assert c1.trackingGuid == c2.trackingGuid
        assert c1.hostItem == c2.hostItem
        assert c1.sku == c2.sku
        assert c1.useCountConsumed == c2.useCountConsumed
    }
}
