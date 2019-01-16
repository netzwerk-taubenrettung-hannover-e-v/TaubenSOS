from flask import Blueprint, request, jsonify

from api.models.user import User, user_schema, users_schema
from . import fcm

bp = Blueprint("user", __name__, url_prefix="/api")

@bp.route("/user", methods=["GET"], strict_slashes=False)
def read_all():
	"""
	file: ../../docs/user/read_all.yml
	"""
	if request.method == "GET":
		users = User.all()
		return users_schema.jsonify(users), 200

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
		fcm.send_to_topic(
			"/topics/admin",
			"New Sign-up Request",
			user.username + " has requested to become part of the community! 🐣",
			"ic_person_add_black_24dp")
		user.save()
		return user_schema.jsonify(user), 201

@bp.route("/user/<username>", methods=["DELETE"], strict_slashes=False)
def delete_user(username):
	"""
	file: ../../docs/user/delete_user.yml
	"""
	if request.method == "DELETE":
		user = User.get(username)
		if user is None:
			return jsonify(message="The user to be deleted could not be found"), 404
		user.delete()
		return "", 204, {"Content-Type": "application/json"}

@bp.route("/user/<username>", methods=["PUT"], strict_slashes=False)
def update_user(username):
	"""
	file: ../../docs/user/update_user.yml
	"""
	if request.method == "PUT":
		json = request.get_json()
		user = User.get(username)
		if user is None:
			return jsonify(message="The user to be updated could not be found"), 404
		errors = user_schema.validate(json, partial=True)
		if errors:
			return jsonify(errors), 400
		"""
		if (user.registrationToken != json.get("registrationToken")):
			if user.isActivated:
				fcm.unsubscribe_from_topic("/topics/member", [user.registrationToken])
				fcm.subscribe_to_topic("/topics/member", [json.get("registrationToken")])
			if user.isAdmin:
				fcm.unsubscribe_from_topic("/topics/admin", [user.registrationToken])
				fcm.subscribe_to_topic("/topics/admin", [json.get("registrationToken")])
		"""
		if (user.registrationToken and json.get("isActivated") and not user.isActivated):
			fcm.subscribe_to_topic("/topics/member", [user.registrationToken])
			fcm.send_to_token(
				user.registrationToken,
				"Your Sign-up Request",
				"Good news, your sign-up request has been approved! 🎉",
				icon="ic_supervisor")
		elif (user.registrationToken and user.isActivated and not json.get("isActivated")):
			fcm.unsubscribe_from_topic("/topics/member", [user.registrationToken])
			fcm.send_to_token(
				user.registrationToken,
				"Your Membership Status",
				"Regrettably, your membership has been deprived. 🥺",
				icon="ic_supervisor")

		if (user.registrationToken and json.get("isAdmin") and not user.isAdmin):
			fcm.subscribe_to_topic("/topics/admin", [user.registrationToken])
			fcm.send_to_token(
				user.registrationToken,
				"Your Membership Status",
				"Good news, you have been promoted to admin! 🎉",
				icon="ic_supervisor")
		elif (user.registrationToken and user.isAdmin and not json.get("isAdmin")):
			fcm.unsubscribe_from_topic("/topics/admin", [user.registrationToken])
			fcm.send_to_token(
				user.registrationToken,
				"Your Membership Status",
				"Regrettably, you have been demoted to member. 🥺",
				icon="ic_supervisor")

		user.update(**json)
		return user_schema.jsonify(user), 200

@bp.route("user/<username>", methods=["GET"], strict_slashes=False)
def read_user(username):
	"""
	file: ../../docs/user/read_user.yml
	"""
	if request.method == "GET":
		user = User.get(username)
		if user is None:
			return jsonify(message="The user to be shown could not be found"), 404
		return user_schema.jsonify(user), 200