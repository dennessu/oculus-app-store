package com.junbo.sharding

import groovy.transform.CompileStatic

/**
 * Created by Shenhua on 4/1/2014.
 */
@CompileStatic
class DefaultHashAlgorithm implements HashAlgorithm {

    int hash(Object key) {
        if (key == null) {
            throw new IllegalArgumentException('key is null')
        }

        if (key instanceof Integer) {
            return (int) key
        }

        if (key instanceof Long) {
            long longKey = (long) key

            return (int) (longKey ^ (longKey >>> 32))
        }

        if (key instanceof String) {
            String strKey = (String) key

            int h = 0
            for (int i = 0; i < strKey.size(); i++) {
                h = 31 * h + ((int) strKey.charAt(i))
            }

            return h;
        }

        throw new IllegalArgumentException('unknown key type ' + key.class)
    }
}

