package com.junbo.ewallet.core;

import com.junbo.common.error.AppErrorException;
import com.junbo.ewallet.common.util.Callback;
import com.junbo.ewallet.db.repo.facade.WalletRepositoryFacade;
import com.junbo.ewallet.service.WalletService;
import com.junbo.ewallet.service.impl.TransactionSupport;
import com.junbo.ewallet.spec.error.ErrorMessages;
import com.junbo.ewallet.spec.model.*;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.ws.rs.WebApplicationException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by x on 6/20/14.
 */
@ContextConfiguration(locations = {"classpath*:spring/context-test.xml"})
@TransactionConfiguration(defaultRollback = true)
public class WalletServiceTest extends AbstractTestNGSpringContextTests {
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

    @Test
    public void testAdd() {
        Wallet wallet = buildAWallet();
        Wallet inserted = walletService.add(wallet);
        Assert.assertNotNull(inserted.getId());
        Assert.assertEquals(inserted.getBalance(), wallet.getBalance());
    }

    @Test
    public void testCreditAndDebit() {
        Wallet wallet = walletService.add(buildAWallet());
        CreditRequest creditRequest = buildACreditRequest();
        creditRequest.setWalletId(wallet.getId());
        walletService.credit(creditRequest);
        creditRequest.setCreditType(com.junbo.ewallet.spec.def.WalletLotType.CASH.toString());
        walletService.credit(creditRequest);
        wallet = walletService.get(wallet.getId());
        Assert.assertEquals(wallet.getBalance(), new BigDecimal(20));
        DebitRequest debitRequest = buildADebitRequest();
        walletService.debit(wallet.getId(), debitRequest);
        wallet = walletService.get(wallet.getId());
        Assert.assertEquals(wallet.getBalance(), new BigDecimal(3));
    }

    @Test
    public void testRefund() {
        Wallet wallet = walletService.add(buildAWallet());
        CreditRequest creditRequest = buildACreditRequest();
        creditRequest.setWalletId(wallet.getId());
        walletService.credit(creditRequest);

        DebitRequest debitRequest = buildADebitRequest();
        debitRequest.setAmount(new BigDecimal(5));
        walletService.debit(wallet.getId(), debitRequest);

        Transaction transaction = walletService.getTransactions(wallet.getId()).get(1);
        wallet = walletService.get(wallet.getId());
        Assert.assertEquals(wallet.getBalance(), new BigDecimal(5));

        RefundRequest refundRequest = buildARefundRequest(transaction.getId());
        walletService.refund(transaction.getId(), refundRequest);
        wallet = walletService.get(wallet.getId());
        Assert.assertEquals(wallet.getBalance(), new BigDecimal(7));

        walletService.refund(transaction.getId(), refundRequest);
        wallet = walletService.get(wallet.getId());
        Assert.assertEquals(wallet.getBalance(), new BigDecimal(9));
    }

    @Test(expectedExceptions = WebApplicationException.class)
    public void testExpiredCredit() {
        Wallet wallet = walletService.add(buildAWallet());
        CreditRequest creditRequest = buildACreditRequest();
        creditRequest.setWalletId(wallet.getId());
        creditRequest.setExpirationDate(new Date(100000));
        creditRequest.setAmount(new BigDecimal(100));
        walletService.credit(creditRequest);
    }

    @Test(expectedExceptions = WebApplicationException.class)
    public void testNotEnoughMoney() {
        Wallet wallet = walletService.add(buildAWallet());
        CreditRequest creditRequest = buildACreditRequest();
        walletService.credit(creditRequest);
        DebitRequest debitRequest = buildADebitRequest();
        walletService.debit(wallet.getId(), debitRequest);
    }

    @Test
    public void testGetTransactions() {
        Wallet wallet = walletService.add(buildAWallet());
        CreditRequest creditRequest = buildACreditRequest();
        creditRequest.setWalletId(wallet.getId());
        walletService.credit(creditRequest);
        creditRequest.setCreditType(com.junbo.ewallet.spec.def.WalletLotType.CASH.toString());
        walletService.credit(creditRequest);
        DebitRequest debitRequest = buildADebitRequest();
        walletService.debit(wallet.getId(), debitRequest);
        List<Transaction> transactionList = walletService.getTransactions(wallet.getId());
        Assert.assertEquals(transactionList.size(), 3);
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

    private RefundRequest buildARefundRequest(Long transactionId) {
        RefundRequest request = new RefundRequest();
        request.setAmount(new BigDecimal(2));
        request.setCurrency("USD");
        return request;
    }

}
