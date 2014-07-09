package com.junbo.ewallet.core;

import com.junbo.common.error.AppErrorException;
import com.junbo.ewallet.common.util.Callback;
import com.junbo.ewallet.db.repo.facade.WalletRepositoryFacade;
import com.junbo.ewallet.service.WalletService;
import com.junbo.ewallet.service.impl.TransactionSupport;
import com.junbo.ewallet.spec.error.ErrorMessages;
import com.junbo.ewallet.spec.model.CreditRequest;
import com.junbo.ewallet.spec.model.DebitRequest;
import com.junbo.ewallet.spec.model.Wallet;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by x on 6/20/14.
 */
@ContextConfiguration(locations = {"classpath*:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = true)
public class WalletDebitTransactionTest extends AbstractTestNGSpringContextTests {
    @Autowired
    @Qualifier("oculus48IdGenerator")
    protected IdGenerator idGenerator;

    @Autowired
    private TransactionSupport transactionSupport;
    @Autowired
    private WalletService walletService;
    @Autowired
    private WalletRepositoryFacade walletRepo;

    @Test
    public void testExpiredWalletLot() {
        final Wallet[] wallet = new Wallet[1];
        transactionSupport.executeInNewTransaction(new Callback() {
            @Override
            public void apply() {
                wallet[0] = walletService.add(buildAWallet());
                CreditRequest creditRequest = buildACreditRequest();
                creditRequest.setAmount(new BigDecimal(10));
                creditRequest.setCreditType(com.junbo.ewallet.spec.def.WalletLotType.CASH.toString());
                walletRepo.credit(wallet[0], creditRequest);
                creditRequest.setCreditType(com.junbo.ewallet.spec.def.WalletLotType.PROMOTION.toString());
                creditRequest.setExpirationDate(new Date(new Date().getTime() - 20000000));
                wallet[0] = walletService.get(wallet[0].getId());
                walletRepo.credit(wallet[0], creditRequest);
            }
        });

        final DebitRequest debitRequest = buildADebitRequest();
        debitRequest.setAmount(new BigDecimal(17));
        try {
            transactionSupport.executeInNewTransaction(new Callback() {
                @Override
                public void apply() {
                    walletService.debit(wallet[0].getId(), debitRequest);
                }
            });
        } catch (AppErrorException e) {
            Assert.assertEquals(e.getError().error().getMessage(), ErrorMessages.INSUFFICIENT_FUND);
            transactionSupport.executeInNewTransaction(new Callback() {
                @Override
                public void apply() {
                    Assert.assertEquals(walletRepo.get(wallet[0].getId()).getBalance(), new BigDecimal(10));
                }
            });
        }
    }

    private Wallet buildAWallet() {
        Wallet wallet = new Wallet();
        wallet.setUserId(idGenerator.nextId());
        wallet.setType(com.junbo.ewallet.spec.def.WalletType.STORED_VALUE.toString());
        wallet.setCurrency(com.junbo.ewallet.spec.def.Currency.USD.toString());
        wallet.setBalance(BigDecimal.ZERO);
        return wallet;
    }

    private CreditRequest buildACreditRequest() {
        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setCreditType(com.junbo.ewallet.spec.def.WalletLotType.CASH.toString());
        creditRequest.setOfferId(idGenerator.nextId());
        creditRequest.setAmount(new BigDecimal(10));
        return creditRequest;
    }

    private DebitRequest buildADebitRequest() {
        DebitRequest request = new DebitRequest();
        request.setAmount(new BigDecimal(17));
        request.setOfferId(idGenerator.nextId());
        return request;
    }
}
