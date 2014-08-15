package com.junbo.common.id.util

import com.junbo.common.id.AddressId
import com.junbo.common.id.Id
import com.junbo.common.id.UserId
import com.junbo.common.model.Link
import org.testng.Assert
import org.testng.annotations.Test

/**
 * Created by Zhanxin on 5/15/2014.
 */
class IdUtilTest {
    @Test
    void testFromHref() {
        Link link = new Link(href: '/v1/addresses/6B54FDB4BC9F', id: '6B54FDB4BC9F')
        Id id = IdUtil.fromLink(link)
        Assert.assertTrue(id instanceof AddressId)
        Assert.assertEquals(id.value, 33570816L)
    }

    @Test
    void testToHref() {
        AddressId id = new AddressId(33570816L)
        String href = IdUtil.toHref(id)
        Assert.assertEquals(href, '/v1/addresses/6B54FDB4BC9F')
    }

    //@Test
    void testUserId() {
        UserId id = new UserId(32768L)
        String href = IdUtil.toHref(id)
        Assert.assertEquals(href, '/v1/addresses/6B54FDB4BC9F')
    }
}
