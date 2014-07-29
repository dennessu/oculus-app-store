#payment instrument & payment transaction
API about payment instrument and payment transaction


##POST /payment-instruments
add a new payment method for the user

+ Request (application/json)

        { 
            “trackingUuid”: "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", 
            "admins": [
            	{
                	"href": "http://api.wan-san.com/v1/users/12345", 
                	"id": "12345"
            	}
            ],
            “accountName”: “David”, 
            “accountNumber”: “kgvqKnMcgIUn7rl1vkFXF0g4SodEd3dxAJo/mVN6ef211B1MZelg7OyjEHf4ZXwlCdtOFebIdlnK”,     // encrypted
            // "lastValidated": "",    // must be omitted on POST; this it generated/snythesized by server
            "isValidated": true,       // use true to force validation now, use false for lazy-validation
            “type”: {
                “href”: “http://api.wan-san.com/v1/payment-instrument-types/creditCard”,
                “id”: “creditCard”
    		},
            “creditCard”: {
                “expireDate”: "2015-12",      // or "2015-12-13"
                “encryptedCvmCode”: “kgvqKnMcgIUn7rl1vkFXF0g4SodEd3dxAJo/mVN6ef211B1MZelg7OyjEHf4ZXwlCdtOFebIdlnK”,    // input only
                “address”: {
                	“addressLine1”: “Third Street Ferriday”,
                	“city”: “LA”,
                	“state”: “CA”,
                	“country”: “US”,
                	“postalCode”: “12345”
            	},
            	“phone”: {
                	“type”: “WORK”,
                	"number”: “12345678”
            	}
            },
        } 

+ Response 200 (application/json)

        {
            "self": {
                "href":  “http://api.wan-san.com/v1/payment-instruments/1234",
                "id": "1234"
            },
            “trackingUuid”: "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", 
            "admins": [
            	{
                	"href": "http://api.wan-san.com/v1/users/12345", 
                	"id": "12345"
            	}
            ],
            "isValidated": true,
            "lastValidated": "2014-01-13T12:34:56Z",   // server-synthesized
            “type”: {
                “href”: “http://api.wan-san.com/v1/payment-instrument-types/creditCard”,
                “id”: “creditCard”
    		},
            ““accountName””: “David”, 
            ““accountNumber””: “4111********1111”,
            "status": “ACTIVE”, 
            “creditCard”: {
                "type": "VISA",
                "prepaid": "true",
                “expireDate”: "2015-12-01",
                “address”: {
                	“addressLine1”: “Third Street Ferriday”,
                	“city”: “LA”,
                	“state”: “CA”,
                	“country”: “US”,
                	“postalCode”: “12345”
            	},
           	 	“phone”: {
                	“type”: “WORK”,
                	"number”: “12345678”
            	}
            },
        }


        
        
##DELETE /payment-instruments/{id}
delete an existing payment method

+ Response 200 (application/json)






##PUT /payment-instruments/{id}
update an existing payment method. validation could be done via this by setting 

+ Request (application/json)

        { 
        	"self": {
                "href":  “http://api.wan-san.com/v1/payment-instruments/1234",
                "id": "1234"
            },
            “trackingUuid”: "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", 
            "admins": [
            	{
                	"href": "http://api.wan-san.com/v1/users/12345", 
                	"id": "12345"
            	}
            ],
            “type”: {
                “href”: “http://api.wan-san.com/v1/payment-instrument-types/creditCard”,
                “id”: “creditCard”
    		},
            ““accountName””: “David”, 
            ““accountNumber””: “4111********1111”,
            "status": “ACTIVE”, 
            “creditCard”: {
                "type": "VISA",
                "prepaid": "true",
                “expireDate”: "2015-12-01",
                “address”: {
                	“addressLine1”: “Forth Street Ferriday”,
                	“city”: “LA”,
                	“state”: “CA”,
                	“country”: “US”,
                	“postalCode”: “12345”
            	},
           	 	“phone”: {
                	“type”: “WORK”,
                	"number”: “12345678”
            	}
            },
            "isValidated": true,
            "lastValidated": "2014-01-13T12:34:56Z"   // must be the exactly as provided by the server (via GET)
            // the client can request validation by PUT with validated:true; the server updates lastValidated
            // the client can FORCE validation by PUT with validated:false (server sets lastValidate:"") then validated:true
        }

