package com.junbo.identity.data.hash

import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/16/14.
 */
@CompileStatic
interface PiiHashFactory {
    List<PiiHash> getAllPiiHashes()
}