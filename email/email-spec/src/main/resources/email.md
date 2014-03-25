FORMAT: 1A
HOST: http://www.servicecommerce.com

# Email API
Email API defines a set of APIs to operate on email and related resources.

* emails ... The core data of the email.
* templates ... The template data of the email.

## Group Emails
The API operating on the email data.

#### Email Fields
* self ... href and id to identify the email.
* user ... string to identity the customer.
* source ... The source of the email.
* action ... The action of the email, like "welcome", "reset", "purchase".
* locale ... Consists of 2 character ISO 639 language code and 2 character ISO 3166 country code.
* priority ... Define the email priority.
* properties ... The properties of email, all properties are depend on **source** and **action**.
* type ... The type of email.
* recipient ... The recipient of email.
* scheduleDate ... The schedule date of email.
* createdTime... The date time when the email is created. The value is auto-generated.
* modifiedTime... The date time when the email is updated. The value is auto-generated.

### Email [/emails]

#### Create a Email [POST]
The general email will be create if "scheduleDate" is not specified. otherwise, that is a schedule email, it will be send at specified date by service.

+ Request (application/json)

		{
			"user": {
				"href": "http://api.servicecommerce.com/users/123456",
				"id": "123456"
			},
			"source":"servicecommerce",
			"action":"welcome",
			"locale":"en_US",
			"priority":0,
			"type":"COMMERCE",
			"recipient":"user@example.com",
			"scheduleDate":null,
			"properties":{
				"name1":"value1",
				"name2":"value2",
				"name3":"value3"
			}
		}

+ Response 200 (application/json)

		{	
			"self": {
	            "href": "http://api.servicecommerce.com/emails/12345",
	            "id": "12345"
	        },
			"user": {
				"href": "http://api.servicecommerce.com/users/123456",
				"id": "123456"
			},
			"source":"servicecommerce",
			"action":"welcome",
			"locale":"en_US",
			"priority":0,
			"type":"COMMERCE",
			"recipient":"user@example.com",
			"status":"SUCCEED",
			"statusReason":null,
			"retryCount":0,
			"isResend":false,
			"sentDate":"2014-02-01",
			"scheduleDate":null,
			"properties":{
				"name1":"value1",
				"name2":"value2",
				"name3":"value3"
			},
			"createdTime":"2014-02-01T02:00:03.123Z",
			"modifiedTime":"2014-02-01T02:00:06.123Z"
		}

### Email Collections [/emails/{id}]

+ Parameters
	+ id (required, string) ... The id of the email.

#### Retrieve a Email [GET]
Retrieve the email object with all its details by id.

+ Response 200 (application/json)

		{	
			"self": {
	            "href": "http://api.servicecommerce.com/emails/12345",
	            "id": "12345"
	        },
			"user": {
				"href": "http://api.servicecommerce.com/users/123456",
				"id": "123456"
			},
			"source":"servicecommerce",
			"action":"welcome",
			"locale":"en_US",
			"priority":0,
			"type":"COMMERCE",
			"recipient":"user@example.com",
			"status":"SUCCEED",
			"statusReason":null,
			"retryCount":0,
			"isResend":false,
			"sentDate":"2014-02-01",
			"scheduleDate":null,
			"properties":{
				"name1":"value1",
				"name2":"value2",
				"name3":"value3"
			}
		}

#### Update Schedule Email [PUT]
Update an existing schedule email, only schedule email can be update.

+ Request (application/json)

		{	
			"self": {
	            "href": "http://api.servicecommerce.com/emails/12345",
	            "id": "12345"
	        },
			"user": {
				"href": "http://api.servicecommerce.com/users/123456",
				"id": "123456"
			},
			"source":"servicecommerce",
			"action":"welcome",
			"locale":"en_US",
			"priority":0,
			"recipient":"user@example.com",
			"scheduleDate":"2014-04-01",
			"properties":{
				"name1":"value1",
				"name2":"value2",
				"name3":"value3"
			}
		}

+ Repsonse 200 (application/json)

		{	
			"self": {
	            "href": "http://api.servicecommerce.com/emails/12345",
	            "id": "12345"
	        },
			"user": {
				"href": "http://api.servicecommerce.com/users/123456",
				"id": "123456"
			},
			"source":"servicecommerce",
			"action":"welcome",
			"locale":"en_US",
			"priority":0,
			"recipient":"user@example.com",
			"scheduleDate":"2014-04-01",
			"properties":{
				"name1":"value1",
				"name2":"value2",
				"name3":"value3"
			},
            "createdTime":"2014-02-01T02:00:03.123Z",
            "modifiedTime":"2014-02-01T02:00:06.123Z"
		}