+ Response 200 (application/json)

        {
            "self": {
                "href":  “http://api.wan-san.com/v1/payment-instruments/1234",
                "id": "1234"
            },
            “trackingUuid”: "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", 
            "admins": [
            	{
                	"href": "http://api.wan-san.com/v1/users/12345", 
                	"id": "12345"
            	}
            ],
            “type”: {
                “href”: “http://api.wan-san.com/v1/payment-instrument-types/creditCard”,
                “id”: “creditCard”
    		},
            ““accountName””: “David”, 
            ““accountNumber””: “4111********1111”,
            "status": “ACTIVE”, 
            “creditCard”: {
                "type": "VISA",
                "prepaid": "true",
                “expireDate”: "2015-12-01",
                “address”: {
                	“addressLine1”: “Forth Street Ferriday”,
                	“city”: “LA”,
                	“state”: “CA”,
                	“country”: “US”,
                	“postalCode”: “12345”
            	},
           	 	“phone”: {
                	“type”: “WORK”,
                	"number”: “12345678”
            	}
            },
            "isValidated": true,
            "lastValidated": "2014-01-13T12:34:56Z"   // must be the exactly as provided by the server (via GET)
            // the client can request validation by PUT with validated:true; the server updates lastValidated
            // the client can FORCE validation by PUT with validated:false (server sets lastValidate:"") then validated:true
        }


##GET /payment-instruments/{id}
GET an existing payment method through the payment method id


+ Response 200 (application/json)

        {
            "self": {
                "href":  “http://api.wan-san.com/v1/payment-instruments/1234",
                "id": "1234"
            },
            “trackingUuid”: "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", 
            "admins": [
            	{
                	"href": "http://api.wan-san.com/v1/users/12345", 
                	"id": "12345"
            	}
            ],
            “type”: {
                “href”: “http://api.wan-san.com/v1/payment-instrument-types/creditCard”,
                “id”: “creditCard”
    		},
            ““accountName””: “David”, 
            ““accountNumber””: “4111********1111”,
            "status": “ACTIVE”, 
            “creditCard”: {
                "type": "VISA",
                "prepaid": "true",
                “expireDate”: "2015-12-01",
                “address”: {
                	“addressLine1”: “Forth Street Ferriday”,
                	“city”: “LA”,
                	“state”: “CA”,
                	“country”: “US”,
                	“postalCode”: “12345”
            	},
           	 	“phone”: {
                	“type”: “WORK”,
                	"number”: “12345678”
            	}
            },
            "isValidated": true,
            "lastValidated": "2014-01-13T12:34:56Z"   // must be the exactly as provided by the server (via GET)
        }



##GET /payment-instruments?user={userId}
GET all the existing payment methods for the specific user


+ Response 200 (application/json)

        {
            "href":  “http://api.wan-san.com/v1/payment-instruments?user=12345678&cursor=...&count=100",
            "results":  [
                {
                    "self": {
                        "href":  “http://api.wan-san.com/v1/payment-instruments/1234",
                        "id": "1234"
                    },
                    “trackingUuid”: "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", 
                    "admins": [
            	        {
                	        "href": "http://api.wan-san.com/v1/users/12345", 
                	        "id": "12345"
            	        }
                   ],
                   “type”: {
                        “href”: “http://api.wan-san.com/v1/payment-instrument-types/creditCard”,
                        “id”: “creditCard”
    		        },
                    “accountName””: “David”, 
                    “accountNumber”: “4111********1111”,
                    "status": “ACTIVE”, 
                    “creditCard”: {
                         "type": "VISA",
                         "prepaid": "true",
                         “expireDate”: "2015-12-01",
                         “address”: {
                	          “addressLine1”: “Forth Street Ferriday”,
                	          “city”: “LA”,
                	          “state”: “CA”,
                	          “country”: “US”,
                	          “postalCode”: “12345”
            	          },
           	 	          “phone”: {
                	            “type”: “WORK”,
                	            "number”: “12345678”
            	          }   
            	      } ,
                     "isValidated": true,
                     "lastValidated": "2014-01-13T12:34:56Z"   // must be the exactly as provided by the server (via GET)
                },
                {
                    "self": {
                        "href":  “http://api.wan-san.com/v1/payment-instruments/1234",
                        "id": "1234"
                    },
                    “trackingUuid”: "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", 
                    "admins": [
            	        {
                	        "href": "http://api.wan-san.com/v1/users/12345", 
                	        "id": "12345"
            	        }
                   ],
                   “type”: {
                        “href”: “http://api.wan-san.com/v1/payment-instrument-types/creditCard”,
                        “id”: “creditCard”
    		        },
                    “accountName””: “David”, 
                    “accountNumber”: “4111********1111”,
                    "status": “ACTIVE”, 
                    “creditCard”: {
                         "type": "VISA",
                         "prepaid": "true",
                         “expireDate”: "2015-12-01",
                         “address”: {
                	          “addressLine1”: “Forth Street Ferriday”,
                	          “city”: “LA”,
                	          “state”: “CA”,
                	          “country”: “US”,
                	          “postalCode”: “12345”
            	          },
           	 	          “phone”: {
                	            “type”: “WORK”,
                	            "number”: “12345678”
            	          }   
            	      } ,
                     "isValidated": true,
                     "lastValidated": "2014-01-13T12:34:56Z"   // must be the exactly as provided by the server (via GET)
                }
            ]
        }

        

