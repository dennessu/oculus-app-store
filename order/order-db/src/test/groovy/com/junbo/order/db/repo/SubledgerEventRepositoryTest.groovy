package com.junbo.order.db.repo

import com.junbo.common.id.SubledgerEventId
import com.junbo.common.id.SubledgerId
import com.junbo.order.db.BaseTest
import com.junbo.order.db.common.TestHelper
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.SubledgerEvent
import com.junbo.order.spec.model.enums.SubledgerActionType
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.testng.Assert
import org.testng.annotations.Test

/**
 * Created by fzhang on 1/19/2015.
 */
@CompileStatic
class SubledgerEventRepositoryTest extends BaseTest {

    @Autowired(required = true)
    @Qualifier('sqlSubledgerEventRepository')
    SubledgerEventRepository sqlRepository

    @Autowired(required = true)
    @Qualifier('cloudantSubledgerEventRepository')
    SubledgerEventRepository cloudantRepository

    @Test
    void testSubledgerEventSql() {
        SubledgerId subledgerId = new SubledgerId(TestHelper.generateId())
        List<SubledgerEvent> events = [
                TestHelper.generateSubledgerEvent(subledgerId),
                TestHelper.generateSubledgerEvent(subledgerId),
                TestHelper.generateSubledgerEvent(subledgerId)
        ]

        for (int i = 0;i < events.size();++i) {
            events[i] = sqlRepository.create(events[i]).get()
        }

        List<SubledgerEvent> read = sqlRepository.getBySubledgerId(subledgerId).get()
        Assert.assertEquals(read.size(), events.size())
        events.each { SubledgerEvent event ->
            SubledgerEvent actual = read.find {SubledgerEvent e -> e.getId() == event.getId()}
            assertSubledgerEventEquals(actual, event)
        }
    }

    @Test
    void testSubledgerEventCloudant() {
        SubledgerId subledgerId = new SubledgerId(TestHelper.generateId())
        List<SubledgerEvent> events = [
                TestHelper.generateSubledgerEvent(subledgerId),
                TestHelper.generateSubledgerEvent(subledgerId),
                TestHelper.generateSubledgerEvent(subledgerId)
        ]

        for (int i = 0;i < events.size();++i) {
            events[i].setId(new SubledgerEventId(idGenerator.nextId(SubledgerEventId, subledgerId.value)))
            events[i] = cloudantRepository.create(events[i]).get()
        }

        List<SubledgerEvent> read = cloudantRepository.getBySubledgerId(subledgerId).get()
        Assert.assertEquals(read.size(), events.size())
        events.each { SubledgerEvent event ->
            SubledgerEvent actual = read.find {SubledgerEvent e -> e.getId() == event.getId()}
            assertSubledgerEventEquals(actual, event)
        }
    }

    void assertSubledgerEventEquals(SubledgerEvent actual, SubledgerEvent expected) {
        Assert.assertEquals(actual.action, expected.action)
        Assert.assertEquals(actual.getId(), expected.getId())
        Assert.assertEquals(actual.subledger, expected.subledger)
        Assert.assertEquals(actual.status, expected.status)
        Assert.assertEquals(actual.properties, expected.properties)
    }

}
