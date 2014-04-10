#eWallet
API about eWallet. trackingUuid is only used for post and put.

##resource eWallet

        {
            "self": {
                "href": "https://data.oculusvr.com/v1/wallets/123",
                "id": 123
            },
            "trackingUuid": "21e61490-9eb6-11e3-a5e2-0800200c9a66",
            "userId": {
                "href": "https://data.oculusvr.com/v1/users/123",
                "id": 123
            },
            "type":"SV",
            "status":"ACTIVE",
            "currency": "USD",
            "balance": "100.00"
        }

##resource eWallet list

        {
            "self":{
                "href": "https://data.oculusvr.com/v1/users/123/wallets",
                "id": ""
            },
            "criteria":
            [{
                "self": {
                    "href": "https://data.oculusvr.com/v1/wallets/123",
                    "id": 123
                },
                "userId": {
                    "href": "https://data.oculusvr.com/v1/users/123",
                    "id": 123
                },
                "type":"SV",
                "status":"ACTIVE",
                "currency": "USD",
                "balance": "100.00"
            }, 
            {
                "self": {
                    "href": "https://data.oculusvr.com/v1/wallets/234",
                    "id": 234
                },
                "userId": {
                    "href": "https://data.oculusvr.com/v1/users/123",
                    "id": 123
                },
                "type":"SV",
                "status":"ACTIVE",
                "currency": "JPY",
                "balance": "1000"
            }],
            "next": "https://data.oculusvr.com/v1/users/123/wallets?start=50"
        }

## /wallets/{id}
###GET
get eWallet by eWallet Id

+ Response 200 (application/json)

    the [eWallet](#resource-ewallet)

+ Response 404

###PUT
update an existing eWallet

+ Request (application/json)

    the entire [eWallet](#resource-ewallet) which needs to be updated

+ Response 200 (application/json)

    the updated [eWallet](#resource-ewallet)

##POST /wallets/credit
add balance to an eWallet. if the eWallet does not exist, create a new eWallet and add the balance.

+ Request (application/json)

    the [eWallet](#resource-ewallet) which does not contain the status field

+ Response 200 (application/json)

    the result [eWallet](#resource-eWallet)

##POST /wallets/debit
consume balance in an eWallet.

+ Request (application/json)

    the [eWallet](#resource-ewallet) which does not contain the status field

+ Response 200 (application/json)

    the result [eWallet](#resource-eWallet)

+ Response 404 (application/json)

        {
            "message": EWALLET_NOT_FOUND
        }


##GET /users/{userId}/wallets
get all eWallet for specified user.

+ Response 200 (application/json)

    the result [eWallets](#resource-ewallet-list)

##GET /wallets/{id}/transactions
get eWallet with its transactions.

+ Response 200 (application/json)

        {
            "self": {
                "href": "https://data.oculusvr.com/v1/wallets/123",
                "id": 123
            },
            "userId": {
                "href": "https://data.oculusvr.com/v1/users/123",
                "id": 123
            },
            "type":"SV",
            "status":"ACTIVE",
            "currency": "USD",
            "trasactions":{[
                {
                    "type": "create",
                    "amount": "100.00",
                    "offerId":{
                        "href": "https://data.oculusvr.com/v1/offers/123",
                        "id": 123
                    },
                    "commentTime": "2014-01-01T00:00:00Z",
                    "commentBy": "123"
                },
                {
                    "type": "debit",
                    "amount": "50.00",
                    "offerId":{
                        "href": "https://data.oculusvr.com/v1/offers/345",
                        "id": 345
                    },
                    "commentTime": "2014-02-01T00:00:00Z",
                    "commentBy": "123"
                }    
            ]}
        }
