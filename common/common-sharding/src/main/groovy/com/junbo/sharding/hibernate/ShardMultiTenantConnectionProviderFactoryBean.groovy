package com.junbo.sharding.hibernate

import bitronix.tm.resource.common.ResourceBean
import bitronix.tm.resource.jdbc.PoolingDataSource
import bitronix.tm.utils.PropertyUtils
import groovy.transform.CompileStatic
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
        extends ResourceBean
        implements FactoryBean<ShardMultiTenantConnectionProvider>, DisposableBean {

    private String jdbcUrls

    @Required
    void setJdbcUrls(String jdbcUrls) {
        this.jdbcUrls = jdbcUrls
    }

    private Map<String, PoolingDataSource> dataSourceMap

    private ShardMultiTenantConnectionProvider connectionProvider

    @Override
    ShardMultiTenantConnectionProvider getObject() throws Exception {
        if (connectionProvider == null) {
            if (jdbcUrls == null) {
                throw new IllegalArgumentException('jdbcUrls is null')
            }

            dataSourceMap = new HashMap<>()

            List<String> schemaList = []
            List<DataSource> dataSourceList = []

            for (String url : jdbcUrls.split(',')) {
                url = url.trim()
                if (url.length() == 0) {
                    continue
                }

                def parts = url.split(';', 3)
                if (parts.length != 3) {
                    throw new IllegalArgumentException("jdbcUrl(url;schema;range) is invalid. $url")
                }

                url = parts[0].trim()
                def schema = parts[1].trim()
                def range = parseRange(parts[2].trim())

                if (range[0] != dataSourceList.size()) {
                    throw new IllegalArgumentException("Range $range is not in a row")
                }
                int count = range[1] - range[0] + 1

                for (int i = 0; i < count; i++) {
                    dataSourceList.add(getOrCreateDataSource(url))
                    schemaList.add(schema)
                }
            }

            connectionProvider = new ShardMultiTenantConnectionProvider(dataSourceList, schemaList)
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

            for (PoolingDataSource dataSource : dataSourceMap.values()) {
                dataSource.close()
            }
            dataSourceMap = null
        }
    }

    private PoolingDataSource getOrCreateDataSource(String url) {
        def result = dataSourceMap.get(url)
        if (result == null) {
            result = new PoolingDataSource()
            PropertyUtils.setProperties(result, PropertyUtils.getProperties(this))

            result.uniqueName = result.uniqueName + '_' + dataSourceMap.size()
            result.driverProperties.put('url', url)

            result.init()
            dataSourceMap.put(url, result)
        }
        return result
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
