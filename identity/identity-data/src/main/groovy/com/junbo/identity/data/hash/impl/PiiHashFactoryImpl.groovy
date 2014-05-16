package com.junbo.identity.data.hash.impl

import com.junbo.identity.data.hash.PiiHash
import com.junbo.identity.data.hash.PiiHashFactory
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 5/16/14.
 */
@CompileStatic
class PiiHashFactoryImpl implements PiiHashFactory {

    private final List<PiiHash> piiHashList

    PiiHashFactoryImpl(List<PiiHash> piiHashList) {
        this.piiHashList = piiHashList
    }

    @Override
    List<PiiHash> getAllPiiHashes() {
        return this.piiHashList
    }
}
