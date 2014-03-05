#Cart
API for shopping cart operation

##Post /carts
Create a shopping cart for a user including anonymous one.

+ Request (application/json)
	+ Body

			{
				"userId": "1000002",
				"cartName": "sample",
				"requestorId": "dev",
				"userAnonymous": false,
				"currency": "USD"
			}

+ Response 200 (application/json)

	+ Body

			{
				"cartId": "1000003",
				"userId": "1000002",
				"cartName": "sample",
				"requestorId": "dev",
				"userAnonymous": false,
				"currency": "USD",
				"token": 0,
				"dateCreated": "2014-01-12 12:00:00",
				"dateModified": "2014-01-12 12:00:00"
			}

##GET /carts/{cartId}
Get the shopping cart information

+ Parameters
	+ excludeRating (=`false`, optional, boolean) ... indicates whether to exclude rating information(including prices before discount and after discount) from the result.

+ Response 200 (application/json)

	+ Body

			{
				"cartId": "1000003",
				"userId": "1000002",
				"cartName": "sample",
				"requestorId": "dev",
				"userAnonymous": false,
				"currency": "USD",
				"token": 10,
				"dateCreated": "2014-01-12 12:00:00",
				"dateModified": "2014-01-12 12:00:00",
				"productItems": 
				[
					{
						"cartId": "1000003",
						"productItemId": "1000004",
						"itemId": "1000005",
						"skuId": "1000006",
						"quantity": 10,
						"price": 
							{
								"unitPrice": 1.0,
								"totalPriceWithoutTax": 10.0,
								"totalDiscountAmount": 1.0,
								"totalDiscountRate": 0.1
							},
						"selected": true,
						"discountLineItems":
						[
							{
								"discountRate": 0.1,
								"discountAmount": 1.0,
								"discountRuleId": "123221"
							}
						],
						"dateCreated": "2014-01-12 12:00:00",
						"dateModified": "2014-01-12 12:00:00"
					}
				]
			}

##GET /carts
Get the shopping cart by the combination of user, requestor id and cart name.
+ Parameters
	+ cartName (required, string) ... the cart name
	+ userId (required, string) ... the user id
	+ anonymous (required, boolean) ... the user is anonymous or not
	+ autoCreate  = `false` (optional, boolean) ... Whether to create the cart if the cart could not be found by the given arguments combination 
	+ excludeRating (=`false`, optional, boolean) ... indicates whether to exclude rating information(including prices before discount and after discount) from the result.

+ Response 200 (application/json)

	+ Body

			{
				"cartId": "1000003",
				"userId": "1000002",
				"cartName": "sample",
				"requestorId": "dev",
				"userAnonymous": false,
				"currency": "USD",
				"token": 10,
				"dateCreated": "2014-01-12 12:00:00",
				"dateModified": "2014-01-12 12:00:00",
				"productItems": 
				[
					{
						"cartId": "1000003",
						"productItemId": "1000004",
						"itemId": "1000005",
						"skuId": "1000006",
						"quantity": 10,
						"dateCreated": "2014-01-12 12:00:00",
						"dateModified": "2014-01-12 12:00:00"
					}
				]
			}

##PUT /carts/{cartId}
Update the cart. Fields of the cart could be updated including: shippingMethod,shippingCountry,currency

+ Request (application/json)
	+ Body

			{
				"shippingMethod": "STANDARD",
				"shippingCountry": "US"
			}

+ Response 200 (application/json)
	+ Body

			{
				"cartId": "1000003",
				"userId": "1000002",
				"cartName": "sample",
				"requestorId": "dev",
				"userAnonymous": false,
				"currency": "USD",
				"shippingMethod": "STANDARD",
				"shippingCountry": "US",
				"token": 10,
				"dateCreated": "2014-01-12 12:00:00",
				"dateModified": "2014-01-12 16:00:00",
				"productItems": 
				[
					{
						"cartId": "1000003",
						"productItemId": "1000004",
						"itemId": "1000005",
						"skuId": "1000006",
						"quantity": 10,
						"price": 
							{
								"unitPrice": 1.0,
								"totalPriceWithoutTax": 10.0,
								"totalDiscountAmount": 1.0,
								"totalDiscountRate": 0.1
							},
						"selected": true,
						"discountLineItems":
						[
							{
								"discountRate": 0.1,
								"discountAmount": 1.0,
								"discountRuleId": "123221"
							}
						],
						"dateCreated": "2014-01-12 12:00:00",
						"dateModified": "2014-01-12 12:00:00"
					}
				]
			}

