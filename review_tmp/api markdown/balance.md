#Balance
API about billing balance

##Post /balances
create a billing balance for an order.

+ Request (application/json)

        {
            "orderId": 1234
            "currency": "USD",
            "country": "US",
            "dueDate": "2014-01-28",
            "balanceItem":
            [
            {
                "orderItemId": 123
                "amount": 9.99,
                "financeId": "9999",
                "taxIncluded": false,
                "taxItems":
                [{
                    "authority": "CITY",
                    "taxAmount": 0.45,
                    "taxRate": 0.045
                },
                {
                    "authority": "STATE",
                    "taxAmount": 0.5,
                    "taxRate": 0.05
                }],
                "discountItems":
                [{
                    "discountAmount": 1.00
                }]
            ,}
            ]
        }

+ Response 200 (application/json)

        {
            "balanceUri":  "/balances/98765432"
        }

##GET /balances?orderId=1234
get all balances for specified order

+ Response 200 (application/json)

        {
            "balances":
            [{
                "balanceId": 12345,
                "orderId": 1234,
                "currency": "USD",
                "country": "US",
                ...
            },
            {
                "balanceId": 12346,
                "orderId": 1234,
                "currency": "USD",
                "country": "US",,
                ...
            }]
        }
