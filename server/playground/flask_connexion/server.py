from flask import send_file
import connexion

# create the application instance
app = connexion.App(__name__, specification_dir='./swagger/')
app.add_api('swagger_config.yml')

# create a URL route in our application for "/"
@app.route('/')

def index():
    return send_file('./static/index.html')

# if we're running in stand alone mode, run the application
if __name__ == '__main__':
    app.run(host='localhost', port=5000, debug=True)