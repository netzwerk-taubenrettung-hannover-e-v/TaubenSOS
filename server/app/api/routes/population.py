import json
from flask import (Blueprint, request, jsonify)
from api.models.populationMarker import (PopulationMarker, populationMarker_schema, populationMarkers_schema)
from api.models.populationValue import (PopulationValue, populationValue_schema, populationValues_schema)

bp = Blueprint("population", __name__, url_prefix="/api")

@bp.route("/population", methods=["GET"], strict_slashes=False)
def get_Markers():
	if request.method == "GET":
		populationMarkers = PopulationMarker.all()
		result = [make_json_marker(populationMarker = p) for p in populationMarkers]
		return jsonify(result)

@bp.route("/population/<populationMarkerID>", methods=["GET"], strict_slashes=False)
def get_values_for_marker(populationMarkerID):
	if request.method == "GET":
		populationMarker = PopulationMarker.get(populationMarkerID)
		if populationMarker is None:
			return jsonify({"message": "The population marker could not be found"}), 404
		values = PopulationValue.get_values_for_marker(populationMarkerID)
		return populationValues_schema.jsonify(values)

@bp.route("/population", methods=["POST"], strict_slashes=False)
def create_marker():
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
	if request.method == "POST":
		populationMarker = PopulationMarker.get(populationMarkerID)
		if populationMarker is None:
			return jsonify({"message": "The population marker to be added a value could not be found"}), 404
		json = request.get_json()
		json["populationMarkerID"] = int(populationMarkerID)
		populationValue, errors = populationValue_schema.load(data=json)
		if errors:
			return jsonify(errors), 400
		else:
			populationValue.save()
			result = make_json_value(populationValue=populationValue)
			return jsonify(result), 201

@bp.route("/population/<populationMarkerID>", methods=["DELETE"], strict_slashes=False)
def delete_marker(populationMarkerID):
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





def make_json_marker(populationMarker):
	result = populationMarker_schema.dump(populationMarker).data
	return result

def make_json_value(populationValue):
	result = populationValue_schema.dump(populationValue).data
	return result
