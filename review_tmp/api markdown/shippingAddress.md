#Shipping Address
API about shipping address

## /shippingAddress/{addressId}
###GET
get shipping address by address id

+ Response 200 (application/json)

        {
            "addressId": 987654321,
            "userId": 123456,
            "street": "NO. 1000 Twin Dophin Dr",
            "city": "Redwood City",
            "state": "CA",
            "postalCode": "96045",
            "country": "US",
            "firstName": "Steve",
            "lastName": "Smith",
            "phoneNumber": "207-655-2345"
        }

###PUT
update an existing shipping address

+ Request (application/json)

        {
            "postalCode": "96066"
        }

+ Response 200 (application/json)

        {
            "addressId": 987654321,
            "userId": 123456,
            "street": "NO. 1000 Twin Dophin Dr",
            "city": "Redwood City",
            "state": "CA",
            "postalCode": "96066",
            "country": "US",
            "firstName": "Steve",
            "lastName": "Smith",
            "phoneNumber": "207-655-2345"
        }

###DELETE
delete an existing shipping address

+ Response 204

+ Response 500 (application/json)

        {
            "status": "failed"
            "errorMsg": "INVALID_SHIPPING_ADDRESS_ID"
        }

##POST /shippingAddresses
create a new shipping address for a user

+ Request (application/json)

        {
            "userId": 12345
            "street": "NO. 1000 Twin Dophin Dr",
            "city": "Redwood City",
            "state": "CA",
            "postalCode": "96045",
            "country": "US",
            "firstName": "Steve",
            "lastName": "Smith",
            "phoneNumber": "207-655-2345"
        }

+ Response 200 (application/json)

        {
            "shippingAddressUri":  "/shippingAddresses/98765432"
        }


##GET /shippingAddresses?userId=1234
get all shipping addresses for specified user

+ Response 200 (application/json)

        {
            "shippingAddresses":
            [{
                "addressId": 2345,
                "userId": 1234,
                "street": "NO. 511, Weining RD",
                ...
            },
            {
                "addressId": 12345,
                "userId": 1234,
                "street": "NO. 1, Dapu RD",
                ...
            }]
        }
