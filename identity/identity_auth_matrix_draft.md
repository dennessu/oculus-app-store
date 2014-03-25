Users Resource claim list
-------------------------
- create_user 
        displayName, nickName, name, preferredLanguage, timezone, birthday, gender, locale, type
- get_user 
        id, displayName, nickName, name, preferredLanguage, timezone, birthday, gender, birthday, locale, type, active, type
- get_user_meta
        created_time, updated_time, created_time, created_by
- update_user_basic 
        displayName, nickName, name, preferredLanguage, timezone, birthday, gender
- update_user_restric
        locale, active, type
- user_search
        id, displayName, nickName, name, preferredLanguage, timezone, birthday, gender, birthday, locale, type, active, type

Users Resource scope list
------------------------
- users
- users.readonly
- users.search
- internal.service

Users Resource precondition list
--------------------------------
- owner_logged_in
- csr_logged_in
- any
    	
POST /users - create a new user
----------------------------------
- scope: internal.service
    	precondition: any
    	claims: 
    		* create_user
    		* get_user
			
PUT /users/{userId} - put a user
----------------------------------
- scope: users
		precondition: owner_logged_in
		claims: 
			* update_user_basic
			* get_user
		precondition: csr_logged_in
		claims:
			* update_user_basic
			* update_user_restric
			* get_user
			* get_user_meta

- scope: internal.service
		precondition: any
		claims:
			* update_user_basic
			* update_user_restric
			* get_user
			* get_user_meta
			
PATCH /users/{userId} - patch a user
----------------------------------
- scope: users
		precondition: owner_logged_in
		claims: 
			* update_user_basic
			* get_user
		precondition: csr_logged_in
		claims:
			* update_user_basic
			* update_user_restric
			* get_user
			* get_user_meta

- scope: internal.service
		precondition: any
		claims:
			* update_user_basic
			* update_user_restric
			* get_user
			* get_user_meta
			
GET /users/{userId} - get a user
--------------------------------
- scope: users
		precondition: owner_logged_in
		claims:
			* get_user
		precondition: csr_logged_in
		claims:
			* get_user
			* get_user_meta
		
- scope: users.readonly
		precondition: owner_logged_in
		claims:
			* get_user
		precondition: csr_logged_in
		claims:
			* get_user
			* get_user_meta

- scope: internal.service
		precondition: any
		claims:
			* get_user
			* get_user_meta
			
GET /users - get or search users by name
--------------------------------
- scope: users.search
		precondition: csr_logged_in
			* user_search
			
- scope: internal.service
		precondition: any
			* user_search

			
User Email Resource claim list
-------------------------
- create_user_email 
        value, type, primary
- get_user_email 
        id, value, type, primary, verified
- get_user_email_meta
        id, created_time, updated_time, created_time, created_by
- update_user_email_basic 
        value, type, primary
- update_user_email_restric
        verified
- delete_user_email
		
User Email Resource scope list
------------------------
- users.pii
- users.pii.readonly
- users.pii.search
- internal.service

User Email Resource precondition list
--------------------------------
- owner_logged_in
- csr_logged_in
- any
		
POST /users/{userId}/emails - create a new user email
----------------------------------
- scope: users.pii
    	precondition: owner_logged_in
    	claims: 
    		* create_user_email
    		* get_user_email
			
			
PUT /users/{userId}/emails/{userEmailId} - put a user email
----------------------------------
- scope: users.pii
		precondition: owner_logged_in
		claims: 
			* update_user_email_basic
			* get_user_email
		precondition: csr_logged_in
		claims:
			* update_user_email_basic
			* update_user_email_restric
			* get_user_email
			* get_user_email_meta

PATCH /users/{userId}/emails/{userEmailId} - patch a user email
----------------------------------
- scope: users.pii
		precondition: owner_logged_in
		claims: 
			* update_user_email_basic
			* get_user_email
		precondition: csr_logged_in
		claims:
			* update_user_email_basic
			* update_user_email_restric
			* get_user_email
			* get_user_email_meta
			
