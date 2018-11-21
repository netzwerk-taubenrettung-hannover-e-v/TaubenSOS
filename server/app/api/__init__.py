from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_marshmallow import Marshmallow

from instance.config import app_config

db = SQLAlchemy()
ma = Marshmallow()

def create_app(config_name):
    app = Flask(__name__, instance_relative_config=True)
    app.config.from_object(app_config[config_name])
    app.config.from_pyfile("config.py")
    db.init_app(app)
    ma.init_app(app)

    from api.routes import test
    app.register_blueprint(test.bp)

    from api.routes import case
    app.register_blueprint(case.bp)

    from api.routes import population
    app.register_blueprint(population.bp)

    return app
