from flask import Blueprint, request, jsonify
from datetime import datetime
from marshmallow import utils
from api.models.feed import Feed, feed_schema, feeds_schema
from . import fcm

bp = Blueprint("feed", __name__, url_prefix="/api")

@bp.route("/feed", methods=["GET"], strict_slashes=False)
def read_all():
	"""
	file: ../../docs/feed/read_all.yml
	"""
	news = Feed.recents()
	return feeds_schema.jsonify(news), 200

@bp.route("/feed", methods=["POST"], strict_slashes=False)
def create_news():
	"""
	file: ../../docs/feed/create_news.yml
	"""
	json = request.get_json()
	feed, errors = feed_schema.load(json)
	if errors:
		return jsonify(errors), 400
	fcm.send_to_topic(
		"/topics/member",
		["push_new_event_title", feed.title],
		feed.text,
		"ic_today",
		dict(news=feed_schema.dumps(feed)))
	feed.save()
	return feed_schema.jsonify(feed), 201

@bp.route("/feed/<int:feedID>", methods=["PUT"], strict_slashes=False)
def update_news(feedID):
	"""
	file: ../../docs/feed/update_news.yml
	"""
	json = request.get_json()
	feed = Feed.get(feedID)
	if feed is None:
		return jsonify(message="The news to be updated could not be found"), 404
	errors = feed_schema.validate(json, partial=True)
	if errors:
		return jsonify(errors), 400
	feed.update(**json)
	return feed_schema.jsonify(feed), 200

@bp.route("/feed/<int:feedID>", methods=["DELETE"], strict_slashes=False)
def delete_news(feedID):
	"""
	file: ../../docs/feed/delete_news.yml
	"""
	feed = Feed.get(feedID)
	if feed is None:
		return jsonify(message="The feed to be deleted could not be found"), 404
	feed.delete()
	return "", 204 , {"Content-Type": "application/json"}