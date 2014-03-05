#Order
Order service APIs

##Post /orders
Create orders for a user with a full cart object.

+ Parameters

    + userId (=12345, required, long) ... indicates the user who posts the order.

+ Request (application/json)
    + Headers

            Delegate-User-Id: "54321"
            Requestor-Id: "CheckoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            Tracking-Uuid: "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            User-Ip: "157.123.45.67"

    + Body

           {
                PaymentMethods:
                [
                    {
                        PaymentMethodId: "12345"
                    }
                ]
                ShippingMethodId: "12345"
                ShippingAddressId: "12345"
                Locale: "en-US"
                Currency: "USD"
                Cart:
                {
                    CartId: "12345"
                    Items:
                    [
                        {
                            ItemId: "12345"
                            SkuId: "12345"
                            FederatedId: "abcde"
                            UnitPrice: "60.00"
                            Quantity: "1"
                            IsTaxExempt: "false"
                            Discounts:
                            [
                                {
                                    DiscountType: "OfferDiscount"
                                    DiscountAmount: "30.00"
                                    DiscountRate: "0.50"
                                    DiscountRuleId: "12345"
                                    DiscountCode: "ABCD-EFGH-0012"
                                }
                            ]
                            Properties:
                            [
                                {
                                    Namespace: "abc"
                                    Name: "def"
                                    Value: "123"
                                }
                            ]
                        }
                        SellerInfo:
                        {
                            SellerId: "12345"
                            SellerMode: "Agent"
                            RevenueAllocations:
                            [
                                {
                                    AllocationType: "Liability"
                                    AllocationAmount: "42.00"
                                    AllocationPercentage: "0.70"
                                }
                            ]
                        }
                    ]
                    Discounts:
                    [
                        {
                            DiscountType: "OrderDiscount"
                            DiscountAmount: "10.00"
                            DiscountRate: "0.16"
                            DiscountRuleId: "12345"
                            DiscountCode: "ABCD-EFGH-0012"
                        }
                    ]
                    Properties:
                    [
                        {
                            Namespace: "abc"
                            Name: "def"
                            Value: "123"
                        }
                    ]
                }
            }

+ Response 200 (application/json)

        {
            UserId: "12345"
            TrackingUuid: "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            Orders:
            [
                {
                    OrderId: "12345"
                    OrderStatus: "Completed"
                    OrderDate: "2014-01-25 17:08:07"
                    Locale: "en-US"
                    Currency: "USD"
                    BillingInfos:
                    [
                        {
                            BalanceUri: "balances/12345"
                            BalanceType: "Debit"
                            BalanceStatus: "Settled"
                            OrderAmount: "20.00"
                            TaxAmount: "2.00"
                            IsTaxInclusive: "false"
                            DiscountAmount: "40.00"
                            PaymentMethodId: "12345"
                        }
                    ]
                    Fulfillments:
                    [
                        {
                            FulfillmentUri: "/entitlements/12345"
                            FulfillmentType: "Entitlement"
                            FulfillmentStatus: "Fulfilled"
                        }
                    ]
                }
            ]
        }

##POST /orders
Create orders for a user with a shopping cart id.

+ Parameters

    + userId (=12345, required, long) ... indicates the user who checkout the shopping cart.

+ Request (application/json)
    + Headers

            Delegate-User-Id: "csr"
            Requestor-Id: "CheckoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            Tracking-Uuid: "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            User-Ip: "157.123.45.67"

    + Body

           {
                PaymentMethods:
                [
                    {
                        PaymentMethodId: "12345"
                    }
                ]
                CartId: "12345"
                CartChangeToken: "1"
                ShippingMethodId: "12345"
                ShippingAddressId: "12345"
                Locale: "en-US"
                Currency: "USD"
                Properties:
                [
                    {
                        Namespace: "abc"
                        Name: "def"
                        Value: "123"
                    }
                ]
                }
            }

+ Response 200 (application/json)

        {
            UserId: "12345"
            TrackingUuid: "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            Orders:
            [
                {
                    OrderId: "12345"
                    OrderStatus: "Completed"
                    OrderDate: "2014-01-25 17:08:07"
                    Locale: "en-US"
                    Currency: "USD"
                    BillingInfos:
                    [
                        {
                            BalanceUri: "balances/12345"
                            BalanceType: "Debit"
                            BalanceStatus: "Settled"
                            OrderAmount: "20.00"
                            TaxAmount: "2.00"
                            IsTaxInclusive: "false"
                            DiscountAmount: "40.00"
                            PaymentMethodId: "12345"
                        }
                    ]
                    Fulfillments:
                    [
                        {
                            FulfillmentUri: "/entitlements/12345"
                            FulfillmentType: "Entitlement"
                            FulfillmentStatus: "Fulfilled"
                        }
                    ]
                }
            ]
        }