#### Delete a Schedule Email [DELETE]
Delete an existing schedule email, only schedule email can be delete.

+ Response 204



## Group Email Template
The API operating on the email template. The template is associated with email service provider. All variables are defined with template.

#### Email Template Fields
* template ... href and id to identify the email template.
* providerName ... The provider name of the email template.
* providerIndex ... The provider index of the email template.
* name ... The unique name of the email, made up of **source**,**source**,**locale**. The format is **"source"."action"."locale"**.
* listOfVariables ... The variables of the template defined.
* subject ... The subject of email.
* fromAddress ... The from address of email.
* fromName ... The from name of email.
* createTime ... The date time when template is created. The value is auto-generated.
* modifiedTime ... The date time when template is updated. The value is auto-generated.

### Email Template [/email-templates?page={page}&size={size}]

+ Parameter
    + page (optional, int) ... The page number of email templates. The default value is 1.
    + size (optional, int) ... The page size of email templates. The default value is 20.

#### Get all Email Templates [GET]
All the email templates are returned.

+ Response 200 (application/json)

        {
            "criteria":[
                {
                    "self":{
                        "href":"http://api.servicecommerce.com/email-templates/123456",
                        "id":"123456"
                    },
                    "name":"source.action.en_US",
                    "providerName":"mandrill",
                    "providerIndex":"123456789",
                    "listOfVariables":["var1","var2","var3","var4"],
                    "subject":"welcome123",
                    "fromAddress":"from@example.com",
                    "fromName":"from name",
                    "createTime":"2014-02-01T02:00:03.123Z",
                    "modifiedTime":"2014-02-01T02:00:03.123Z"
                }
            ],
            //...
            "next":"http://api.servicecommerce.com/email-templates?page=1&size=2"
        }

### Email Template Collections [/emails-templates/{id}]

+ Parameter
	+ id (required, string) ... The id of the email template.

#### Retrieve an Email Template [GET]
Get a specific email template.

+ Response 200 (application/json)

		{
			"self":{
				"href":"http://api.servicecommerce.com/email-templates/123456",
				"id":"123456"
			},
			"name":"source.action.en_US",
			"providerName":"mandrill",
			"providerIndex":"123456789",
			"listOfVariables":["var1","var2","var3","var4"],
			"subject":"welcome123",
			"fromAddress":"from@example.com",
			"fromName":"from name",
			"createTime":"2014-02-01T02:00:03.123Z",
			"modifiedTime":"2014-02-01T02:00:03.123Z"
		}

#### Create Email Template [POST]
Create a new email template.

+ Request (application/json)

        {
        	"name":"source.action.en_US",
		    "providerName":"mandrill",
		    "providerIndex":"123456789",
			"listOfVariables":["var1","var2","var3","var4"],
			"subject":"welcome123",
			"fromAddress":"from@example.com",
			"fromName":"from name"
		}

+ Response 200 (application/json)

		{
			"self":{
				"href":"http://api.servicecommerce.com/email-templates/123456",
				"id":"123456"
			},
			"name":"source.action.en_US",
			"providerName":"mandrill",
		    "providerIndex":"123456789",
			"listOfVariables":["var1","var2","var3","var4"],
			"subject":"welcome123",
			"fromAddress":"from@example.com",
			"fromName":"from name",
			"createTime":"2014-02-01T02:00:03.123Z",
			"modifiedTime":"2014-02-01T02:00:03.123Z"
		}

#### Update Email Template [PUT]
Update an existing templates. Only the attribute include the hash will be update.

+ Request (application/json)

		{
			"self":{
				"href":"http://api.servicecommerce.com/email-templates/123456",
				"id":"123456"
			},
			"name":"source.action.en_US",
			"providerName":"mandrill",
			"providerIndex":"123456789",
			"listOfVariables":["var1","var2","var3","var4"],
			"subject":"welcome",
			"fromAddress":"from@example.com",
			"fromName":"from name"
		}


+ Response 200 (application/json)

		{
			"self":{
				"href":"http://api.servicecommerce.com/email-templates/123456",
				"id":"123456"
			},
			"name":"source.action.en_US",
			"providerName":"mandrill",
			"providerIndex":"1234567890",
			"listOfVariables":["var1","var2","var3","var4"],
			"subject":"welcome",
			"fromAddress":"from@example.com",
			"fromName":"from name",
			"createTime":"2014-02-01T02:00:03.123Z",
			"modifiedTime":"2014-02-01T02:00:03.123Z"
		}

#### Delete Email Template [DELETE]
Delete an existing email template.

+ Response 204
