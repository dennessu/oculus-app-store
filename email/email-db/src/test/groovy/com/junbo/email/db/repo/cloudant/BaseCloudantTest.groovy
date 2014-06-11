package com.junbo.email.db.repo.cloudant

import com.junbo.sharding.IdGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests

/**
 * BaseCloudantTest Class.
 */
@ContextConfiguration(locations = ['classpath:spring/context-test.xml'])
class BaseCloudantTest extends AbstractTestNGSpringContextTests {
    @Autowired
    @Qualifier("oculus48IdGenerator")
    protected IdGenerator idGenerator
}