DELETE /users/{userId}/emails/{userEmailId} - delete a user email
------------------------------------------------------------
- scope: users.pii
		precondition: owner_logged_in
		claims: 
			* delete_user_email
		precondition: csr_logged_in
		claims:
			* delete_user_email
						
GET /users/{userId}/emails/{userEmailId} - get a user email
--------------------------------
- scope: users.pii
		precondition: owner_logged_in
		claims:
			* get_user_email
		precondition: csr_logged_in
		claims:
			* get_user_email
			* get_user_email_meta
		
- scope: users.pii.readonly
		precondition: owner_logged_in
		claims:
			* get_user_email
		precondition: csr_logged_in
		claims:
			* get_user_email
			* get_user_email_meta

- scope: internal.service
		precondition: any
		claims:
			* get_user_email
			* get_user_email_meta
				
GET /users/{userId}/emails - get or search email list for a user
--------------------------------
- scope: users.pii
		precondition: owner_logged_in
		claims:
			* get_user_email
		precondition: csr_logged_in
		claims:
			* get_user_email
			* get_user_email_meta
		
- scope: users.pii.readonly
		precondition: owner_logged_in
		claims:
			* get_user_email
		precondition: csr_logged_in
		claims:
			* get_user_email
			* get_user_email_meta

- scope: internal.service
		precondition: any
		claims:
			* get_user_email
			* get_user_email_meta
			
GET /users/emails - get user email by email
--------------------------------
- scope: users.pii.search
		precondition: csr_logged_in
		claims:
			* get_user_email
			* get_user_email_meta	

- scope: internal.service
		precondition: any
		claims:
			* get_user_email
			* get_user_email_meta	
			
User Phone Number Resource claim list
-------------------------
- create_user_phone 
        value, type, primary
- get_user_phone
        id, value, type, primary, verified
- get_user_phone_meta
        id, created_time, updated_time, created_time, created_by
- update_user_phone_basic 
        value, type, primary
- update_user_phone_restric
        verified
- delete_user_phone
		
User Phone Number Resource scope list
------------------------
- users.pii
- users.pii.readonly
- users.pii.search
- internal.service

User Phone Number Resource precondition list
--------------------------------
- owner_logged_in
- csr_logged_in
- any
		
POST /users/{userId}/phone-numbers - create a new user phone number
----------------------------------
- scope: users.pii
    	precondition: owner_logged_in
    	claims: 
    		* create_user_phone
    		* get_user_phone
		precondition: csr_logged_in
    	claims: 
    		* create_user_phone
    		* get_user_phone
			
PUT /users/{userId}/phone-numbers/{userPhoneNumberId} - put a user phone number
----------------------------------
- scope: users.pii
		precondition: owner_logged_in
		claims: 
			* update_user_phone_basic
			* get_user_phone
		precondition: csr_logged_in
		claims:
			* update_user_phone_basic
			* update_user_phone_restric
			* get_user_phone
			* get_user_phone_meta
			
PATCH /users/{userId}/phone-numbers/{userPhoneNumberId} - patch a user phone number
----------------------------------
- scope: users.pii
		precondition: owner_logged_in
		claims: 
			* update_user_phone_basic
			* get_user_phone
		precondition: csr_logged_in
		claims:
			* update_user_phone_basic
			* update_user_phone_restric
			* get_user_phone
			* get_user_phone_meta
						
DELETE /users/{userId}/phone-numbers/{userPhoneNumberId} - delete a user phone number
------------------------------------------------------------
- scope: users.pii
		precondition: owner_logged_in
		claims: 
			* delete_user_phone
		precondition: csr_logged_in
		claims:
			* delete_user_phone

GET /users/{userId}/phone-numbers/{userPhoneNumberId} - get a user phone number
--------------------------------
- scope: users.pii
		precondition: owner_logged_in
		claims:
			* get_user_phone
		precondition: csr_logged_in
		claims:
			* get_user_phone
			* get_user_phone_meta
		
