# The file contains the common information about Sonata Authentication

### Table of contents

* [Endpoints](#endpoints)

# Endpoints
* [/signup - to register the user](#signup-endpoint)
* [/login - to login existed user](#login-endpoint)


## Signup Endpoint

``
HTTP 1.1 curl -X POST -H  'application/json' /api/v1/auth/signup
`` used to authenticate the user. 

The json must contain the following fields:

* email - email provided by the user in registration form. The email must be valid
* password - password provided by the user in registration form. The password must contain at least 8 characters and one number.
* birthdate - date when the user was born in YYYY-MM-DD format
* gender - the end-user's gender. 
    * MALE 
    * FEMALE
    * NONE - if the user prefer not to say the gender
* notification_enabled - boolean value that represents if user wants to get notification from Sonata Project or not.

The example of valid JSON body:
```json
{
  "username": "odeyalo@gmail.com",
  "password": "mysupercoolpassword123",
  "birthdate": "2002-11-23",
  "gender": "MALE",
  "notification_enabled": "true"
}
```
Possible responses:
200 OK - if registration was success, the HTTP 200 OK will be returned with following JSON body:
```json
{
  "message": "To confirm your email we sent message to your email. Check it out",
  "confirmation_url": "/api/v1/auth/email/verify",
  "confirmation_url_http_methods": [
      "GET"
  ],
  "confirmation_url_parameters": "code"
}
```

* confirmation_url - url to send the confirmation code to exchange the confirmation code for access token.
* confirmation_url_http_methods - supported HTTP methods by the 'confirmation_url'
* confirmation_url_parameters - parameters 
