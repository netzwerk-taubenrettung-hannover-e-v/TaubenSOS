# Read me! 🤖

This folder contains resources pertaining to the development of the server's back-end, including the RESTful API. The playground folder serves prototyping purposes and already contains a little demo project, which utilizes [Zalando's Connexion framework](https://github.com/zalando/connexion) for building a RESTful web service. Connexion is a Swagger/OpenAPI First framework for Python that runs on top of Flask and provides automatic endpoint validation & OAuth 2.0 support. For an introductory tutorial, take a look at [this](https://realpython.com/flask-connexion-rest-api/). In order to understand OAuth 2.0, [Medium](https://medium.com/google-cloud/understanding-oauth2-and-building-a-basic-authorization-server-of-your-own-a-beginners-guide-cf7451a16f66) provides a brief and intelligible summary. The Resource Owner Password Credentials authorization flow is discussed [here](https://medium.com/@ratrosy/building-a-basic-authorization-server-using-resource-owner-password-credentials-flow-a666d06900fb). The flask_connexion project uses a Python virtual environment (venv), which may be set to work by following the step-by-step guide below.

## Setting up your virtual environment

1. Navigate to the flask_connexion folder
2. Create a virtual environment named 'env' by typing `python3 -m venv env`
3. Activate that environment by running `source env/bin/activate`
4. Install the packages required by the project with `pip install -r requirements.txt`
5. Deactivate the virtual environment by typing `deactivate`
6. Now you're set up to run the project: `env/bin/python server.py`

## Other helpful stuff

* [OpenAPI Specification ver. 2 (fka Swagger)](https://swagger.io/docs/specification/2-0/basic-structure/)
* For more information on Python virtual environments check [this](https://docs.python.org/3/library/venv.html) out