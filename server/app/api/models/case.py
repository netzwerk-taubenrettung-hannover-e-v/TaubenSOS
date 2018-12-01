from api import db, ma
from api.models import injury, medium
from datetime import datetime
from marshmallow import post_dump, pre_load, post_load, utils

class Case(db.Model):
    __tablename__ = "case"
    caseID = db.Column(db.Integer, primary_key=True)
    timestamp = db.Column(db.DateTime, nullable=False, default=datetime.utcnow)
    priority = db.Column(db.Integer, nullable=False)
    rescuer = db.Column(db.String(255), nullable=True)
    isCarrierPigeon = db.Column(db.Boolean, nullable=False)
    isWeddingPigeon = db.Column(db.Boolean, nullable=False)
    additionalInfo = db.Column(db.String, nullable=True)
    phone = db.Column(db.String(255), nullable=False)
    latitude = db.Column(db.Float, nullable=False)
    longitude = db.Column(db.Float, nullable=False)
    wasFoundDead = db.Column(db.Boolean, nullable=True)
    isClosed = db.Column(db.Boolean, nullable=False, default=False)
    injury = db.relationship("Injury", backref="case", lazy=True, uselist=False)
    media = db.relationship("Medium", backref="case", lazy=True, uselist=True)

    def __init__(self, timestamp, priority, rescuer, isCarrierPigeon, isWeddingPigeon, additionalInfo, phone, latitude, longitude, wasFoundDead, isClosed, injury, media):
        self.timestamp = timestamp
        self.priority = priority
        self.rescuer = rescuer
        self.isCarrierPigeon = isCarrierPigeon
        self.isWeddingPigeon = isWeddingPigeon
        self.additionalInfo = additionalInfo
        self.phone = phone
        self.latitude = latitude
        self.longitude = longitude
        self.wasFoundDead = wasFoundDead
        self.isClosed = isClosed
        self.injury = injury
        self.media = media

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

class CaseSchema(ma.Schema):
    caseID = ma.Integer(dump_only=True)
    timestamp = ma.DateTime("rfc", missing=None)
    priority = ma.Integer(required=True)
    rescuer = ma.String(missing=None)
    isCarrierPigeon = ma.Boolean(required=True)
    isWeddingPigeon = ma.Boolean(required=True)
    additionalInfo = ma.String(missing=None)
    phone = ma.String(required=True)
    latitude = ma.Float(required=True)
    longitude = ma.Float(required=True)
    wasFoundDead = ma.Boolean(missing=None)
    isClosed = ma.Boolean(missing=None)
    injury = ma.Nested(injury.InjurySchema, required=True)
    media = ma.Nested(medium.MediumSchema, missing=[], many=True)

    @post_dump
    def wrap(self, data):
        d = utils.from_rfc(data["timestamp"])
        data["timestamp"] = d.strftime("%s")
        return data

    @pre_load
    def process_input(self, data):
        d = datetime.fromtimestamp(int(data["timestamp"]))
        data["timestamp"] = utils.rfcformat(d)
        return data

    @post_load
    def make_case(self, data):
        return Case(**data)

case_schema = CaseSchema()
cases_schema = CaseSchema(many=True)