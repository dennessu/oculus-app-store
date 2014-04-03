/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.spec.resource;

import com.junbo.common.id.WalletId;
import com.junbo.ewallet.spec.model.CreditRequest;
import com.junbo.ewallet.spec.model.DebitRequest;
import com.junbo.ewallet.spec.model.Wallet;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Wallet Resource API.
 */
@Api("wallets")
@Path("/wallets")
@RestResource
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface WalletResource {
    @ApiOperation("Get a wallet by id")
    @GET
    @Path("/{walletId}")
    Promise<Wallet> getWallet(@PathParam("walletId") WalletId walletId);

    @ApiOperation("Create a new wallet")
    @POST
    Promise<Wallet> postWallet(Wallet wallet);

    @ApiOperation("Update a wallet")
    @PUT
    @Path("/{walletId}")
    Promise<Wallet> updateWallet(@PathParam("walletId") WalletId walletId, Wallet wallet);

    @ApiOperation("Credit a wallet")
    @POST
    @Path("/credit")
    Promise<Wallet> credit(CreditRequest creditRequest);

    @ApiOperation("Debit a wallet")
    @POST
    @Path("/{walletId}/debit")
    Promise<Wallet> debit(@PathParam("walletId") WalletId walletId, DebitRequest debitRequest);

    @ApiOperation("Get transactions of a wallet")
    @GET
    @Path("/{walletId}/transactions")
    Promise<Wallet> getTransactions(@PathParam("walletId") WalletId walletId);
}
