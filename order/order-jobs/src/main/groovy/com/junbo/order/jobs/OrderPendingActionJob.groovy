package com.junbo.order.jobs
import com.junbo.configuration.topo.DataCenters
import com.junbo.configuration.topo.model.DataCenter
import com.junbo.order.clientproxy.TransactionHelper
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.spec.model.OrderPendingAction
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.enums.OrderPendingActionType
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Required
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

import java.text.SimpleDateFormat
import java.util.concurrent.Callable
import java.util.concurrent.Future
/**
 * Created by fzhang on 2015/2/3.
 */
@CompileStatic
class OrderPendingActionJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPendingActionJob)

    private OrderPendingActionProcessor pendingActionProcessor

    private List<OrderPendingActionType> orderPendingActionTypes

    private OrderRepositoryFacade orderRepository

    private ThreadPoolTaskExecutor threadPoolTaskExecutor

    private TransactionHelper transactionHelper

    @Required
    void setPendingActionProcessor(OrderPendingActionProcessor pendingActionProcessor) {
        this.pendingActionProcessor = pendingActionProcessor
    }

    @Required
    void setOrderPendingActionTypes(List<OrderPendingActionType> orderPendingActionType) {
        this.orderPendingActionTypes = orderPendingActionType
    }

    @Required
    void setOrderRepository(OrderRepositoryFacade orderRepository) {
        this.orderRepository = orderRepository
    }

    @Required
    void setThreadPoolTaskExecutor(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor
    }

    @Required
    void setTransactionHelper(TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper
    }

    public void execute() {
        innerExecute(new Date())
    }

    public void execute(String timeString) {
        innerExecute(new SimpleDateFormat('yyyy-MM-dd HH:mm:ss').parse(timeString))
    }

    private synchronized void innerExecute(Date endTime) {
        orderPendingActionTypes.each { OrderPendingActionType actionType ->
            processPendingActions(actionType, endTime)
        }
    }

    private void processPendingActions(OrderPendingActionType orderPendingActionType, Date endTime) {
        long startTime = System.currentTimeMillis()
        MDC.put(Constants.X_REQUEST_ID, UUID.randomUUID().toString());

        LOGGER.info('name=Process_Pending_Action_Start,actionType={},endTime={}', orderPendingActionType, endTime)
        DataCenter dataCenter = DataCenters.instance().getDataCenter(DataCenters.instance().currentDataCenterId())
        for (int shardId = 0; shardId < dataCenter.numberOfShard; ++shardId) {
            processPendingActions(dataCenter.getId(), shardId, orderPendingActionType, endTime)
        }

        LOGGER.info('name=Process_Pending_Action_End, latencyMs={}', System.currentTimeMillis() - startTime)
    }

    private void processPendingActions(int dcId, int shardId, OrderPendingActionType actionType, Date endTime) {
        Date startTime = null
        List<Future> futureList = [] as List

        while(true) {
            List<OrderPendingAction> actionList = []
            startTime = read(dcId, shardId, actionType, startTime, endTime, actionList)
            actionList.each { OrderPendingAction orderPendingAction ->
                Future future = threadPoolTaskExecutor.submit(new Callable() {
                    @Override
                    Object call() {
                        try {
                            MDC.put(Constants.X_REQUEST_ID, UUID.randomUUID().toString());
                            boolean processed = pendingActionProcessor.processPendingAction(orderPendingAction)
                            if (processed) {
                                orderPendingAction.completed = true
                                transactionHelper.executeInTransaction {
                                    orderRepository.updateOrderPendingAction(orderPendingAction)
                                }
                            }
                        } catch (Exception ex) {
                            LOGGER.error('name=Error_Process_OrderPendingAction, id={}, orderId={}', orderPendingAction.getId(), orderPendingAction.getOrderId(), ex)
                        }
                        return null
                    }
                })

                futureList.add(future)
                waitComplete(futureList, false)
            }
            if (startTime == null) {
                break
            }
        }

        waitComplete(futureList, true)
    }

    private void waitComplete(List<Future> futureList, boolean forceWait) {
        if (futureList.size() >= Constants.BATCH_SIZE || forceWait) {
            futureList.each { Future future ->
                future.get()
            }
            futureList.clear()
        }
    }

    private Date read(int dcId, int shardId, OrderPendingActionType actionType, Date startTime, Date endTime, List<OrderPendingAction> result) {
        int start = 0
        Date nextStartTime = null

        while(true) {
            List<OrderPendingAction> actionList = null
            transactionHelper.executeInTransaction {
                actionList = orderRepository.listOrderPendingActionsCreateTimeAsc(dcId, shardId, actionType, startTime, endTime, new PageParam(start: start, count: Constants.PAGE_SIZE))
            }

            for (int i = actionList.size() - 1; i >= 1; --i) {
                if (!actionList[i].createdTime.equals(actionList[i - 1])) {
                    nextStartTime = actionList[i].createdTime
                    break;
                }
            }

            if (nextStartTime != null) {
                result.addAll(actionList.findAll {OrderPendingAction e -> e.createdTime.before(nextStartTime)})
                break;
            }

            start += actionList.size()
            result.addAll(actionList)
            if (actionList.size() < Constants.PAGE_SIZE) {
                break
            }
        }
        return nextStartTime
    }
}
