/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.clientproxy.impl.avalara;

/**
 * Created by LinYi on 14-3-10.
 */
public enum DocType {
    // This is a temporary document type and is not saved in tax history.
    // GetTaxResult will return with a DocStatus of Temporary.
    SalesOrder,
    // The document is a permanent invoice; document and tax calculation results are saved in the tax history.
    // GetTaxResult will return with a DocStatus of Saved.
    SalesInvoice,
    // This is a temporary document type and is not saved in tax history.
    // GetTaxResult will return with a DocStatus of Temporary.
    ReturnOrder,
    // The document is a permanent sales return invoice;
    // document and tax calculation results are saved in the tax history.
    // GetTaxResult will return with a DocStatus of Saved.
    ReturnInvoice,
    // This is a temporary document type and is not saved in tax history.
    // GetTaxResult will return with a DocStatus of Temporary.
    PurchaseOrder,
    // The document is a permanent invoice; document and tax calculation results are saved in the tax history.
    // GetTaxResult will return with a DocStatus of Saved.
    PurchaseInvoice
}
