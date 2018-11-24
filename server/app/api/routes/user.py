from flask import (Blueprint, request)

from api.models.user import (User, user_schema, users_schema)

bp = Blueprint("user", __name__, url_prefix="/api")

@bp.route("/user", methods=["GET"], strict_slashes=False)
def read_users():
    if request.method == "GET":
        users = User.all()
        return users_schema.jsonify(users)

@bp.route("/user", methods=["POST"], strict_slashes=False)
def create_user():
    if request.method == "POST":
        username = request.json["username"]
        phone = request.json["phone"]
        isAdmin = request.json["isAdmin"]

        user = User(username=username, phone=phone, isAdmin=isAdmin)
        user.save()
        
        return user_schema.jsonify(user), 201

@bp.route("/user/<username>", methods=["GET"], strict_slashes=False)
def read_user(username):
    if request.method == "GET":
        user = User.get(username)
        return user_schema.jsonify(user)