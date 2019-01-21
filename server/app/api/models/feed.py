from api import db, ma, spec
from datetime import datetime
from api.models import user
from marshmallow import post_dump, pre_load, post_load, utils, validate

class Feed(db.Model):
	__tablename__ = "feed"
	feedID = db.Column(db.Integer, primary_key=True)
	author = db.Column(db.String(20), db.ForeignKey("user.username"), nullable=True)
	title = db.Column(db.String(128), nullable=False)
	text = db.Column(db.String, nullable=False)
	timestamp = db.Column(db.DateTime, nullable=False, default=datetime.utcnow)
	eventStart = db.Column(db.DateTime, nullable=True)
	eventEnd = db.Column(db.DateTime, nullable=True)

	def __init__(self, author, title, text, timestamp, eventStart, eventEnd):
		self.author = author
		self.title = title
		self.text = text
		self.timestamp = timestamp
		self.eventStart = eventStart
		self.eventEnd = eventEnd

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
		return Feed.query.order_by(Feed.timestamp.desc())

	@staticmethod
	def get(feedID):
		return Feed.query.get(feedID)

	@staticmethod
	def recents():
		except_query = Feed.query.filter(Feed.eventEnd < datetime.utcnow())
		return Feed.query.except_(except_query).order_by(Feed.timestamp.desc())

class FeedSchema(ma.Schema):
	feedID = ma.Integer(dump_only=True)
	author = ma.String(required=True, validate=user.User.exists)
	title = ma.String(required=True, validate=validate.Length(max=128))
	text = ma.String(missing=None)
	timestamp = ma.DateTime("rfc", missing=None)
	eventStart = ma.DateTime("rfc", missing=None)
	eventEnd = ma.DateTime("rfc", missing=None)

	@post_dump
	def wrap(self, data):
		timestamp = utils.from_rfc(data["timestamp"])
		data["timestamp"] = timestamp.strftime("%s")
		if data.get("eventStart") is not None:
			eventStart = utils.from_rfc(data["eventStart"])
			data["eventStart"] = eventStart.strftime("%s")
		if data.get("eventEnd") is not None:
			eventEnd = utils.from_rfc(data["eventEnd"])
			data["eventEnd"] = eventEnd.strftime("%s")
		return data

	@pre_load
	def process_input(self, data):
		if data.get("timestamp") is not None:
			d = datetime.fromtimestamp(int(data["timestamp"]))
			data["timestamp"] = utils.rfcformat(d)
		if data.get("eventStart") is not None:
			e = datetime.fromtimestamp(int(data["eventStart"]))
			data["eventStart"] = utils.rfcformat(e)
		if data.get("eventEnd") is not None:
			e = datetime.fromtimestamp(int(data["eventEnd"]))
			data["eventEnd"] = utils.rfcformat(e)
		return data

	@post_load
	def make_news(self, data):
		return Feed(**data)

feed_schema = FeedSchema()
feeds_schema = FeedSchema(many=True)

spec.definition("Feed", schema=FeedSchema)