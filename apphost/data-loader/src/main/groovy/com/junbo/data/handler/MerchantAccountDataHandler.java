package com.junbo.data.handler;

import com.junbo.langur.core.client.TypeReference;
import com.junbo.payment.db.repository.MerchantAccountRepository;
import com.junbo.payment.spec.internal.MerchantAccount;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;

/**
 * Created by wenzhumac on 10/31/14.
 */
public class MerchantAccountDataHandler extends BaseDataHandler {

    private MerchantAccountRepository merchantAccountRepository;

    @Override
    public Resource[] resolveDependencies(Resource[] resources) {
        return resources;
    }

    @Override
    public void handle(String content) {
        MerchantAccount merchantAccount = null;
        try {
            merchantAccount = transcoder.decode(new TypeReference<MerchantAccount>() {
            }, content);
        } catch (Exception e) {
            logger.error("Error parsing merchant Account:" + content, e);
            exit();
        }

        MerchantAccount existing = merchantAccountRepository.get(merchantAccount.getMerchantAccountId());

        if (existing != null) {
            logger.debug("Overwrite merchant Account:" + merchantAccount.getMerchantAccountId());
            try {
                merchantAccountRepository.update(merchantAccount, existing);
            } catch (Exception e) {
                logger.error("Error updating merchant Account" + merchantAccount.getMerchantAccountId(), e);
            }
        } else {
            logger.debug("Create new merchant Account:" + merchantAccount.getMerchantAccountId());
            try {
                merchantAccountRepository.save(merchantAccount);
            } catch (Exception e) {
                logger.error("Error creating merchant Account:" + merchantAccount.getMerchantAccountId(), e);
            }
        }
    }

    @Required
    public void setMerchantAccountRepository(MerchantAccountRepository merchantAccountRepository) {
        this.merchantAccountRepository = merchantAccountRepository;
    }

}