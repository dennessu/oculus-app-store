#Subledger
Subledger service APIs

##GET /subledgers
Get developer subledger information with seller id.

+ Parameters

    + sellerId (=12345, required, long) ... indicates the seller.
    + status (="pending", optional, string) ... indicates the status.
    + fromDate (="2014-01-25", optional, date) ... indicates the from date.
    + toDate (="2014-02-25", optional, date) ... indicates the to date.

+ Request (application/json)

    + Headers

            Delegate-User-Id: "54321"
            Requestor-Id: "PayoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            User-Ip: "157.123.45.67"

+ Response 200 (application/json)

        {
            SellerId: "12345"
            SellerTaxProfile: "12345"
            Subledgers:
            [
                {
                    SubledgerId: "12345"
                    PayoutStatus: "Pending"
                    PayoutDate: "2014-02-25 17:08:07"
                    Locale: "en-US"
                    Currency: "USD"
                    TotalPayoutAmount: "1234.00"
                }
            ]
        }

##POST /subledgerItems
Create and append a payin item to payout subledger.

+ Parameters

    + sellerId (=12345, required, long) ... indicates the seller.

+ Request (application/json)
    + Headers

            Delegate-User-Id: "csr"
            Requestor-Id: "PayoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            Tracking-Uuid: "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            User-Ip: "157.123.45.67"

    + Body

        {
            SubledgerId: "12345"
            SellerTaxProfileId: "12345"
            Currency: "USD"
            SubledgerItem
            {
                OrderItemId: "12345"
                PayoutAmount: "100.00"
                SubledgerItemType: "Payin/Revert"
                OriginalOrderItemId: "12346"
            }
        }

+ Response 200 (application/json)

        {
            SellerId: "12345"
            SellerTaxProfile: "12345"
            Subledger:
            {
                SubledgerId: "12345"
                PayoutStatus: "Pending"
                PayoutDate: "2014-02-25 17:08:07"
                Locale: "en-US"
                Currency: "USD"
                TotalPayoutAmount: "1234.00"
            }
        }