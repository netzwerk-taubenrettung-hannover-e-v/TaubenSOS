import os
import jwt
import uuid
from datetime import datetime
from flask import (Blueprint, request)
from flask import jsonify, abort
from api.models.user import (User)
from api.models.token import (Token)

bp = Blueprint("auth", __name__, url_prefix="/api/auth")

@bp.route("/login", methods=["POST"], strict_slashes=False)
def login():
	if "username" not in request.json or "password" not in request.json:
		return abort(400)

	username = request.json["username"]
	password = request.json["password"]

	if None in [username, password]:
		return abort(400)

	user = User.get(username)

	if user is None or password != user.password:
		return abort(401)

	access_token = generate_access_token(user)
	return jsonify({"token": access_token})

@bp.route('/logout', methods=["DELETE"], strict_slashes=False)
def logout():
	access_token = request.headers.get("Authorization")
	if access_token is None:
		return abort(401)
	token_data = decode_access_token(access_token)
	dbToken = Token.get(token_data['jit'])
	if dbToken is None:
		return abort(401)
	dbToken.delete()
	return jsonify(success=True), 200

def generate_access_token(user):
	with open(os.path.join(os.path.dirname(__file__), "../keys/private.pem"), 'rb') as f:
		private_key = f.read()
		
	tokenID = uuid.uuid4().hex

	payload = {
		"username": user.username,
		"iat": datetime.utcnow(),
		"jit": tokenID
	}

	dbToken = Token(tokenID, user.username)
	dbToken.save()

	access_token = jwt.encode(payload, private_key, algorithm='RS256')
	return access_token.decode()

def only(scope):
	def onlyAdmin(func):
		def func_wrapper(**kwargs):
			access_token = request.headers.get("Authorization")
			if access_token is None:
				return abort(401)
			token_data = decode_access_token(access_token)
			dbToken = Token.get(token_data['jit'])

			if dbToken is None:
				return abort(401)
			values = list(kwargs.values())

			if not kwargs:
				f = func()
			else:
				f = func(values[0])

			if 'member' in scope:
				return f

			if 'admin' in scope:
				user = User.get(token_data['username'])
				if user.isAdmin:
					return f

			if 'me' in scope:
				if "username" not in kwargs:
					return abort(401)

				if kwargs['username'] == token_data['username']:
					return f

			return abort(401)
		return func_wrapper
	return onlyAdmin

def decode_access_token(access_token):
	with open(os.path.join(os.path.dirname(__file__), "../keys/public.pem"), 'rb') as f:
		public_key = f.read()

	try:
		decoded_token = jwt.decode(access_token.encode(), public_key, algorithm='RS256')
	except:
		return abort(401)
	return decoded_token