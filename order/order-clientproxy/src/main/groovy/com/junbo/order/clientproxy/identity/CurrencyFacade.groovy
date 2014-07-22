package com.junbo.order.clientproxy.identity

import com.junbo.langur.core.promise.Promise

/**
 * Interface for Currency Facade.
 */
public interface CurrencyFacade {
    Promise<com.junbo.identity.spec.v1.model.Currency> getCurrency(String currency)
}