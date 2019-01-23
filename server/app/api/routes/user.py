from flask import Blueprint, request, jsonify

from api.models.user import User, user_schema, users_schema
from . import fcm

bp = Blueprint("user", __name__, url_prefix="/api")

@bp.route("/user", methods=["GET"], strict_slashes=False)
def read_all():
	"""
	file: ../../docs/user/read_all.yml
	"""
	users = User.all()
	return users_schema.jsonify(users), 200

@bp.route("/user", methods=["POST"], strict_slashes=False)
def create_user():
	"""
	file: ../../docs/user/create_user.yml
	"""
	json = request.get_json()
	user, errors = user_schema.load(json)
	if errors:
		return jsonify(errors), 400
	fcm.send_to_topic(
		"/topics/admin",
		["push_new_sign_up_request_title"],
		["push_new_sign_up_request_body", user.username],
		"ic_person_add_black_24dp")
	user.save()
	return user_schema.jsonify(user), 201

@bp.route("/user/<username>", methods=["DELETE"], strict_slashes=False)
def delete_user(username):
	"""
	file: ../../docs/user/delete_user.yml
	"""
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
	json = request.get_json()
	user = User.get(username)
	if user is None:
		return jsonify(message="The user to be updated could not be found"), 404
	# ignore 'username' field if a change has not been requested
	if username == json.get("username"):
		json.pop("username", None)
	errors = user_schema.validate(json, partial=True)
	if errors:
		return jsonify(errors), 400

	if "registrationToken" in json and user.registrationToken != json.get("registrationToken"):
		if user.isActivated:
			if user.registrationToken:
				fcm.unsubscribe_from_topic("/topics/member", user.registrationToken)
			if json.get("registrationToken"):
				fcm.subscribe_to_topic("/topics/member", json.get("registrationToken"))
		if user.isAdmin:
			if user.registrationToken:
				fcm.unsubscribe_from_topic("/topics/admin", user.registrationToken)
			if json.get("registrationToken"):
				fcm.subscribe_to_topic("/topics/admin", json.get("registrationToken"))

	if user.registrationToken:
		if json.get("isActivated") and not user.isActivated:
			fcm.subscribe_to_topic("/topics/member", user.registrationToken)
			fcm.send_to_token(
				user.registrationToken,
				["push_your_sign_up_request_title"],
				["push_your_sign_up_request_body", username],
				icon="ic_supervisor")
		elif user.isActivated and not json.get("isActivated"):
			fcm.unsubscribe_from_topic("/topics/member", user.registrationToken)
			fcm.send_to_token(
				user.registrationToken,
				["push_your_membership_status_title"],
				["push_revoked_membership_body"],
				icon="ic_supervisor")

		if json.get("isAdmin") and not user.isAdmin:
			fcm.subscribe_to_topic("/topics/admin", user.registrationToken)
			fcm.send_to_token(
				user.registrationToken,
				["push_your_membership_status_title"],
				["push_promotion_body", username],
				icon="ic_supervisor")
		elif user.isAdmin and not json.get("isAdmin"):
			fcm.unsubscribe_from_topic("/topics/admin", user.registrationToken)
			fcm.send_to_token(
				user.registrationToken,
				["push_your_membership_status_title"],
				["push_demotion_body"],
				icon="ic_supervisor")

	user.update(**json)
	return user_schema.jsonify(user), 200

@bp.route("user/<username>", methods=["GET"], strict_slashes=False)
def read_user(username):
	"""
	file: ../../docs/user/read_user.yml
	"""
	user = User.get(username)
	if user is None:
		return jsonify(message="The user to be shown could not be found"), 404
	return user_schema.jsonify(user), 200