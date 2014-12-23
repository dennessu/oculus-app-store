package com.junbo.sharding.hibernate
import com.junbo.configuration.topo.DataCenters
import com.junbo.configuration.topo.model.DataCenter
import com.junbo.sharding.transaction.SimpleDataSourceProxy
import com.zaxxer.hikari.HikariDataSource
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.annotation.Required

import javax.sql.DataSource
import java.util.regex.Matcher
import java.util.regex.Pattern
/**
 * Created by Shenhua on 4/1/2014.
 */
@CompileStatic
class ShardMultiTenantConnectionProviderFactoryBean
        implements FactoryBean<ShardMultiTenantConnectionProvider>, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShardMultiTenantConnectionProviderFactoryBean)

    protected String uniqueName

    protected String jdbcUrls

    protected String className

    protected int minPoolSize

    protected int maxPoolSize

    protected int connectionTimeout

    protected Properties driverProperties

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

    @Required
    void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout
    }

    @Required
    void setDriverProperties(Properties driverProperties) {
        this.driverProperties = driverProperties
    }

    ShardMultiTenantConnectionProviderFactoryBean() {
    }

    private static Object lock = new Object();
    private static Map<String, HikariDataSource> dataSourceMap = new HashMap<>()
    private static Map<String, SimpleDataSourceProxy> dataSourceProxyMap = new HashMap<>()

    private ShardMultiTenantConnectionProvider connectionProvider

    @Override
    ShardMultiTenantConnectionProvider getObject() throws Exception {
        synchronized (lock) {
            if (connectionProvider == null) {
                if (jdbcUrls == null) {
                    throw new IllegalArgumentException('jdbcUrls is null')
                }

                Map<String, SimpleDataSourceProxy> dataSourceMap = [:]

                for (String url : jdbcUrls.split(',')) {
                    url = url.trim()
                    if (url.length() == 0) {
                        continue
                    }

                    def parts = url.split(';', 4)
                    if (parts.length != 4) {
                        throw new IllegalArgumentException("jdbcUrl(url;schema;range;dc) is invalid. $url")
                    }

                    url = parts[0].trim().replace('|', ',')
                    def schema = parts[1].trim()
                    def range = parseRange(parts[2].trim())
                    def dc = parts[3].trim()
                    DataCenter dataCenter = DataCenters.instance().getDataCenter(dc)

                    // If not in locale DC, just do the same
                    def currentDCProxy = dataSourceMap.findAll { Map.Entry<String, SimpleDataSourceProxy> entry ->
                        return entry != null && entry.getKey() != null && entry.getKey().startsWith(dataCenter.getId() + ":")
                    }
                    if (range[0] != currentDCProxy.size()) {
                        throw new IllegalArgumentException("Range $range is not in a row")
                    }
                    for (int i = range[0]; i <= range[1]; i++) {
                        if (dataCenter == null) {
                            throw new IllegalArgumentException("DataCenter $dataCenter does not exist.")
                        }
                        dataSourceMap.put(dataCenter.getId() + ':' + i, createDataSourceProxy(url, schema))
                    }
                }

                connectionProvider = new ShardMultiTenantConnectionProvider(dataSourceMap)
            }

            return connectionProvider
        }
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
        }

        if (dataSourceMap != null) {
            for (HikariDataSource dataSource : dataSourceMap.values()) {
                try {
                    dataSource.close()
                } catch (Exception ex) {
                    LOGGER.warn("Failed to close $dataSource", ex)
                }
            }

            dataSourceMap = null
        }
    }

    private SimpleDataSourceProxy createDataSourceProxy(String url, String schema) {
        DataSource dataSource = dataSourceMap.get(url)

        if (dataSource == null) {
            dataSource = createDataSource(url)
            dataSourceMap.put(url, dataSource)
        }

        def key = "$url:$schema"
        SimpleDataSourceProxy dataSourceProxy = dataSourceProxyMap.get(key)

        if (dataSourceProxy == null) {
            dataSourceProxy = new SimpleDataSourceProxy(dataSource, schema)
            dataSourceProxyMap.put(key, dataSourceProxy)
        }

        return dataSourceProxy
    }

    protected HikariDataSource createDataSource(String url) {
        HikariDataSource dataSource = new HikariDataSource()

        dataSource.setDriverClassName(className)
        dataSource.setJdbcUrl(url)

        dataSource.setMaximumPoolSize(maxPoolSize)
        // Hikari Pool is using minIdle as min pool size...
        dataSource.setMinimumIdle(minPoolSize)

        dataSource.setConnectionTimeout(connectionTimeout)

        dataSource.setUsername(driverProperties.getProperty('user'))
        dataSource.setPassword(driverProperties.getProperty('password'))

        dataSource.setDataSourceProperties(driverProperties)

        return dataSource
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