##POST /carts/{cartId}/close
Close the shopping cart
+ Parameters
	+ partialClose (=`false` optional, boolean) ... Indicates do partial close or not. If partial close, only selected item will be closed.

+ Response 200 (application/json)
	+ Body

			{
				"cartId": "1000003",
				"userId": "1000002",
				"cartName": "sample",
				"requestorId": "dev",
				"userAnonymous": false,
				"currency": "USD",
				"shippingMethod": "",
				"shippingCountry": "US",
				"token": 11,
				"dateCreated": "2014-01-12 12:00:00",
				"dateModified": "2014-01-12 14:00:00"
			}

##POST /carts/{cartId}/merge
Merge the items from one shopping cart into another cart identified by {cartId}
+ Parameters
	+ fromCartId (optional, string) ... The id of the cart to merge from

+ Response 200 (application/json)
	+ Body

			{
				"cartId": "1000003",
				"userId": "1000002",
				"cartName": "sample",
				"requestorId": "dev",
				"userAnonymous": false,
				"currency": "USD",
				"shippingMethod": "STANDARD",
				"shippingCountry": "US",
				"token": 13,
				"dateCreated": "2014-01-12 12:00:00",
				"dateModified": "2014-01-12 12:00:00",
				"productItems": 
				[
					{
						"cartId": "1000003",
						"productItemId": "1000004",
						"itemId": "1000005",
						"skuId": "1000006",
						"quantity": 10,
						"price": 
							{
								"unitPrice": 1.0,
								"totalPriceWithoutTax": 10.0,
								"totalDiscountAmount": 1.0,
								"totalDiscountRate": 0.1
							},
						"selected": true,
						"discountLineItems":
						[
							{
								"discountRate": 0.1,
								"discountAmount": 1.0,
								"discountRuleId": "123221"
							}
						],
						"dateCreated": "2014-01-12 12:00:00",
						"dateModified": "2014-01-12 12:00:00"
					}
				]
			}

##POST /carts/{cartId}/productItems
Add a product item into cart.

+ Request (application/json)
	+ Body

			{
				"itemId": "1000005",
				"skuId": "1000006",
				"quantity": 10,
			}

+ Response 200 (application/json)
	+ Body

			{
				"cartId": "1000003",
				"userId": "1000002",
				"cartName": "sample",
				"requestorId": "dev",
				"userAnonymous": false,
				"currency": "USD",
				"shippingMethod": "STANDARD",
				"shippingCountry": "US",
				"token": 13,
				"dateCreated": "2014-01-12 12:00:00",
				"dateModified": "2014-01-12 19:00:00",
				"productItems": 
				[
					{
						"cartId": "1000003",
						"productItemId": "1000004",
						"itemId": "1000005",
						"skuId": "1000006",
						"quantity": 10,
						"price": 
							{
								"unitPrice": 1.0,
								"totalPriceWithoutTax": 10.0,
								"totalDiscountAmount": 1.0,
								"totalDiscountRate": 0.1
							},
						"selected": true,
						"discountLineItems":
						[
							{
								"discountRate": 0.1,
								"discountAmount": 1.0,
								"discountRuleId": "123221"
							}
						],
						"dateCreated": "2014-01-12 12:00:00",
						"dateModified": "2014-01-12 12:00:00"
					}
				]
			}

##PUT /carts/{cartId}/productItems/{productItem}
Update the product item of cart. Fields could be updated are the following: quantity, skuId, properties

+ Request (application/json)
	+ Body

			{
				"quantity": 20,
			}

