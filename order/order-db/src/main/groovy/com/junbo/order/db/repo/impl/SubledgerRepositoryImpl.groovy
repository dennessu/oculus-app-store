package com.junbo.order.db.repo.impl
import com.junbo.common.id.OfferId
import com.junbo.common.id.SubledgerId
import com.junbo.common.id.SubledgerItemId
import com.junbo.common.id.UserId
import com.junbo.oom.core.MappingContext
import com.junbo.order.db.dao.SubledgerDao
import com.junbo.order.db.dao.SubledgerItemDao
import com.junbo.order.db.entity.SubledgerEntity
import com.junbo.order.db.entity.SubledgerItemEntity
import com.junbo.order.db.entity.enums.PayoutStatus
import com.junbo.order.db.entity.enums.SubledgerItemStatus
import com.junbo.order.db.mapper.ModelMapper
import com.junbo.order.db.repo.SubledgerRepository
import com.junbo.order.spec.error.AppErrors
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.Subledger
import com.junbo.order.spec.model.SubledgerItem
import com.junbo.order.spec.model.SubledgerParam
import com.junbo.sharding.IdGenerator
import com.junbo.sharding.IdGeneratorFacade
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
/**
 * Created by fzhang on 4/11/2014.
 */
@CompileStatic
class SubledgerRepositoryImpl implements SubledgerRepository {

    @Autowired
    SubledgerDao subledgerDao
    @Autowired
    SubledgerItemDao subledgerItemDao
    @Autowired
    ModelMapper modelMapper
    @Autowired
    IdGeneratorFacade idGeneratorFacade
    @Autowired
    IdGenerator idGenerator

    @Override
    Subledger createSubledger(Subledger subledger) {
        def subledgerEntity = modelMapper.toSubledgerEntity(subledger, new MappingContext())
        subledgerEntity.subledgerId = idGeneratorFacade.nextId(SubledgerId, subledger.sellerId.value)
        subledgerDao.create(subledgerEntity)
        Utils.fillDateInfo(subledger, subledgerEntity)
        return subledger
    }

    @Override
    Subledger updateSubledger(Subledger subledger) {
        def oldEntity = subledgerDao.read(subledger.subledgerId.value)
        if (oldEntity == null) {
            throw AppErrors.INSTANCE.subledgerNotFound().exception()
        }

        def newEntity = modelMapper.toSubledgerEntity(subledger, new MappingContext())
        newEntity.createdTime = oldEntity.createdTime
        newEntity.createdBy = oldEntity.createdBy
        subledgerDao.update(newEntity)
        Utils.fillDateInfo(subledger, newEntity)

        return subledger
    }

    @Override
    Subledger getSubledger(SubledgerId subledgerId) {
        def entity = subledgerDao.read(subledgerId.value)
        if (entity == null) {
            return null
        }
        return modelMapper.toSubledgerModel(entity, new MappingContext())
    }

    @Override
    List<Subledger> getSubledgers(SubledgerParam subledgerParam, PageParam pageParam) {
        List<Subledger> result = []
        subledgerDao.getBySellerId(
                subledgerParam.sellerId.value, PayoutStatus.valueOf(subledgerParam.payOutStatus),
                subledgerParam.fromDate, subledgerParam.toDate,
                pageParam.start, pageParam.count).each { SubledgerEntity entity ->
            result << modelMapper.toSubledgerModel(entity, new MappingContext())
        }
        return result
    }

    @Override
    Subledger findSubledger(UserId sellerId, String payoutStatus, OfferId offerId,
                            Date startTime, String currency, String country) {
        def entity = subledgerDao.find(sellerId.value, PayoutStatus.valueOf(payoutStatus),
                        startTime, offerId.value.toString(), currency, country)
        return entity == null ? null : modelMapper.toSubledgerModel(entity, new MappingContext())
    }

    @Override
    SubledgerItem createSubledgerItem(SubledgerItem subledgerItem) {
        def subledgerItemEntity = modelMapper.toSubledgerItemEntity(subledgerItem, new MappingContext())
        subledgerItemEntity.subledgerItemId = idGeneratorFacade.nextId(SubledgerItemId, subledgerItem.orderItemId.value)
        subledgerItemDao.create(subledgerItemEntity)
        Utils.fillDateInfo(subledgerItem, subledgerItemEntity)
        return subledgerItem
    }

    @Override
    List<SubledgerItem> getSubledgerItem(String status, PageParam pageParam) {
        List<SubledgerItem> result = []
        subledgerItemDao.getByStatus(0, // todo iterate all the shard
                SubledgerItemStatus.valueOf(status),
                pageParam.start, pageParam.count).each { SubledgerItemEntity entity ->
            result << modelMapper.toSubledgerItemModel(entity, new MappingContext())
        }
        return result
    }

    @Override
    SubledgerItem updateSubledgerItem(SubledgerItem subledgerItem) {
        def oldEntity = subledgerItemDao.read(subledgerItem.subledgerItemId.value)

        def newEntity = modelMapper.toSubledgerItemEntity(subledgerItem, new MappingContext())
        newEntity.createdTime = oldEntity.createdTime
        newEntity.createdBy = oldEntity.createdBy
        subledgerItemDao.update(newEntity)
        Utils.fillDateInfo(subledgerItem, newEntity)

        return subledgerItem
    }
}
