package com.junbo.sharding.hibernate

import com.jolbox.bonecp.BoneCPDataSource
import com.junbo.configuration.topo.DataCenters
import com.junbo.sharding.transaction.SimpleDataSourceProxy
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.annotation.Required

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by Shenhua on 4/1/2014.
 */
@CompileStatic
class ShardMultiTenantConnectionProviderFactoryBean
        implements FactoryBean<ShardMultiTenantConnectionProvider>, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShardMultiTenantConnectionProviderFactoryBean)

    private String uniqueName

    private String jdbcUrls

    private String className

    private int minPoolSize

    private int maxPoolSize

    private int partitionCount

    private Properties driverProperties

    void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName
    }

    @Required
    void setJdbcUrls(String jdbcUrls) {
        this.jdbcUrls = jdbcUrls
    }

    @Required
    void setClassName(String className) {
        this.className = className
    }

    @Required
    void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize
    }

    @Required
    void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize
    }

    void setPartitionCount(int partitionCount) {
        this.partitionCount = partitionCount
    }

    @Required
    void setDriverProperties(Properties driverProperties) {
        this.driverProperties = driverProperties
    }

    ShardMultiTenantConnectionProviderFactoryBean() {
        // default override
        this.partitionCount = 4
    }

    private final SchemaSetter schemaSetter = new SchemaSetter()

    private Map<String, SimpleDataSourceProxy> dataSourceMap

    private ShardMultiTenantConnectionProvider connectionProvider

    @Override
    ShardMultiTenantConnectionProvider getObject() throws Exception {
        if (connectionProvider == null) {
            if (jdbcUrls == null) {
                throw new IllegalArgumentException('jdbcUrls is null')
            }

            dataSourceMap = new HashMap<>()

            List<String> schemaList = []
            List<SimpleDataSourceProxy> dataSourceList = []

            for (String url : jdbcUrls.split(',')) {
                url = url.trim()
                if (url.length() == 0) {
                    continue
                }

                def parts = url.split(';', 4)
                if (parts.length != 4) {
                    throw new IllegalArgumentException("jdbcUrl(url;schema;range;dc) is invalid. $url")
                }

                url = parts[0].trim()
                def schema = parts[1].trim()
                def range = parseRange(parts[2].trim())
                def dc = parts[3].trim()

                if (!DataCenters.instance().isLocalDataCenter(dc)) {
                    // not in local dc
                    continue;
                }

                if (range[0] != dataSourceList.size()) {
                    throw new IllegalArgumentException("Range $range is not in a row")
                }
                int count = range[1] - range[0] + 1

                for (int i = 0; i < count; i++) {
                    dataSourceList.add(getOrCreateDataSource(url))
                    schemaList.add(schema)
                }
            }

            connectionProvider = new ShardMultiTenantConnectionProvider(
                    dataSourceList, schemaList, schemaSetter)
        }

        return connectionProvider
    }

    @Override
    Class<?> getObjectType() {
        return ShardMultiTenantConnectionProvider
    }

    @Override
    boolean isSingleton() {
        return true
    }

    @Override
    void destroy() throws Exception {
        if (connectionProvider != null) {
            connectionProvider = null

            for (SimpleDataSourceProxy dataSource : dataSourceMap.values()) {
                def boneCPDataSource = (BoneCPDataSource) dataSource.targetDataSource

                try {
                    boneCPDataSource.close()
                } catch (Exception ex) {
                    LOGGER.warn("Failed to close $boneCPDataSource", ex)
                }
            }

            dataSourceMap = null
        }
    }

    private SimpleDataSourceProxy getOrCreateDataSource(String url) {
        SimpleDataSourceProxy result = dataSourceMap.get(url)

        if (result == null) {
            result = createSimpleDataSourceProxy(url)

            dataSourceMap.put(url, result)
        }

        return result
    }

    private SimpleDataSourceProxy createSimpleDataSourceProxy(String url) {
        BoneCPDataSource dataSource = new BoneCPDataSource()

        dataSource.setDriverClass(className)
        dataSource.setJdbcUrl(url)

        dataSource.setPartitionCount(partitionCount)
        dataSource.setMinConnectionsPerPartition((int) (minPoolSize + partitionCount - 1) / partitionCount)
        dataSource.setMaxConnectionsPerPartition((int) (maxPoolSize + partitionCount - 1) / partitionCount)

        dataSource.setUser(driverProperties.getProperty('user'))
        dataSource.setPassword(driverProperties.getProperty('password'))

        dataSource.setConnectionHook(schemaSetter)

        return new SimpleDataSourceProxy(dataSource)
    }

    private static final String PATTERN_STR = '^(0|[1-9][0-9]*)[\\.]{2}(0|[1-9][0-9]*)$'
    private static final Pattern PATTERN = Pattern.compile(PATTERN_STR)

    private static List<Integer> parseRange(String range) {

        Matcher matcher = PATTERN.matcher(range)
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid range: $range")
        }

        int start = Integer.parseInt(matcher.group(1))
        int end = Integer.parseInt(matcher.group(2))

        if (start > end) {
            throw new IllegalArgumentException("Invalid range: $range")
        }

        return [start, end]
    }
}
