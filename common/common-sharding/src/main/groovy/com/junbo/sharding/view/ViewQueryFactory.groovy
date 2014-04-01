package com.junbo.sharding.view

import com.junbo.common.util.Identifiable

/**
 * Created by kg on 3/30/2014.
 */
interface ViewQueryFactory {

    public <ID, E extends Identifiable<ID>> ViewQuery<ID> from(E entityExample)
}
