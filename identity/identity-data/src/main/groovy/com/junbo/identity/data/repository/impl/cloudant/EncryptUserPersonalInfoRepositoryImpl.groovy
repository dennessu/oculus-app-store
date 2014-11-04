package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.client.CloudantDbUri
import com.junbo.common.cloudant.client.CloudantUri
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.UserPersonalInfoId
import com.junbo.common.shuffle.Oculus48Id
import com.junbo.configuration.topo.DataCenters
import com.junbo.identity.data.repository.EncryptUserPersonalInfoRepository
import com.junbo.identity.spec.v1.model.EncryptUserPersonalInfo
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
/**
 * Created by liangfu on 5/14/14.
 */
@CompileStatic
class EncryptUserPersonalInfoRepositoryImpl extends CloudantClient<EncryptUserPersonalInfo>
        implements EncryptUserPersonalInfoRepository {
    private static Logger logger = LoggerFactory.getLogger(EncryptUserPersonalInfoRepositoryImpl.class)

    // force lookup in two home DC URL to ensure code coverage
    private boolean forceRoute

    void setForceRoute(boolean forceRoute) {
        this.forceRoute = forceRoute
    }

    @Override
    Promise<EncryptUserPersonalInfo> create(EncryptUserPersonalInfo model) {
        if (model.id == null) {
            throw new RuntimeException("EncryptUserPersonalInfo.id should be filled with UserPersonalInfo.id.")
        }
        return cloudantPost(model)
    }

    @Override
    Promise<EncryptUserPersonalInfo> update(EncryptUserPersonalInfo model, EncryptUserPersonalInfo oldModel) {
        if (model.id == null) {
            throw new RuntimeException("EncryptUserPersonalInfo.id should be filled with UserPersonalInfo.id.")
        }
        return cloudantPut(model, oldModel)
    }

    @Override
    Promise<EncryptUserPersonalInfo> get(UserPersonalInfoId id) {
        return cloudantGetForUserPersonalInfo(id.toString())
    }

    @Override
    Promise<Void> delete(UserPersonalInfoId id) {
        return cloudantDelete(id.toString())
    }

    public CloudantDbUri getDbUri(String id) {
        // get the home DC
        CloudantUri uri = cloudantGlobalUri.currentDcUri
        if (id != null) {
            try {
                Long value = Long.parseLong(id)
                if (value != null) {
                    int dc = getDcById(value.longValue())
                    uri = cloudantGlobalUri.getUri(dc)
                    if (uri == null) {
                        logger.error("Cloudant URI not found for datacenter: $dc id: $id")
                        throw AppCommonErrors.INSTANCE.invalidId("id", Oculus48Id.format(value)).exception()
                    }
                }
            } catch (NumberFormatException ex) {
                logger.error("Error parsing id to long. id: $id")
                throw AppCommonErrors.INSTANCE.invalidId("id", id).exception()
            }
        }
        return new CloudantDbUri(cloudantUri: uri, dbName: dbName, fullDbName: cloudantDbUri.fullDbName)
    }

    private Promise<EncryptUserPersonalInfo> cloudantGetForUserPersonalInfo(String id) {
        // search local data center first, then try the home data center.
        return getEffective().cloudantGet(cloudantDbUri, entityClass, id).then { EncryptUserPersonalInfo result ->
            if (result == null || forceRoute) {
                // not found in local DC, try the home DC
                Long value = Long.parseLong(id);
                if (value != null) {
                    int dc = getDcById(value.longValue())
                    if (DataCenters.instance().isLocalDataCenter(dc) && !forceRoute) {
                        // already in the home dc of the token, no need to retry
                        return Promise.pure();
                    }
                    def fallbackDbUri = getDbUriByDc(cloudantGlobalUri, dc)
                    if (fallbackDbUri == null) {
                        logger.error("Cloudant URI not found for datacenter: $dc id: $id")
                        return Promise.pure();
                    }
                    return getEffective().cloudantGet(fallbackDbUri, entityClass, id);
                }
            }
            return Promise.pure(result);
        }
    }

    public static int getDcById(long id) {
        return (int) ((id >> 2) & 0xF);
    }
}
