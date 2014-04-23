package com.junbo.apphost.core

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by kg on 4/21/2014.
 */
@CompileStatic
class ResourcePackagesProviderImpl implements ResourcePackagesProvider {

    private List<String> packages

    @Required
    void setPackages(String packages) {

        this.packages = []

        if (packages != null) {
            for (String item : packages.split(',')) {
                item = item.trim()

                if (!item.empty) {
                    this.packages.add(item)
                }
            }
        }
    }

    List<String> getPackages() {
        return packages
    }
}