+ Response 200 (application/json)
	+ Body

			{
				"cartId": "1000003",
				"userId": "1000002",
				"cartName": "sample",
				"requestorId": "dev",
				"userAnonymous": false,
				"currency": "USD",
				"shippingMethod": "STANDARD",
				"shippingCountry": "US",
				"token": 13,
				"dateCreated": "2014-01-12 12:00:00",
				"dateModified": "2014-01-12 12:00:00",
				"productItems": 
				[
					{
						"cartId": "1000003",
						"productItemId": "1000004",
						"itemId": "1000005",
						"skuId": "1000006",
						"quantity": 20,
						"price": 
							{
								"unitPrice": 1.0,
								"totalPriceWithoutTax": 20.0,
								"totalDiscountAmount": 2.0,
								"totalDiscountRate": 0.1
							},
						"selected": true,
						"discountLineItems":
						[
							{
								"couponCode": "2134123",
								"discountRate": 0.1,
								"discountAmount": 2.0,
								"discountRuleId": "123221"
							}
						],
						"dateCreated": "2014-01-12 12:00:00",
						"dateModified": "2014-01-12 12:00:00"
					}
				]
			}

##DELETE /carts/{cartId}/productItems/{productItem}
Remove an item from cart

+ Response 200 (application/json)
	+ Body

			{
				"cartId": "1000003",
				"userId": "1000002",
				"cartName": "sample",
				"requestorId": "dev",
				"userAnonymous": false,
				"currency": "USD",
				"shippingMethod": "STANDARD",
				"shippingCountry": "US",
				"token": 18,
				"dateCreated": "2014-01-12 12:00:00",
				"dateModified": "2014-01-12 12:00:00"
			}

##POST /carts/{cartId}/couponItems
Add an coupon item into cart.

+ Request (application/json)
	+ Body

			{
				"couponCode": "172918281"
			}

+ Response 200 (application/json)
	+ Body

			{
				"cartId": "1000003",
				"userId": "1000002",
				"cartName": "sample",
				"requestorId": "dev",
				"userAnonymous": false,
				"currency": "USD",
				"shippingMethod": "STANDARD",
				"shippingCountry": "US",
				"token": 13,
				"dateCreated": "2014-01-12 12:00:00",
				"dateModified": "2014-01-12 18:00:00",
				"productItems": 
				[
					{
						"cartId": "1000003",
						"productItemId": "1000004",
						"itemId": "1000005",
						"skuId": "1000006",
						"quantity": 10,
						"price": 
							{
								"unitPrice": 1.0,
								"totalPriceWithoutTax": 10.0,
								"totalDiscountAmount": 2.0,
								"totalDiscountRate": 0.2
							},
						"selected": true,
						"discountLineItems":
						[
							{
								"discountRate": 0.1,
								"discountAmount": 1.0,
								"discountRuleId": "123221"
							},
							{
								"couponCode": "172918281",
								"discountRate": 0.1,
								"discountAmount": 1.0,
								"discountRuleId": "29123"
							}
						],
						"dateCreated": "2014-01-12 12:00:00",
						"dateModified": "2014-01-12 17:00:00"
					}
				],
				"couponItems":
				[
					{
						"cartId": "1000003",
						"couponItemId": "1000013",
						"couponCode": "172918281",
						"dateCreated": "2014-01-12 18:00:00",
						"dateModified": "2014-01-12 18:00:00"
					}
				]
			}

##DELETE /carts/{cartId}/couponItems/{couponItemId}
Remove an coupon item from the cart.

+ Response 200 (application/json)
	+ Body

			{
				"cartId": "1000003",
				"userId": "1000002",
				"cartName": "sample",
				"requestorId": "dev",
				"userAnonymous": false,
				"currency": "USD",
				"shippingMethod": "STANDARD",
				"shippingCountry": "US",
				"token": 21,
				"dateCreated": "2014-01-12 12:00:00",
				"dateModified": "2014-01-12 19:00:00",
				"productItems": 
				[
					{
						"cartId": "1000003",
						"productItemId": "1000004",
						"itemId": "1000005",
						"skuId": "1000006",
						"quantity": 10,
						"price": 
							{
								"unitPrice": 1.0,
								"totalPriceWithoutTax": 10.0,
								"totalDiscountAmount": 1.0,
								"totalDiscountRate": 0.1
							},
						"selected": true,
						"discountLineItems":
						[
							{
								"discountRate": 0.1,
								"discountAmount": 1.0,
								"discountRuleId": "123221"
							}
						],
						"dateCreated": "2014-01-12 12:00:00",
						"dateModified": "2014-01-12 12:00:00"
					}
				]
			}
