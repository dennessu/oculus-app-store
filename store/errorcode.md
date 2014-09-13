## Common Errors

### 130.101 Unknown Error
Sample Response

    {
        "message": "Unknown Error",
        "code": "130.101",
        "details": null
    }

### 132.108 Expired Access Token
Request

    GET /v1/horizon-api/user-profile
    Authorization: Bearer QPNmRGoipgpecItVc23Rtm6GfgU6YZstUVR7FyfceBw // Access Token that already expired
Response    
    
    {
        "message": "Expired Access Token",
        "code": "132.108",
        "details": [
            {
                "field": "access_token",
                "reason": "The access_token 9xRhH6G-jONHKidmN~Icaf~X0L8yo-jRfQ74vg0zYOU is already expired"
            }
        ]
    }

### 132.107	Invalid Access Token
Request

    GET /v1/horizon-api/user-profile
    Authorization: Bearer abcd // Access Token that is invalid
Response    
    
    {
        "message": "Invalid Access Token",
        "code": "132.107",
        "details": [
            {
                "field": "access_token",
                "reason": "The access_token abcd is invalid"
            }
        ]
    }


### 130.003	Forbidden
Request

    GET /v1/horizon-api/user-profile // no access token is provided
Response    
    
    {
        "message": "Forbidden",
        "code": "130.003",
        "details": [
            {
                "field": "access_token",
                "reason": "The access token does not have sufficient scope to make the request to StoreResource.getUserProfile"
            }
        ]
    }


### 199.001 Input Error(Bad Json Request)
Request

    POST /v1/horizon-api/billing-profile/instruments
    Authorization: Bearer SCp3MCYoQafvKHhEvqtLVwqGD18fPSqYXATDiWjNHLc
    {,}
Response

    {
        "message": "Input Error",
        "code": "199.001",
        "details": [
            {
                "field": "request.body",
                "reason": "invalid Json: Unexpected character (',' (code 44)): was expecting double-quote to start field name"
            }
        ]
    }

Request
    
    POST /v1/horizon-api/billing-profile/instruments
    Authorization: Bearer SCp3MCYoQafvKHhEvqtLVwqGD18fPSqYXATDiWjNHLc
    {"notexist" : 1}
    
Response    

    {
        "message": "Input Error",
        "code": "199.001",
        "details": [
            {
                "field": "notexist",
                "reason": "invalid Json: Unrecognized Property"
            }
        ]
    }

### 130.001 Empty Request Body
Request

    POST /v1/horizon-api/billing-profile/instruments
    Authorization: Bearer SCp3MCYoQafvKHhEvqtLVwqGD18fPSqYXATDiWjNHLc   
Response    
    
    {
        "message": "Request body is required",
        "code": "130.001",
        "details": null
    }

### 131.122 Country Not Found
Request

    GET /v1/horizon-api/billing-profile?country=U2zS&locale=en_US
    Authorization: Bearer SCp3MCYoQafvKHhEvqtLVwqGD18fPSqYXATDiWjNHLc
Response
    
    {
        "message": "Country Not Found",
        "code": "131.122",
        "details": [
            {
                "field": "country",
                "reason": "Country with ID U2zS is not found"
            }
        ]
    }

### 131.124 Locale Not Found
Request

    GET http://localhost:8080/v1/horizon-api/billing-profile?country=US&locale=en_USS
    Authorization: Bearer SCp3MCYoQafvKHhEvqtLVwqGD18fPSqYXATDiWjNHLc
Response    

    {
        "message": "Locale Not Found",
        "code": "131.124",
        "details": [
            {
                "field": "locale",
                "reason": "Locale with ID en_USS is not found"
            }
        ]
    }

### 123.004 Offer Not Found
Request

    POST /v1/horizon-api/purchase/free
    {
      "offer" : {"id" : "nonexistingoffer"},
      "country" : {"id" : "US"}
    }
Response
    
    {
        "message": "Resource Not Found",
        "code": "123.004",
        "details": [
            {
                "field": "id",
                "reason": "offer nonexistingoffer is not found"
            }
        ]
    }

## POST /v1/horizon-api/id/name-check
### 130.001 Input Error
Request
 
    POST /v1/horizon-api/id/name-check
    {
    }
    
Response    

    {
        "message": "Input Error",
        "code": "130.001",
        "details": [
            {
                "field": "username",
                "reason": "Field is required"
            }
        ]
    }

## POST /v1/horizon-api/id/rate-credential
### 130.001 Input Error
Request

    POST /v1/horizon-api/id/rate-credential
    {
      "userCredential" : {}
    }
    
Response
    
    {
        "message": "Input Error",
        "code": "130.001",
        "details": [
            {
                "field": "userCredential.type",
                "reason": "Field is required"
            }
        ]
    }

