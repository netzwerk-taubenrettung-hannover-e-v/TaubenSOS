from api import db, ma
from datetime import datetime
from marshmallow import post_dump, pre_load, post_load, utils, validate

class PopulationValue(db.Model):
	__tablename__ = "populationValue"
	populationMarkerID = db.Column(db.Integer, db.ForeignKey("populationMarker.populationMarkerID"), primary_key=True)
	timestamp = db.Column(db.DateTime, nullable=False, default=datetime.utcnow, primary_key=True)
	pigeonCount = db.Column(db.Integer, nullable=False)

	def __init__(populationMarkerID, timestamp, pigeonCount):
		self.populationMarkerID = populationMarkerID
		self.timestamp = timestamp
		self.pigeonCount = pigeonCount

	def save(self):
		db.session.add(self)
		db.session.commit()

	def delete(self):
		db.session.delete(self)
		db.session.commit()

	def __repr__(self):
		return "<Population Value: {}>".format(self.populationMarkerID)

	@staticmethod
	def all():
		return PopulationValue.query.all()

	@staticmethod
	def get(populationMarkerID):
		return PopulationValue.query.get(populationMarkerID)

class PopulationValueSchema(ma.Schema):
	populationMarkerID = ma.Integer(dump_only=True)
	pigeonCount = ma.Integer(required=True)
	timestamp = ma.DateTime("rfc", missing=None)

	@post_dump
	def wrap(self, data):
		d = utils.from_rfc(data["timestamp"])
		data["timestamp"] = d.strftime("%s")
		return data

	@pre_load
	def process_input(self, data):
		if data.get("timestamp") is not None:
			d = datetime.fromtimestamp(int(data["timestamp"]))
			data["timestamp"] = utils.rfcformat(d)
		return data

	@post_load
	def make_populationValue(self, data):
		return PopulationValue(**data)

populationValue_schema = PopulationValueSchema()
populationValues_schema = PopulationValueSchema(many=True)