##POST /orders
Quote order for a user with a shopping cart object.

+ Parameters

    + userId (=12345, required, long) ... indicates the user who checkout the shopping cart.
    + quote (=true, optional, boolean) ... quote the cart

+ Request (application/json)
    + Headers

            Delegate-User-Id: "csr"
            Requestor-Id: "CheckoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            Tracking-Uuid: "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            User-Ip: "157.123.45.67"

    + Body

           {
                ShippingMethodId: "12345"
                ShippingAddressId: "12345"
                Locale: "en-US"
                Currency: "USD"
                Cart:
                {
                    CartId: "12345"
                    Items:
                    [
                        {
                            ItemId: "12345"
                            SkuId: "12345"
                            FederatedId: "abcde"
                            UnitPrice: "60.00"
                            Quantity: "1"
                            IsTaxExempt: "false"
                            Properties:
                            [
                                {
                                    Namespace: "abc"
                                    Name: "def"
                                    Value: "123"
                                }
                            ]
                        }
                    ]
                    Coupons:
                    [
                        {
                            "AAAA-BBBB-CCCC-DDDD"
                        }
                    ]
                    Properties:
                    [
                        {
                            Namespace: "abc"
                            Name: "def"
                            Value: "123"
                        }
                    ]
                }
                Properties:
                [
                    {
                        Namespace: "abc"
                        Name: "def"
                        Value: "123"
                    }
                ]
                }
            }

+ Response 200 (application/json)

        {
            UserId: "12345"
            TrackingUuid: "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            Cart:
            {
                CartId: "12345"
                Items:
                [
                    {
                        ItemId: "12345"
                        SkuId: "12345"
                        FederatedId: "abcde"
                        UnitPrice: "60.00"
                        Quantity: "1"
                        IsTaxExempt: "false"
                        Discounts:
                        [
                            {
                                DiscountType: "OfferDiscount"
                                DiscountAmount: "30.00"
                                DiscountRate: "0.50"
                                DiscountRuleId: "12345"
                                DiscountCode: "AAAA-BBBB-CCCC-DDDD"
                            }
                        ]
                        Taxes:
                        [
                            {
                                TaxAmount: "2.00"
                                TaxAuthority: "State"
                                IsTaxInclusive: "false"
                                IsTaxExempt: "false"
                            }
                        ]
                        Properties:
                        [
                            {
                                Namespace: "abc"
                                Name: "def"
                                Value: "123"
                            }
                        ]
                    }
                    SellerInfo:
                    {
                        SellerId: "12345"
                        SellerMode: "Agent"
                        RevenueAllocations:
                        [
                            {
                                AllocationType: "Liability"
                                AllocationAmount: "42.00"
                                AllocationPercentage: "0.70"
                            }
                        ]
                    }
                ]
                Discounts:
                [
                    {
                        DiscountType: "OrderDiscount"
                        DiscountAmount: "10.00"
                        DiscountRate: "0.16"
                        DiscountRuleId: "12345"
                        DiscountCode: "ABCD-EFGH-0012"
                    }
                ]
                Properties:
                [
                    {
                        Namespace: "abc"
                        Name: "def"
                        Value: "123"
                    }
                ]
            }
        }

##POST /orders
Quote order for a user with a shopping cart id.

+ Parameters

    + userId (=12345, required, long) ... indicates the user who checkout the shopping cart.
    + quote (=true, optional, boolean) ... quote the cart

+ Request (application/json)
    + Headers

            Delegate-User-Id: "csr"
            Requestor-Id: "CheckoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            Tracking-Uuid: "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            User-Ip: "157.123.45.67"

    + Body

           {
                CartId: "12345"
                CartChangeToken: "1"
                ShippingMethodId: "12345"
                ShippingAddressId: "12345"
                Locale: "en-US"
                Currency: "USD"
                Properties:
                [
                    {
                        Namespace: "abc"
                        Name: "def"
                        Value: "123"
                    }
                ]
                }
            }