Request

    POST /v1/horizon-api/id/rate-credential
    {
        "userCredential" : {
            "type" : "PIN",
            "value" :  "1234"       
        }
    }
    
Response
    
    {
        "message": "Input Error",
        "code": "130.001",
        "details": [
            {
                "field": "type",
                "reason": "Field value is invalid. Allowed values: PASSWORD"
            }
        ]
    }   
    
## POST /v1/horizon-api/id/create  
### 130.001 Input Error
Request
 
    POST v1/horizon-api/id/create
    {
      "username" : "test"
    }
    
Response    

    {
        "message": "Input Error",
        "code": "130.001",
        "details": [
            {
                "field": "email",
                "reason": "Field is required"
            }
        ]
    }

Request

    POST v1/horizon-api/id/create
    {
      "username" : "johndoz",
      "email" : "johndoe@test.com",
      "password" : "213W#e3213",
      "cor" :  "US",
      "preferredLocale" : "en_US",
      "pin" : "123",
      "dob" : "2014-08-15T19:20:55Z",
      "firstName" : "John",
      "lastName" : "Doe"
    }
Response
    
    {
        "message": "Input Error",
        "code": "130.001",
        "details": [
            {
                "field": "pin",
                "reason": "Field value is invalid. Invalid Pin Pattern"
            }
        ]
    }

Request

    POST v1/horizon-api/id/create
    {
      "username" : "johndoz",
      "email" : "johndoe@test.com",
      "password" : "12345678",
      "cor" :  "US",
      "preferredLocale" : "en_US",
      "pin" : "123",
      "dob" : "2014-08-15T19:20:55Z",
      "firstName" : "John",
      "lastName" : "Doe"
    }
Response
    
    {
        "message": "Invalid Password",
        "code": "130.001",
        "details": [
            {
                "field": "password",
                "reason": "Field value is invalid. Password must contain at least three of LowerCase character, Upper case character, Number and Special character"
            }
        ]
    }

Request
    
    {
    	"username" : "johndoe",
    	"email" : "johndoe@test.com",
    	"password" : "#Bugsfor$",
    	"cor" :  "US",
    	"preferredLocale" : "en_US",
    	"pin" : "1234",
    	"dob" : "2014-08-15T19:20:55Z",
    	"firstName" : "John",
    	"lastName" : "Doe"
    }
    
Response
    
    {
        "message": "Input Error",
        "code": "130.001",
        "details": [
            {
                "field": "email",
                "reason": "Field value is invalid. Mail is already used."
            }
        ]
    }

### 131.101 Invalid Password   
Request
 
    POST v1/horizon-api/id/create
    {
    	"username" : "johndoz",
    	"email" : "johndoe@test.com",
    	"password" : "123456",
    	"cor" :  "US",
    	"preferredLocale" : "en_US",
    	"pin" : "123",
    	"dob" : "2014-08-15T19:20:55Z",
    	"firstName" : "John",
    	"lastName" : "Doe"
    }
    
Response
    
    POST v1/horizon-api/id/create
    {
        "message": "Invalid Password",
        "code": "131.101",
        "details": [
            {
                "field": "password",
                "reason": "Password should be between 8 and 16"
            }
        ]
    }
    
    
### 131.002 Username Is Duplicate
Request

    POST v1/horizon-api/id/create
    {
      "username" : "johndoe",
      "email" : "johndoe@test.com",
      "password" : "12345",
      "cor" :  "US",
      "preferredLocale" : "en_US",
      "pin" : "123",
      "dob" : "2014-08-15T19:20:55Z",
      "firstName" : "John",
      "lastName" : "Doe"
    }
    
Response
    
    {
        "message": "Input Validation Failure",
        "code": "131.002",
        "details": [
            {
                "field": "username",
                "reason": "Field value is duplicate"
            }
        ]
    }

## POST /v1/horizon-api/id/sign-in
### 130.001 Input Error
Request

    POST /v1/horizon-api/id/sign-in
    {
        "username" : "johndoe",
        "userCredential" : {
          "type" : "PASSWORD",
          "value" : ""
        }
    }
Response

    {
        "message": "Input Error",
        "code": "130.001",
        "details": [
            {
                "field": "userCredential.value",
                "reason": "Field is required"
            }
        ]
    }
    
Request

    POST /v1/horizon-api/id/sign-in
    {
        "username" : "johndoe",
        "userCredential" : {
          "type" : "PIN",
          "value" : "1234"
        }
    }
Response

    {
        "message": "Input Error",
        "code": "130.001",
        "details": [
            {
                "field": "userCredential.type",
                "reason": "Field value is invalid. type must be PASSWORD "
            }
        ]
    }
### 132.103 Invalid Credential
Request

    POST /v1/horizon-api/id/sign-in
    {
        "username" : "johndoe",
        "userCredential" : {
          "type" : "PASSWORD",
          "value" : "abcd"
        }
    }