- scope: users.pii.readonly
		precondition: owner_logged_in
		claims:
			* get_user_phone
		precondition: csr_logged_in
		claims:
			* get_user_phone
			* get_user_phone_meta

- scope: internal.service
		precondition: any
		claims:
			* get_user_phone
			* get_user_phone_meta
			
GET /users/{userId}/phone-numbers - get or search phone number list for a user
--------------------------------
- scope: users.pii
		precondition: owner_logged_in
		claims:
			* get_user_phone
		precondition: csr_logged_in
		claims:
			* get_user_phone
			* get_user_phone_meta
		
- scope: users.pii.readonly
		precondition: owner_logged_in
		claims:
			* get_user_phone
		precondition: csr_logged_in
		claims:
			* get_user_phone
			* get_user_phone_meta

- scope: internal.service
		precondition: any
		claims:
			* get_user_phone
			* get_user_phone_meta			
			
User Optin Resource claim list
-------------------------
- create_user_optin 
        value, type, primary
- get_user_optin 
        id, type
- get_user_optin_meta
        id, created_time, updated_time, created_time, created_by
- update_user_optin 
        type
- delete_user_optin

User Optin Resource scope list
------------------------
- users
- users.readonly
- internal.service

User Optin Resource precondition list
--------------------------------
- owner_logged_in
- csr_logged_in
- any
		
POST /users/{userId}/optins - create a new user optin
----------------------------------
- scope: users
    	precondition: owner_logged_in
    	claims: 
    		* create_user_optin
    		* get_user_optin
		precondition: csr_logged_in
    	claims: 
    		* create_user_optin
    		* get_user_optin
				
PUT /users/{userId}/optins/{userOptinId} - put a user optin
----------------------------------
- scope: users
		precondition: owner_logged_in
		claims: 
			* update_user_optin
			* get_user_optin
		precondition: csr_logged_in
		claims:
			* update_user_optin
			* get_user_optin
			* get_user_optin_meta
			
PATCH /users/{userId}/optins/{userOptinId} - patch a user optin
----------------------------------
- scope: users
		precondition: owner_logged_in
		claims: 
			* update_user_optin
			* get_user_optin
		precondition: csr_logged_in
		claims:
			* update_user_optin
			* get_user_optin
			* get_user_optin_meta

DELETE /users/{userId}/optins/{userOptinId} - delete a user optin
------------------------------------------------------------
- scope: users
		precondition: owner_logged_in
		claims: 
			* delete_user_optin
		precondition: csr_logged_in
		claims:
			* delete_user_optin
			
GET /users/{userId}/optins/{userOptinId} - get a user optin
--------------------------------
- scope: users
		precondition: owner_logged_in
		claims:
			* get_user_optin
		precondition: csr_logged_in
		claims:
			* get_user_optin
			* get_user_optin_meta
		
- scope: users.readonly
		precondition: owner_logged_in
		claims:
			* get_user_optin
		precondition: csr_logged_in
		claims:
			* get_user_optin
			* get_user_optin_meta
			
- scope: internal.service			
		precondition: any
		claims:
			* get_user_optin
			* get_user_optin_meta		
			
GET /users/{userId}/optins - get or search optin list for a user
--------------------------------
- scope: users
		precondition: owner_logged_in
		claims:
			* get_user_optin
		precondition: csr_logged_in
		claims:
			* get_user_optin
			* get_user_optin_meta
		
- scope: users.readonly
		precondition: owner_logged_in
		claims:
			* get_user_optin
		precondition: csr_logged_in
		claims:
			* get_user_optin
			* get_user_optin_meta		

- scope: internal.service
		precondition: any
		claims:
			* get_user_optin
			* get_user_optin_meta
			
User Tos Resource claim list
-------------------------
- create_user_tos
        tosUri
- get_user_tos 
        id, tosUri
- get_user_tos_meta
        id, created_time, updated_time, created_time, created_by
- update_user_tos 
        tosUri
- delete_user_tos

