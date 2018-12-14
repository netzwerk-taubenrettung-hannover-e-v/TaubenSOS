import json
from flask import (Blueprint, request, jsonify)
from datetime import datetime
from marshmallow import utils
from api.models.populationMarker import (PopulationMarker, populationMarker_schema, populationMarkers_schema)
from api.models.populationValue import (PopulationValue, populationValue_schema, populationValues_schema)

bp = Blueprint("population", __name__, url_prefix="/api")

@bp.route("/population", methods=["GET"], strict_slashes=False)
def get_Markers():
	"""
	file: ../../docs/population/read_all.yml
	"""
	if request.method == "GET":
		if request.is_json == True:
			jsonData = request.get_json()
			data = json.loads(str(jsonData).replace('\'', '\"'))
			if "lastUpdate" in data:
				populationMarkers = PopulationMarker.get_newly_updated_markers(convert_timestamp(int(jsonData["lastUpdate"])))
			else:
				return jsonify("Json Body has no key lastUpdate"), 400
			populationMarkers = PopulationMarker.all()
		result = [make_json_marker(populationMarker = p) for p in populationMarkers]
		return jsonify(result)

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
			result = make_json_marker(populationMarker=populationMarker)
			return jsonify(result), 201

@bp.route("/population/<populationMarkerID>", methods=["POST"], strict_slashes=False)
def create_value_for_marker(populationMarkerID):
	"""
	file: ../../docs/population/create_value.yml
	"""
	if request.method == "POST":
		populationMarker = PopulationMarker.get(populationMarkerID)
		if populationMarker is None:
			return jsonify({"message": "The population marker to be added a value could not be found"}), 404
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
			result = make_json_value(populationValue=populationValue)
			return jsonify(result), 201

@bp.route("/population/<populationMarkerID>", methods=["DELETE"], strict_slashes=False)
def delete_marker(populationMarkerID):
	"""
	file: ../../docs/population/delete_marker.yml
	"""
	if request.method == "DELETE":
		populationMarker = PopulationMarker.get(populationMarkerID)
		if populationMarker is None:
			return jsonify({"message": "The population marker to be deleted could not be found"}), 404
		populationMarker.delete()
		return "", 204, {"Content-Type": "application/json"}

@bp.route("/population/<populationMarkerID>", methods=["PUT"], strict_slashes=False)
def change_marker(populationMarkerID):
	if request.method == "PUT":
		populationMarker = PopulationMarker.get(populationMarkerID)
		if populationMarker is None:
			return jsonify({"message": "The population marker to be changed could not be found"}), 404
		json = request.get_json()
		errors = populationMarker_schema.validate(json, partial=True)
		if errors:
			return jsonify(errors), 400
		PopulationMarker.update(populationMarker, **json)
		result = make_json_marker(populationMarker=populationMarker)
		return jsonify(result), 200

def convert_timestamp(unix):
	return utils.rfcformat(datetime.fromtimestamp(unix))

def make_json_marker(populationMarker):
	result = populationMarker_schema.dump(populationMarker).data
	return result

def make_json_value(populationValue):
	result = populationValue_schema.dump(populationValue).data
	return result
