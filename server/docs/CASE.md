# API Documentation for the Case Routes 🛣

## Create Case

Creates a new case.

* ### URL
    /case

* ### Method
    `POST`

* ### URL Params
    None

* ### Data Params
    Name | Description | Optional | Type | Example
    --- | --- | :---: | --- | ---:
    timestamp | A UNIX timestamp. | YES | String | "1543397014"
    priority | An integer (1-5) representing the case's severity. | NO | Integer | 5
    rescuer | The pigeon rescuer's username. | YES | String | "Taubenretter"
    isCarrierPigeon | Is the bird a carrier pigeon? | NO | Boolean | true
    isWeddingPigeon | Is the bird a wedding pigeon? | NO | Boolean | false
    additionalInfo | Additional information about the pigeon's condition/location. | YES | String | "Taube liegt unter der Brücke."
    phone | The rescuer's mobile number. | NO | String | "015237342956"
    latitude | The latitude of the pigeon's location. | NO | Float | 52.3744
    longitude | The longitude of the pigeon's location. | NO | Float | 9.73886
    wasFoundDead | Was the pigeon found dead? | YES | Boolean | false
    isClosed | Is the case closed? | YES | Boolean | true
    injury | The pigeon's injuries. | NO | JSON Object | `{"fledgling": false, "footOrLeg": true, "headOrEye": false, "openWound": false, "other": false, "paralyzedOrFlightless": false, "wing": false}`

* ### Success Response
  * Code: `201 CREATED`  
  Content:
    ```json
    {
        "additionalInfo": "Taube liegt unter der Brücke.",
        "caseID": 20180001,
        "injury": {
            "fledgling": false,
            "footOrLeg": true,
            "headOrEye": false,
            "openWound": false,
            "other": false,
            "paralyzedOrFlightless": false,
            "wing": false
        },
        "isCarrierPigeon": false,
        "isClosed": false,
        "isWeddingPigeon": true,
        "latitude": 52.3744,
        "longitude": 9.73886,
        "media": [],
        "phone": "015237342956",
        "priority": 5,
        "rescuer": null,
        "timestamp": "1543397014",
        "wasFoundDead": null
    }
    ```

* ### Error Response
  * Code: `400 BAD REQUEST`  
  Content (example):
    ```json
    {
        "isCarrierPigeon": "Not a valid boolean."
    }
    ```