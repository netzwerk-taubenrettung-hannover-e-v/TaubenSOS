from api import db, ma, spec
from marshmallow import pre_load, post_load, utils, validate
import hashlib

class User(db.Model):
	__tablename__ = "user"
	username = db.Column(db.String(20), primary_key=True)
	password = db.Column(db.String(255), nullable=False)
	phone = db.Column(db.String(30))
	isAdmin = db.Column(db.Boolean)
	isActivated = db.Column(db.Boolean)
	registrationToken = db.Column(db.String(255))
	asReporter = db.relationship("Case", foreign_keys="Case.reporter")
	asRescuer = db.relationship("Case", foreign_keys="Case.rescuer")
	asAuthor = db.relationship("Feed", foreign_keys="Feed.author")
	asUsername = db.relationship("Token", foreign_keys="Token.username")

	def __init__(self, username, password, phone, isAdmin, isActivated, registrationToken=None):
		self.username = username
		self.password = password
		self.phone = phone
		self.isAdmin = isAdmin
		self.isActivated = isActivated
		self.registrationToken = registrationToken

	def save(self):
		db.session.add(self)
		db.session.commit()

	def delete(self):
		db.session.delete(self)
		db.session.commit()

	def update(self, **kwargs):
		for key, value in kwargs.items():
			setattr(self, key, value)
		db.session.commit()

	def __repr__(self):
		return "<User: {}>".format(self.username)

	@staticmethod
	def all():
		return User.query.all()

	@staticmethod
	def get(username):
		return User.query.get(username)

	@staticmethod
	def exists(username):
		return db.session.query(User.query.filter(User.username == username).exists()).scalar()

class UserSchema(ma.Schema):
	username = ma.String(required=True, validate=lambda x: not User.exists(x))
	phone = ma.String(required=True)
	isActivated = ma.Boolean(missing=False)
	isAdmin = ma.Boolean(missing=False)
	password = ma.String(required=True, load_only=True)
	registrationToken = ma.String(missing=None, load_only=True)

	@pre_load
	def process_input(self, data):
		if data.get("password") is not None:
			pw = str(data["password"])
			data["password"] = hashlib.sha256(pw.encode('utf-8')).hexdigest()
		return data

	@post_load
	def make_user(self, data):
		return User(**data)

user_schema = UserSchema()
users_schema = UserSchema(many=True)

spec.definition("User", schema=UserSchema)