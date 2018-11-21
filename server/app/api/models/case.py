from marshmallow import fields
from api import db, ma
from api.models import injury

class Case(db.Model):
    __tablename__ = "case"
    caseID = db.Column(db.Integer, primary_key=True)
    timestamp = db.Column(db.DateTime)
    priority = db.Column(db.Integer)
    media1 = db.Column(db.String(255))
    media2 = db.Column(db.String(255))
    media3 = db.Column(db.String(255))
    rescuer = db.Column(db.String(255))
    isCarrierPigeon = db.Column(db.Boolean)
    isWeddingPigeon = db.Column(db.Boolean)
    additionalInfo = db.Column(db.String)
    phone = db.Column(db.String(255))
    latitude = db.Column(db.Float)
    longitude = db.Column(db.Float)
    wasFoundDead = db.Column(db.Boolean)
    isClosed = db.Column(db.Boolean)
    injury = db.relationship("Injury", backref="case", lazy=True, uselist=False)

    def __init__(self, timestamp, priority, media1, media2, media3, rescuer, isCarrierPigeon, isWeddingPigeon, additionalInfo, phone, latitude, longitude, wasFoundDead, isClosed):
        self.timestamp = timestamp
        self.priority = priority
        self.media1 = media1
        self.media2 = media2
        self.media3 = media3
        self.rescuer = rescuer
        self.isCarrierPigeon = isCarrierPigeon
        self.isWeddingPigeon = isWeddingPigeon
        self.additionalInfo = additionalInfo
        self.phone = phone
        self.latitude = latitude
        self.longitude = longitude
        self.wasFoundDead = wasFoundDead
        self.isClosed = isClosed

    def save(self):
        db.session.add(self)
        db.session.commit()

    def delete(self):
        db.session.delete(self)
        db.session.commit()

    def __repr__(self):
        return "<Case: {}>".format(self.caseID)

    @staticmethod
    def all():
        return Case.query.all()

    @staticmethod
    def get(caseID):
        return Case.query.get(caseID)

class CaseSchema(ma.ModelSchema):
    timestamp = fields.DateTime("%s")
    class Meta:
        model = Case
        sqla_session = db.session

case_schema = CaseSchema()
cases_schema = CaseSchema(many=True)