+ Response 200 (application/json)

        {
            UserId: "12345"
            TrackingUuid: "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            Cart:
            {
                CartId: "12345"
                Items:
                [
                    {
                        ItemId: "12345"
                        SkuId: "12345"
                        FederatedId: "abcde"
                        UnitPrice: "60.00"
                        Quantity: "1"
                        IsTaxExempt: "false"
                        Discounts:
                        [
                            {
                                DiscountType: "OfferDiscount"
                                DiscountAmount: "30.00"
                                DiscountRate: "0.50"
                                DiscountRuleId: "12345"
                                DiscountCode: "AAAA-BBBB-CCCC-DDDD"
                            }
                        ]
                        Taxes:
                        [
                            {
                                TaxAmount: "2.00"
                                TaxAuthority: "State"
                                IsTaxInclusive: "false"
                                IsTaxExempt: "false"
                            }
                        ]
                        Properties:
                        [
                            {
                                Namespace: "abc"
                                Name: "def"
                                Value: "123"
                            }
                        ]
                    }
                    SellerInfo:
                    {
                        SellerId: "12345"
                        SellerMode: "Agent"
                        RevenueAllocations:
                        [
                            {
                                AllocationType: "Liability"
                                AllocationAmount: "42.00"
                                AllocationPercentage: "0.70"
                            }
                        ]
                    }
                ]
                Discounts:
                [
                    {
                        DiscountType: "OrderDiscount"
                        DiscountAmount: "10.00"
                        DiscountRate: "0.16"
                        DiscountRuleId: "12345"
                        DiscountCode: "ABCD-EFGH-0012"
                    }
                ]
                Properties:
                [
                    {
                        Namespace: "abc"
                        Name: "def"
                        Value: "123"
                    }
                ]
            }
        }

##GET /orders/{orderId}
Get the order information with orderId

+ Parameters

    + userId (=12345, required, long) ... indicates the user who owns the order.

+ Request (application/json)

    + Headers

            Delegate-User-Id: "54321"
            Requestor-Id: "CheckoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            User-Ip: "157.123.45.67"

+ Response 200 (application/json)

        {
            UserId: "12345"
            Order:
            {
                OrderId: "12345"
                OrderType: "Debit"
                OrderStatus: "Completed"
                CreatedDate: "2014-01-25 17:08:07"
                ModifiedDate: "2014-01-25 17:08:07"
                Locale: "en-US"
                Currency: "USD"
                OriginalOrderId: "123456"
                RefundOrderIds:
                [
                    {
                        "1234567"
                        "1234568"
                    }
                ]
                ShippingMethod: "Standard"
                ShippingAddress:
                {
                    FirstName: "Chris"
                    MiddleName: "Pacific"
                    FirstName: "Zhu"
                    Country: "US",
                    State: "CA",
                    City: "Redwood",
                    Street: "test street",
                    Stree1: "test street1",
                    Stree2: "test street2",
                    Stree3: "test street3",
                    PostCode: "12345",
                    PhoneNumber: "1234567"
                }
                OrderEvents
                [
                    {
                        EventId: "12345"
                        Action: "Fulfill"
                        EventDate: "2014-01-25 17:08:10"
                        Status: "Completed"
                        IsLatest: "true"
                    }
                ]
                BillingInfos:
                [
                    {
                        Balances:
                        [
                            {
                                BalanceId: "12345"
                                BalanceType: "Debit"
                                BalanceStatus: "Settled"
                                OrderAmount: "20.00"
                                TaxAmount: "2.00"
                                IsTaxInclusive: "false"
                                DiscountAmount: "40.00"
                                PaymentMethodId: "12345"
                                CreatedDate: "2014-01-25 17:08:07"
                                ModifiedDate: "2014-01-25 17:08:08"
                                BillingDate: "2014-01-25 17:08:08"
                                BillingEvents:
                                [
                                    {
                                        Action: "Settle"
                                        EventDate: "2014-01-25 17:08:08"
                                        Status: "Settled"
                                        Amount: "22.00"
                                        IsLatest: "true"
                                    }
                                ]
                                Transactions:
                                [
                                    {
                                        TransactionId: "12345"
                                        TransactionAmount: "20.00"
                                        TransactionStatus: "Completed"
                                    }
                                ]
                            }
                        ]
                        BillingItems:
                        [
                            {
                                BalanceId: "balances/12345"
                                OrderItemId: "12345"
                                Amount: "20"
                                CreatedDate: "2014-01-25 17:08:08"
                                ModifiedDate: "2014-01-25 17:08:08"
                                TaxItems:
                                [
                                    {
                                        TaxItemId: "12345"
                                        TaxAmount: "2.00"
                                        TaxAuthority: "State"
                                        IsTaxInclusive: "false"
                                        IsTaxExempt: "false"
                                    }
                                ]
                                DiscountItems:
                                [
                                    {
                                        DiscountItemId: "12345"
                                        DiscountAmount: "20.00"
                                        DiscountRate: "0.50"
                                    }
                                ]
                                RevenueAllocationItems:
                                [
                                    {
                                        AllocationType: "Liability"
                                        AllocationAmount: "14.00"
                                        AllocationPercentage: "0.70"
                                    }
                                ]
                            }
                        ]
                    }
                ]
                Items:
                [
                    {
                        ItemId: "12345"
                        ItemType: "DigitalDownloads"
                        ProductItemId: "12345"
                        SkuId: "12345"
                        Description: "DigitalGame"
                        UnitPrice: "60.00"
                        Quantity: "1"
                        CreatedDate: "2014-01-25 17:08:10"
                        ModifiedDate: "2014-01-25 17:08:10"
                        Fulfillments:
                        [
                            {
                                FulfillmentUri: "/entitlements/12345"
                                FulfillmentType: "Entitlement"
                                FulfillmentStatus: "Fulfilled"
                                FulfillDate: "2014-01-25 17:08:10"
                            }
                        ]
                        FulfillmentEvents:
                        [
                            {
                                EventId: "12345"
                                Action: "Ship"
                                EventDate: "2014-01-25 17:08:10"
                                Status: "Shipped"
                                IsLatest: "true"
                            }
                        ]
                        PreorderInfo
                        {
                            BillingDate: "2014-01-25 17:08:10"
                            PreNotificationDate: "2014-01-25 17:08:10"
                            ReleaseDate: "2014-01-25 17:08:10"
                            UpdateHistory:
                            [
                                {
                                    UpdatedDate: "2014-01-25 17:08:10"
                                    UpdatedType: "Date"
                                    UpdatedColumn: "ReleaseDate"
                                    BeforeValue: "2015-01-25 17:08:10"
                                    AfterValue: "2014-01-25 17:08:10"
                                }
                            ]
                        }
                        SellerInfo
                        {
                            SellerId: "12345"
                            SellerMode: "Agent"
                        }
                    }
                ]
            }
        }

