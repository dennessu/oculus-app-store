package com.junbo.order.db.repo.sql

import com.junbo.common.id.OfferId
import com.junbo.common.id.OrderItemId
import com.junbo.common.id.SubledgerItemId
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import com.junbo.order.db.dao.SubledgerItemDao
import com.junbo.order.db.entity.SubledgerItemEntity
import com.junbo.order.spec.model.enums.SubledgerItemStatus
import com.junbo.order.db.mapper.ModelMapper
import com.junbo.order.db.repo.SubledgerItemRepository
import com.junbo.order.db.repo.util.Utils
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.SubledgerItem
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
/**
 * Created by fzhang on 4/11/2014.
 */
@CompileStatic
@Component('sqlSubledgerItemRepository')
class SubledgerItemRepositorySqlImpl implements SubledgerItemRepository {

    @Autowired
    private SubledgerItemDao subledgerItemDao

    @Autowired
    private ModelMapper modelMapper

    @Autowired
    @Qualifier('oculus48IdGenerator')
    private IdGenerator idGenerator

    @Override
    Promise<SubledgerItem> get(SubledgerItemId id) {
        return Promise.pure(modelMapper.toSubledgerItemModel(subledgerItemDao.read(id.value), new MappingContext()))
    }

    @Override
    Promise<SubledgerItem> create(SubledgerItem subledgerItem) {
        def subledgerItemEntity = modelMapper.toSubledgerItemEntity(subledgerItem, new MappingContext())
        subledgerItemEntity.subledgerItemId = idGenerator.nextId(subledgerItem.orderItem.value)
        subledgerItemDao.create(subledgerItemEntity)
        subledgerItem.id = new SubledgerItemId(subledgerItemEntity.subledgerItemId)
        Utils.fillDateInfo(subledgerItem, subledgerItemEntity)
        return Promise.pure(subledgerItem)
    }

    @Override
    Promise<SubledgerItem> update(SubledgerItem subledgerItem, SubledgerItem oldSubledgerItem) {
        def oldEntity = subledgerItemDao.read(subledgerItem.getId().value)

        def newEntity = modelMapper.toSubledgerItemEntity(subledgerItem, new MappingContext())
        newEntity.createdTime = oldEntity.createdTime
        newEntity.createdBy = oldEntity.createdBy
        subledgerItemDao.update(newEntity)
        Utils.fillDateInfo(subledgerItem, newEntity)

        return Promise.pure(subledgerItem)
    }

    @Override
    Promise<Void> delete(SubledgerItemId id) {
        subledgerItemDao.markDelete(id.value)
        return Promise.pure(null)
    }

    @Override
    Promise<List<SubledgerItem>> getSubledgerItems(Integer dataCenterId, Object shardKey, String status, OfferId offerId, Date endTime, PageParam pageParam) {
        List<SubledgerItem> result = [] as List
        subledgerItemDao.getByStatusOfferIdCreatedTime(dataCenterId, (Integer) shardKey,
                SubledgerItemStatus.valueOf(status), offerId.value, endTime,
                pageParam.start, pageParam.count).each { SubledgerItemEntity entity ->
            result << modelMapper.toSubledgerItemModel(entity, new MappingContext())
        }
        return Promise.pure(result)
    }

    @Override
    Promise<List<SubledgerItem>> getByOrderItemId(OrderItemId orderItemId) {
        return Promise.pure(subledgerItemDao.getByOrderItemId(orderItemId.value).collect { SubledgerItemEntity entity ->
            return modelMapper.toSubledgerItemModel(entity, new MappingContext());
        })
    }

    @Override
    Promise<List<OfferId>> getDistinctOfferIds(Integer dataCenterId, Object shardKey, String status, PageParam pageParam) {
        List<OfferId> result = [] as List
        subledgerItemDao.getDistrictOfferIds(dataCenterId, (Integer) shardKey,
                SubledgerItemStatus.valueOf(status),
                pageParam.start, pageParam.count).each { String offerId ->
            result << new OfferId(offerId)
        }
        return Promise.pure(result)
    }
}