User Tos Resource scope list
------------------------
- users
- users.readonly
- internal.service

User Tos Resource precondition list
--------------------------------
- owner_logged_in
- csr_logged_in
- any
		
POST /users/{userId}/tos - create a new user tos acceptance
----------------------------------
- scope: users
    	precondition: owner_logged_in
    	claims: 
    		* create_user_tos
    		* get_user_tos
		precondition: csr_logged_in
    	claims: 
    		* create_user_tos
    		* get_user_tos
			
PUT /users/{userId}/tos/{userTosId} - put a user tos acceptance
----------------------------------
- scope: users
		precondition: owner_logged_in
		claims: 
			* update_user_tos
			* get_user_tos
		precondition: csr_logged_in
		claims:
			* update_user_tos
			* get_user_tos
			* get_user_tos_meta
			
PATCH /users/{userId}/tos/{userTosId} - patch a user tos acceptance
----------------------------------
- scope: users
		precondition: owner_logged_in
		claims: 
			* update_user_tos
			* get_user_tos
		precondition: csr_logged_in
		claims:
			* update_user_tos
			* get_user_tos
			* get_user_tos_meta

DELETE /users/{userId}/tos/{userTosId} - delete a user tos
------------------------------------------------------------
- scope: users
		precondition: owner_logged_in
		claims: 
			* delete_user_tos
		precondition: csr_logged_in
		claims:
			* delete_user_tos
			
GET /users/{userId}/tos/{userTosId} - get a user tos
--------------------------------
- scope: users
		precondition: owner_logged_in
		claims:
			* get_user_tos
		precondition: csr_logged_in
		claims:
			* get_user_tos
			* get_user_tos_meta
		
- scope: users.readonly
		precondition: owner_logged_in
		claims:
			* get_user_tos
		precondition: csr_logged_in
		claims:
			* get_user_tos
			* get_user_tos_meta

- scope: internal.service
		precondition: any
		claims:
			* get_user_tos
			* get_user_tos_meta
			
GET /users/{userId}/tos - get or search tos list for a user
--------------------------------
- scope: users
		precondition: owner_logged_in
		claims:
			* get_user_tos
		precondition: csr_logged_in
		claims:
			* get_user_tos
			* get_user_tos_meta
		
- scope: users.readonly
		precondition: owner_logged_in
		claims:
			* get_user_tos
		precondition: csr_logged_in
		claims:
			* get_user_tos
			* get_user_tos_meta			

- scope: internal.service
		precondition: any
		claims:
			* get_user_tos
			* get_user_tos_meta
			
User Device Resource claim list
-------------------------
- create_user_device
        deviceId, os, type, name
- get_user_device
        id, deviceId, os, type, name
- get_user_device_meta
        id, created_time, updated_time, created_time, created_by
- update_user_device 
        deviceId, os, type, name
- delete_user_device

User Device Resource scope list
------------------------
- users
- users.readonly
- internal.service

User Device Resource precondition list
--------------------------------
- owner_logged_in
- csr_logged_in
- any
		
POST /users/{userId}/devices - create a new user device
----------------------------------
- scope: users
    	precondition: owner_logged_in
    	claims: 
    		* create_user_device
    		* get_user_device
		precondition: csr_logged_in
    	claims: 
    		* create_user_device
    		* get_user_device

PUT /users/{userId}/devices/{userDeviceId} - put a user device
----------------------------------
- scope: users
		precondition: owner_logged_in
		claims: 
			* update_user_device
			* get_user_device
		precondition: csr_logged_in
		claims:
			* update_user_device
			* get_user_device
			* get_user_device_meta
			
PATCH /users/{userId}/devices/{userDeviceId} - patch a user device
----------------------------------
- scope: users
		precondition: owner_logged_in
		claims: 
			* update_user_device
			* get_user_device
		precondition: csr_logged_in
		claims:
			* update_user_device
			* get_user_device
			* get_user_device_meta
			
