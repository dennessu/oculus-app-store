package com.junbo.apphost.core.health
import com.junbo.common.health.ping.Ping
import com.junbo.common.health.ping.PingCloudantRepo
import com.junbo.common.health.ping.PingSqlRepo
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.configuration.topo.DataCenters
import com.junbo.crypto.spec.model.CryptoMessage
import com.junbo.crypto.spec.resource.CryptoResource
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Required
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionCallback
import org.springframework.transaction.support.TransactionTemplate

import javax.ws.rs.core.Response
import java.util.concurrent.TimeUnit

/**
 * Created by kgu on 6/3/14.
 */
@CompileStatic
class HealthService {
    private static final Logger logger = LoggerFactory.getLogger(HealthService.class);

    private List<PingSqlRepo> pingSqlRepos;
    private PingCloudantRepo pingCloudantRepo;
    private long cloudantGetRemoteTimeout;
    private long cloudantGetRemoteInterval;
    private long cloudantSnifferTimeout;
    private long cloudantSnifferInterval;

    private CryptoResource cryptoResource;

    private PlatformTransactionManager transactionManager

    @Autowired(required = false)
    void setPingSqlRepos(List<PingSqlRepo> pingSqlRepos) {
        this.pingSqlRepos = pingSqlRepos
    }

    @Required
    void setPingCloudantRepo(PingCloudantRepo pingCloudantRepo) {
        this.pingCloudantRepo = pingCloudantRepo
    }

    @Required
    void setCloudantGetRemoteTimeout(long getRemoteTimeout) {
        this.cloudantGetRemoteTimeout = getRemoteTimeout
    }

    @Required
    void setCloudantGetRemoteInterval(long getRemoteInterval) {
        this.cloudantGetRemoteInterval = getRemoteInterval
    }

    @Required
    void setCloudantSnifferTimeout(long cloudantSnifferTimeout) {
        this.cloudantSnifferTimeout = cloudantSnifferTimeout
    }

    @Required
    void setCloudantSnifferInterval(long cloudantSnifferInterval) {
        this.cloudantSnifferInterval = cloudantSnifferInterval
    }

    @Autowired(required = false)
    @Qualifier("identityCryptoResource")
    void setCryptoResource(CryptoResource cryptoResource) {
        this.cryptoResource = cryptoResource
    }

