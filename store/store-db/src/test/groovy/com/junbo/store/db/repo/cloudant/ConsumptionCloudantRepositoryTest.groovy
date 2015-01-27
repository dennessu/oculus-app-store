package com.junbo.store.db.repo.cloudant

import com.junbo.common.id.EntitlementId
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

}
