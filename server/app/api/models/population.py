from api import db, ma
from datetime import datetime

class Population(db.Model):
	__tablename__ = "population"
	timestamp = db.Column(db.DateTime, primary_key=True, default=datetime.utcnow)
	pigeonCount = db.Column(db.Integer)
	latitude = db.Column(db.Float, primary_key=True)
	longitude = db.Column(db.Float, primary_key=True)

	def __init__(self, pigeonCount, latitude, longitude):
		self.pigeonCount = pigeonCount
		self.latitude = latitude
		self.longitude = longitude

	def save(self):
		db.session.add(self)
		db.session.commit()

	def delete(self):
		db.session.delete(self)
		db.session.commit()

	def __repr__(self):
		return "<Population: {0}, {1}, {2}>".format(self.timestamp, self.longitude, self.latitude)

	@staticmethod
	def all():
		return Population.query.all()

	#Returns a list of population data which matches the time and coordinates
	@staticmethod
	def get_pigeon_count(startTime, untilTime, latTopLeft, lonTopLeft, latBotRight, lonBotRight):
		return Population.query.filter(db.and_(db.between(Population.timestamp, startTime, untilTime), db.between(Population.latitude, latBotRight, latTopLeft), db.between(Population.longitude, lonTopLeft, lonBotRight))).all()

class PopulationSchema(ma.ModelSchema):
	class Meta:
		model = Population
		sqla_session = db.session

population_schema = PopulationSchema()
populations_schema = PopulationSchema(many=True)
