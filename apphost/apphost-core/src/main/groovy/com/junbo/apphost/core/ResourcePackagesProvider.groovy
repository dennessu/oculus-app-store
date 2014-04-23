package com.junbo.apphost.core

import groovy.transform.CompileStatic

/**
 * Created by kg on 4/21/2014.
 */
@CompileStatic
interface ResourcePackagesProvider {

    List<String> getPackages()
}
