package com.junbo.identity.core.service.normalize

import groovy.transform.CompileStatic

/**
 * Created by liangfu on 4/10/14.
 */
@CompileStatic
interface NormalizeService {
    String normalize(String name)
}