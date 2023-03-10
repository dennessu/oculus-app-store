package com.junbo.identity.job

import com.junbo.configuration.topo.DataCenters
import com.junbo.identity.service.UserService
import com.junbo.identity.spec.v1.model.User
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Required
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.util.CollectionUtils

import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by liangfu on 7/2/14.
 */
@CompileStatic
class IdentityJob implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityJob)

    private IdentityProcessor identityProcessor
    private UserService userService
    private Integer limit
    private Integer maxThreadPoolSize
    private ThreadPoolTaskExecutor  threadPoolTaskExecutor

    void execute() {
        if (DataCenters.instance().currentDataCenterId() != 0) {
            LOGGER.info("The IdentityJob only runs in first datacenter.");
            return;
        }

        LOGGER.info('name=IdentityProcessJobStart')
        def start = System.currentTimeMillis()
        def count = 0, numSuccess = new AtomicInteger(), numFail = new AtomicInteger()
        List<Future> futures = [] as LinkedList<Future>

        List<User> userList = readUsers(limit, 0).get()

        while (!CollectionUtils.isEmpty(userList)) {
            userList.each { User user ->
                def future = threadPoolTaskExecutor.submit(new Callable<Void>() {
                    @Override
                    Void call() throws Exception {
                        def result = identityProcessor.process(user).get()
                        if (result.success) {
                            numSuccess.andIncrement
                        } else {
                            numFail.andIncrement
                        }
                        return null
                    }
                })
                appendFuture(futures, future)
            }

            count += userList.size()
            // If the value is updated, it will remove from the view. So just need to read from the first index
            // And it won't have blocking users for some user. (Just check the vat resource)
            userList = readUsers(limit, 0).get()
        }

        // wait all task to finish
        futures.each { Future future ->
            future.get()
        }

        LOGGER.info('name=IdentityProcessJobEnd, numOfUsers={}, numSuccess={}, numFail={}, latency={}ms',
                count, numSuccess, numFail, System.currentTimeMillis() - start)
        assert count == numSuccess.get() + numFail.get()
    }

    @Override
    void afterPropertiesSet() throws Exception {
        assert identityProcessor != null, 'identityProcessor should not be null'
    }

    private void appendFuture(List<Future> futures, Future future) {
        futures.add(future)
        if (futures.size() >= maxThreadPoolSize) {
            def numOfFutureToRemove = maxThreadPoolSize / 2
            Iterator<Future> iterator = futures.iterator()
            for (int i = 0; i < numOfFutureToRemove; ++i) {
                iterator.next().get()
                iterator.remove()
            }
        }
    }

    private Promise<List<User>> readUsers(int limit, int offset) {
        return userService.getActiveUsersWithInvalidVatUser(limit, offset).then { List<User> userList ->
            return Promise.pure(userList)
        }
    }

    @Required
    void setIdentityProcessor(IdentityProcessor identityProcessor) {
        this.identityProcessor = identityProcessor
    }

    @Required
    void setUserService(UserService userService) {
        this.userService = userService
    }

    @Required
    void setLimit(Integer limit) {
        this.limit = limit
    }

    @Required
    void setMaxThreadPoolSize(Integer maxThreadPoolSize) {
        this.maxThreadPoolSize = maxThreadPoolSize
    }

    @Required
    void setThreadPoolTaskExecutor(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor
    }
}