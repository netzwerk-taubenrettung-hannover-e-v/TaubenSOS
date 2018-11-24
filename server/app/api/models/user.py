from api import db, ma

class User(db.Model):
    __tablename__="user"
    username = db.Column(db.String(20), primary_key=True)
    phone = db.Column(db.String(30))
    isAdmin = db.Column(db.Boolean)

    def __init__(self, username, phone, isAdmin):
        self.username = username
        self.phone = phone
        self.isAdmin = isAdmin

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
