from api import db

class Test(db.Model):
    __tablename__ = "test"
    number = db.Column(db.Integer, primary_key=True)
    string = db.Column(db.String(50))

    def __init__(self, string):
        self.string = string

    def save(self):
        db.session.add(self)
        db.session.commit()

    def delete(self):
        db.session.delete(self)
        db.session.commit()

    def __repr__(self):
        return "<Test: {}>".format(self.string)

    @staticmethod
    def get_all():
        return Test.query.all()

