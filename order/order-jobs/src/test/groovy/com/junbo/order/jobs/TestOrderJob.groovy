package com.junbo.order.jobs
import com.junbo.common.id.OrderId
import com.junbo.order.db.entity.enums.OrderStatus
import com.junbo.order.db.repo.OrderRepository
import com.junbo.order.spec.model.Order
import com.junbo.order.spec.model.PageParam
import groovy.transform.CompileStatic
import org.easymock.EasyMock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
/**
 * Created by fzhang on 4/18/2014.
 */
@ContextConfiguration(locations = ['classpath:spring/context-job-test.xml'])
@CompileStatic
class TestOrderJob extends AbstractTestNGSpringContextTests {

    OrderJob orderJob

    long nextId = System.currentTimeMillis()

    @Autowired
    @Qualifier('orderJobAsyncTaskExecutor')
    ThreadPoolTaskExecutor threadPoolTaskExecutor

    OrderRepository orderRepository

    @BeforeMethod
    void beforeTest() {

        orderRepository = EasyMock.createMock(OrderRepository)

        orderJob = new OrderJob(
                threadPoolTaskExecutor: threadPoolTaskExecutor,
                orderRepository: orderRepository,
                orderProcessor: new OrderProcessor() {
                    @Override
                    OrderProcessResult process(Order order) {
                        new OrderProcessResult(success: true)
                    }
                },
                numOfFuturesToTrack: 100,
                pageSizePerShard: 10,
                orderProcessNumLimit: 100000,
                statusToProcess: [OrderStatus.PENDING_CHARGE.name()],
        )
    }

    @Test(enabled = false) // todo : enable the test
    public void testJob() {
        def shards = [0, 1]
        shards.each { Integer shardKey ->
            EasyMock.expect(orderRepository.getOrdersByStatus(
                    EasyMock.eq(shardKey),
                    EasyMock.eq([OrderStatus.PENDING_CHARGE.name()]),
                    EasyMock.eq(true), EasyMock.isA(PageParam))).andReturn(orders(10)).anyTimes()
        }

        EasyMock.replay(orderRepository)
        orderJob.execute()
        EasyMock.verify(orderRepository)
    }

    private List<Order> orders(int n) {
        List<Order> list = []
        for (int i = 0; i < n; ++i) {
            list << new Order(id: new OrderId(nextId++))
        }
        return list
    }
}
