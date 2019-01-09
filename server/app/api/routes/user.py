from flask import (Blueprint, request)

from api.models.user import (User, user_schema, users_schema)
import hashlib

bp = Blueprint("user", __name__, url_prefix="/api")

@bp.route("/user", methods=["GET"], strict_slashes=False)
def read_users():
    if request.method == "GET":
        users = User.all()
        return users_schema.jsonify(users)

@bp.route("/user", methods=["POST"], strict_slashes=False)
def sign_up():
	if request.method == "POST":
		username = request.json["username"]
		password = request.json["password"]
		phone = request.json["phone"]

		passwordHashed = hashlib.sha256(password.encode('utf-8')).hexdigest()

		user = User(username=username, password=passwordHashed, phone=phone, isAdmin=False, isActivated=False)
		user.save()

		return user_schema.jsonify(user), 201

@bp.route("/user/<username>", methods=["GET", "PUT", "DELETE"], strict_slashes=False)
def read_or_update_user(username):
	if request.method == "GET":
		user = User.get(username)
		return user_schema.jsonify(user)
	if request.method == "PUT":
		user = User.get(username)
		user.isAdmin = request.json["isAdmin"]
		user.isActivated = request.json["isActivated"]
		user.save()
		return user_schema.jsonify(user), 200
	if request.method == "DELETE":
		user = User.get(username)
		user.delete()
		return 200
