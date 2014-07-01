/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.crypto.spec.model;

import com.junbo.common.id.ItemCryptoId;
import com.junbo.common.model.ResourceMeta;

/**
 * Created by liangfu on 6/30/14.
 */
public class ItemCryptoRepoData extends ResourceMeta<ItemCryptoId> {

    private ItemCryptoId id;

    private String itemId;

    private String encryptedPublicKey;

    private String encryptedPrivateKey;

    @Override
    public ItemCryptoId getId() {
        return id;
    }

    @Override
    public void setId(ItemCryptoId id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getEncryptedPublicKey() {
        return encryptedPublicKey;
    }

    public void setEncryptedPublicKey(String encryptedPublicKey) {
        this.encryptedPublicKey = encryptedPublicKey;
    }

    public String getEncryptedPrivateKey() {
        return encryptedPrivateKey;
    }

    public void setEncryptedPrivateKey(String encryptedPrivateKey) {
        this.encryptedPrivateKey = encryptedPrivateKey;
    }
}
