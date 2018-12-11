from flask import (Blueprint, request, jsonify)
from api.models.populationMarker import (PopulationMarker, populationMarker_schema, populationMarkers_schema)
from api.models.populationValue import (PopulationValue, populationValue_schema, populationValues_schema)

bp = Blueprint("population", __name__, url_prefix="/api")

@bp.route("/population", methods=["GET"], strict_slashes=False)
def get_Markers():
	if request.method == "GET":
		populationMarkers = PopulationMarker.all()
		result = [make_json(populationMarker = p) for p in populationMarkers]
		return jsonify(result)

@bp.route("/population/<populationMarkerID>", methods=["GET", "POST"], strict_slashes=False)
def get_or_put_value_for_marker(populationMarkerID):
	if request.method == "GET":
		return

@bp.route("/population", methods=["POST"], strict_slashes=False)
def create_marker():
	if request.method == "POST":
		json = request.get_json()
		print(json)
		populationMarker, errors = populationMarker_schema.load(data=json)
		if errors:
			return jsonify(errors), 400
		else:
			populationMarker.save()
			result = make_json_marker(populationMarker=populationMarker)
			return jsonify(result), 201

@bp.route("/population/<populationMarkerID>", methods=["POST"], strict_slashes=False)
def create_value_for_marker(populationMarkerID):
	if request.method == "PUT":
		json = request.get_json()
		populationValue, errors = populationValue_schema.load(data=json)
		if errors:
			return jsonify(errors), 400
		else:
			populationValue.save()
			result = make_json_marker(populationMarker=populationMarker)
			return jsonify(result), 201


def make_json_marker(populationMarker):
	result = populationMarker_schema.dump(populationMarker).data
	return result

def make_json_value(populationValue):
	result = populationValue_schema.dump(populationValue).data
	return result
