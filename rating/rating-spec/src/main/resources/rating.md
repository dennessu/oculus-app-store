#rating
APIs about rating engine

##POST /rating/offers
rate offers

+ Request (application/json)

        {
            "userId": {"href": "https://data.oculusvr.com/v1/users/123", "id": 123},
            "country": "US",
            "currency": "USD",
            "offers":
            [
                {"href": "https://data.oculusvr.com/v1/offers/offer001", "id": "offer001"},
                {"href": "https://data.oculusvr.com/v1/offers/offer002", "id": "offer002"}
            ]
        }

+ Response 200 (application/json)

        {
            "results":
            [{
                "offerId": {"href": "https://data.oculusvr.com/v1/offers/offer001", "id": "offer001"},
                "originalAmount": 50.00,
                "finalAmount": 40.00,
                "discountAmount": 10.00,
                "promotions": 
                [
                    {"href": ""https://data.oculusvr.com/v1/promotions/promotion001", "id": "promotion001"}
                ]
            },
            {
                "offerId": {"href": "https://data.oculusvr.com/v1/offers/offer002", "id": "offer002"},
                "originalAmount": 80.00, 
                "finalAmount": 80.00,
                "discountAmount": 0.00, 
                "promotionRules": []
            }]
        }
        
+ Response 500 (application/json)

        {
            "errorMsg": "PRICE_NOT_FOUND"
        }
        
##POST /rating/order
rate an order

+ Request (application/json)

        {
            "userId": {"href": "https://data.oculusvr.com/v1/users/123", "id": 123},
            "country": "US",
            "currency": "USD",
            "couponCodes":
            [
                {"href": "http://api.oculus.com/coupons/111111", "id": "111111"},
                {"href": "http://api.oculus.com/coupons/222222", "id": "222222"}
            ],
            "offers":
            [{ 
                "offerId": {"href": "https://data.oculusvr.com/v1/offers/offer001", "id": "offer001"},
                "quantity": 2,
                "shippingInfo":
                {
                    "shippingMethod": "Standard",
                    "destinationCountry": "US"
                }
            }, 
            { 
                "offerId": {"href": "https://data.oculusvr.com/v1/offers/offer002", "id": "offer002"},
                "quantity": 5 ,
                "shippingInfo":
                {
                    "shippingMethod": "Standard",
                    "destinationCountry": "CA"
                }
            },
            {
                "offerId": {"href": "https://data.oculusvr.com/v1/offers/offer003", "id": "offer003"},
                "quantity": 2
            }]
            "shippingInfo":
            {
                "shippingMethod": "Economy",
                "destinationCountry": "US"
            }
        }
 
        
+ Response 200 (application/json)

        {  
            "results":
            [{ 
                "offerId": {"href": "https://data.oculusvr.com/v1/offers/offer001", "id": "offer001"},
                "quantity": 2, 
                "originalAmount": 50.00,
                "finalAmount": 40.00, 
                "discountAmount": 10.00, 
                "promotions":
                [
                    {"href": ""https://data.oculusvr.com/v1/promotions/promotion001", "id": "promotion001"}
                ]
            }, 
            
            {
                "offerId": {"href": "https://data.oculusvr.com/v1/offers/offer002", "id": "offer002"},
                "quantity": 2, 
                "originalAmount": 120.00, 
                "finalAmount": 90.00,
                "discountAmount": 30.00, 
                "promotions":
                [
                    {"href": "https://data.oculusvr.com/v1/promotions/promotion004", "id": "promotion004"}
                ]
            },
            {
                "offerId": {"href": "https://data.oculusvr.com/v1/offers/offer002", "id": "offer002"},
                "quantity": 3, 
                "originalAmount": 120.00, 
                "finalAmount": 120.00,
                "discountAmount": 0.00, 
                "promotions": []
            }], 
            "cart": 
            { 
                "cartPromotion": {"href": "https://data.oculusvr.com/v1/promotions/promotion007", "id": "promotion007"},
                "cartDiscountAmount": 10.00,
                "cartFinalAmount": 370.00
            },
            "shipping":
            {
                "shippingPromotion": [],
                "shippingFee": 10.00
            },
            "violations":
            [{
                "type": "COUPON_NOT_APPLIED",
                "couponCodes":
                [
                    {"href": "http://api.oculus.com/coupons/111111", "id": "111111"}
                ]
            },
            {
                "type": "PRICE_NOT_FOUND",
                "offers":
                [
                    {"href": "https://data.oculusvr.com/v1/offers/offer003", "id": "offer003"}
                ]
            }]
        }
