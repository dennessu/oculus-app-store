package com.junbo.order.clientproxy
import com.junbo.order.clientproxy.common.FacadeBuilder
import com.junbo.order.clientproxy.common.TestBuilder
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.testng.annotations.Test
/**
 * Created by chriszhu on 4/25/14.
 */
@CompileStatic
@TypeChecked
class FacadeBuilderTest extends BaseTest {

    @Test
    void testRatingRequestBuilder() {
        def order = TestBuilder.buildOrderRequest()
        order.honoredTime = new Date()
        def ratingRequest = FacadeBuilder.buildRatingRequest(TestBuilder.buildOrderRequest())
        // def time = DatatypeConverter.parseDateTime(ratingRequest.time).getTime()
        assert ratingRequest.time == null
    }
}