##POST /payment-transactions/authorizations
create a new payment authorization transaction

+ Request (application/json)

        { 
            “trackingUuid”: "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", 
            "user": {
                "href": "http://api.wan-san.com/v1/users/12345", 
                "id": "12345"
            }, 
            "paymentInstrument": {
                "href":  “http://api.wan-san.com/v1/payment-instruments/1234",
                "id": "1234"
            },
            “chargeInfo”: {
                “currency”: “USD”,
                “country”: “US”,
                “amount”: “100”
            }
        } 

+ Response 200 (application/json)

        {
            "self": {
                "href":  “http://api.wan-san.com/v1/payment-transactions/1234",
                "id": "1234"
            },
            “trackingUuid”: "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
            "user": {
                "href": "http://api.wan-san.com/v1/users/12345", 
                "id": "12345"
            },
            “paymentType”: “AUTH”,
            “chargeInfo”: {
                “currency”: “USD”,
                “country”: “US”,
                “amount”: “100”
            }
            “paymentInstrument”: {
                "href": "http://api.wan-san.com/v1/payment-instruments/12345", 
                "id": "12345"
            }, 
            “provider”: “BrainTree”,
            “merchantAccount”: “6xuysbasdf9ds”,
            “status”: “AUTHED”,
            “updateDate”: “2014-01-20”,
            “paymentEvents”: [
                {
                    "paymentEventType": "CREATE",
                    "eventDate": "2014-02-12",
                    "currency": "USD",
                    "amount": "100"
                },
                {
                    "paymentEventType": "AUTH",
                    "eventDate": "2014-02-12",
                    "currency": "USD",
                    "amount": "100"
                }
            ]
        }


##POST /payment-transactions/captures
create a new payment capture transaction for a specific payment transaction

+ Request (application/json)

        { 
            “trackingUuid”: "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", 
            "user": {
                "href": "http://api.wan-san.com/v1/users/12345", 
                "id": "12345"
            },
            "self": {
                "href":  “http://api.wan-san.com/v1/payment-transactions/1234",
                "id": "1234"
            },
            “chargeInfo”: {
                “currency”: “USD”,
                “country”: “US”,
                “amount”: “100”
            }
        } 

+ Response 200 (application/json)

        {
            "self": {
                "href":  “http://api.wan-san.com/v1/payment-transactions/1234",
                "id": "1234"
            },
            “trackingUuid”: "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
            "user": {
                "href": "http://api.wan-san.com/v1/users/12345", 
                "id": "12345"
            },
            “paymentType”: “CAPTURE”,
            “chargeInfo”: {
                “currency”: “USD”,
                “country”: “US”,
                “amount”: “100”
            }
            “paymentInstrument”: {
                "href": "http://api.wan-san.com/v1/payment-instruments/12345", 
                "id": "12345"
            }, 
            “provider”: “BrainTree”,
            “merchantAccount”: “6xuysbasdf9ds”,
            “status”: “COMPLETED”,
            “updateDate”: “2014-01-20”,
            “paymentEvents”: [
                {
                    "paymentEventType": "CAPTURE",
                    "eventDate": "2014-02-12",
                    "currency": "USD",
                    "amount": "100"
                }
            ]
        }


##POST /payment-transactions/charges
create a new payment charge transaction

+ Request (application/json)

        { 
            “trackingUuid”: "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", 
            "user": {
                "href": "http://api.wan-san.com/v1/users/12345", 
                "id": "12345"
            }, 
            "paymentInstrument": {
                "href":  “http://api.wan-san.com/v1/payment-instruments/1234",
                "id": "1234"
            },
            “chargeInfo”: {
                “currency”: “USD”,
                “country”: “US”,
                “amount”: “100”
            }
        } 

