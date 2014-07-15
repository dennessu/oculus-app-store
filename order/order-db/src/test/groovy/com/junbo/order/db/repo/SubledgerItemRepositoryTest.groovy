package com.junbo.order.db.repo
import com.junbo.common.id.OrderItemId
import com.junbo.common.id.SubledgerItemId
import com.junbo.order.db.BaseTest
import com.junbo.order.db.common.TestHelper
import com.junbo.order.spec.model.SubledgerItem
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.testng.annotations.Test

/**
 * Created by fzhang on 7/10/2014.
 */
class SubledgerItemRepositoryTest extends BaseTest  {

    @Autowired(required = true)
    @Qualifier('sqlSubledgerItemRepository')
    private SubledgerItemRepository sqlSubledgerItemRepository

    @Autowired(required = true)
    @Qualifier('cloudantSubledgerItemRepository')
    private SubledgerItemRepository cloudantSubledgerItemRepository

    @Test
    public void testGetByOrderItemIdSql() {
        innerTestGetByOrderItemId(sqlSubledgerItemRepository)
    }

    @Test
    public void testGetByOrderItemIdCloudant() {
        innerTestGetByOrderItemId(cloudantSubledgerItemRepository)
    }

    private void innerTestGetByOrderItemId(SubledgerItemRepository subledgerItemRepository) {
        Long orderItemId = generateId()
        for (int i = 0; i < 3; ++i) {
            SubledgerItem item = TestHelper.generateSubledgerItem(new OrderItemId(orderItemId))
            item.id = new SubledgerItemId(generateId())
            subledgerItemRepository.create(item).testGet()
        }
        List<SubledgerItem> result = subledgerItemRepository.getByOrderItemId(new OrderItemId(orderItemId)).testGet()
        assert result.size() == 3
        result.each { SubledgerItem s ->
            if (s == null) {
                int a = 2
            }
            assert s.orderItem == new OrderItemId(orderItemId)
        }
    }
}
