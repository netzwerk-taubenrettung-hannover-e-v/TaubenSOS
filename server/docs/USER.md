# API Documentation for the User Routes 🛣

## Registrate

A new user can registrate

* **URL**  
    /user

* **Method**  
    `POST`

* **URL Params**  
    None

* **Data Params**  
    Name | Description | Optional | Type | Example
    --- | --- | :---: | --- | ---:
    username | Name of the user (must be unique). | NO | String | "Pigeonator"
    password | password of the user(will be stored as a hash). | NO | String | "securepassword"
    phone | The phone number of the user | NO | String | "0123456789"

* **Success Response**  
  * Code: `201 CREATED`
    ```json
    {
        "isActivated": false,
        "isAdmin": false,
        "password": "25d90f5038746e3695496cb08a52f9d7e03f0792e2a7f208774947a341d9d998",
        "phone": "99999999",
        "username": "PigeonMasterRace"
    }
    ```

* **Error Response**  
  * Code: `400 BAD REQUEST`
    ```json
    {
        
    }
    ```

## Get User List

Returns a list of all registrated users

* **URL**  
    /user

* **Method**  
    `GET`

* **URL Params**  
    None

* **Data Params**  
    Name | Description | Optional | Type | Example
    --- | --- | :---: | --- | ---:

* **Success Response**  
  * Code: `200 OK`
    ```json
    {
        "isActivated": false,
        "isAdmin": false,
        "password": "25d90f5038746e3695496cb08a52f9d7e03f0792e2a7f208774947a341d9d998",
        "phone": "99999999",
        "username": "PigeonMasterRace"
    }
    ```

* **Error Response**  
  * Code: `400 BAD REQUEST`
    ```json
    {
        
    }
    ```

## Activate or change admin status of user

Activate a user or make user an admin or revoke admin status. Only admins should be able to do this.

* **URL**  
    /user/:username

* **Method**  
    `PUT`

* **URL Params**  
    username=[String]

* **Data Params**  
    Name | Description | Optional | Type | Example
    --- | --- | :---: | --- | ---:
    isActivated | Is false until the user is activated by an Admin | NO | Boolean | true
    isAdmin | Is false until the user gets admin permissions by an admin | NO | Boolean | false

* **Success Response**  
  * Code: `200 OK`
    ```json
    {
        "isActivated": true,
        "isAdmin": false,
        "password": "25d90f5038746e3695496cb08a52f9d7e03f0792e2a7f208774947a341d9d998",
        "phone": "99999999",
        "username": "PigeonMasterRace"
    }
    ```

* **Error Response**  
  * Code: `400 BAD REQUEST`
    ```json
    {
        
    }
    ```

## Get user

get user

* **URL**  
    /user/:username

* **Method**  
    `GET`

* **URL Params**  
    username=[String]

* **Data Params**  
    Name | Description | Optional | Type | Example
    --- | --- | :---: | --- | ---:

* **Success Response**  
  * Code: `200 OK`
    ```json
    {
        "isActivated": true,
        "isAdmin": false,
        "password": "25d90f5038746e3695496cb08a52f9d7e03f0792e2a7f208774947a341d9d998",
        "phone": "99999999",
        "username": "PigeonMasterRace"
    }
    ```

* **Error Response**  
  * Code: `400 BAD REQUEST`
    ```json
    {
        
    }
    ```

## delete user

delete user. Only admins should be able to do this.

* **URL**  
    /user/:username

* **Method**  
    `DELETE`

* **URL Params**  
    username=[String]

* **Data Params**  
    Name | Description | Optional | Type | Example
    --- | --- | :---: | --- | ---:

* **Success Response**  
  * Code: `200 OK`

* **Error Response**  
  * Code: `400 BAD REQUEST`
    ```json
    {
        
    }
    ```