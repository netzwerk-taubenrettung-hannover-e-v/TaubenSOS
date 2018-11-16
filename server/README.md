# Read me! 🤖

This folder contains resources pertaining to the development of the server's back-end, including the RESTful API.

## The Flask/Connexion demo project

The playground folder serves prototyping purposes and already contains a little demo project, which utilizes [Zalando's Connexion framework](https://github.com/zalando/connexion) for building a RESTful web service. Connexion is a Swagger/OpenAPI First framework for Python that runs on top of Flask and provides automatic endpoint validation & OAuth 2.0 support. For an introductory tutorial, take a look at [this](https://realpython.com/flask-connexion-rest-api/). In order to understand OAuth 2.0, [Medium](https://medium.com/google-cloud/understanding-oauth2-and-building-a-basic-authorization-server-of-your-own-a-beginners-guide-cf7451a16f66) provides a brief and intelligible summary. The Resource Owner Password Credentials authorization flow is discussed [here](https://medium.com/@ratrosy/building-a-basic-authorization-server-using-resource-owner-password-credentials-flow-a666d06900fb). The flask_connexion project uses a Python virtual environment (venv), which may be set to work by following the step-by-step guide below.

## Utilizing plain 'vanilla' Flask w/o Connexion

The Connexion framework doesn't serve our purposes as it lacks in terms of OAuth 2.0 support. The only flow supported is the implicit grant type, which rather suits browser-based client-side web applications than native mobile apps. Being an in-house development, the Android application is a trusted first-party client for sure, which suggests using the resource owner password credentials flow. Due to safety concerns, it is appropriate to let an IDaaS-provider such as Auth0 take care of the authorization server. The [Auth0 API](https://auth0.com/docs/quickstart/backend/python) plays nicely with Python Flask, for a quick tutorial check [this](https://auth0.com/blog/developing-restful-apis-with-python-and-flask/) out. In order to understand how to use Flask with PostgreSQL, take a look at [this](https://scotch.io/tutorials/build-a-restful-api-with-flask-the-tdd-way). Utilizing SQLAlchemy in combination with Marshmallow is discussed [here](https://medium.com/python-pandemonium/build-simple-restful-api-with-python-and-flask-part-2-724ebf04d12) and [here](https://realpython.com/flask-connexion-rest-api-part-2/).

## Setting up your virtual environment

1. Navigate to the respective project folder
2. Create a virtual environment named 'env' by typing `python3 -m venv env`
3. Activate that environment by running `source env/bin/activate`
4. Install the packages required by the project with `pip install -r requirements.txt`
5. Deactivate the virtual environment by typing `deactivate`
6. Now you're set up to run the project: `env/bin/python server.py`

## Other helpful stuff

* [OpenAPI Specification ver. 2 (fka Swagger)](https://swagger.io/docs/specification/2-0/basic-structure/)
* For more information on Python virtual environments check [this](https://docs.python.org/3/library/venv.html) out
* For a nice and comprehensive tutorial on Flask, check [this](http://flask.pocoo.org/docs/1.0/tutorial/) out
* [Flask-Marshmallow docs](https://flask-marshmallow.readthedocs.io/en/latest/)
