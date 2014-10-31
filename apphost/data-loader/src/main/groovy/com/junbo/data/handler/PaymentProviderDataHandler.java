package com.junbo.data.handler;

import com.junbo.langur.core.client.TypeReference;
import com.junbo.payment.db.repository.PaymentProviderRepository;
import com.junbo.payment.spec.internal.PaymentProviderModel;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;

/**
 * Created by wenzhumac on 10/31/14.
 */
public class PaymentProviderDataHandler extends BaseDataHandler {
    private PaymentProviderRepository paymentProvideRepository;

    @Override
    public Resource[] resolveDependencies(Resource[] resources) {
        return resources;
    }

    @Override
    public void handle(String content) {
        PaymentProviderModel paymentProvider = null;
        try {
            paymentProvider = transcoder.decode(new TypeReference<PaymentProviderModel>() {}, content);
        } catch (Exception e) {
            logger.error("Error parsing payment provider:" + content, e);
            exit();
        }

        PaymentProviderModel existing = paymentProvideRepository.get(paymentProvider.getId());

        if (existing != null) {
            if (alwaysOverwrite) {
                logger.debug("Overwrite Payment Provider:" + paymentProvider.getId());
                try {
                    paymentProvideRepository.update(paymentProvider, existing);
                } catch (Exception e) {
                    logger.error("Error updating Payment Provider" + paymentProvider.getId(), e);
                }
            } else {
                logger.debug("The Payment Provider:" + paymentProvider.getId());
            }
        } else {
            logger.debug("Create new Payment Provider:" + paymentProvider.getId());
            try {
                paymentProvideRepository.save(paymentProvider);
            } catch (Exception e) {
                logger.error("Error creating Payment Provider:" + paymentProvider.getId(), e);
            }
        }
    }

    @Required
    public void setPaymentProvideRepository(PaymentProviderRepository paymentProvideRepository) {
        this.paymentProvideRepository = paymentProvideRepository;
    }

}
