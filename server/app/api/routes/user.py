from flask import (Blueprint, request, jsonify)

from api.models.user import (User, user_schema, users_schema)

bp = Blueprint("user", __name__, url_prefix="/api")

@bp.route("/user", methods=["GET"], strict_slashes=False)
def read_all():
	"""
	file: ../../docs/user/read_all.yml
	"""
	if request.method == "GET":
		users = User.all()
		result = [make_json(User=u) for u in users]
		return jsonify(result)

@bp.route("/user", methods=["POST"], strict_slashes=False)
def create_user():
	"""
	file: ../../docs/user/create_user.yml
	"""
	if request.method == "POST":
		json = request.get_json()
		user, errors = user_schema.load(json)
		if errors:
			return jsonify(errors), 400
		else:
			user.save()
			return jsonify(make_json(User=user)), 201

@bp.route("/user/<username>", methods=["DELETE"], strict_slashes=False)
def delete_user(username):
	"""
	file: ../../docs/user/delete_user.yml
	"""
	if request.method == "DELETE":
		user = User.get(username)
		if user is None:
			return jsonify({"message": "The user to be deleted could not be found"}), 404
		user.delete()
		return jsonify({"message": "The user has been deleted"}), 204

@bp.route("/user/<username>", methods=["PUT"], strict_slashes=False)
def update_user(username):
	"""
	file: ../../docs/user/update_user.yml
	"""
	if request.method == "PUT":
		json = request.get_json()
		user = User.get(username)
		if user is None:
			return jsonify({"message": "The user to be updated could not be found"}), 404
		errors = user_schema.validate(json, partial=True)
		if errors:
			return jsonify(errors), 400
		user.update(**json)
		return jsonify(make_json(User=user)), 200

@bp.route("user/<username>", methods=["GET"], strict_slashes=False)
def read_user(username):
	"""
	file: ../../docs/user/read_user.yml
	"""
	if request.method == "GET":
		user = User.get(username)
		if user is None:
			return jsonify({"message": "The user to be shown could not be found"}), 404
		result = make_json(User=user)
		return jsonify(result)

def make_json(User):
	result = user_schema.dump(User).data
	return result