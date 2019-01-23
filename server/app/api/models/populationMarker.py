from api import db, ma, spec
from api.models import populationValue
from api.models.populationValue import PopulationValue
from datetime import datetime
from sqlalchemy import text, bindparam
from marshmallow import post_dump, pre_load, post_load, utils, validate

class PopulationMarker(db.Model):
	__tablename__ = "populationMarker"
	populationMarkerID = db.Column(db.Integer, primary_key=True)
	latitude = db.Column(db.Float, nullable=False)
	longitude = db.Column(db.Float, nullable=False)
	description = db.Column(db.String(60), nullable=True)
	radius = db.Column(db.Float, nullable=False)
	lastUpdate = db.Column(db.DateTime, nullable=False, default=datetime.utcnow())
	values = db.relationship("PopulationValue", cascade="all, delete-orphan")

	def __init__(self, latitude, longitude, description, radius, lastUpdate, values):
		self.latitude = latitude
		self.longitude = longitude
		self.description = description
		self.radius = radius
		self.lastUpdate = lastUpdate
		self.values = values

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
		return db.session.query(PopulationMarker).all()

	@staticmethod
	def get_newly_updated_markers(lastUpdate):
		return db.session.query(PopulationMarker).filter(PopulationMarker.lastUpdate > lastUpdate)

	@staticmethod
	def get(populationMarkerID):
		return PopulationMarker.query.get(populationMarkerID)

	@staticmethod
	def get_stats(latNE, lonNE, latSW, lonSW, fromTime=None, untilTime=None):
		if fromTime is not None and untilTime is not None:
			sql = text('select date(timestamp) as "day", sum("pigeonCount") as "count" from "populationMarker" join "populationValue" using("populationMarkerID") where latitude between :latSW and :latNE and longitude between :lonSW and :lonNE and timestamp between :fromTime and :untilTime group by "day" order by "day"')
			sql = sql.bindparams(latSW=latSW, latNE=latNE, lonSW=lonSW, lonNE=lonNE, fromTime=fromTime, untilTime=untilTime)
		elif fromTime is not None:
			sql = text('select date(timestamp) as "day", sum("pigeonCount") as "count" from "populationMarker" join "populationValue" using("populationMarkerID") where latitude between :latSW and :latNE and longitude between :lonSW and :lonNE and timestamp > :fromTime group by "day" order by "day"')
			sql = sql.bindparams(latSW=latSW, latNE=latNE, lonSW=lonSW, lonNE=lonNE, fromTime=fromTime)
		elif untilTime is not None:
			sql = text('select date(timestamp) as "day", sum("pigeonCount") as "count" from "populationMarker" join "populationValue" using("populationMarkerID") where latitude between :latSW and :latNE and longitude between :lonSW and :lonNE and timestamp < :untilTime group by "day" order by "day"')
			sql = sql.bindparams(latSW=latSW, latNE=latNE, lonSW=lonSW, lonNE=lonNE, untilTime=untilTime)
		else:
			sql = text('select date(timestamp) as "day", sum("pigeonCount") as "count" from "populationMarker" join "populationValue" using("populationMarkerID") where latitude between :latSW and :latNE and longitude between :lonSW and :lonNE group by "day" order by "day"')
			sql = sql.bindparams(latSW=latSW, latNE=latNE, lonSW=lonSW, lonNE=lonNE)
		result = db.engine.execute(sql)
		res = result.fetchall()
		x = []
		for i in res:
			x.append(dict(i.items()))
		for i in x:
			i['day'] = utils.from_rfc(str(i['day'])).strftime("%s")
		return x

class PopulationMarkerSchema(ma.Schema):
	populationMarkerID = ma.Integer(dump_only=True)
	latitude = ma.Float(required=True)
	longitude = ma.Float(required=True)
	radius = ma.Float(required=True)
	description = ma.String(missing=None)
	lastUpdate = ma.DateTime("rfc", missing=None)
	values = ma.Nested(populationValue.PopulationValueSchema, missing=[], many=True)

	@post_dump
	def wrap(self, data):
		d = utils.from_rfc(data["lastUpdate"])
		data["lastUpdate"] = d.strftime("%s")
		return data

	@pre_load
	def process_input(self, data):
		if data.get("lastUpdate") is not None:
			d = datetime.fromtimestamp(int(data["lastUpdate"]))
			data["lastUpdate"] = utils.rfcformat(d)
		return data

	@post_load
	def make_populationMarker(self, data):
		return PopulationMarker(**data)

populationMarker_schema = PopulationMarkerSchema()
populationMarkers_schema = PopulationMarkerSchema(many=True)

spec.definition("PopulationMarker", schema=PopulationMarkerSchema)