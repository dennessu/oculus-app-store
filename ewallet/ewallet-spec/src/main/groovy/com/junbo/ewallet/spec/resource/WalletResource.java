/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.WalletId;
import com.junbo.ewallet.spec.model.CreditRequest;
import com.junbo.ewallet.spec.model.DebitRequest;
import com.junbo.ewallet.spec.model.ResultList;
import com.junbo.ewallet.spec.model.Wallet;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import groovy.transform.CompileStatic;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Wallet Resource API.
 */
@Path("/")
@RestResource
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CompileStatic
public interface WalletResource {
    @GET
    @Path("wallets/{walletId}")
    Promise<Wallet> getWallet(@PathParam("walletId") WalletId walletId);

    @GET
    @Path("users/{userId}/wallets")
    Promise<ResultList<Wallet>> getWallets(@PathParam("userId") UserId userId);

    @PUT
    @Path("wallets/{walletId}")
    Promise<Wallet> updateWallet(@PathParam("walletId") WalletId walletId, Wallet wallet);

    @POST
    @Path("wallets/{walletId}/credit")
    Promise<Wallet> credit(@PathParam("walletId") WalletId walletId, CreditRequest creditRequest);

    @POST
    @Path("wallets/{walletId}/debit")
    Promise<Wallet> debit(@PathParam("walletId") WalletId walletId, DebitRequest debitRequest);

    @GET
    @Path("wallets/{walletId}/transactions")
    Promise<Wallet> getTransactions(@PathParam("walletId") WalletId walletId);
}
