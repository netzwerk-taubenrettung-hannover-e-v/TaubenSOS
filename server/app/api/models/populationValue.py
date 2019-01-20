from api import db, ma, spec
from datetime import datetime
from marshmallow import post_dump, pre_load, post_load, utils, validate

class PopulationValue(db.Model):
	__tablename__ = "populationValue"
	populationMarkerID = db.Column(db.Integer, db.ForeignKey("populationMarker.populationMarkerID"), primary_key=True)
	timestamp = db.Column(db.DateTime, nullable=False, default=datetime.utcnow, primary_key=True)
	pigeonCount = db.Column(db.Integer, nullable=False)

	def __init__(self, populationMarkerID, timestamp, pigeonCount):
		self.populationMarkerID = populationMarkerID
		self.timestamp = timestamp
		self.pigeonCount = pigeonCount

	def save(self):
		db.session.add(self)
		db.session.commit()

	def update(self, **kwargs):
		for key, value in kwargs.items():
			setattr(self, key, value)
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
	def get_value(populationMarkerID, timestamp):
		return PopulationValue.query.get((populationMarkerID, timestamp))

	@staticmethod
	def already_has_value(populationMarkerID, timestamp):
		return db.session.query(PopulationValue).filter(db.and_(PopulationValue.populationMarkerID == populationMarkerID, db.func.date(PopulationValue.timestamp) == db.func.date(timestamp)))

	@staticmethod
	def get_values_for_marker(populationMarkerID):
		return db.session.query(PopulationValue).filter(PopulationValue.populationMarkerID == populationMarkerID)

class PopulationValueSchema(ma.Schema):
	populationMarkerID = ma.Integer(required=True)
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
			d = datetime.fromtimestamp(int(data["timestamp"])).date()
			data["timestamp"] = utils.rfcformat(datetime(year=d.year, month=d.month, day=d.day))
		return data

	@post_load
	def make_populationValue(self, data):
		return PopulationValue(**data)

populationValue_schema = PopulationValueSchema()
populationValues_schema = PopulationValueSchema(many=True)

spec.definition("PopulationValue", schema=PopulationValueSchema)