from api import db, ma

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
    def all():
        return Test.query.all()

    @staticmethod
    def get(number):
        return Test.query.get(number)

class TestSchema(ma.ModelSchema):
    class Meta:
        model = Test
        sqla_session = db.session

test_schema = TestSchema()
tests_schema = TestSchema(many=True)