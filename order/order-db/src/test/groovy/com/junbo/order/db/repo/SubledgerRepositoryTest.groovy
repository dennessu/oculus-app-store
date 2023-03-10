package com.junbo.order.db.repo
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.PayoutId
import com.junbo.common.id.SubledgerId
import com.junbo.order.db.BaseTest
import com.junbo.order.db.common.TestHelper
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.enums.PayoutStatus
import com.junbo.order.db.repo.cloudant.SubledgerRepositoryCloudantImpl
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.SubledgerParam
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.testng.Assert
import org.testng.annotations.Test
/**
 * Created by fzhang on 5/29/2014.
 */
class SubledgerRepositoryTest extends BaseTest {

    @Autowired
    @Qualifier('oculus48IdGenerator')
    private IdGenerator idGenerator

    @Autowired
    @Qualifier('cloudantSubledgerRepository')
    private SubledgerRepositoryCloudantImpl subledgerRepositoryCloudant

    @Autowired
    @Qualifier('sqlSubledgerRepository')
    private SubledgerRepository subledgerRepositorySql

    @Test(enabled = false) // disabled since find is not supported on cloudant
    public void testFindCloudant() {
        Date startTime = new Date();
        def subledger = TestHelper.generateSubledger()
        def sellerId = subledger.seller
        subledger.id = new SubledgerId(idGenerator.nextId())
        subledger.startTime = startTime;

        def created = subledgerRepositoryCloudant.create(subledger).get()
        assert created.currency == subledger.currency
        def find = subledgerRepositoryCloudant.find(sellerId, PayoutStatus.COMPLETED.name(),
                subledger.item, startTime, subledger.key, subledger.currency,
            subledger.country).get()
        assert created.id == find.id

        find = subledgerRepositoryCloudant.find(sellerId, PayoutStatus.COMPLETED.name(),
                subledger.item, new Date(startTime.getTime() + 100000), subledger.key, subledger.currency,
                subledger.country).get()
        assert find == null

        find = subledgerRepositoryCloudant.find(sellerId, PayoutStatus.COMPLETED.name(),
                subledger.item, startTime, subledger.key, new CurrencyId('A'),
                subledger.country).get()

        subledgerRepositoryCloudant.delete(subledger.getId())
        assert find == null
    }

    @Test
    public void testListCloudant() {
        Date startTime = new Date();
        def sample = TestHelper.generateSubledger()
        List<Subledger> created = []
        for (int i = 0;i < 8; ++i) {
            Date time = new Date(startTime.time + i * 1000)
            for (int j = 0; j < 2; ++j) {
                def subledger = TestHelper.generateSubledger()
                subledger.id = new SubledgerId(idGenerator.nextId())
                subledger.startTime = time
                subledger.payoutStatus = sample.payoutStatus
                subledger.seller = sample.seller
                created << subledgerRepositoryCloudant.create(subledger).get()
            }

        }

        assert subledgerRepositoryCloudant.list(new SubledgerParam(sellerId: sample.seller, payOutStatus: 'NotExist'),
            null).get().isEmpty()

        assert subledgerRepositoryCloudant.list(
                new SubledgerParam(sellerId: sample.seller, payOutStatus: sample.payoutStatus),
                null).get().size() == 16

        assert subledgerRepositoryCloudant.list(
                new SubledgerParam(sellerId: sample.seller, payOutStatus: sample.payoutStatus),
                new PageParam(start: 14)).get().size() == 2

        assert subledgerRepositoryCloudant.list(
                new SubledgerParam(sellerId: sample.seller, payOutStatus: sample.payoutStatus),
                new PageParam(start: 0, count: 5)).get().size() == 5

        assert subledgerRepositoryCloudant.list(
                new SubledgerParam(sellerId: sample.seller, payOutStatus: sample.payoutStatus,
                        fromDate: new Date(startTime.time + 2 * 1000)), null
                ).get().size() == 12

        assert subledgerRepositoryCloudant.list(
                new SubledgerParam(sellerId: sample.seller, payOutStatus: sample.payoutStatus,
                        toDate: new Date(startTime.time + 2 * 1000)), null
        ).get().size() == 4

        assert subledgerRepositoryCloudant.list(
                new SubledgerParam(sellerId: sample.seller, payOutStatus: sample.payoutStatus,
                      fromDate: new Date(startTime.time + 3 * 1000), toDate: new Date(startTime.time + 3 * 1000)), null
        ).get().size() == 0

        assert subledgerRepositoryCloudant.list(
                new SubledgerParam(sellerId: sample.seller, payOutStatus: sample.payoutStatus,
                        fromDate: new Date(startTime.time + 3 * 1000), toDate: new Date(startTime.time + 5 * 1000)), null
        ).get().size() == 4

        subledgerRepositoryCloudant.list(
                new SubledgerParam(sellerId: sample.seller, payOutStatus: sample.payoutStatus,
                        fromDate: new Date(startTime.time + 3 * 1000), toDate: new Date(startTime.time + 5 * 1000)), null
        ).get().each { it
            assert (long) (it.startTime.getTime() / 1000) >=
                    (long) (new Date(startTime.time + 3 * 1000).getTime() / 1000)
            assert (long) (it.startTime.getTime() / 1000) <
                    (long) (new Date(startTime.time + 5 * 1000).getTime() / 1000)
        }

        created.each { Subledger subledger ->
            subledgerRepositoryCloudant.delete(subledger.getId())
        }
    }

