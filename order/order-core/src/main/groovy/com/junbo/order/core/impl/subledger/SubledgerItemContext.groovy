package com.junbo.order.core.impl.subledger

import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.id.OfferId
import com.junbo.common.id.OrganizationId
import groovy.transform.CompileStatic

/**
 * Created by fzhang on 4/10/2014.
 */
@CompileStatic
class SubledgerItemContext {

    OrganizationId seller

    OfferId offer

    CurrencyId currency

    CountryId country

    Date createdTime
}
