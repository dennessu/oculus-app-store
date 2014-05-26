/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.WalletId;
import com.junbo.common.model.Results;
import com.junbo.ewallet.spec.model.*;
import com.junbo.langur.core.InProcessCallable;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Wallet Resource API.
 */
//@Api("wallets")
@Path("/wallets")
@RestResource
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@InProcessCallable
public interface WalletResource {
    //    @ApiOperation("Get a wallet by id")
    @GET
    @Path("/{walletId}")
    Promise<Wallet> getWallet(@PathParam("walletId") WalletId walletId);

    //    @ApiOperation("Get all wallets for a user")
    @GET
    Promise<Results<Wallet>> getWallets(@QueryParam("userId") UserId userId);

    //    @ApiOperation("Create a new wallet")
    @POST
    Promise<Wallet> postWallet(Wallet wallet);

    //    @ApiOperation("Update a wallet")
    @PUT
    @Path("/{walletId}")
    Promise<Wallet> updateWallet(@PathParam("walletId") WalletId walletId, Wallet wallet);

    //    @ApiOperation("Credit a wallet")
    @POST
    @Path("/credit")
    Promise<Transaction> credit(CreditRequest creditRequest);

    //    @ApiOperation("Debit a wallet")
    @POST
    @Path("/{walletId}/debit")
    Promise<Transaction> debit(@PathParam("walletId") WalletId walletId, DebitRequest debitRequest);

    @POST
    @Path("/refund/{transactionId}")
    Promise<Transaction> refund(@PathParam("transactionId") Long transactionId, RefundRequest refundRequest);

    //    @ApiOperation("Get transactions of a wallet")
    @GET
    @Path("/{walletId}/transactions")
    Promise<Results<Transaction>> getTransactions(@PathParam("walletId") WalletId walletId);
}
