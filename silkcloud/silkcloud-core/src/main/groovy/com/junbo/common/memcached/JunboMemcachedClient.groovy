package com.junbo.common.memcached
import com.junbo.configuration.ConfigService
import com.junbo.configuration.ConfigServiceManager
import com.junbo.langur.core.profiling.ProfilingHelper
import groovy.transform.CompileStatic
import net.spy.memcached.*
import net.spy.memcached.auth.AuthDescriptor
import net.spy.memcached.auth.PlainCallbackHandler
import net.spy.memcached.transcoders.SerializingTranscoder
import net.spy.memcached.transcoders.Transcoder
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean

import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * JunboMemcachedClient.
 */
@CompileStatic
class JunboMemcachedClient implements InitializingBean {
    protected static final Logger logger = LoggerFactory.getLogger(JunboMemcachedClient)

    private static JunboMemcachedClient singleton = createInstance();
    public static JunboMemcachedClient instance() {
        return singleton
    }

    private MemcachedClientIF memcachedClient
    private String id
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

        client.id = "1"
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
                logger.warn("Error creating memcached client to servers: " + servers, ex)
            }
        } else {
            logger.info("CloudantClient memcached is globally disabled.")
        }
    }

    void setServers(String servers) {
        this.servers = servers
    }

    void setId(String id) {
        this.id = id
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

    String getId() {
        return id
    }

    String getServers() {
        return servers
    }

    boolean getEnabled() {
        return enabled
    }

    Integer getTimeout() {
        return timeout
    }

    Integer getCompressionThreshold() {
        return compressionThreshold
    }

    String getUsername() {
        return username
    }

    String getPassword() {
        return password
    }

    String getAuthType() {
        return authType
    }

    private static Integer safeParseInt(String str) {
        if (StringUtils.isEmpty(str)) return null
        return Integer.parseInt(str)
    }

    //region profile

    private static <T> T withProfile(String method, String key, Closure<T> closure) {
        ProfilingHelper.begin("MC", "%s %s", method, key);
        return FutureWrapper.finishProfile(closure);
    }

    //endregion

    //region MemcachedClientIF

    Collection<SocketAddress> getAvailableServers() {
        return memcachedClient.getAvailableServers()
    }

    Collection<SocketAddress> getUnavailableServers() {
        return memcachedClient.getUnavailableServers()
    }

    Transcoder<Object> getTranscoder() {
        return memcachedClient.getTranscoder()
    }

    NodeLocator getNodeLocator() {
        return memcachedClient.getNodeLocator()
    }

    Future<Boolean> append(long cas, String key, Object val) {
        return new FutureWrapper<Boolean>("append", key, {
            return memcachedClient.append(cas, key, val);
        })
    }

    Future<Boolean> append(String key, Object val) {
        return new FutureWrapper<Boolean>("append", key, {
            return memcachedClient.append(key, val)
        })
    }

    def <T> Future<Boolean> append(long cas, String key, T val, Transcoder<T> tc) {
        return new FutureWrapper<Boolean>("append", key, {
            return memcachedClient.append(cas, key, val, tc)
        })
    }

    def <T> Future<Boolean> append(String key, T val, Transcoder<T> tc) {
        return new FutureWrapper<Boolean>("append", key, {
            return memcachedClient.append(key, val, tc)
        })
    }

    Future<Boolean> prepend(long cas, String key, Object val) {
        return new FutureWrapper<Boolean>("prepend", key, {
            return memcachedClient.prepend(cas, key, val)
        })
    }

    Future<Boolean> prepend(String key, Object val) {
        return new FutureWrapper<Boolean>("prepend", key, {
            return memcachedClient.prepend(key, val)
        })
    }

    def <T> Future<Boolean> prepend(long cas, String key, T val, Transcoder<T> tc) {
        return new FutureWrapper<Boolean>("prepend", key, {
            return memcachedClient.prepend(cas, key, val, tc)
        })
    }

    def <T> Future<Boolean> prepend(String key, T val, Transcoder<T> tc) {
        return new FutureWrapper<Boolean>("prepend", key, {
            return memcachedClient.prepend(key, val, tc)
        })
    }

    def <T> Future<CASResponse> asyncCAS(String key, long casId, T value, Transcoder<T> tc) {
        return new FutureWrapper<CASResponse>("asyncCAS", key, {
            return memcachedClient.asyncCAS(key, casId, value, tc)
        })
    }

    Future<CASResponse> asyncCAS(String key, long casId, Object value) {
        return new FutureWrapper<CASResponse>("asyncCAS", key, {
            return memcachedClient.asyncCAS(key, casId, value)
        })
    }

    Future<CASResponse> asyncCAS(String key, long casId, int exp, Object value) {
        return new FutureWrapper<CASResponse>("asyncCAS", key, {
            return memcachedClient.asyncCAS(key, casId, exp, value)
        })
    }

    def <T> Future<CASResponse> asyncCAS(String key, long casId, int exp, T value, Transcoder<T> tc) {
        return new FutureWrapper<CASResponse>("asyncCAS", key, {
            return (Future<CASResponse>)memcachedClient.asyncCAS(key, casId, exp, value, tc)
        })
    }

    def <T> CASResponse cas(String key, long casId, int exp, T value, Transcoder<T> tc) {
        return withProfile("cas", key, {
            return memcachedClient.cas(key, casId, exp, value, tc)
        })
    }

    CASResponse cas(String key, long casId, Object value) {
        return withProfile("cas", key, {
            return memcachedClient.cas(key, casId, value)
        })
    }

    CASResponse cas(String key, long casId, int exp, Object value) {
        return withProfile("cas", key, {
            return memcachedClient.cas(key, casId, exp, value)
        })
    }

    def <T> CASResponse cas(String key, long casId, T value, Transcoder<T> tc) {
        return withProfile("cas", key, {
            return memcachedClient.cas(key, casId, value, tc)
        })
    }

    def <T> Future<Boolean> add(String key, int exp, T o, Transcoder<T> tc) {
        return new FutureWrapper<Boolean>("add", key, {
            return memcachedClient.add(key, exp, o, tc)
        })
    }

    Future<Boolean> add(String key, int exp, Object o) {
        return new FutureWrapper<Boolean>("add", key, {
            return memcachedClient.add(key, exp, o)
        })
    }

    def <T> Future<Boolean> set(String key, int exp, T o, Transcoder<T> tc) {
        return new FutureWrapper<Boolean>("add", key, {
            return memcachedClient.add(key, exp, o, tc)
        })
    }

    Future<Boolean> set(String key, int exp, Object o) {
        return new FutureWrapper<Boolean>("set", key, {
            return memcachedClient.set(key, exp, o)
        })
    }

    def <T> Future<Boolean> replace(String key, int exp, T o, Transcoder<T> tc) {
        return new FutureWrapper<Boolean>("replace", key, {
            return memcachedClient.set(key, exp, o, tc)
        })
    }

    Future<Boolean> replace(String key, int exp, Object o) {
        return new FutureWrapper<Boolean>("replace", key, {
            return memcachedClient.set(key, exp, o)
        })
    }

    def <T> Future<T> asyncGet(String key, Transcoder<T> tc) {
        return new FutureWrapper<>("asyncGet", key, {
            return (Future)memcachedClient.asyncGet(key, tc)
        })
    }

    Future<Object> asyncGet(String key) {
        return new FutureWrapper<Object>("asyncGet", key, {
            return memcachedClient.asyncGet(key)
        })
    }

    Future<CASValue<Object>> asyncGetAndTouch(String key, int exp) {
        return new FutureWrapper<CASValue<Object>>("asyncGetAndTouch", key, {
            return memcachedClient.asyncGetAndTouch(key, exp)
        })
    }

    def <T> Future<CASValue<T>> asyncGetAndTouch(String key, int exp, Transcoder<T> tc) {
        return new FutureWrapper<CASValue<T>>("asyncGetAndTouch", key, {
            return memcachedClient.asyncGetAndTouch(key, exp, tc)
        })
    }

    CASValue<Object> getAndTouch(String key, int exp) {
        return withProfile("getAndTouch", key, {
            return memcachedClient.getAndTouch(key, exp)
        })
    }

    def <T> CASValue<T> getAndTouch(String key, int exp, Transcoder<T> tc) {
        return withProfile("getAndTouch", key, {
            return memcachedClient.getAndTouch(key, exp, tc)
        })
    }

    def <T> Future<CASValue<T>> asyncGets(String key, Transcoder<T> tc) {
        return new FutureWrapper<CASValue<T>>("asyncGets", key, {
            return memcachedClient.asyncGets(key, tc)
        })
    }

    Future<CASValue<Object>> asyncGets(String key) {
        return new FutureWrapper<CASValue<Object>>("asyncGets", key, {
            return memcachedClient.asyncGets(key)
        })
    }

    def <T> CASValue<T> gets(String key, Transcoder<T> tc) {
        return withProfile("gets", key, {
            return memcachedClient.gets(key, tc)
        })
    }

    CASValue<Object> gets(String key) {
        return withProfile("gets", key, {
            return memcachedClient.gets(key)
        })
    }

    def <T> T get(String key, Transcoder<T> tc) {
        return withProfile("get", key, {
            return memcachedClient.get(key, tc)
        })
    }

    Object get(String key) {
        return withProfile("get", key, {
            return memcachedClient.get(key)
        })
    }

    Map<SocketAddress, String> getVersions() {
        return memcachedClient.getVersions()
    }

    Map<SocketAddress, Map<String, String>> getStats() {
        return memcachedClient.getStats()
    }

    Map<SocketAddress, Map<String, String>> getStats(String prefix) {
        return memcachedClient.getStats(prefix)
    }

    long incr(String key, long by) {
        return withProfile("incr", key, {
            return memcachedClient.incr(key, by)
        })
    }

    long incr(String key, int by) {
        return withProfile("incr", key, {
            return memcachedClient.incr(key, by)
        })
    }

    long decr(String key, long by) {
        return withProfile("decr", key, {
            return memcachedClient.decr(key, by)
        })
    }

    long decr(String key, int by) {
        return withProfile("decr", key, {
            return memcachedClient.decr(key, by)
        })
    }

    long incr(String key, long by, long l, int exp) {
        return withProfile("incr", key, {
            return memcachedClient.incr(key, by, l, exp)
        })
    }

    long incr(String key, int by, long l, int exp) {
        return withProfile("incr", key, {
            return memcachedClient.incr(key, by, l, exp)
        })
    }

    long decr(String key, long by, long l, int exp) {
        return withProfile("decr", key, {
            return memcachedClient.decr(key, by, l, exp)
        })
    }

    long decr(String key, int by, long l, int exp) {
        return withProfile("decr", key, {
            return memcachedClient.decr(key, by, l, exp)
        })
    }

    long incr(String key, long by, long l) {
        return withProfile("incr", key, {
            return memcachedClient.incr(key, by, l)
        })
    }

    long incr(String key, int by, long l) {
        return withProfile("incr", key, {
            return memcachedClient.incr(key, by, l)
        })
    }

    long decr(String key, long by, long l) {
        return withProfile("decr", key, {
            return memcachedClient.decr(key, by, l)
        })
    }

    long decr(String key, int by, long l) {
        return withProfile("decr", key, {
            return memcachedClient.decr(key, by, l)
        })
    }

    void deleteAsync(String key) {
        ProfilingHelper.begin("MC", "%s %s", "delete-async", key);
        try {
            memcachedClient.delete(key)
            ProfilingHelper.end("(OK) (async)")
        } catch (Throwable ex) {
            ProfilingHelper.err(ex)
            throw ex
        }
    }

    Future<Boolean> delete(String key) {
        return new FutureWrapper<Boolean>("delete", key, {
            return memcachedClient.delete(key)
        })
    }

    Future<Boolean> delete(String key, long cas) {
        return new FutureWrapper<Boolean>("delete", key, {
            return memcachedClient.delete(key, cas)
        })
    }

    Future<Boolean> flush(int delay) {
        return memcachedClient.flush(delay)
    }

    Future<Boolean> flush() {
        return memcachedClient.flush()
    }

    void shutdown() {
        memcachedClient.shutdown()
    }

    boolean shutdown(long timeout, TimeUnit unit) {
        return memcachedClient.shutdown(timeout, unit)
    }

    //endregion
}
