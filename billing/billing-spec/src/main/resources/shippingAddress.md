FORMAT: 1A

HOST: http://www.wan-san.com

# Shipping Address API

Shipping Address API is a collection of APIs that implements Shipping Address operation.

# Group Balance

Fields:

+  <span id="addressId">addressId...string to identify the shipping address.</span>
+  <span id="userId">userId...string to identify the user.</span>
+  <span id="firstName">firstName...string to identify the ship to customer's first name.</span>
+  <span id="middleName">middleName...string to identify the ship to customer's middle name.</span>
+  <span id="lastName">lastName...string to identify the ship to customer's last name.</span>
+  <span id="companyName">companyName...string to identify the ship to customer's company name.</span>
+  <span id="phoneNumber">phoneNumber...string to identify the ship to customer's phone number.</span>
+  <span id="street">street...string to identify the shipping address street.</span>
+  <span id="street1">street1...string to identify the shipping address street1.</span>
+  <span id="street2">street2...string to identify the shipping address street2.</span>
+  <span id="city">city...string to identify the shipping address city.</span>
+  <span id="state">state...string to identify the shipping address state.</span>
+  <span id="postalCode">postalCode...string to identify the shipping address postal code.</span>
+  <span id="country">country...2 character ISO 3166 country code for shipping address.</span>
+  <span id="createdDate">createdDate...the balance's created date</span>
+  <span id="createdBy">createdBy...indicates who creates the balance</span>
+  <span id="modifiedDate">modifiedDate...the balance's modified date</span>
+  <span id="modifiedBy">modifiedBy...indicates who modifies the balance</span>


## POST /shipping-addresses
Create shipping address for a user

+ Request
    + Headers

            Delegate-User-Id: "users/54321"
            Requestor-Id: "CheckoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            Tracking-Uuid: "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            User-Ip: "157.123.45.67"

    + Body
        
            [
            {
                "user": {
                    "href": "http://api.wan-san.com/users/12345",
                    "id": "12345"
                },
                "street": "NO. 1000 Twin Dophin Dr",
                "city": "Redwood City",
                "state": "CA",
                "postalCode": "96045",
                "country": "US",
                "firstName": "Steve",
                "lastName": "Smith",
                "phoneNumber": "207-655-2345"
            }
            ]
    
+ Response 200 (application/json)
        
            [
            {
                "self": {
                    "href": "http://api.wan-san.com/shipping-addresses/70953532335535",
                    "id": "70953532335535"
                },
                "user": {
                    "href": "http://api.wan-san.com/users/12345",
                    "id": "12345"
                },
                "street": "NO. 1000 Twin Dophin Dr",
                "city": "Redwood City",
                "state": "CA",
                "postalCode": "96045",
                "country": "US",
                "firstName": "Steve",
                "lastName": "Smith",
                "phoneNumber": "207-655-2345"
            }
            ]

##GET /shipping-addresses/{key}
Get the balances information with balance id

+ Parameters

    + key (required, string) ... indicates the shipping address.

+ Request

    + Headers

            Delegate-User-Id: "users/54321"
            Requestor-Id: "CheckoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            User-Ip: "157.123.45.67"

+ Response 200 (application/json)
    
            [
            {
                "self": {
                    "href": "http://api.wan-san.com/shipping-addresses/70953532335535",
                    "id": "70953532335535"
                },
                "user": {
                    "href": "http://api.wan-san.com/users/12345",
                    "id": "12345"
                },
                "street": "NO. 1000 Twin Dophin Dr",
                "city": "Redwood City",
                "state": "CA",
                "postalCode": "96045",
                "country": "US",
                "firstName": "Steve",
                "lastName": "Smith",
                "phoneNumber": "207-655-2345"
            }
            ]

## GET /shipping-addresses(?userId)
Get the shipping address by userId.

+ Parameters

    + userId (required, string) ... indicates the user id when posting the shipping address.

+ Request

    + Headers

            Delegate-User-Id: "users/54321"
            Requestor-Id: "CheckoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            User-Ip: "157.123.45.67"


+ Response 200 (application/json)
    
            {
                "shippingAddressUri":  "/shipping-addresses/98765432",
                "shippingAddressUri":  "/shipping-addresses/98765431"
            }

## DELETE /shipping-addresses/{id}
Delete the shipping address by address id.

+ Parameters

    + id (required, string) ... indicates the shipping address.

+ Request

    + Headers

            Delegate-User-Id: "users/54321"
            Requestor-Id: "CheckoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            User-Ip: "157.123.45.67"

+ Response 204

+ Response 403 (application/json)

        {
            "status": "failed"
            "errorMsg": "INVALID_SHIPPING_ADDRESS_ID"
        }
