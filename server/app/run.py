import os

from api import create_app

config_name = "development"
application = create_app(config_name)

if __name__ == "__main__":
    application.run()