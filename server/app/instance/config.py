import os

class Config(object):
    DEBUG = False
    CSRF_ENABLED = True
    SQLALCHEMY_TRACK_MODIFICATIONS = False

class DevelopmentConfig(Config):
    DEBUG = True
    SQLALCHEMY_DATABASE_URI = "postgres://viecqfjzopmqdw:f580bc210888d434f4e4a9020de436cc96e33973749374121973e3ac1f6a36e3@ec2-54-247-86-89.eu-west-1.compute.amazonaws.com:5432/d69altfl4mgemi"

class ProductionConfig(Config):
    SQLALCHEMY_DATABASE_URI = os.getenv("DATABASE_URL")

app_config = {
    "development": DevelopmentConfig,
    "production": ProductionConfig
}