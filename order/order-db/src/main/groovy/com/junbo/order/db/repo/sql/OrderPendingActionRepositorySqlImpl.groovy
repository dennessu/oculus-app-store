package com.junbo.order.db.repo.sql
import com.junbo.common.id.OrderId
import com.junbo.common.id.OrderPendingActionId
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import com.junbo.order.db.dao.OrderPendingActionDao
import com.junbo.order.db.entity.OrderPendingActionEntity
import com.junbo.order.db.mapper.ModelMapper
import com.junbo.order.db.repo.OrderPendingActionRepository
import com.junbo.order.db.repo.util.Utils
import com.junbo.order.spec.model.OrderPendingAction
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.enums.OrderPendingActionType
import com.junbo.sharding.IdGenerator
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
/**
 * Created by fzhang on 2015/2/2.
 */
@CompileStatic
@TypeChecked
@Component('sqlOrderPendingActionRepository')
class OrderPendingActionRepositorySqlImpl implements OrderPendingActionRepository {

    @Autowired
    private OrderPendingActionDao orderPendingActionDao

    @Autowired
    private ModelMapper modelMapper

    @Autowired
    @Qualifier('oculus48IdGenerator')
    private IdGenerator idGenerator

    @Override
    Promise<List<OrderPendingAction>> getOrderPendingActionsByOrderId(OrderId orderId, OrderPendingActionType actionType) {
        List<OrderPendingAction> actionList = orderPendingActionDao.getByOrderId(orderId.value, actionType).collect { OrderPendingActionEntity entity ->
            return modelMapper.toOrderPendingAction(entity, new MappingContext())
        }.asList()
        return Promise.pure(actionList)
    }

    @Override
    Promise<List<OrderPendingAction>> listOrderPendingActionsCreateTimeAsc(int dataCenterId, int shardId, OrderPendingActionType actionType,
                                                                           Date startTime, Date endTime, PageParam pageParam) {
        List<OrderPendingAction> actionList = orderPendingActionDao.list(dataCenterId, shardId, actionType, startTime, endTime, pageParam.start, pageParam.count).collect {
            OrderPendingActionEntity entity ->
            return modelMapper.toOrderPendingAction(entity, new MappingContext())
        }.asList()
        return Promise.pure(actionList)
    }

    @Override
    Promise<OrderPendingAction> get(OrderPendingActionId id) {
        return Promise.pure(modelMapper.toOrderPendingAction(orderPendingActionDao.read(id.value), new MappingContext()))
    }

    @Override
    Promise<OrderPendingAction> create(OrderPendingAction orderPendingAction) {
        def entity = modelMapper.toOrderPendingActionEntity(orderPendingAction, new MappingContext())
        entity.pendingActionId = idGenerator.nextId(entity.orderId)
        orderPendingActionDao.create(entity)
        orderPendingAction.setId(new OrderPendingActionId(entity.pendingActionId))
        Utils.fillDateInfo(orderPendingAction, entity)

        return Promise.pure(orderPendingAction)
    }

    @Override
    Promise<OrderPendingAction> update(OrderPendingAction newOrderPendingAction, OrderPendingAction oldOrderPendingAction) {
        def oldEntity = orderPendingActionDao.read(newOrderPendingAction.getId().value)

        def newEntity = modelMapper.toOrderPendingActionEntity(newOrderPendingAction, new MappingContext())
        newEntity.createdTime = oldEntity.createdTime
        newEntity.createdBy = oldEntity.createdBy
        orderPendingActionDao.update(newEntity)
        Utils.fillDateInfo(newOrderPendingAction, newEntity)

        return Promise.pure(newOrderPendingAction)
    }

    @Override
    Promise<Void> delete(OrderPendingActionId id) {
        throw new UnsupportedOperationException("delete is not allowed")
    }
}
