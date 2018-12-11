from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_marshmallow import Marshmallow
from flasgger import APISpec, Swagger
from apispec.ext.flask import FlaskPlugin
from apispec.ext.marshmallow import MarshmallowPlugin

from instance.config import app_config

db = SQLAlchemy()
ma = Marshmallow()
swag = Swagger(config={
    "headers": [],
    "specs": [
        {
            "endpoint": "specification",
            "route": "/specification.json",
            "rule_filter": lambda rule: True,
            "model_filter": lambda tag: True
        }
    ],
    "static_url_path": "/flasgger_static",
    "specs_route": "/api/ui/"
})
spec = APISpec(
    title="Tauben2 API Documentation",
    version="1.0.0",
    openapi_version="2.0",
    plugins=[FlaskPlugin(), MarshmallowPlugin()]
)

def create_app(config_name):
    app = Flask(__name__, instance_relative_config=True)
    app.config.from_object(app_config[config_name])
    app.config.from_pyfile("config.py")
    db.init_app(app)
    ma.init_app(app)
    swag.init_app(app)

    from api.routes import test
    app.register_blueprint(test.bp)

    from api.routes import case
    app.register_blueprint(case.bp)

    from api.routes import population
    app.register_blueprint(population.bp)

    from api.routes import user
    app.register_blueprint(user.bp)

    from api.routes import auth
    app.register_blueprint(auth.bp)

    from api.routes import stats
    app.register_blueprint(stats.bp)

    swag.template = spec.to_flasgger(app)

    return app
