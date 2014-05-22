package com.junbo.order.db.repo.util

import groovy.transform.CompileStatic
/**
 * Created by fzhang on 4/11/2014.
 */
@CompileStatic
class RepositoryFuncSet {
    Closure create
    Closure update
    Closure delete
}
