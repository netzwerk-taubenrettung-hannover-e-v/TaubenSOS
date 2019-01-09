import json
from flask import (Blueprint, request, jsonify)
from datetime import datetime
from marshmallow import utils
from api.models.populationMarker import (PopulationMarker, populationMarker_schema, populationMarkers_schema)
from api.models.populationValue import (PopulationValue, populationValue_schema, populationValues_schema)

bp = Blueprint("population", __name__, url_prefix="/api")

@bp.route("/population", methods=["GET"], strict_slashes=False)
def get_markers():
	"""
	file: ../../docs/population/read_all.yml
	"""
	if request.method == "GET":
		data = request.args.get("lastUpdate")
		if data is not None:
			try:
				lastUpdate = convert_timestamp(int(data))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400
			populationMarkers = PopulationMarker.get_newly_updated_markers(lastUpdate)
		else:
			populationMarkers = PopulationMarker.all()
		return populationMarkers_schema.jsonify(populationMarkers), 200

@bp.route("population/<int:populationMarkerID>", methods=["GET"], strict_slashes=False)
def get_marker(populationMarkerID):
	"""
	file: ../../docs/population/read_marker.yml
	"""
	if request.method == "GET":
		populationMarker = PopulationMarker.get(populationMarkerID)
		if populationMarker is None:
			return jsonify(message="The marker to be shown could not be found"), 404
		return populationMarker_schema.jsonify(populationMarker), 200


@bp.route("/population", methods=["POST"], strict_slashes=False)
def create_marker():
	"""
	file: ../../docs/population/create_marker.yml
	"""
	if request.method == "POST":
		json = request.get_json()
		populationMarker, errors = populationMarker_schema.load(data=json)
		if errors:
			return jsonify(errors), 400
		else:
			populationMarker.save()
			return populationMarker_schema.jsonify(populationMarker), 201

@bp.route("/population/<int:populationMarkerID>", methods=["POST"], strict_slashes=False)
def create_value_for_marker(populationMarkerID):
	"""
	file: ../../docs/population/create_value.yml
	"""
	if request.method == "POST":
		populationMarker = PopulationMarker.get(populationMarkerID)
		if populationMarker is None:
			return jsonify(message="The population marker to be added a value could not be found"), 404
		json = request.get_json()
		json["populationMarkerID"] = int(populationMarkerID)
		populationValue, errors = populationValue_schema.load(data=json)
		lastUpdate = datetime.utcnow()
		lastUpdateDict = {
			"lastUpdate": lastUpdate
		}
		PopulationMarker.update(populationMarker, **lastUpdateDict)
		if errors:
			return jsonify(errors), 400
		else:
			populationValue.save()
			return populationValue_schema.jsonify(populationValue), 201

@bp.route("/population/<int:populationMarkerID>", methods=["DELETE"], strict_slashes=False)
def delete_marker(populationMarkerID):
	"""
	file: ../../docs/population/delete_marker.yml
	"""
	if request.method == "DELETE":
		populationMarker = PopulationMarker.get(populationMarkerID)
		if populationMarker is None:
			return jsonify(message="The population marker to be deleted could not be found"), 404
		populationMarker.delete()
		return "", 204, {"Content-Type": "application/json"}

@bp.route("/population/<int:populationMarkerID>", methods=["PUT"], strict_slashes=False)
def change_marker(populationMarkerID):
	"""
	file: ../../docs/population/update_marker.yml
	"""
	if request.method == "PUT":
		populationMarker = PopulationMarker.get(populationMarkerID)
		if populationMarker is None:
			return jsonify(message="The population marker to be changed could not be found"), 404
		json = request.get_json()
		errors = populationMarker_schema.validate(json, partial=True)
		if errors:
			return jsonify(errors), 400
		json["lastUpdate"] = datetime.utcnow()
		PopulationMarker.update(populationMarker, **json)
		return populationMarker_schema.jsonify(populationMarker), 200

def convert_timestamp(unix):
	return utils.rfcformat(datetime.fromtimestamp(unix))