package com.junbo.data.handler

import com.junbo.common.enumid.CurrencyId
import com.junbo.common.error.AppErrorException
import com.junbo.identity.spec.v1.model.Currency
import com.junbo.identity.spec.v1.option.model.CurrencyGetOptions
import com.junbo.identity.spec.v1.resource.CurrencyResource
import com.junbo.langur.core.client.TypeReference
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by haomin on 14-6-3.
 */
@CompileStatic
class CurrencyDataHandler extends BaseDataHandler {
    private CurrencyResource currencyResource

    @Required
    void setCurrencyResource(CurrencyResource currencyResource) {
        this.currencyResource = currencyResource
    }

    @Override
    void handle(String content) {
        Currency currency = null

        try {
            currency = transcoder.decode(new TypeReference<Currency>() {}, content) as Currency
        } catch (Exception e) {
            logger.warn('Error parsing Currency, skip this content:' + content, e)
            return
        }

        Currency existing = null
        try {
            existing = currencyResource.get(new CurrencyId(currency.currencyCode), new CurrencyGetOptions()).get()
        } catch (AppErrorException e) {
            logger.debug('This content does not exist in current database', e)
        }

        if (existing != null) {
            if (alwaysOverwrite) {
                logger.debug("Overwrite Currency $currency.currencyCode with this content.")
                currency.id = (CurrencyId)existing.id
                currency.rev = existing.rev
                currencyResource.put(new CurrencyId(currency.currencyCode), currency)
            } else {
                logger.debug("$currency.currencyCode already exists, skipped!")
            }
        } else {
            logger.debug('Create new currency with this content')
            currencyResource.create(currency)
        }
    }
}