##GET /orders
Get the order uris by trackingUuid.

+ Parameters

    + userId (=12345, required, long) ... indicates the user who owns the order.
    + trackingUuid (required, uuid) ... indicates the tracking uuid when posting the orders.

+ Request (application/json)

    + Headers

            Delegate-User-Id: "54321"
            Requestor-Id: "CheckoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            User-Ip: "157.123.45.67"


+ Response 200 (application/json)
        {
            orderUris:
            [
                {
                    "/orders/12345"
                    "/orders/123456"
                }
            ]
        }

##PUT /orders/{orderId}
Refund the order

+ Parameters

    + userId (="12345", required, long) ... indicates the user who owns the order.
    + status (="refunded", required, string) ... refunds the order.
    + reason (="userRefund", string) .. the refund reason

+ Request (application/json)

    + Headers

            Delegate-User-Id: "54321"
            Requestor-Id: "CheckoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            User-Ip: "157.123.45.67"
            Tracking-Uuid: "f47ac10b-58cc-4372-a567-0e02b2c3d479"

+ Response 200 (application/json)

        {
            UserId: "12345"
            TrackingUuid: "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            OriginalOrder:
            {
                OrderId: "12345"
                OrderStatus: "Refunded"
                RefundDate: "2014-01-25 17:08:07"
                Locale: "en-US"
                Currency: "USD"
                BillingInfos:
                [
                    {
                        BalanceUri: "balances/12345"
                        BalanceType: "Debit"
                        BalanceStatus: "Refunded"
                        OrderAmount: "20.00"
                        TaxAmount: "2.00"
                        IsTaxInclusive: "false"
                        DiscountAmount: "40.00"
                        PaymentMethodId: "12345"
                    }
                ]
                Fulfillments:
                [
                    {
                        FulfillmentUri: "/entitlements/12345"
                        FulfillmentType: "Entitlement"
                        FulfillmentStatus: "Reverted"
                    }
                ]
            }
            RefundOrder:
            {
                 OrderId: "12346"
                 OrderStatus: "Completed"
                 CreatedDate: "2014-01-25 17:08:07"
                 Locale: "en-US"
                 Currency: "USD"
                 BillingInfos:
                 [
                     {
                         BalanceUri: "balances/12346"
                         BalanceType: "Credit"
                         BalanceStatus: "Completed"
                         OrderAmount: "20.00"
                         TaxAmount: "2.00"
                         IsTaxInclusive: "false"
                         DiscountAmount: "40.00"
                         PaymentMethodId: "12345"
                     }
                 ]
            }
        }

##PUT /orders/{orderId}
Resume the order.

+ Parameters

    + userId (=12345, required, long) ... indicates the user who owns the order.
    + status (=resumed, required, string) ... resume the order for async settlement or risk manual review.
    + reason (="riskApprove", string) ... the resume reason

