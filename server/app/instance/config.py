import os

class Config(object):
    DEBUG = False
    CSRF_ENABLED = True
    SQLALCHEMY_TRACK_MODIFICATIONS = False

class DevelopmentConfig(Config):
    DEBUG = True
    SQLALCHEMY_DATABASE_URI = os.getenv("AWS_RDS_DATABASE_URL")
    MAX_CONTENT_LENGTH = 500000000

class ProductionConfig(Config):
    SQLALCHEMY_DATABASE_URI = os.getenv("DATABASE_URL")

app_config = {
    "development": DevelopmentConfig,
    "production": ProductionConfig
}