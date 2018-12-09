from api import db

class Token(db.Model):
    __tablename__ = "token"
    tokenID = db.Column(db.String(32), primary_key=True)
    username = db.Column(db.String(20), db.ForeignKey("user.username"), nullable=False)

    def __init__(self, tokenID, username):
        self.tokenID = tokenID
        self.username = username

    def save(self):
        db.session.add(self)
        db.session.commit()

    def delete(self):
        db.session.delete(self)
        db.session.commit()

    def __repr__(self):
        return "<Token: {0}, {1}>".format(self.tokenID, self.username)

    @staticmethod
    def get(tokenID):
        return Token.query.get(tokenID)