+ Request (application/json)

    + Headers

            Delegate-User-Id: "54321"
            Requestor-Id: "CheckoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            User-Ip: "157.123.45.67"
            Tracking-Uuid: "f47ac10b-58cc-4372-a567-0e02b2c3d479"

+ Response 200 (application/json)

        {
            UserId: "12345"
            TrackingUuid: "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            Order:
            {
                OrderId: "12345"
                OrderStatus: "Completed"
                OrderDate: "2014-01-25 17:08:07"
                Locale: "en-US"
                Currency: "USD"
                BillingInfos:
                [
                    {
                        BalanceUri: "balances/12345"
                        BalanceType: "Debit"
                        BalanceStatus: "Refunded"
                        OrderAmount: "20.00"
                        TaxAmount: "2.00"
                        IsTaxInclusive: "false"
                        DiscountAmount: "40.00"
                        PaymentMethodId: "12345"
                    }
                ]
                Fulfillments:
                [
                    {
                        FulfillmentUri: "/entitlements/12345"
                        FulfillmentType: "Entitlement"
                        FulfillmentStatus: "Reverted"
                    }
                ]

        }

##PUT /orders/{orderId}
callback interface for order billing status change

+ Parameters

    + userId (=12345, required, long) ... indicates the user who owns the order.
    + status (=pending, required, string) ... resume the order for async settlement or risk manual review.
    + reason (="billingStatusChange", string) ... the resume reason

+ Request

    + Headers

            Delegate-User-Id: "54321"
            Requestor-Id: "CheckoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            User-Ip: "157.123.45.67"
            Tracking-Uuid: "f47ac10b-58cc-4372-a567-0e02b2c3d479"

    + Body

        {
            BillingInfos:
            [
                {
                    BalanceId: "12345"
                    BalanceType: "Debit"
                    BalanceStatus: "Failed"
                    OrderAmount: "20.00"
                    TaxAmount: "2.00"
                    IsTaxInclusive: "false"
                    DiscountAmount: "40.00"
                    PaymentMethodId: "12345"
                    BalanceDate: "2014-01-25 17:08:07"
                }
            ]
        }

+ Response 200 (application/json)

        {
            UserId: "12345"
            TrackingUuid: "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            Order:
            {
                OrderId: "12345"
                OrderStatus: "Completed"
                OrderDate: "2014-01-25 17:08:07"
                Locale: "en-US"
                Currency: "USD"
                BillingInfos:
                [
                    {
                        BalanceUri: "balances/12345"
                        BalanceType: "Debit"
                        BalanceStatus: "Refunded"
                        OrderAmount: "20.00"
                        TaxAmount: "2.00"
                        IsTaxInclusive: "false"
                        DiscountAmount: "40.00"
                        PaymentMethodId: "12345"
                    }
                ]
                Fulfillments:
                [
                    {
                        FulfillmentUri: "/entitlements/12345"
                        FulfillmentType: "Entitlement"
                        FulfillmentStatus: "Reverted"
                    }
                ]

        }

##PUT /orders/{orderId}
callback interface for order fulfillment status change

+ Parameters

    + userId (=12345, required, long) ... indicates the user who owns the order.
    + status (=pending, required, string) ... resume the order for async settlement or risk manual review.
    + reason (="fulfillmentStatusChange", string) ... the resume reason

+ Request

    + Headers

            Delegate-User-Id: "54321"
            Requestor-Id: "CheckoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            User-Ip: "157.123.45.67"
            Tracking-Uuid: "f47ac10b-58cc-4372-a567-0e02b2c3d479"

    + Body

        {
            OrderItemFulfillments:
            [
                {
                    OrderItemId: "12345"
                    Status: "Shipping"
                    Quantity: "1"
                    FulfillmentDate: "2014-01-25 17:08:07"
                }
            ]
        }

+ Response 200 (application/json)

        {
            UserId: "12345"
            TrackingUuid: "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            Order:
            {
                OrderId: "12345"
                OrderStatus: "Completed"
                OrderDate: "2014-01-25 17:08:07"
                Locale: "en-US"
                Currency: "USD"
                BillingInfos:
                [
                    {
                        BalanceUri: "balances/12345"
                        BalanceType: "Debit"
                        BalanceStatus: "Refunded"
                        OrderAmount: "20.00"
                        TaxAmount: "2.00"
                        IsTaxInclusive: "false"
                        DiscountAmount: "40.00"
                        PaymentMethodId: "12345"
                    }
                ]
                Fulfillments:
                [
                    {
                        FulfillmentUri: "/entitlements/12345"
                        FulfillmentType: "Entitlement"
                        FulfillmentStatus: "Completed"
                    }
                ]

        }