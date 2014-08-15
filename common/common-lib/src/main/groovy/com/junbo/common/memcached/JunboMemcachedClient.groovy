package com.junbo.common.memcached

import com.junbo.configuration.ConfigService
import com.junbo.configuration.ConfigServiceManager
import groovy.transform.CompileStatic
import net.spy.memcached.*
import net.spy.memcached.auth.AuthDescriptor
import net.spy.memcached.auth.PlainCallbackHandler
import net.spy.memcached.transcoders.SerializingTranscoder
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean

/**
 * JunboMemcachedClient.
 */
@CompileStatic
class JunboMemcachedClient implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JunboMemcachedClient)

    private static JunboMemcachedClient singleton = createInstance();
    public static MemcachedClientIF instance() {
        return singleton.getClient()
    }

    private MemcachedClientIF memcachedClient
    private String servers
    private boolean enabled
    private Integer timeout
    private Integer compressionThreshold
    private String username
    private String password
    private String authType

    private static JunboMemcachedClient createInstance() {
        JunboMemcachedClient client = new JunboMemcachedClient()

        ConfigService configService = ConfigServiceManager.instance()

        String strEnabled = configService.getConfigValue("common.memcached.enabled")
        String strTimeout = configService.getConfigValue("common.memcached.timeout")
        String strCompression = configService.getConfigValue("common.memcached.compressionThreshold")

        client.servers = configService.getConfigValue("common.memcached.servers")
        client.enabled = Boolean.parseBoolean(strEnabled)
        client.timeout = safeParseInt(strTimeout)
        client.compressionThreshold = safeParseInt(strCompression)
        client.username = configService.getConfigValue("common.memcached.username")
        client.password = configService.getConfigValue("common.memcached.password")
        client.authType = configService.getConfigValue("common.memcached.auth")

        client.afterPropertiesSet()
        return client
    }

    public JunboMemcachedClient() {}

    MemcachedClientIF getClient() {
        return memcachedClient
    }

    @Override
    public void afterPropertiesSet() {
        if (enabled) {
            def connectionFactoryBuilder = new ConnectionFactoryBuilder()
                    .setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
                    .setFailureMode(FailureMode.Cancel)
                    .setLocatorType(ConnectionFactoryBuilder.Locator.CONSISTENT)
                    .setHashAlg(DefaultHashAlgorithm.KETAMA_HASH)
                    .setOpTimeout(this.timeout)
                    .setOpQueueMaxBlockTime(this.timeout)
                    .setUseNagleAlgorithm(false)

            if (!StringUtils.isEmpty(username)) {
                def ad = new AuthDescriptor([ authType ] as String[],
                        new PlainCallbackHandler(username, password));
                connectionFactoryBuilder.setAuthDescriptor(ad)
            }

            SerializingTranscoder transcoder = new SerializingTranscoder()
            transcoder.setCharset("UTF-8")
            if (compressionThreshold != null) {
                transcoder.setCompressionThreshold(compressionThreshold)
            }
            connectionFactoryBuilder.setTranscoder(transcoder)

            try {
                this.memcachedClient = new net.spy.memcached.MemcachedClient(connectionFactoryBuilder.build(), AddrUtil.getAddresses(servers))
            } catch (Exception ex) {
                logger.warn("Error creating memcached client.", ex)
            }
        } else {
            logger.info("CloudantClient memcached is globally disabled.")
        }
    }

    void setServers(String servers) {
        this.servers = servers
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled
    }

    void setTimeout(Integer timeout) {
        this.timeout = timeout
    }

    void setExpiration(Integer expiration) {
        this.expiration = expiration
    }

    void setMaxEntitySize(Integer maxEntitySize) {
        this.maxEntitySize = maxEntitySize
    }

    void setCompressionThreshold(Integer compressionThreshold) {
        this.compressionThreshold = compressionThreshold
    }

    void setAsyncUpdate(Boolean asyncUpdate) {
        this.asyncUpdate = asyncUpdate
    }

    void setUsername(String username) {
        this.username = username
    }

    void setPassword(String password) {
        this.password = password
    }

    void setAuthType(String authType) {
        this.authType = authType
    }

    private static Integer safeParseInt(String str) {
        if (StringUtils.isEmpty(str)) return null
        return Integer.parseInt(str)
    }
}
