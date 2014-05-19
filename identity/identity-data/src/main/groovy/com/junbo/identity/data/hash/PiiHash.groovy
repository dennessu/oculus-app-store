package com.junbo.identity.data.hash

import com.fasterxml.jackson.databind.JsonNode
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/16/14.
 */
@CompileStatic
interface PiiHash {
    boolean handles(String piiType)
    String generateHash(JsonNode jsonNode)
    String generateHash(String key)
}