from api import db, ma
from api.models import populationValue
from datetime import datetime
from marshmallow import post_dump, pre_load, post_load, utils, validate

class PopulationMarker(db.Model):
	__tablename__ = "populationMarker"
	populationMarkerID = db.Column(db.Integer, primary_key=True)
	latitude = db.Column(db.Float, nullable=False)
	longitude = db.Column(db.Float, nullable=False)
	description = db.Column(db.String(60), nullable=True)
	radius = db.Column(db.Float, nullable=False)
	values = db.relationship("PopulationValue", cascade="all, delete-orphan")


	def __init__(self, latitude, longitude, description, radius):
		self.latitude = latitude
		self.longitude = longitude
		self.description = description
		self.radius = radius

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
		return "<Population Marker: {}>".format(self.populationMarkerID)

	@staticmethod
	def all():
		return PopulationMarker.query.all()

	@staticmethod
	def get(populationMarkerID):
		return PopulationMarker.query.get(populationMarkerID)

class PopulationMarkerSchema(ma.Schema):
	populationMarkerID = ma.Integer(dump_only=True)
	latitude = ma.Float(required=True)
	longitude = ma.Float(required=True)
	radius = ma.Float(required=True)
	description = ma.String(missing=None)

	@post_load
	def make_populationMarker(self, data):
		return PopulationMarker(**data)

populationMarker_schema = PopulationMarkerSchema()
populationMarkers_schema = PopulationMarkerSchema(many=True)