    @Required
    void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager
    }

    public Promise<Response> testHealth() {
        try {
            Map result = createResultMap();
            Map context = new LinkedHashMap();

            putResult("sql", result, testSqlConnections(context));
            return testCloudantConnections(context).then { Map response ->
                putResult("cloudant", result, response);
                return testCryptoResource();
            }.syncThen { Map response ->
                putResult("crypto", result, response);

                if ("WARN".equalsIgnoreCase((String) result.get("result"))) {
                    logger.warn("Warning in health test. Result:\n {}", ObjectMapperProvider.instance().writeValueAsString(result));
                }

                if ("FAILED".equalsIgnoreCase((String) result.get("result"))) {
                    logger.error("Error in health test. Result:\n {}", ObjectMapperProvider.instance().writeValueAsString(result));
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
                } else {
                    return Response.ok(result).build();
                }
            }.recover { Throwable ex ->
                logger.error("Error testing health.", ex);
                throw ex;
            }
        } catch (Exception ex) {
            logger.error("Error testing health.", ex);
            return Promise.throwing(ex);
        }
    }

    //#region test sql connections
    public Map testSqlConnections(Map context) {
        return measureStep { Map result ->
            for (PingSqlRepo sqlRepo : pingSqlRepos) {
                Map resultSql = new LinkedHashMap<>();

                Ping ping = new Ping();
                ping.setMessage("ping " + System.currentTimeMillis());
                context.put("ping", ping);

                try {
                    if (!putResult("create", resultSql, testSqlCreate(sqlRepo, context))) continue;
                    if (!putResult("get", resultSql, testSqlGet(sqlRepo, context))) continue;
                    if (!putResult("update", resultSql, testSqlUpdate(sqlRepo, context))) continue;
                    if (!putResult("delete", resultSql, testSqlDelete(sqlRepo, context))) continue;
                } finally {
                    putResult(sqlRepo.getDbName(), result, resultSql);
                }
            }
        }
    }

    private Map testSqlCreate(PingSqlRepo sqlRepo, Map context) {
        return measureStep {
            executeInNewTransaction {
                sqlRepo.create((Ping) context.get("ping"));
            }
        };
    }

    private Map testSqlGet(PingSqlRepo sqlRepo, Map context) {
        return measureStep {
            executeInNewTransaction {
                Ping ping = (Ping) context.get("ping");
                Ping ping2 = sqlRepo.get(ping.getId());
                if (ping2 == null) {
                    throw new RuntimeException("ping entity not found in sql")
                }
                if (!ping2.getMessage().equals(ping.getMessage())) {
                    throw new RuntimeException(String.format(
                            "ping message incorrect. expected: %s actual: %s", ping.getMessage(), ping2.getMessage()));
                }
                context.put("ping2", ping2);
            }
        }
    }

    private Map testSqlUpdate(PingSqlRepo sqlRepo, Map context) {
        return measureStep {
            executeInNewTransaction {
                Ping ping2 = (Ping) context.get("ping2");
                ping2.setMessage("ping2 " + System.currentTimeMillis());
                Ping ping3 = sqlRepo.update(ping2);

                if (!ping3.getMessage().equals(ping2.getMessage())) {
                    throw new RuntimeException(String.format(
                            "ping message incorrect. expected: %s actual: %s", ping2.getMessage(), ping3.getMessage()));
                }
            }
        }
    }

    private Map testSqlDelete(PingSqlRepo sqlRepo, Map context) {
        return measureStep {
            executeInNewTransaction {
                Ping ping2 = (Ping) context.get("ping2");
                sqlRepo.delete(ping2.getId());
            }
        }
    }

    //#endregion

    //#region test cloudant connections
    public Promise<Map> testCloudantConnections(Map context) {
        return measureStepAsync { Map result ->
            Ping ping = new Ping();
            ping.setMessage("ping " + System.currentTimeMillis());
            context.put("ping", ping);

            return testCloudantCreate(context).syncThen { Map resultAction ->
                return putResult("create", result, resultAction);
            }.then { Boolean success ->
                if (success) {
                    return testCloudantGet(context).syncThen { Map resultAction ->
                        return putResult("get", result, resultAction);
                    };
                }
                return Promise.pure(false);
            }.then { Boolean success ->
                if (success) {
                    return testCloudantUpdate(context).syncThen { Map resultAction ->
                        return putResult("update", result, resultAction);
                    };
                }
                return Promise.pure(false);
            }.then { Boolean success ->
                if (success) {
                    return testCloudantDelete(context).syncThen { Map resultAction ->
                        return putResult("delete", result, resultAction);
                    };
                }
                return Promise.pure(false);
            }.then { Boolean success ->
                if (success) {
                    return testCloudantRemoteGet().syncThen { Map resultAction ->
                        return putResult("remoteGet", result, resultAction);
                    }
                }
                return Promise.pure(false);
            }.then { Boolean success ->
                if (success) {
                    return testCloudantSniffer().syncThen { Map resultAction ->
                        return putResult("sniffer", result, resultAction);
                    }
                }
            };
        }
    }

    private Promise<Map> testCloudantCreate(Map context) {
        return measureStepAsync {
            return pingCloudantRepo.create((Ping)context.get("ping")).syncThen { Ping ping2 ->
                context.put("ping", ping2)
            }
        }
    }

    private Promise<Map> testCloudantGet(Map context) {
        return measureStepAsync {
            Ping ping = (Ping)context.get("ping");
            return pingCloudantRepo.get(ping.getId()).syncThen { Ping ping2 ->
                if (ping2 == null) {
                    throw new RuntimeException("ping entity not found in sql")
                }
                if (!ping2.getMessage().equals(ping.getMessage())) {
                    throw new RuntimeException(String.format(
                            "ping message incorrect. expected: %s actual: %s", ping.getMessage(), ping2.getMessage()));
                }
                context.put("ping2", ping2);
            }
        }
    }

    private Promise<Map> testCloudantUpdate(Map context) {
        return measureStepAsync {
            Ping ping2 = (Ping)context.get("ping2");
            ping2.setMessage("ping2 " + System.currentTimeMillis());
            return pingCloudantRepo.update(ping2).syncThen { Ping ping3 ->
                if (!ping3.getMessage().equals(ping2.getMessage())) {
                    throw new RuntimeException(String.format(
                            "ping message incorrect. expected: %s actual: %s", ping2.getMessage(), ping3.getMessage()));
                }
            };
        }
    }

    private Promise<Map> testCloudantDelete(Map context) {
        return measureStepAsync {
            Ping ping2 = (Ping)context.get("ping2");
            pingCloudantRepo.delete(ping2.getId());
            return Promise.pure();
        }
    }

    private Promise<Map> testCloudantRemoteGet() {
        return measureStepAsync { Map result ->
            result.put("currentDc", DataCenters.instance().currentDataCenterId());
            result.put("remoteDc", pingCloudantRepo.getRemoteDcId());
            result.put("remoteUri", pingCloudantRepo.getRemoteDbUri().toString());

            Ping ping = new Ping();
            ping.setMessage("ping " + System.currentTimeMillis());

            long now = System.currentTimeMillis();
            Closure tryRemoteGet;
            tryRemoteGet = {
                return pingCloudantRepo.getRemote(ping.getId()).then { Ping ping2 ->
                    if (ping2 == null) {
                        if (System.currentTimeMillis() - now > cloudantGetRemoteTimeout) {
                            result.put("result", "WARN");
                            result.put("message", "Failed to get entity from remote DC.");
                            return Promise.pure();
                        }
                        return Promise.delayed(cloudantGetRemoteInterval, TimeUnit.MILLISECONDS).then(tryRemoteGet)
                    }
                    return Promise.pure(ping2);
                }
            }
            return pingCloudantRepo.create(ping).then { Ping created ->
                ping.cloudantId = created.cloudantId;
                ping.cloudantRev = created.cloudantRev;
                return tryRemoteGet().then {
                    pingCloudantRepo.delete(ping.getId());
                };
            };
        }
    }

    private Promise<Map> testCloudantSniffer() {
        return measureStepAsync { Map result ->
            Ping ping = new Ping();
            ping.setMessage("ping " + System.currentTimeMillis());

            long now = System.currentTimeMillis();
            Closure trySnifferGet;
            trySnifferGet = {
                return pingCloudantRepo.get(ping.getId()).then { Ping ping2 ->
                    if (ping2 != null) {
                        long elapsed = System.currentTimeMillis() - now
                        if (elapsed > cloudantSnifferTimeout) {
                            result.put("result", "WARN");
                            result.put("message", "Latency until Sniffer received DB change is now " + elapsed + "ms. Expected <= " + cloudantSnifferTimeout + "ms");
                            return Promise.pure();
                        }
                        return Promise.delayed(cloudantSnifferInterval, TimeUnit.MILLISECONDS).then(trySnifferGet)
                    }
                    return Promise.pure();
                }
            }
            return pingCloudantRepo.create(ping).then { Ping created ->
                ping.cloudantId = created.cloudantId;
                ping.cloudantRev = created.cloudantRev;
                return pingCloudantRepo.deleteIgnoreCache(ping.getId()).then(trySnifferGet);
            };
        }
    }

    //#endregion

    public Promise<Map> testCryptoResource() {
        Map result = createResultMap();

        if (cryptoResource == null) {
            return Promise.pure(result);
        }

        String message = "ping " + System.currentTimeMillis();
        CryptoMessage encryptedCryptoMessage;
        return measureStepAsync {
            CryptoMessage cryptoMessage = new CryptoMessage()
            cryptoMessage.value = message;
            return cryptoResource.encrypt(cryptoMessage).syncThen { CryptoMessage encrypted ->
                encryptedCryptoMessage = encrypted;
            };
        }.syncThen { Map resultAction ->
            putResult("encrypt", result, resultAction);
        }.then {
            return measureStepAsync {
                CryptoMessage cryptoMessage = new CryptoMessage()
                cryptoMessage.value = encryptedCryptoMessage.value;
                return cryptoResource.decrypt(cryptoMessage).syncThen { CryptoMessage decrypted ->
                    if (!message.equals(decrypted.value)) {
                        throw new RuntimeException(String.format(
                                "ping message incorrect. expected: %s actual: %s", message, decrypted.value
                        ));
                    }
                };
            }
        }.syncThen { Map resultAction ->
            putResult("decrypt", result, resultAction);
        }.syncThen {
            return result;
        }
    }

    private boolean putResult(String key, Map outerResult, Map result) {
        outerResult.put(key, result);
        String currentResult = outerResult.get("result");
        if ("FAILED".equalsIgnoreCase((String)result.get("result"))) {
            outerResult.put("result", "FAILED");
            return false;
        }
        if ("PASS".equalsIgnoreCase(currentResult) &&
            "WARN".equalsIgnoreCase((String)result.get("result"))) {
            outerResult.put("result", "WARN");
        }
        return true;
    }

    private Map measureStep(Closure step) {
        Map result = createResultMap();

        long now = System.currentTimeMillis();
        try {
            step(result);
        } catch (Exception ex) {
            result.put("result", "FAILED");
            result.put("exception", ex.toString());
            result.put("stacktrace", ExceptionUtils.getStackTrace(ex))
        } finally {
            long elapsed = System.currentTimeMillis() - now;
            result.put("elapsed", elapsed.toString() + " ms");
        }
        return result;
    }

    private Promise<Map> measureStepAsync(Closure step) {
        Map result = createResultMap();

        long now = System.currentTimeMillis();
        return Promise.pure().then {
            return step(result)
        }.syncRecover { Throwable ex ->
            result.put("result", "FAILED");
            result.put("exception", ex.toString());
            result.put("stacktrace", ExceptionUtils.getStackTrace(ex))
        }.then {
            long elapsed = System.currentTimeMillis() - now;
            result.put("elapsed", elapsed.toString() + " ms");
            return Promise.pure(result);
        };
    }

    private void executeInNewTransaction(final Closure callback) {
        TransactionTemplate template = new TransactionTemplate(transactionManager)
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW)

        template.execute(new TransactionCallback<Void>() {
            public Void doInTransaction(TransactionStatus status) {
                callback();
                return null;
            }
        })
    }

    private Map createResultMap() {
        Map result = new LinkedHashMap();
        result.put("result", "PASS")
        return result;
    }
}