Response

    {
        "message": "Invalid Credential",
        "code": "132.103",
        "details": [
            {
                "field": "username, password",
                "reason": "User Password Incorrect"
            }
        ]
    }

## POST /v1/horizon-api/id/token
### 132.001 Invalid Refresh Token
Request

    POST /v1/horizon-api/id/token
    {
        "refreshToken" : "adbc"
    }
Response

    {
        "message": "Input Error",
        "code": "132.001",
        "details": [
            {
                "field": "refresh_token",
                "reason": "Field value is invalid. adbc"
            }
        ]
    }

### 132.104 Refresh Token Expired
Request

    POST /v1/horizon-api/id/token
    {
        "refreshToken" : "eexugc2E4CJJy~hcl-0rUb1jtPfYPGrjv~cDSsciL-M.U-7rWpuoPC43upLKQxqfjw"
    }
Response

    {
        "message": "Expired Refresh Token.",
        "code": "132.104",
        "details": [
            {
                "field": "refresh_token",
                "reason": "The refresh_token eexugc2E4CJJy~hcl-0rUb1jtPfYPGrjv~cDSsciL-M.U-7rWpuoPC43upLKQxqfjw is already expired"
            }
        ]
    }    

## POST /v1/horizon-api/user-profile
### 130.108 Invalid Challenge Answer
Request 

    POST /v1/horizon-api/user-profile
    Authorization: Bearer SCp3MCYoQafvKHhEvqtLVwqGD18fPSqYXATDiWjNHLc
    {
        "userProfile": {
            "password" : "abcde"
        },
        "challengeAnswer" : {
            "type" : "PASSWORD", 
            "password" : "adbcd"
        }
    }

Response

    {
        "message": "Invalid Challenge Answer.",
        "code": "130.108",
        "details": null
    }

### 131.101 Invalid Password
Request 

    POST /v1/horizon-api/user-profile
    Authorization: Bearer SCp3MCYoQafvKHhEvqtLVwqGD18fPSqYXATDiWjNHLc
    {
        "userProfile": {
            "password" : "123456" // update to a invalid password
        },
        "challengeAnswer" : {
            "type" : "PASSWORD", 
            "password" : "#Welcome123"
        }
    }    

Response

    {
        "message": "Invalid Password",
        "code": "131.101",
        "details": [
            {
                "field": "password",
                "reason": "Password should be between 8 and 16"
            }
        ]
    }

### 130.001 Invalid Field
Request 

    POST /v1/horizon-api/user-profile
    Authorization: Bearer SCp3MCYoQafvKHhEvqtLVwqGD18fPSqYXATDiWjNHLc
    {
        "userProfile": {
            "pin" : "123c"  // invalid pin pattern
        },
        "challengeAnswer" : {
            "type" : "PASSWORD", 
            "password" : "#Welcome123"
        }
    }    

Response

    {
        "message": "Input Error",
        "code": "130.001",
        "details": [
            {
                "field": "vale",
                "reason": "Field value is invalid. Invalid Pin Pattern"
            }
        ]
    }


## GET /v1/horizon-api/billing-profile
### 130.001 Input Error
Request

    GET /v1/horizon-api/billing-profile
    Authorization: Bearer SCp3MCYoQafvKHhEvqtLVwqGD18fPSqYXATDiWjNHLc
    
Response

    {
        "message": "Input Error",
        "code": "130.001",
        "details": [
            {
                "field": "country",
                "reason": "Field is required"
            }
        ]
    }


## POST /v1/billing-profile/instruments
### 130.001 Input Error
Request
 
    POST /v1/horizon-api/billing-profile/instruments
    Authorization: Bearer SCp3MCYoQafvKHhEvqtLVwqGD18fPSqYXATDiWjNHLc
    {
      "locale": {"id" : "en_US"},
      "country": {"id" : "US"},
      "instrument" : {
        "type" : "PAYPAL"
      }
    }
    
Response
    
    {
        "message": "Input Error",
        "code": "130.001",
        "details": [
            {
                "field": "instrument.type",
                "reason": "Field value is invalid. Unsupported instrument type."
            }
        ]
    }

## POST /v1/horizon-api/purchase/free
### 130.110 Invalid Offer
Request 
    
    POST /v1/horizon-api/purchase/free
    Authorization: Bearer SCp3MCYoQafvKHhEvqtLVwqGD18fPSqYXATDiWjNHLc
    {
      "offer" : {"id" : "9ae6f8961d12cc163156c1fdad1ed7a8"},
      "country" : {"id" : "US"}
    }
    
Response
    
    {
        "message": "Invalid offer: Offer not free.",
        "code": "130.110",
        "details": null
    }