    @Test
    public void testGetByPayoutIdSql() {
        testGetByPayoutId(subledgerRepositorySql)
    }

    @Test
    public void testGetByPayoutIdCloudant() {
        testGetByPayoutId(subledgerRepositorySql)
    }

    @Test
    public void testSqlCreateDuplicatePending() {
        def subledger = TestHelper.generateSubledger()
        subledger.payoutStatus = PayoutStatus.COMPLETED.name()
        subledgerRepositorySql.create(subledger)
        subledgerRepositorySql.create(subledger)

        subledger.payoutStatus = PayoutStatus.PENDING.name()
        subledgerRepositorySql.create(subledger)
        try  {
            subledgerRepositorySql.create(subledger)
            Assert.fail('create pending duplicate should fail');
        } catch (Exception) {
        }

    }

    private void testGetByPayoutId(SubledgerRepository repository) {
        def sellerId = new OrganizationId(idGenerator.nextId())
        def payoutId = new PayoutId(idGenerator.nextId(sellerId.value))
        def isCloudantRepo = repository instanceof BaseCloudantRepositoryForDualWrite
        def List<SubledgerId> idList = [] as List
        for (int i = 0;i < 8; ++i) {
            def subledger = TestHelper.generateSubledger()
            if (isCloudantRepo) {
                subledger.id = new SubledgerId(idGenerator.nextId(sellerId.value))
            }
            subledger.startTime = new Date();
            subledger.payoutStatus = PayoutStatus.COMPLETED.name()
            subledger.seller = sellerId
            subledger.payoutId = payoutId
            idList << repository.create(subledger).get().getId()
        }

        Assert.assertEquals(repository.listByPayoutId(payoutId, new PageParam(start: 0, count: 100)).get().size(), 8)
        Assert.assertEquals(repository.listByPayoutId(payoutId, new PageParam(start: 4, count: 100)).get().size(), 4)
        Assert.assertEquals(repository.listByPayoutId(payoutId, new PageParam(start: 4, count: 2)).get().size(), 2)
        Assert.assertEquals(repository.listByPayoutId(
                new PayoutId(idGenerator.nextId(sellerId.value)), new PageParam(start: 0, count: 100)).get().size(), 0)

        if (isCloudantRepo) {
            idList.each { SubledgerId subledgerId ->
                repository.delete(subledgerId)
            }
        }
    }
}
