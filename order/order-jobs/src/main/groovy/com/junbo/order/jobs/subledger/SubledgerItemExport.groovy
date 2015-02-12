package com.junbo.order.jobs.subledger

import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.util.IdFormatter
import com.junbo.configuration.topo.DataCenters
import com.junbo.configuration.topo.model.DataCenter
import com.junbo.order.clientproxy.TransactionHelper
import com.junbo.order.db.repo.facade.OrderRepositoryFacade
import com.junbo.order.db.repo.facade.SubledgerRepositoryFacade
import com.junbo.order.jobs.utils.csv.CSVWriter
import com.junbo.order.jobs.utils.csv.ConcurrentCSVWriter
import com.junbo.order.spec.model.OrderItem
import com.junbo.order.spec.model.PageParam
import com.junbo.order.spec.model.SubledgerItem
import groovy.transform.CompileStatic
import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.annotation.Resource
import java.text.SimpleDateFormat
/**
 * Created by acer on 2015/2/12.
 */
@CompileStatic
@Component('order.subledgerItemExport')
class SubledgerItemExport {

    private final static Logger LOGGER = LoggerFactory.getLogger(SubledgerItemExport)

    private final static int PAGE_SIZE = 100

    @Resource(name ='orderTransactionHelper')
    TransactionHelper transactionHelper

    @Resource(name ='subledgerRepositoryFacade')
    SubledgerRepositoryFacade subledgerRepository

    @Resource(name = 'orderRepositoryFacade')
    OrderRepositoryFacade orderRepository

    private final static List<String> COLUMNS = ['createdTime', 'orderId', 'itemId', 'offer', 'orderItemId', 'subledgerItemId', 'subledgerType',
                                                 'totalQuantity', 'totalAmount', 'totalPayoutAmount', 'taxAmount']

    public void execute(String startTime, String endTime, String output) {
        def df  = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')
        exportSubledgerItem(df.parse(startTime), df.parse(endTime), output)
    }

    private void exportSubledgerItem(Date startTime, Date endTime, String output) {
        LOGGER.info('name=Start_Export_SubledgerItem, startTime={}, endTime={}, output={}', startTime, endTime, output)
        // create work file
        File work = new File(output)
        FileUtils.openOutputStream(work, false).close()

        // write headers
        CSVWriter csvWriter = new ConcurrentCSVWriter(work, null)
        csvWriter.writeRecords([COLUMNS])
        try {
            DataCenters.instance().dataCenterIds.each { Integer dcId ->
                DataCenter dataCenter = DataCenters.instance().getDataCenter(dcId)
                for (int shardId = 0; shardId < dataCenter.numberOfShard; ++shardId) {
                    exportSubledgerItem(dcId, shardId, startTime, endTime, COLUMNS, csvWriter)

                }
            }
        } finally {
            csvWriter.close()
        }
        LOGGER.info('name=End_Export_SubledgerItem, startTime={}, endTime={}, output={}', startTime, endTime, output)
    }

    private void exportSubledgerItem(int dcId, int shardId, Date startTime, Date endTime, List<String> columns, CSVWriter csvWriter) {
        int start = 0
        while (true) {
            List<SubledgerItem> subledgerItems
            transactionHelper.executeInTransaction {
                subledgerItems = subledgerRepository.getSubledgerItemsByTime(dcId, shardId, startTime, endTime, new PageParam(start: start, count: PAGE_SIZE))
            }

            subledgerItems.each { SubledgerItem subledgerItem ->
                OrderItem orderItem
                transactionHelper.executeInTransaction {
                    orderItem = orderRepository.getOrderItem(subledgerItem.orderItem.value)
                }

                Map<String, String> values = [:]
                values['subledgerItemId'] = IdFormatter.encodeId(subledgerItem.getId())
                values['createdTime'] = ObjectMapperProvider.instance().writeValueAsString(subledgerItem.createdTime)
                values['subledgerType'] = subledgerItem.subledgerType
                values['totalAmount'] = subledgerItem.totalAmount.toString()
                values['totalPayoutAmount'] = subledgerItem.totalPayoutAmount.toString()
                values['taxAmount'] = subledgerItem.taxAmount.toString()
                values['totalQuantity'] = subledgerItem.totalQuantity.toString()
                values['itemId'] = subledgerItem.item?.value
                values['offer'] = subledgerItem.offer?.value
                values['orderId'] = IdFormatter.encodeId(orderItem.orderId)
                values['orderItemId'] = IdFormatter.encodeId(orderItem.getId())

                List<String> record = []
                columns.each { String column ->
                    record << values[column]
                }
                csvWriter.writeRecords([record])
            }

            if (subledgerItems.size() < PAGE_SIZE) {
                break
            }

            start += subledgerItems.size()
        }
    }

}
