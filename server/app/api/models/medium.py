from api import db, ma, spec
from marshmallow import post_dump, post_load

class Medium(db.Model):
    __tablename__ = "medium"
    caseID = db.Column(db.Integer, db.ForeignKey("case.caseID"), nullable=False)
    uri = db.Column(db.String(255), primary_key=True)
    mediaID = db.Column(db.Integer, nullable=True)
    mimeType = db.Column(db.String(255), nullable=True)
    thumbnail = db.Column(db.String(255), nullable=True)

    def __init__(self, caseID, uri=None, mimeType=None, thumbnail=None):
        self.caseID = caseID
        self.uri = uri
        self.mimeType = mimeType
        self.thumbnail = thumbnail
    
    def save(self):
        db.session.add(self)
        db.session.commit()

    def update(self):
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
    def get(caseID, mediaID):
        # return Medium.query.get(caseID)
        return Medium.query.filter_by(caseID=caseID, mediaID=mediaID).first()

class MediumSchema(ma.Schema):
    mediaID = ma.Integer()
    mimeType = ma.String()

    @post_load
    def make_medium(self, data):
        return Medium(**data)

medium_schema = MediumSchema()
media_schema = MediumSchema(many=True)

spec.definition("Medium", schema=MediumSchema)