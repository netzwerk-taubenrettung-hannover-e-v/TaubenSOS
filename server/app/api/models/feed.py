from api import db, ma, spec
from datetime import datetime
from api.models import user
from marshmallow import post_dump, pre_load, post_load, utils, validate

class Feed(db.Model):
	__tablename__ = "feed"
	feedID = db.Column(db.Integer, primary_key=True)
	author = db.Column(db.String(20), db.ForeignKey("user.username"), nullable=True)
	title = db.Column(db.String(50), nullable=False)
	text = db.Column(db.String, nullable=False)
	timestamp = db.Column(db.DateTime, nullable=False, default=datetime.utcnow)
	eventStart = db.Column(db.DateTime, nullable=True)

	def __init__(self, author, title, text, timestamp, eventStart):
		self.author = author
		self.title = title
		self.text = text
		self.timestamp = timestamp
		self.eventStart = eventStart

	def save(self):
		db.session.add(self)
		db.session.commit()

	def delete(self):
		db.session.delete(self)
		db.session.commit()

	def update(self, **kwargs):
		for key, value in kwargs.items():
			setattr(self, key, value)
		db.session.commit()

	def __repr__(self):
		return "<Feed: {}>".format(self.title)

	@staticmethod
	def all():
		return Feed.query.all()

	@staticmethod
	def get(feedID):
		return Feed.query.get(feedID)

	@staticmethod
	def get_newly_posted_news(lastUpdate):
		return db.session.query(Feed).filter(Feed.timestamp > lastUpdate)

class FeedSchema(ma.Schema):
	feedID = ma.Integer(dump_only=True)
	author = ma.String(required=True, validate=user.User.exists)
	title = ma.String(required=True)
	text = ma.String(missing=None)
	timestamp = ma.DateTime("rfc", missing=None)
	eventStart = ma.DateTime("rfc", missing=None)

	@post_dump
	def wrap(self, data):
		timestamp = utils.from_rfc(data["timestamp"])
		data["timestamp"] = timestamp.strftime("%s")
		if data.get("eventStart") is not None:
			eventStart = utils.from_rfc(data["eventStart"])
			data["eventStart"] = eventStart.strftime("%s")
		return data

	@pre_load
	def process_input(self, data):
		if data.get("timestamp") is not None:
			d = datetime.fromtimestamp(int(data["timestamp"]))
			data["timestamp"] = utils.rfcformat(d)
		if data.get("eventStart") is not None:
			e = datetime.fromtimestamp(int(data["eventStart"]))
			data["eventStart"] = utils.rfcformat(e)
		return data

	@post_load
	def make_news(self, data):
		return Feed(**data)

feed_schema = FeedSchema()
feeds_schema = FeedSchema(many=True)

spec.definition("Feed", schema=FeedSchema)