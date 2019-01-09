from api import db, ma

class User(db.Model):
	__tablename__="user"
	username = db.Column(db.String(20), primary_key=True)
	password = db.Column(db.String(255), nullable=False)
	phone = db.Column(db.String(30))
	isAdmin = db.Column(db.Boolean)
	isActivated = db.Column(db.Boolean)

	def __init__(self, username, password, phone, isAdmin, isActivated):
		self.username = username
		self.password = password
		self.phone = phone
		self.isAdmin = isAdmin
		self.isActivated = isActivated

	def save(self):
		db.session.add(self)
		db.session.commit()

	def delete(self):
		db.session.delete(self)
		db.session.commit()

	def __repr__(self):
		return "<User: {}>".format(self.username)

	@staticmethod
	def all():
		return User.query.all()

	@staticmethod
	def get(username):
		return User.query.get(username)

class UserSchema(ma.ModelSchema):
    class Meta:
        model = User
        sqla_session = db.session

user_schema = UserSchema()
users_schema = UserSchema(many=True)
