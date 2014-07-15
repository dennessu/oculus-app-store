package com.junbo.order.db.repo
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.id.SubledgerId
import com.junbo.order.db.BaseTest
import com.junbo.order.db.common.TestHelper
import com.junbo.order.spec.model.enums.PayoutStatus
import com.junbo.order.db.repo.cloudant.SubledgerRepositoryCloudantImpl
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.SubledgerParam
import com.junbo.sharding.IdGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.testng.annotations.Test
/**
 * Created by fzhang on 5/29/2014.
 */
class SubledgerRepositoryCloudantTest extends BaseTest {

    @Autowired
    @Qualifier('oculus48IdGenerator')
    private IdGenerator idGenerator

    @Autowired
    @Qualifier('cloudantSubledgerRepository')
    private SubledgerRepositoryCloudantImpl subledgerRepositoryCloudant

    @Test
    public void testFind() {
        Date startTime = new Date();
        def subledger = TestHelper.generateSubledger()
        def sellerId = subledger.seller
        subledger.id = new SubledgerId(idGenerator.nextId())
        subledger.startTime = startTime;

        def created = subledgerRepositoryCloudant.create(subledger).testGet()
        assert created.currency == subledger.currency
        def find = subledgerRepositoryCloudant.find(sellerId, PayoutStatus.COMPLETED.name(),
                subledger.offer, startTime, subledger.currency,
            subledger.country).testGet()
        assert created.id == find.id

        find = subledgerRepositoryCloudant.find(sellerId, PayoutStatus.COMPLETED.name(),
                subledger.offer, new Date(startTime.getTime() + 100000), subledger.currency,
                subledger.country).testGet()
        assert find == null

        find = subledgerRepositoryCloudant.find(sellerId, PayoutStatus.COMPLETED.name(),
                subledger.offer, startTime, new CurrencyId('A'),
                subledger.country).testGet()
        assert find == null
    }

    @Test
    public void testList() {
        Date startTime = new Date();
        def sample = TestHelper.generateSubledger()

        for (int i = 0;i < 8; ++i) {
            Date time = new Date(startTime.time + i * 1000)
            for (int j = 0; j < 2; ++j) {
                def subledger = TestHelper.generateSubledger()
                subledger.id = new SubledgerId(idGenerator.nextId())
                subledger.startTime = time
                subledger.payoutStatus = sample.payoutStatus
                subledger.seller = sample.seller
                subledgerRepositoryCloudant.create(subledger).testGet()
            }

        }

        assert subledgerRepositoryCloudant.list(new SubledgerParam(sellerId: sample.seller, payOutStatus: 'NotExist'),
            null).testGet().isEmpty()

        assert subledgerRepositoryCloudant.list(
                new SubledgerParam(sellerId: sample.seller, payOutStatus: sample.payoutStatus),
                null).testGet().size() == 16

        assert subledgerRepositoryCloudant.list(
                new SubledgerParam(sellerId: sample.seller, payOutStatus: sample.payoutStatus),
                new PageParam(start: 14)).testGet().size() == 2

        assert subledgerRepositoryCloudant.list(
                new SubledgerParam(sellerId: sample.seller, payOutStatus: sample.payoutStatus),
                new PageParam(start: 0, count: 5)).testGet().size() == 5

        assert subledgerRepositoryCloudant.list(
                new SubledgerParam(sellerId: sample.seller, payOutStatus: sample.payoutStatus,
                        fromDate: new Date(startTime.time + 2 * 1000)), null
                ).testGet().size() == 12

        assert subledgerRepositoryCloudant.list(
                new SubledgerParam(sellerId: sample.seller, payOutStatus: sample.payoutStatus,
                        toDate: new Date(startTime.time + 2 * 1000)), null
        ).testGet().size() == 4

        assert subledgerRepositoryCloudant.list(
                new SubledgerParam(sellerId: sample.seller, payOutStatus: sample.payoutStatus,
                      fromDate: new Date(startTime.time + 3 * 1000), toDate: new Date(startTime.time + 3 * 1000)), null
        ).testGet().size() == 0

        assert subledgerRepositoryCloudant.list(
                new SubledgerParam(sellerId: sample.seller, payOutStatus: sample.payoutStatus,
                        fromDate: new Date(startTime.time + 3 * 1000), toDate: new Date(startTime.time + 5 * 1000)), null
        ).testGet().size() == 4

        subledgerRepositoryCloudant.list(
                new SubledgerParam(sellerId: sample.seller, payOutStatus: sample.payoutStatus,
                        fromDate: new Date(startTime.time + 3 * 1000), toDate: new Date(startTime.time + 5 * 1000)), null
        ).testGet().each { it
            assert (long) (it.startTime.getTime() / 1000) >=
                    (long) (new Date(startTime.time + 3 * 1000).getTime() / 1000)
            assert (long) (it.startTime.getTime() / 1000) <
                    (long) (new Date(startTime.time + 5 * 1000).getTime() / 1000)
        }
    }
}
