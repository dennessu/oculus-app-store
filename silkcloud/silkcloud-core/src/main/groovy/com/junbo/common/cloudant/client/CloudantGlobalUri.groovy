package com.junbo.common.cloudant.client

import com.junbo.common.util.Utils
import com.junbo.configuration.topo.DataCenters
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * CloudantGlobalUri.
 */
@CompileStatic
class CloudantGlobalUri {
    private Map<String, CloudantUri> dcUriMap = new HashMap<>()
    private CloudantUri currentDcUri

    private static String URI_PATTERN_STR = '^(?<protocol>http[s]?://)?((?<username>[^/@:]*):((?<account>[^/@:]*):)?(?<password>[^/@:]*)@)?(?<host>[^/:]+)(?<port>:\\d+)?(?<path>(/|\\?).*)?$'
    private static final Pattern URI_PATTERN = Pattern.compile(URI_PATTERN_STR);

    public CloudantGlobalUri(String uriConfig) {
        def values = Utils.parsePerDataCenterConfig(uriConfig, "cloudantUris")

        for (String key : values.keySet()) {
            String uri = values.get(key)
            Matcher matcher = URI_PATTERN.matcher(uri)
            if (!matcher.matches()) {
                throw new RuntimeException("Invalid cloudant URI: " + uri)
            }

            CloudantUri cloudantUri = new CloudantUri()
            cloudantUri.dc = key
            cloudantUri.value = safeGetGroup(matcher, "protocol") + safeGetGroup(matcher, "host") + safeGetGroup(matcher, "port") + safeGetGroup(matcher, "path")
            cloudantUri.username = matcher.group("username")
            cloudantUri.password = matcher.group("password")
            cloudantUri.account = matcher.group("account")
            dcUriMap.put(key, cloudantUri)
        }
        currentDcUri = dcUriMap.get(DataCenters.instance().currentDataCenter())
        if (currentDcUri == null) {
            throw new RuntimeException("Failed to get cloudant URI from input '$uriConfig' for current DC '${DataCenters.instance().currentDataCenter()}'")
        }
    }

    public CloudantUri getUri(int dc) {
        String dcName = DataCenters.instance().getDataCenter(dc).name
        return dcUriMap.get(dcName)
    }

    public CloudantUri getCurrentDcUri() {
        return dcUriMap.get(DataCenters.instance().currentDataCenter())
    }

    private static String safeGetGroup(Matcher matcher, String group) {
        String value = matcher.group(group)
        if (value == null) {
            return ""
        } else {
            return value
        }
    }
}
