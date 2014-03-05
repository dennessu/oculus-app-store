#payment method
API about payment method


##POST /paymentmethod/validation
create a new payment method validation request

+ Request (application/json)

        { 
            “accountId”: “12345678”, 
            “paymentMethodType”: “CreditCard”, 
            “holderName”: “David”, 
            “encryptedAccountNO”: “kgvqKnMcgIUn7rl1vkFXF0g4SodEd3dxAJo/mVN6ef211B1MZelg7OyjEHf4ZXwlCdtOFebIdlnK”, 
            “displayAccountNO”: “4111********1111”, 
            “creditCardPaymentMethod”: {
                “expireDate”: “Football",
                “encryptedCVMCode”: “kgvqKnMcgIUn7rl1vkFXF0g4SodEd3dxAJo/mVN6ef211B1MZelg7OyjEHf4ZXwlCdtOFebIdlnK”
            },
            “chargeInfo”: {
                “currency”: “USD”,
                “country”: “US”
            },
            “address”: {
                “addressLine1”: “Third Street Ferriday”,
                “city”: “LA”,
                “state”: “CA”,
                “country”: “US”,
                “postalCode”: “12345”
            },
            “phone”: {
                “phoneType”: “WORK”,
                “localNumber”: “12345678”
            }
        } 

+ Response 200 (application/json)

        {
            “paymentmEventUri":  “/payment123/paymentevents/1234” 
        }


##POST /paymentmethod
create a new payment method

+ Request (application/json)

        { 
            “trackingUuid”: "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", 
            “accountId”: “12345678”, 
            “paymentMethodType”: “CreditCard”, 
            “holderName”: “David”, 
            “encryptedAccountNO”: “kgvqKnMcgIUn7rl1vkFXF0g4SodEd3dxAJo/mVN6ef211B1MZelg7OyjEHf4ZXwlCdtOFebIdlnK”, 
            “displayAccountNO”: “4111********1111”, 
            “requireValidate”: “true”, 
            “creditCardPaymentMethod”: {
                “expireDate”: “Football",
                “encryptedCVMCode”: “kgvqKnMcgIUn7rl1vkFXF0g4SodEd3dxAJo/mVN6ef211B1MZelg7OyjEHf4ZXwlCdtOFebIdlnK”
            },
            “address”: {
                “addressLine1”: “Third Street Ferriday”,
                “city”: “LA”,
                “state”: “CA”,
                “country”: “US”,
                “postalCode”: “12345”
            },
            “phone”: {
                “phoneType”: “WORK”,
                “localNumber”: “12345678”
            }
        } 

+ Response 200 (application/json)

        {
            “paymentmethodUri":  “/paymentmethods/123" 
        }


##DELETE /paymentmethod/{payment_method_id}
delete an existing payment method

+ Response 204

+ Response 500 (application/json)

        {
            "status": "failed" 
            "errorMsg": "INVALID_PAYMENT_METHOD_ID " 
        }



##PUT /paymentmethod/{payment_method_id}
update an existing payment method

+ Request (application/json)

        { 
            “address”: {
                “addressLine1”: “Forth Street Ferriday”,
                “city”: “LA”,
                “state”: “CA”,
                “country”: “US”,
                “postalCode”: “12345”
            }
        }

+ Response 200 (application/json)

        {
            “paymentmethodUri":  “/paymentmethods/123" 
        }


##GET /paymentmethod/{payment_method_id}
update an existing payment method


+ Response 200 (application/json)

        {
            “accountId”: “1234567”,
            “type”: “CreditCard”,
            “holderName”: “David”,
            “displayName”: “4111********1111”,
            “creditCardPaymentMethod”: {
                “expireDate”: “Football",
                “lastBillingDate”: “2014-01-20”
            },
            “address”: {
                “addressLine1”: “Third Street Ferriday”,
                “city”: “LA”,
                “state”: “CA”,
                “country”: “US”,
                “postalCode”: “12345”
            },
            “phone”: {
                “phoneType”: “WORK”,
                “localNumber”: “12345678”
            }
        }



##GET /paymentmethod/user/{user_id}
update an existing payment method


+ Response 200 (application/json)

        {
            “paymentmethodUris”:  [
            {
                “paymentmethodUri”: “/paymentmethods/123”
            },
            {
                “paymentmethodUri”: “/paymentmethods/123”
            }
            ]
        }



##POST /payment/authorization
create a new payment authorization

+ Request (application/json)

        { 
            “trackingUuid”: "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", 
            “accountId”: “12345678”, 
            “paymentMethodId”: “12345”,
            “chargeInfo”: {
                “currency”: “USD”,
                “country”: “US”,
                “amount”: “100”
            }
        } 

+ Response 200 (application/json)

        {
            “paymentEventUrl”:  “/payment/123/paymentevents/1234" 
        }


##POST /payment/capture
create a new payment capture

+ Request (application/json)

        { 
            “trackingUuid”: "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", 
            “accountId”: “12345678”, 
            “paymentId”: “123”,
            “chargeInfo”: {
                “currency”: “USD”,
                “country”: “US”,
                “amount”: “100”
            }
        } 

+ Response 200 (application/json)

        {
            “paymentEventUrl":  “/payment/123/paymentevents/1235” 
        }


##POST /payment/charge
create a new payment charge

+ Request (application/json)

        { 
            “trackingUuid”: "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", 
            “accountId”: “12345678”, 
            “paymentMethodId”: “12345”,
            “chargeInfo”: {
                “currency”: “USD”,
                “country”: “US”,
                “amount”: “100”
            }
        } 

+ Response 200 (application/json)

        {
            “paymentEventUrl":  “/payment/123/paymentevents/1235" 
        }



##PUT /payment
reverse an existing payment

+ Request (application/json)

        { 
            “trackingUuid”: "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", 
            “accountId”: “12345678”, 
            “paymentId”: “123”,
            “paymentMethodId”: “12345”,
        }

+ Response 200 (application/json)

        {
            “paymentEventUrl":  “/payment/123/paymentevents/1235" 
        }


##GET /payment/{payment_id}
Get the payment events for an existing payment


+ Response 200 (application/json)

        {
            “trackingUuid”: "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
            “accountId”: “12345678”,
            “paymentType”: “CHARGE”,
            “currency”: “USD”,
            “amount”: “100”,
            “country”: “US”,
            “paymentmethodUri”: “/paymentmethods/123”,
            “provider”: “BrainTree”,
            “merchantAccount”: “6xuysbasdf9ds”,
            “status”: “COMPLETED”,
            “updateDate”: “2014-01-20”
            “paymentEvents”: [
                {“paymentEventUrl":  “/payment/123/paymentevents/1234”},
                {“paymentEventUrl":  “/payment/123/paymentevents/1235”}
            ]
        }

##GET /payment/{payment_id}/paymentevents/{payment_event_id}
Get an existing payment event


+ Response 200 (application/json)

        {
            “paymentEventType”: “CREATE”,
            “eventDate”: “2014-01-20”,
            “currency”: “USD”,
            “amount”: “100”
        }
