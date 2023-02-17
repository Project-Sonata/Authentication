# The file contains documentation about '/auth/signup' endpoint

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
  "confirmation_url": "/api/v1/auth/email/verify"
}
```
* confirmation_url - url to send the confirmation code to exchange the confirmation code to access token.

400 BAD REQUEST - if the request contains malformed email, invalid password, etc. 

Example of the JSON response
```json
{
  "status": "BAD_REQUEST",
  "status_code": "400",
  "error": {
    "code": "invalid_password",
    "description": "The password is invalid, password must contain at least 8 characters and 1 number",
    "possible_solution": "To fix the problem - input the correct password with required format"
  }
}
```
JSON fields description:  
* status - HTTP status name of the response code
* status_code - HTTP status code of the response
* error.code - unique identifier of the error
* error.description - description about error
* error.possible_solution - Possible way to fix the error 

500 SERVER ERROR - If the request processing failed because server cannot process it