+ Response 200 (application/json)

        {
            "self": {
                "href":  “http://api.wan-san.com/v1/payment-transactions/1234",
                "id": "1234"
            },
            “trackingUuid”: "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
            "user": {
                "href": "http://api.wan-san.com/v1/users/12345", 
                "id": "12345"
            }, 
            “paymentType”: “CHARGE”,
            “chargeInfo”: {
                “currency”: “USD”,
                “country”: “US”,
                “amount”: “100”
            },
            "paymentInstrument": {
                "href":  “http://api.wan-san.com/v1/payment-instruments/1234",
                "id": "1234"
            },
            “provider”: “BrainTree”,
            “merchantAccount”: “6xuysbasdf9ds”,
            “status”: “COMPLETED”,
            “updateDate”: “2014-01-20”,
            “paymentEvents”: [
                {
                    "paymentEventType": "CREATE",
                    "eventDate": "2014-02-12",
                    "currency": "USD",
                    "amount": "100"
                },
                {
                    "paymentEventType": "CHARGE",
                    "eventDate": "2014-02-12",
                    "currency": "USD",
                    "amount": "100"
                }
            ]
        }



##PUT /payment-transactions/{id}
reverse an existing payment

+ Request (application/json)

        { 
        	"self": {
                "href":  “http://api.wan-san.com/v1/payment-transactions/1234",
                "id": "1234"
            },
            “trackingUuid”: "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", 
            "user": {
                "href": "http://api.wan-san.com/v1/users/12345", 
                "id": "12345"
            },             
            "paymentInstrument": {
                "href":  “http://api.wan-san.com/v1/payment-instruments/1234",
                "id": "1234"
            },
            “paymentType”: “REVERSE”,
            “provider”: “BrainTree”,
            “merchantAccount”: “6xuysbasdf9ds”,
            “status”: “AUTHED”,
            “updateDate”: “2014-01-20”
            “paymentEvents”: [
                {
                    "paymentEventType": "CREATE",
                    "eventDate": "2014-02-12",
                    "currency": "USD",
                    "amount": "100"
                },
                {
                    "paymentEventType": "AUTH",
                    "eventDate": "2014-02-12",
                    "currency": "USD",
                    "amount": "100"
                }
            ]
        }

+ Response 200 (application/json)

        {
            "self": {
                "href":  “http://api.wan-san.com/v1/payment-transactions/1234",
                "id": "1234"
            },
            “trackingUuid”: "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
            "user": {
                "href": "http://api.wan-san.com/v1/users/12345", 
                "id": "12345"
            }, 
            “paymentType”: “REVERSE”,
            “chargeInfo”: {
                “currency”: “USD”,
                “country”: “US”,
                “amount”: “100”
            },
            "paymentInstrument": {
                "href":  “http://api.wan-san.com/v1/payment-instruments/1234",
                "id": "1234"
            },
            “provider”: “BrainTree”,
            “merchantAccount”: “6xuysbasdf9ds”,
            “status”: “CLOSED”,
            “updateDate”: “2014-01-20”
            “paymentEvents”: [
                {
                    "paymentEventType": "CREATE",
                    "eventDate": "2014-02-12",
                    "currency": "USD",
                    "amount": "100"
                },
                {
                    "paymentEventType": "AUTH",
                    "eventDate": "2014-02-12",
                    "currency": "USD",
                    "amount": "100"
                },
                {
                    "paymentEventType": "REVERSE",
                    "eventDate": "2014-02-12",
                    "currency": "USD",
                    "amount": "100"
                }
            ]
        }


##GET /payment-transactions/{id}
Get the payment details for an existing payment


+ Response 200 (application/json)

        {
            "self": {
                "href":  “http://api.wan-san.com/v1/payment-transactions/1234",
                "id": "1234"
            },
            “trackingUuid”: "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
            "user": {
                "href": "http://api.wan-san.com/v1/users/12345", 
                "id": "12345"
            }, 
            “paymentType”: “REVERSE”,
            “chargeInfo”: {
                “currency”: “USD”,
                “country”: “US”,
                “amount”: “100”
            }
            "paymentInstrument": {
                "href":  “http://api.wan-san.com/v1/payment-instruments/1234",
                "id": "1234"
            },
            “provider”: “BrainTree”,
            “merchantAccount”: “6xuysbasdf9ds”,
            “status”: “COMPLETED”,
            “updateDate”: “2014-01-20”
            “paymentEvents”: [
                {
                    "paymentEventType": "CREATE",
                    "eventDate": "2014-02-12",
                    "currency": "USD",
                    "amount": "100"
                },
                {
                    "paymentEventType": "AUTH",
                    "eventDate": "2014-02-12",
                    "currency": "USD",
                    "amount": "100"
                },
                {
                    "paymentEventType": "REVERSE",
                    "eventDate": "2014-02-12",
                    "currency": "USD",
                    "amount": "100"
                }
            ]
        }
