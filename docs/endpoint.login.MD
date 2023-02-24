# The file contains documentation about '/auth/login' endpoint

``
HTTP 1.1 curl -X POST -H  'application/json' /api/v1/auth/login
`` used to authenticate the user. 

The endpoint consumes application/json content type and produces it.


## The json must contain the following fields:

* username - user's email
* password - user's password.

Example of valid request body:
```json
{
  "username": "odeyalo@gmail.com",
  "password": "mysupercoolpassword123"
}
```

## Possible responses:

200 OK - if the user's credentials are valid the response body will contain:

1) If MFA is enabled for the user then following JSON body will be returned:
```json
{
  "status": 200,
  "mfa_enabled": true,
  "message": "The MFA is enabled.",
  "_links": {
    "self": {
      "href": "https://sonataproject.com/api/v1/auth/signup"
    },
    "mfa_checker_url": {
      "href": "https://sonataproject.com/api/v1/auth/mfa/check"
    }
  }
}
```
Response Parameters

* `status` (integer, required): The HTTP status code of the response.
* `mfa_enabled` (boolean, required): A flag indicating whether MFA is enabled for the user.
* `message` (string, required): A message describing the status of MFA.
  * `_links` (object, required): An object containing the links to related resources.
    * `self` (object, required): An object containing the URL of the current resource.
    * `href` (string, required): The URL of the current resource.
    * `mfa_checker_url` (object, required): An object containing the URL of the MFA checker resource.
    * `href` (string, required): The URL of the MFA checker resource.



2) If MFA is disabled then this JSON will be returned:
```json
{
  "status": 200,
  "mfa_enabled": false,
  "message": "The MFA is disabled",
  "tokens": {
    "access_token":{
      "body": "user's token that can be used to access secured resources",
      "expires_in": 3600
    },
    "refresh_token": {
      "body": "refresh token that can be used to obtain a new access token",
      "expires_in": 10000
    }
  }
}
```
Response Fields:

* `status` (integer, required): The HTTP status code of the response.
* `mfa_enabled` (boolean, required): A flag indicating whether MFA is enabled for the user.
* `message` (string, required): A message describing the status of MFA.
* `tokens`(object, required): An object containing the user's access and refresh tokens.
    * `access_token` (object, required): An object containing the user's access token.
        * `body` (string, required): The user's access token.
        * `expires_in` (integer, required): The number of seconds until the access token expires.
    * `refresh_token` (object, required): An object containing the user's refresh token.
        * `body` (string, required): The user's refresh token.
        * `expires_in` (integer, required): The number of seconds until the refresh token expires.


