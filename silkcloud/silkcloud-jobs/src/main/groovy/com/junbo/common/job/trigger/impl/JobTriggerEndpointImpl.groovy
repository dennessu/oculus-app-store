package com.junbo.common.job.trigger.impl
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.job.trigger.JobDefinition
import com.junbo.common.job.trigger.JobTriggerEndpoint
import com.junbo.common.job.trigger.JobTriggerRequest
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

import javax.annotation.Resource
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider
import java.lang.reflect.Method
import java.util.concurrent.Callable
import java.util.concurrent.Future
/**
 * Created by acer on 2015/2/6.
 */
@Provider
@CompileStatic
@Component('defaultJobTriggerEndpoint')
class JobTriggerEndpointImpl implements JobTriggerEndpoint {

    private final static Logger LOGGER = LoggerFactory.getLogger(JobTriggerEndpointImpl)

    private Map<String, JobDefinition> jobDefinitionMap

    @Resource(name = 'jobTriggerJobDefinitions')
    void setJobDefinitions(List<String> jobDefinitions) {
        jobDefinitionMap = [:]
        jobDefinitions.each { JobDefinition jobDefinition ->
            if (jobDefinitionMap[jobDefinition.jobName] != null) {
                throw new IllegalArgumentException("duplicate jobName in job definitions")
            }
            jobDefinitionMap[jobDefinition.jobName] = jobDefinition
        }
    }

    @Autowired
    private ApplicationContext applicationContext

    @Resource(name = 'commonJobAsyncTaskExecutor')
    private ThreadPoolTaskExecutor commonJobAsyncTaskExecutor

    @Override
    Promise<Response> triggerJob(JobTriggerRequest request) {
        if (request == null) {
            throw AppCommonErrors.INSTANCE.requestBodyRequired().exception()
        }
        if (StringUtils.isEmpty(request.jobName)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('jobName').exception()
        }

        if (!jobDefinitionMap.containsKey(request.jobName)) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('jobName', 'job with the name does not exist').exception()
        }
        if (request.jobArguments == null) {
            request.jobArguments = []
        }

        JobDefinition jobDefinition = jobDefinitionMap[request.jobName]
        Object bean = getBean(jobDefinition)
        Method method = getMethod(bean, request, jobDefinition)
        Object[] arguments = new Object[request.jobArguments.size()]
        request.jobArguments.eachWithIndex { String arg, Integer index ->
            arguments[index] = arg
        }

        Future<Void> future = commonJobAsyncTaskExecutor.submit(new Callable<Void>() {
            @Override
            Void call() throws Exception {
                method.invoke(bean, arguments)
                return null
            }
        })

        try {
            future.get()
        } catch (Exception ex) {
            LOGGER.error('name=Error_Trigger_Job', ex)
            StringWriter sw = new StringWriter()
            ex.printStackTrace(new PrintWriter(sw))
            return Promise.pure(Response.status(Response.Status.PRECONDITION_FAILED).entity(sw.toString()).build())
        }

        return Promise.pure(Response.status(Response.Status.OK).entity('Job Success!').build())
    }

    private Object getBean(JobDefinition jobDefinition) {
        try {
            return applicationContext.getBean(jobDefinition.beanName)
        } catch (BeansException exception) {
            LOGGER.error('name=Get_Bean_Error', exception)
            throw AppCommonErrors.INSTANCE.fieldInvalid('jobName', 'bean not found').exception()
        }
    }

    private Method getMethod(Object bean, JobTriggerRequest request, JobDefinition jobDefinition) {
        int argLength = CollectionUtils.isEmpty(request.jobArguments) ? 0 : request.jobArguments.size()
        Method method = bean.class.methods.find { Method m ->
            return m.name == jobDefinition.methodName && m.parameterTypes.size() == argLength && !(m.parameterTypes.any { Class clazz ->
                clazz != String
            })
        }

        if (method == null) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('jobName', 'could not find match job method').exception()
        }

        return method
    }

}