DELETE /users/{userId}/devices/{userDeviceId} - delete a user device
------------------------------------------------------------
- scope: users
		precondition: owner_logged_in
		claims: 
			* delete_user_device
		precondition: csr_logged_in
		claims:
			* delete_user_device
						
GET /users/{userId}/devices/{userDeviceId} - get a user device
--------------------------------
- scope: users
		precondition: owner_logged_in
		claims:
			* get_user_device
		precondition: csr_logged_in
		claims:
			* get_user_device
			* get_user_device_meta
		
- scope: users.readonly
		precondition: owner_logged_in
		claims:
			* get_user_device
		precondition: csr_logged_in
		claims:
			* get_user_device
			* get_user_device_meta

- scope: internal.service
		precondition: any
		claims:
			* get_user_device
			* get_user_device_meta
			
GET /users/{userId}/devices - get or search device list for a user
--------------------------------
- scope: users
		precondition: owner_logged_in
		claims:
			* get_user_device
		precondition: csr_logged_in
		claims:
			* get_user_device
			* get_user_device_meta
		
- scope: users.readonly
		precondition: owner_logged_in
		claims:
			* get_user_device
		precondition: csr_logged_in
		claims:
			* get_user_device
			* get_user_device_meta

- scope: internal.service
		precondition: any
		claims:
			* get_user_device
			* get_user_device_meta			
			
User Security Question Resource claim list
-------------------------
- create_user_secq
        security_question_id, answer
- get_user_secq 
        id, security_question_id, active
- get_user_secq_meta
        id, created_time, updated_time, created_time, created_by

User Security Question Resource scope list
------------------------
- users.security
- internal.service

User Security Question Resource precondition list
--------------------------------
- owner_logged_in
- csr_logged_in
- any
		
POST /users/{userId}/security-questions - create a new user security question
----------------------------------
- scope: users.security
    	precondition: owner_logged_in
    	claims: 
    		* create_user_secq
    		* get_user_secq
	
GET /users/{userId}/security-questions/{userSecurityQuestionId} - get a user security question
--------------------------------
- scope: users.security
		precondition: owner_logged_in
		claims:
			* get_user_secq
		precondition: csr_logged_in
		claims:
			* get_user_secq
			* get_user_secq_meta

- scope: internal.service
		precondition: any
		claims:
			* get_user_secq
			* get_user_secq_meta
			
GET /users/{userId}/security-questions - get or search security question list for a user
--------------------------------
- scope: users.security
		precondition: owner_logged_in
		claims:
			* get_user_secq
		precondition: csr_logged_in
		claims:
			* get_user_secq
			* get_user_secq_meta	

- scope: internal.service
		precondition: any
		claims:
			* get_user_secq
			* get_user_secq_meta
			
User Password Resource claim list
-------------------------
- create_user_pwd
        value
- create_user_tmp_pwd
		value, userId, changeAtNextLogin, expiresBy
- get_user_pwd
        id, active, expiresBy, changeAtNextLogin, strength
- get_user_pwd_meta
        id, created_time, updated_time, created_time, created_by

User Password Resource scope list
------------------------
- users.security
- internal.service

User Password Resource precondition list
--------------------------------
- owner_logged_in
- csr_logged_in
- any
		
POST /users/{userId}/passwords - create a new user password
----------------------------------
- scope: users.security
    	precondition: owner_logged_in
    	claims: 
    		* create_user_pwd
    		* get_user_pwd
		precondition: csr_logged_in
    	claims: 
    		* create_user_tmp_pwd
    		* get_user_pwd
			* get_user_pwd_meta
			
- scope: internal.service			
		precondition: any
    	claims: 
    		* create_user_tmp_pwd
    		* get_user_pwd
			* get_user_pwd_meta	
			
GET /users/{userId}/passwords/{userPasswordId} - get a user password
--------------------------------
- scope: users.security
		precondition: owner_logged_in
		claims:
			* get_user_pwd
		precondition: csr_logged_in
		claims:
			* get_user_pwd
			* get_user_pwd_meta

