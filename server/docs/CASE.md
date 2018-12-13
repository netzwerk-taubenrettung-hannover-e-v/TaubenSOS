# API Documentation for the Case Routes 🛣

## Create Case

Creates a new case.

* **URL**  
    /case

* **Method**  
    `POST`

* **URL Params**  
    None

* **Data Params**

    Name | Description | Optional | Type | Default | Example
    --- | --- | :---: | --- | :---: | ---:
    timestamp | A UNIX timestamp. | YES | String | Current time. | "1543397014"
    priority | An integer between 1 and 5 representing the case's severity. | NO | Integer | — | 5
    reporter | The pigeon reporter's username. | YES | String | null | "Pigeonator"
    rescuer | The pigeon rescuer's username. | YES | String | null | "Taubenfreund24"
    ~~isCarrierPigeon~~ | ~~Is the bird a carrier pigeon?~~ | ~~NO~~ | ~~Boolean~~ | — | ~~true~~
    ~~isWeddingPigeon~~ | ~~Is the bird a wedding pigeon?~~ | ~~NO~~ | ~~Boolean~~ | — | ~~false~~
    breed | The pigeon's breed. May be one of the following options: Carrier Pigeon, Fancy Pigeon, Feral Pigeon, Common Wood Pigeon | YES | String | — | "Common Wood Pigeon"
    additionalInfo | Additional info on the pigeon's condition/location. | YES | String | null | "Taube liegt unter der Brücke."
    phone | The rescuer's mobile number. | NO | String | — | "015237342956"
    latitude | The latitude of the pigeon's location. | NO | Float | — | 52.3744
    longitude | The longitude of the pigeon's location. | NO | Float | — | 9.73886
    wasFoundDead | Was the pigeon found dead? | YES | Boolean | null | false
    wasNotFound | True if the pigeon couldn't be found at the specified location. | YES | Boolean | null | false
    isClosed | Is the case closed? | YES | Boolean | false | true
    injury | The pigeon's injuries. | NO | JSON Object | — | See table below.
    media | An array of the names of the files to be uploaded. | YES | JSON Array of Strings | [] | ["photo1.png", "photo2.png"]

    An 'injury' object has the following fields:

    Name | Description | Optional | Type | Default | Example
    --- | --- | :---: | --- | :---: | ---:
    footOrLeg | Is the pigeon's foot or leg injured? | NO | Boolean | — | true
    strappedFeet | Are the pigeon's feet strapped? | NO | Boolean | — | true
    wing | Is the pigeon's wing injured? | NO | Boolean | — | false
    headOrEye | Is the pigeon's head or eye injured? | NO | Boolean | — | false
    openWound | Has the pigeon an open wound? | NO | Boolean | — | false
    paralyzedOrFlightless | Is the pigeon paralyzed or flightless? | NO | Boolean | — | false
    fledgling | Is the pigeon a fledgling? | NO | Boolean | — | false
    other | Has the pigeon other injuries? | NO | Boolean | — | false

* **Success Response**
  * In case that the names of the files to be uploaded were specified, the response's 'media' field contains an array of pre-signed URLs for uploading to AWS S3. Note that the HTTP `PUT` method has to be used for the file upload, e.g.  
  `curl --request PUT --upload-file path/to/file "https://tauben2.s3.amazonaws.com/photos/..."`.
  * Code: `201 CREATED`
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
        "isCarrierPigeon": true,
        "isClosed": false,
        "isWeddingPigeon": false,
        "latitude": 52.3744,
        "longitude": 9.73886,
        "media": [],
        "phone": "015237342956",
        "priority": 5,
        "reporter": null,
        "rescuer": null,
        "timestamp": "1543397014",
        "wasFoundDead": null
    }
    ```

* **Error Response**
  * Code: `400 BAD REQUEST`
    ```json
    {
        "priority": "Must be between 1 and 5."
    }
    ```