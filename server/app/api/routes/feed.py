from flask import (Blueprint, request, jsonify)

from api.models.feed import (Feed, feed_schema, feeds_schema)

bp = Blueprint("feed", __name__, url_prefix="/api")

@bp.route("/feed", methods=["GET"], strict_slashes=False)
def read_all():
	"""
	file: ../../docs/feed/read_all.yml
	"""
	if request.method == "GET":
		news = Feed.all()
		result = [make_json(Feed=u) for u in news]
		return jsonify(result)

@bp.route("/feed", methods=["POST"], strict_slashes=False)
def create_news():
	"""
	file: ../../docs/feed/create_news.yml
	"""
	if request.method == "POST":
		json = request.get_json()
		feed, errors = feed_schema.load(json)
		if errors:
			return jsonify(errors), 400
		else:
			feed.save()
			return jsonify(make_json(Feed=feed)), 201

@bp.route("/feed/<feedID>", methods=["PUT"], strict_slashes=False)
def update_news(feedID):
	"""
	file: ../../docs/feed/update_news.yml
	"""
	if request.method == "PUT":
		json = request.get_json()
		feed = Feed.get(feedID)
		if feed is None:
			return jsonify({"message": "The news to be updated could not be found"}), 404
		errors = feed_schema.validate(json, partial=True)
		if errors:
			return jsonify(errors), 400
		feed.update(**json)
		result = make_json(Feed=feed)
		return jsonify(result), 200

@bp.route("/feed/<feedID>", methods=["DELETE"], strict_slashes=False)
def delete_news(feedID):
	"""
	file: ../../docs/feed/delete_news.yml
	"""
	if request.method == "DELETE":
		feed = Feed.get(feedID)
		if feed is None:
			return jsonify({"message": "The feed to be deleted could not be found"}), 404
		feed.delete()
		return jsonify({"message": "The feed has been deleted"}), 204

def make_json(Feed):
	result = feed_schema.dump(Feed).data
	return result