- scope: internal.service
		precondition: any
		claims:
			* get_user_pwd
			* get_user_pwd_meta
			
GET /users/{userId}/passwords - get or search password list for a user
--------------------------------
- scope: users.security
		precondition: owner_logged_in
		claims:
			* get_user_pwd
		precondition: csr_logged_in
		claims:
			* get_user_pwd
			* get_user_pwd_meta	

- scope: internal.service
		precondition: any
		claims:
			* get_user_pwd
			* get_user_pwd_meta
			
User Pin Resource claim list
-------------------------
- create_user_pin
        value
- create_user_tmp_pin
		value, userId, changeAtNextLogin, expiresBy
- get_user_pin
        id, active, expiresBy, changeAtNextLogin, strength
- get_user_pin_meta
        id, created_time, updated_time, created_time, created_by

User Pin Resource scope list
------------------------
- users.security
- internal.service

User Pin Resource precondition list
--------------------------------
- owner_logged_in
- csr_logged_in
- any
		
POST /users/{userId}/pins - create a new user pin
----------------------------------
- scope: users.security
    	precondition: owner_logged_in
    	claims: 
    		* create_user_pin
    		* get_user_pin
		precondition: csr_logged_in
    	claims: 
    		* create_user_tmp_pin
    		* get_user_pin
			* get_user_pin_meta

- scope: internal.service
		precondition: any
    	claims: 
    		* create_user_tmp_pin
    		* get_user_pin
			* get_user_pin_meta
			
			
GET /users/{userId}/pins/{userPinId} - get a user pin
--------------------------------
- scope: users.security
		precondition: owner_logged_in
		claims:
			* get_user_pin
		precondition: csr_logged_in
		claims:
			* get_user_pin
			* get_user_pin_meta

- scope: internal.service
		precondition: any
		claims:
			* get_user_pin
			* get_user_pin_meta
			
GET /users/{userId}/pins - get or search pin list for a user
--------------------------------
- scope: users.security
		precondition: owner_logged_in
		claims:
			* get_user_pin
		precondition: csr_logged_in
		claims:
			* get_user_pin
			* get_user_pin_meta				

- scope: internal.service
		precondition: any
		claims:
			* get_user_pin
			* get_user_pin_meta				
			
User Authenticator Resource claim list
-------------------------
- create_user_authenticator
		type, value, userId
- update_user_authenticator
		type, value, userId
- get_user_authenticator
        id, type, value
- get_user_authenticator_meta
        id, created_time, updated_time, created_time, created_by
- delete_user_authenticator
		
User Authenticator Resource scope list
------------------------
- users
- users.search
- internal.service

User Authenticator Resource precondition list
--------------------------------
- owner_logged_in
- csr_logged_in
- any

POST /users/{userId}/authenticators - create a new user authenticator for openId
- scope: internal.service
		precondition: any
		claims:
			* create_user_authenticator
			* get_user_authenticator
			
PUT /users/{userId}/authenticators/{userAuthenticatorId} - put a user authenticator for openId
- scope: internal.service
		precondition: any
		claims:
			* update_user_authenticator

PATCH /users/{userId}/authenticators/{userAuthenticatorId} - patch a user authenticator for openId			
- scope: internal.service
		precondition: any
		claims:
			* update_user_authenticator
			
GET /users/{userId}/authenticators/{userAuthenticatorId} - get a user authenticator
--------------------------------
- scope: users
		precondition: owner_logged_in
		claims:
			* get_user_authenticator
		precondition: csr_logged_in
		claims:
			* get_user_authenticator
			* get_user_authenticator_meta

- scope: users.readonly
		precondition: owner_logged_in
		claims:
			* get_user_authenticator
		precondition: csr_logged_in
		claims:
			* get_user_authenticator
			* get_user_authenticator_meta			
		
GET /users/{userId}/authenticators - get or search authenticator list for a user
--------------------------------
- scope: users
		precondition: owner_logged_in
		claims:
			* get_user_authenticator
		precondition: csr_logged_in
		claims:
			* get_user_authenticator
			* get_user_authenticator_meta

