package com.junbo.order.db.repo.util
import com.google.common.collect.HashMultimap
import com.junbo.common.model.ResourceMetaForDualWrite
import com.junbo.order.db.entity.CommonDbEntityWithDate
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
/**
 * Created by fzhang on 4/11/2014.
 */
@CompileStatic
class Utils {
    private final static Logger LOGGER = LoggerFactory.getLogger(Utils)

    static void updateListTypeField(List newList, List oldList, RepositoryFuncSet repositoryFuncSet, Closure keyFunc,
                             String field) {
        def oldMap = HashMultimap.create()
        oldList.each {
            oldMap.put(keyFunc.call(it), it)
        }
        def numCreated = 0, numUpdated = 0, numDeleted = 0

        def lookupAndRemove = {
            def values = oldMap.get(keyFunc.call(it))
            if (values.empty) {
                return null
            }
            def iterator = values.iterator()
            def found = iterator.next()
            iterator.remove()
            return found
        }

        newList.each { newItem ->
            def oldItem = lookupAndRemove(newItem)
            if (oldItem == null) { // create
                repositoryFuncSet.create.call(newItem)
                numCreated++
            } else { // update
                if (repositoryFuncSet.update.call(newItem, oldItem)) {
                    numUpdated++
                }
            }
        }

        oldMap.values().each { // delete
            if (repositoryFuncSet.delete.call(it)) {
                numDeleted++
            }
        }

        LOGGER.debug('name=Save_List_Fields, fieldName={}, numCreated={}, numUpdated={}, numDeleted={}',
                field, numCreated, numUpdated, numDeleted)
    }

    static void fillDateInfo(ResourceMetaForDualWrite baseModelWithDate, CommonDbEntityWithDate commonDbEntityWithDate) {
        baseModelWithDate.createdBy = commonDbEntityWithDate.createdBy
        baseModelWithDate.createdTime = commonDbEntityWithDate.createdTime
        baseModelWithDate.updatedByClient = commonDbEntityWithDate.updatedBy
        baseModelWithDate.updatedTime = commonDbEntityWithDate.updatedTime
    }
}
