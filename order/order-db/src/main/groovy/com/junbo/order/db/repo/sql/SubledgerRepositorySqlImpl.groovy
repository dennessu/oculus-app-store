package com.junbo.order.db.repo.sql
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.id.ItemId
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.PayoutId
import com.junbo.common.id.SubledgerId
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import com.junbo.order.db.dao.SubledgerDao
import com.junbo.order.db.entity.SubledgerEntity
import com.junbo.order.db.mapper.ModelMapper
import com.junbo.order.db.repo.SubledgerRepository
import com.junbo.order.db.repo.util.Utils
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.SubledgerParam
import com.junbo.order.spec.model.enums.PayoutStatus
import com.junbo.order.spec.model.enums.SubledgerType
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
/**
 * Created by fzhang on 4/11/2014.
 */
@CompileStatic
@Component('sqlSubledgerRepository')
class SubledgerRepositorySqlImpl implements SubledgerRepository {

    @Autowired
    private SubledgerDao subledgerDao

    @Autowired
    private ModelMapper modelMapper

    @Autowired
    @Qualifier('oculus48IdGenerator')
    private IdGenerator idGenerator

    @Override
    Promise<Subledger> get(SubledgerId id) {
        return Promise.pure(modelMapper.toSubledgerModel(subledgerDao.read(id.value), new MappingContext()))
    }

    @Override
    Promise<Subledger> create(Subledger subledger) {
        def subledgerEntity = modelMapper.toSubledgerEntity(subledger, new MappingContext())
        subledgerEntity.subledgerId = idGenerator.nextId(subledger.seller.value)
        subledgerDao.create(subledgerEntity)
        subledger.id = new SubledgerId(subledgerEntity.subledgerId)
        Utils.fillDateInfo(subledger, subledgerEntity)
        return Promise.pure(subledger)
    }

    @Override
    Promise<Subledger> update(Subledger subledger, Subledger oldSubledger) {
        def oldEntity = subledgerDao.read(subledger.getId().value)
        if (oldEntity == null) {
            throw AppErrors.INSTANCE.subledgerNotFound().exception()
        }

        def newEntity = modelMapper.toSubledgerEntity(subledger, new MappingContext())
        newEntity.createdTime = oldEntity.createdTime
        newEntity.createdBy = oldEntity.createdBy
        newEntity.resourceAge = oldEntity.resourceAge
        subledgerDao.update(newEntity)
        Utils.fillDateInfo(subledger, newEntity)

        return Promise.pure(subledger)
    }

    @Override
    Promise<Void> delete(SubledgerId id) {
        return null
    }

    @Override
    Promise<List<Subledger>> list(SubledgerParam subledgerParam, PageParam pageParam) {
        List<Subledger> result = []
        subledgerDao.getBySellerId(
                subledgerParam.sellerId.value, PayoutStatus.valueOf(subledgerParam.payOutStatus),
                subledgerParam.fromDate, subledgerParam.toDate,
                pageParam.start, pageParam.count).each { SubledgerEntity entity ->
            result << modelMapper.toSubledgerModel(entity, new MappingContext())
        }
        return Promise.pure(result)
    }

    @Override
    Promise<List<Subledger>> listOrderBySeller(int dataCenterId, int shardId, String payOutStatus, Date startDate, Date endDate,
                                               PageParam pageParam) {
        List<Subledger> result = []
        subledgerDao.getByStatusOrderBySeller(
                dataCenterId, shardId, PayoutStatus.valueOf(payOutStatus),
                startDate, endDate,
                pageParam.start, pageParam.count).each { SubledgerEntity entity ->
            result << modelMapper.toSubledgerModel(entity, new MappingContext())
        }
        return Promise.pure(result)
    }

    @Override
    Promise<List<Subledger>> listByTime(int dataCenterId, int shardId, Date startDate, Date endDate, PageParam pageParam) {
        List<Subledger> result = []
        subledgerDao.getByTime(
                dataCenterId, shardId,
                startDate, endDate,
                pageParam.start, pageParam.count).each { SubledgerEntity entity ->
            result << modelMapper.toSubledgerModel(entity, new MappingContext())
        }
        return Promise.pure(result)
    }

    @Override
    Promise<List<Subledger>> listByPayoutId(PayoutId payoutId, PageParam pageParam) {
        List<Subledger> result = []
        subledgerDao.getByPayoutId(payoutId.value, pageParam.start, pageParam.count).each { SubledgerEntity entity ->
            result << modelMapper.toSubledgerModel(entity, new MappingContext())
        }
        return Promise.pure(result)
    }

    @Override
    Promise<Subledger> find(OrganizationId sellerId, String payoutStatus, ItemId itemId, Date startTime, SubledgerType subledgerType,
                            String subledgerKey, CurrencyId currency, CountryId country) {
        def entity = subledgerDao.find(sellerId.value, PayoutStatus.valueOf(payoutStatus),
                startTime, itemId.value.toString(), subledgerType, subledgerKey, currency?.toString(), country?.toString())
        return Promise.pure(modelMapper.toSubledgerModel(entity, new MappingContext()));
    }
}
