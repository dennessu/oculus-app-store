#rating
API about rating engine


##POST /rating/item
rate single item

+ Request (application/json)

        { 
            "country": "US", 
            "item": 
            {
                "itemId": "item001",
                "sku": "sku001",
                "quantity": 2
            }
        } 

+ Response 200 (application/json)

        {
            "item": 
            {
                "itemId": "item001",
                "sku": "sku001",
                "quantity": 2,
                "originalAmount": 19.99,
                "finalAmount": 9.99,
                "discountAmount": 10.00,
                "promotionRules": ["promotion001"]            
            }
        }


##POST /rating/items
rate multiple items

+ Request (application/json)

        {
            "userId": "user123",
            "items": 
            [{ 
                "itemId": "item001", 
                "sku": "sku001", 
                "quantity": 2
            }, 
            { 
                "itemId": "item001", 
                "sku": "sku002", 
                "quantity": 5
            }] 
        }

+ Response 200 (application/json)

        {
            "status": "SUCCESS",
            "items":
            [{
                "itemId": "item001",
                "sku": "sku001",
                "quantity": 2,
                "originalAmount": 50.00,
                "finalAmount": 40.00,
                "discountAmount": 10.00,
                "promotionRules": ["promotion001", "promotion002", "promotion003"]
            },
            {
                "itemId": "item002", 
                "sku": "sku001", 
                "quantity": 2, 
                "originalAmount": 80.00, 
                "finalAmount": 50.00,
                "discountAmount": 30.00, 
                "promotionRules": [ "promotion004"] 
            },
            {
                "itemId": "item002", 
                "sku": "sku001", 
                "quantity": 3, 
                "originalAmount": 120.00, 
                "finalAmount": 120.00,
                "discountAmount": 0.00, 
                "promotionRules": []
            }]
        }
        
+ Response 500 (application/json)

        {
            "status": "FAILED",
            "errorMsg": "PRICE_NOT_CONFIGURED"
        }
        
##POST /rating/cart
rate shopping cart 

+ Request (application/json)

        {
            "userId": "user123", 
            "country": "US", 
            "couponCode": "1234567", 
            "items":  
            [{ 
                "itemId": "item001", 
                "sku": "sku001", 
                "quantity": 2 
            }, 
            { 
                "itemId": "item002", 
                "sku": "sku002", 
                "quantity": 2 
            }] 
        }

+ Response 500 (application/json)

        { 
            "status": "FAILED", 
            "errorMsg": "ITEM_NOT_FOUND" 
        } 
        
+ Response 200 (application/json)

        { 
            "status": "SUCCESS", 
            "warningMsg": "INVALID_COUPON_CODE", 
            "items": 
            [{ 
                "itemId": "item001", 
                "sku": "sku001", 
                "quantity": 2, 
                "originalAmount": 50.00,
                "finalAmount": 40.00, 
                "discountAmount": 10.00, 
                "promotionRules": ["promotion001", "promotion002", "promotion003"] 
            }, 
            { 
                "itemId": "item002", 
                "sku": "sku002", 
                "quantity": 2,
                "originalAmount": 80.00, 
                "finalAmount": 50.00,
                "discountAmount": 30.00, 
                "promotionRules": [] 
            }], 
            "cart": 
            { 
                "cartDiscountAmount": 90.00, 
                "cartPromotionRules": ["promotion007"], 
                "shippingDiscountAmount": 0.00, 
                "shippingPromotionRules": [] 
            } 
        }