- scope: users.readonly
		precondition: owner_logged_in
		claims:
			* get_user_authenticator
		precondition: csr_logged_in
		claims:
			* get_user_authenticator
			* get_user_authenticator_meta			

GET /users/authenticators - find user authenticator by authenticator
--------------------------------
- scope: users.search
		precondition: csr_logged_in
		claims:
			* get_user_authenticator
			* get_user_authenticator_meta

- scope: internal.service
		precondition: any
		claims:
			* get_user_authenticator
			* get_user_authenticator_meta			
			
DELETE /users/{userId}/authenticators/{userAuthenticatorId}	- delete/unlink an authenticator for a user
- scope: users
		precondition: owner_logged_in
		claims:
			* delete_user_authenticator
		precondition: csr_logged_in
			* delete_user_authenticator
			
- scope: internal.service
		precondition: csr_logged_in
			* delete_user_authenticator		

Security Question Resource claim list
-------------------------
- create_sec_question
		type, active
- update_sec_question
		id, type, active
- get_sec_question
        id, type, active
- get_sec_question_meta
        id, created_time, updated_time, created_time, created_by
- delete_sec_question
		
Security Question Resource scope list
------------------------
- global.config
- any

Security Question Resource precondition list
--------------------------------
- any

POST /security-questions - create a new definition of security question
- scope: global.config
		precondition: any
		claims:
			* create_sec_question
			* get_sec_question
			
PUT /security-questions/{securityQuestionId} - put a definition of security question
- scope: global.config
		precondition: any
		claims:
			* update_sec_question

PATCH /security-questions/{securityQuestionId} - patch a definition of security question
- scope: global.config
		precondition: any
		claims:
			* update_sec_question
			
GET /security-questions/{securityQuestionId} - get a definition of security question
--------------------------------			
- scope: any
		precondition: any
		claims:
			* get_sec_questions		
			
GET /security-questions - get list of security question
--------------------------------
- scope: any
		precondition: any
		claims:
			* get_sec_questions

- scope: global.config
		precondition: any
			* get_sec_question
			* get_sec_question_meta

User Login Attempts Resource claim list
-------------------------
- create_login_attempt
		type, value, ip_address, user_agent, client_id, userId
- get_login_attempt
        id, type, ip_address, user_agent, client_id, userId, succeeded
		
User Login Attempts Resource scope list
------------------------
- internal.service

User Login Attempts Resource precondition list
--------------------------------
- any

POST /users/login-attempts - create a new login attempt
- scope: internal.service
		precondition: any
		claims:
			* create_login_attempt
			* get_login_attempt
			
GET /users/login-attempts/{userLoginAttemptId} - get a user login attempt
--------------------------------			
- scope: internal.service
		precondition: any
		claims:
			* get_login_attempt		
			
GET /users/login-attempts - get user login attempt history
--------------------------------
- scope: internal.service
		precondition: any
		claims:
			* get_login_attempt			
			
User Security Question Attempts Resource claim list
-------------------------
- create_sec_question_attempt
		type, value, ip_address, user_agent, client_id
- get_sec_question_attempt
        id, type, ip_address, user_agent, client_id, succeeded
		
User Security Question Attempts Resource scope list
------------------------
- internal.service

User Security Question Attempts Resource precondition list
--------------------------------
- any

POST /users/{userId}/security-questions-attempt - create a new security question attempt
- scope: internal.service
		precondition: any
		claims:
			* create_sec_question_attempt
			* get_sec_question_attempt
			
GET /users/{userId}/security-questions-attempt/{userSecurityQuestionAttemptId} - get a user security question attempt
--------------------------------			
- scope: internal.service
		precondition: any
		claims:
			* get_sec_question_attempt		
			
GET /users/{userId}/security-questions-attempt - get user security question attempt history
--------------------------------
- scope: internal.service
		precondition: any
		claims:
			* get_sec_question_attempt			