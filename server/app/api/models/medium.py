from api import db, ma
from marshmallow import post_dump, post_load

class Medium(db.Model):
    __tablename__ = "medium"
    caseID = db.Column(db.Integer, db.ForeignKey("case.caseID"), nullable=False)
    uri = db.Column(db.String(255), primary_key=True)

    def __init__(self, uri):
        self.uri = uri
    
    def save(self):
        db.session.add(self)
        db.session.commit()

    def delete(self):
        db.session.delete(self)
        db.session.commit()

    def __repr__(self):
        return "<Medium: {}>".format(self.caseID)

    @staticmethod
    def all():
        return Medium.query.all()

    @staticmethod
    def get(caseID):
        return Medium.query.get(caseID)

class MediumSchema(ma.Schema):
    uri = ma.String()

    @post_load
    def make_medium(self, data):
        return Medium(**data)

medium_schema = MediumSchema()
media_schema = MediumSchema(many=True)