FORMAT: 1A

HOST: http://www.wan-san.com

# Cart API

Cart API is a collection of APIs for Cart operation.

# Group Cart

Fields:

+  <span id="self">self...reference to the self resource</span>
+  <span id="user">user...reference to the user who owns the cart</span>
+  <span id="cartName">cartName...string to represent the name of the cart</span>
+  <span id="resourceAge">resourceAge...the resource age of the cart resource</span>
+  <span id="createdTime">createdTime...the cart's created date.</span>
+  <span id="updatedTime">updatedTime...the cart's modified date.</span>
+  <span id="offers">offers...list of offers contained in the cart.</span>
+  <span id="coupons">coupons...list of coupons used in the cart.</span>

# Cart [/carts]

+ Model (application/json)

        {
                "self":
                    {
                        "href": "http://api.oculusvr.com/users/1000002/carts/1000003",
                        "id": "1000003"
                    },
                "user":
                    {
                        "href": "http://api.oculusvr.com/users/1000002",
                        "id": "1000002"
                    },
                "cartName": "sample",
                "createdTime": "2014-02-19T03:25:20Z",
                "updatedTime": "2014-02-19T03:25:20Z",
                "resourceAge": 12,
                "offers":
                [
                    {
                        "self":
                            {
                                "href": "http://api.oculusvr.com/users/1000002/carts/1000003/offers/1000004",
                                "id": "1000004"
                            },
                        "offer":
                            {
                                "href": "http://api.oculusvr.com/offers/12345",
                                "id": "12345"
                            },
                        "quantity": 20,
                        "selected": true,
                        "createdTime": "2014-02-19T03:25:20Z",
                        "updatedTime": "2014-02-19T03:25:20Z"
                    }
                ],
                "coupons":
                [
                    {
                        "self":
                            {
                                "href": "http://api.oculusvr.com/users/1000002/carts/1000003/coupons/1000013",
                                "id": "1000013"
                            },
                        "coupon":
                            {
                                "href": "http://api.oculusvr.com/coupons/2000013",
                                "id": "2000013"
                            },
                        "createdTime": "2014-02-19T03:25:20Z",
                        "updatedTime": "2014-02-19T03:25:20Z"
                    }
                ]
         }

## POST /users/{key}/carts
Create a named shopping cart for a user.

+ Request (application/json)
    + Body

            {
                "cartName": "sample",
	            "offers": [] ,
	            "coupons": []
            }

+ Response 200

    [Cart][]

## GET /users/{keyUser}/carts/{keyCart}
Get the shopping cart by key of the cart. This is the canonical URI for the cart resources.

+ Parameters

+ Response 200

    [Cart][]

## GET /users/{key}/carts/primary
Get the primary cart of a user. This is the convenience URI for the cart resources.

+ Response 302
    + Header

            Location: http://api.oculusvr.com/users/1000002/carts/1000003

    + Body
    [Cart][]

## GET /users/{key}/carts(?cartName)
Get the carts by cart name. This is the convenience URI for the cart resources.

+ Parameters
    + cartName (required, string) ... the cart name

+ Response 302
    + Header

            Location: http://api.oculusvr.com/users/1000002/carts/1000003

    + Body
    [Cart][]

## PUT /users/{keyUser}/carts/{keyCart}
Fully update the cart.

+ Request (application/json)
    + Body
     [Cart][]

+ Response 200
    [Cart][]

## POST /users/{keyUser}/carts/{keyCart}/merge
Merge the items from the shopping cart given by the request into the cart given by Uri

+ Request (application/json)
    + Body

            {
                 "self":
                     {
                             "href": "http://api.oculusvr.com/users/12124/carts/1000003",
                             "id": "1000003"
                     },
                "user":
                    {
                             "href": "http://api.oculusvr.com/users/12124",
                             "id": "12124"
                     }
            }

+ Response 200
    [Cart][]

## POST /users/{keyUser}/carts/{keyCart}/offers
Add an offer into the cart.

+ Request (application/json)
    + Body

            {
                "offer":
                    {
                        "href": "http://api.oculusvr.com/offers/12345",
                        "id": "12345"
                    },
                "quantity": 20,
                "selected": true
            }

+ Response 200
    [Cart][]

## PUT /users/{userKey}/carts/{cartKey}/offers/{offerKey}
Update the offers in the cart.

+ Request (application/json)
    + Body

            {
                "offer":
                    {
                        "href": "http://api.oculusvr.com/offers/12345",
                        "id": "12345"
                    },
                "quantity": 20,
	            "selected": true
            }

+ Response 200
    [Cart][]

## DELETE /users/{userKey}/carts/{cartKey}/offers/{offerKey}
Remove an item from cart

+ Response 204


## POST /users/{userKey}/carts/{cartKey}/coupons
Add an coupon item into cart.

+ Request (application/json)
    + Body

            {
                "coupon":
                    {
                        "href": "http://api.oculusvr.com/coupons/1000013",
                        "id": "1000013"
                    }
            }

+ Response 200
    [Cart][]

## DELETE  /users/{userKey}/carts/{cartKey}/coupons/{couponKey}
Remove an coupon item from the cart.


+ Response 204