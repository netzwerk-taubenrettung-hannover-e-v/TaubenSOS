from flask import Flask
from flask_sqlalchemy import SQLAlchemy

from instance.config import app_config

db = SQLAlchemy()

def create_app(config_name):
    from api.models.test import Test
    app = Flask(__name__, instance_relative_config=True)
    app.config.from_object(app_config[config_name])
    app.config.from_pyfile("config.py")
    with app.app_context():
        db.init_app(app)
        result = Test.get_all()
        print(result)
    return app