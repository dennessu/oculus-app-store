#TAX
API about tax(tax calculation and address verification)

## /calculateTax
###POST
calculate tax

+ Request (application/json)

		{
			"currency": "USD",
			"isRefund": false,
			"originalInvoiceId": null,
			"originalInvoiceDate": null,
			"invoiceId": 123,
			"invoiceDate": "2013-06-20 17:50:34",
			"billToAddress": {
				"zip": "90024",
				"country": "US",
				"city": "Los Angeles",
				"state": "CA",
			},
			"shipToAddress": null,
			"invoiceItems":
			[{
				"productId": "Id1",
				"quantity": 1,
				"productCode": "code1",
				"subTotal": "10",
				"productCategory": "category1",
			},
			{
				"productId": "Id2",
				"quantity": 1,
				"productCode": "code2",
				"subTotal": "10",
				"productCategory": "category2",
			}],
			"shipFromCountry": "US"
		}


+ Response 200 (application/json)

		{
			"taxTotal": 30,
			"isInclusive": "I"
			"currency": "USD",
			"isRefund": false,
			"originalInvoiceId": null,
			"originalInvoiceDate": null,
			"invoiceId": 123,
			"invoiceDate": "2013-06-20 17:50:34",
			"billToAddress": {
				"zip": "90024",
				"country": "US",
				"city": "Los Angeles",
				"state": "CA",
			},
			"shipToAddress": null,
			"invoiceItems":
			[{
				"taxTotal": 15,
				"productId": "Id1",
				"quantity": 1,
				"productCode": "code1",
				"subTotal": "10",
				"productCategory": "category1",
			},
			{
				"taxTotal": 15,
				"productId": "Id2",
				"quantity": 1,
				"productCode": "code2",
				"subTotal": "10",
				"productCategory": "category2",
			}],
			"shipFromCountry": "US"
		}


## /validateAddress
###POST
validate address

+ Request (application/json)

		{
			"zip": "90024",
			"country": "US",
			"city": "Some Fake City",
			"state": "CA"
		}


+ Response 200 (application/json)

        {
			"zip": "90024",
			"country": "US",
			"city": "Los Angeles",
			"state": "CA"
        }