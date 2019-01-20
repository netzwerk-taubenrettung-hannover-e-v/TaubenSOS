import json
from flask import (Blueprint, request, jsonify)
from datetime import datetime
from marshmallow import utils
from api.models.populationMarker import (PopulationMarker, populationMarker_schema, populationMarkers_schema)
from api.models.populationValue import (PopulationValue, populationValue_schema, populationValues_schema)
from api.models.injury import (Injury, injury_schema, injuries_schema)
from api.models.breed import (Breed)
from api.models.case import (Case, case_schema, cases_schema)

bp = Blueprint("stats", __name__, url_prefix="/api")

@bp.route("/stats/case", methods=["GET"], strict_slashes=False)
def get_closed_cases():
	"""
	file: ../../docs/stats/read_closed_cases.yml
	"""
	if request.method == "GET":
		untilTime = request.args.get("untilTime")
		fromTime = request.args.get("fromTime")
		if untilTime is not None and fromTime is not None:
			try:
				untilTime = convert_timestamp(int(untilTime))
				fromTime = convert_timestamp(int(fromTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400
			cases = Case.get_closed_cases(fromTime=fromTime, untilTime=untilTime)
		elif untilTime is not None:
			try:
				untilTime = convert_timestamp(int(untilTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400
			cases = Case.get_closed_cases(untilTime=untilTime)
		elif fromTime is not None:
			try:
				fromTime = convert_timestamp(int(fromTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400
			cases = Case.get_closed_cases(fromTime=fromTime)
		else:
			cases = Case.get_closed_cases()
		return cases_schema.jsonify(cases), 200

@bp.route("/stats/population", methods=["GET"], strict_slashes=False)
def get_population_stats():
	if request.method == "GET":
		untilTime = request.args.get("untilTime")
		fromTime = request.args.get("fromTime")
		latNW = float(request.args.get("latNW"))
		lonNW = float(request.args.get("lonNW"))
		latSE = float(request.args.get("latSE"))
		lonSE = float(request.args.get("lonSE"))

		if latNW is None or lonNW is None or latSE is None or lonSE is None:
			return jsonify(message="Provide coordinates correctly"), 400

		if untilTime is not None and fromTime is not None:
			try:
				untilTime = convert_timestamp(int(untilTime))
				fromTime = convert_timestamp(int(fromTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400
			
			values = PopulationMarker.get_stats(latNW=latNW, lonNW=lonNW, latSE=latSE, lonSE=lonSE, untilTime=untilTime, fromTime=fromTime)

		elif untilTime is not None:
			try:
				untilTime = convert_timestamp(int(untilTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400
			
			values = PopulationMarker.get_stats(latNW=latNW, lonNW=lonNW, latSE=latSE, lonSE=lonSE, untilTime=untilTime)
		
		elif fromTime is not None:
			try:
				fromTime = convert_timestamp(int(fromTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400
			
			values = PopulationMarker.get_stats(latNW=latNW, lonNW=lonNW, latSE=latSE, lonSE=lonSE, fromTime=fromTime)

		else:
			values = PopulationMarker.get_stats(latNW=latNW, lonNW=lonNW, latSE=latSE, lonSE=lonSE)

		return jsonify(values), 200

@bp.route("/stats/pigeonNumbers", methods=["GET"], strict_slashes=False)
def read_pigeon_numbers():
	if request.method == "GET":
		untilTime = request.args.get("untilTime")
		fromTime = request.args.get("fromTime")
		latNW = float(request.args.get("latNW"))
		lonNW = float(request.args.get("lonNW"))
		latSE = float(request.args.get("latSE"))
		lonSE = float(request.args.get("lonSE"))

		if latNW is None or lonNW is None or latSE is None or lonSE is None:
			return jsonify(message="Provide coordinates correctly"), 400

		if untilTime is not None and fromTime is not None:
			try:
				untilTime = convert_timestamp(int(untilTime))
				fromTime = convert_timestamp(int(fromTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400

			pigeonNumbers = Case.get_pigeon_numbers(fromTime=fromTime, untilTime=untilTime, latNW=latNW, lonNW=lonNW, latSE=latSE, lonSE=lonSE)

		elif untilTime is not None:
			try:
				untilTime = convert_timestamp(int(untilTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400

			pigeonNumbers = Case.get_pigeon_numbers(untilTime=untilTime, latNW=latNW, lonNW=lonNW, latSE=latSE, lonSE=lonSE)

		elif fromTime is not None:
			try:
				fromTime = convert_timestamp(int(fromTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400

			pigeonNumbers = Case.get_pigeon_numbers(fromTime=fromTime, latNW=latNW, lonNW=lonNW, latSE=latSE, lonSE=lonSE)

		else:
			pigeonNumbers = Case.get_pigeon_numbers(latNW=latNW, lonNW=lonNW, latSE=latSE, lonSE=lonSE)

		return jsonify(pigeonNumbers), 200

@bp.route("/stats/breed", methods=["GET"], strict_slashes=False)
def read_breed():
	if request.method == "GET":
		untilTime = request.args.get("untilTime")
		fromTime = request.args.get("fromTime")
		latNW = float(request.args.get("latNW"))
		lonNW = float(request.args.get("lonNW"))
		latSE = float(request.args.get("latSE"))
		lonSE = float(request.args.get("lonSE"))

		if latNW is None or lonNW is None or latSE is None or lonSE is None:
			return jsonify(message="Provide coordinates correctly"), 400

		if untilTime is not None and fromTime is not None:
			try:
				untilTime = convert_timestamp(int(untilTime))
				fromTime = convert_timestamp(int(fromTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400

			breed = Case.get_breed(latNW=latNW, latSE=latSE, lonNW=lonNW, lonSE=lonSE, fromTime=fromTime, untilTime=untilTime)

		elif untilTime is not None:
			try:
				untilTime = convert_timestamp(int(untilTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400

			breed = Case.get_breed(latNW=latNW, latSE=latSE, lonNW=lonNW, lonSE=lonSE, untilTime=untilTime)

		elif fromTime is not None:
			try:
				fromTime = convert_timestamp(int(fromTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400

			breed = Case.get_breed(latNW=latNW, latSE=latSE, lonNW=lonNW, lonSE=lonSE, fromTime=fromTime)

		else:
			breed = Case.get_breed(latNW=latNW, latSE=latSE, lonNW=lonNW, lonSE=lonSE)

		return jsonify(breed), 200

@bp.route("/stats/injury", methods=["GET"], strict_slashes=False)
def read_injury():
	if request.method == "GET":
		untilTime = request.args.get("untilTime")
		fromTime = request.args.get("fromTime")
		latNW = float(request.args.get("latNW"))
		lonNW = float(request.args.get("lonNW"))
		latSE = float(request.args.get("latSE"))
		lonSE = float(request.args.get("lonSE"))

		if latNW is None or lonNW is None or latSE is None or lonSE is None:
			return jsonify(message="Provide coordinates correctly"), 400

		if untilTime is not None and fromTime is not None:
			try:
				untilTime = convert_timestamp(int(untilTime))
				fromTime = convert_timestamp(int(fromTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400	

			injury = Case.get_injury(latNW=latNW, latSE=latSE, lonNW=lonNW, lonSE=lonSE, fromTime=fromTime, untilTime=untilTime)

		elif untilTime is not None:
			try:
				untilTime = convert_timestamp(int(untilTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400	
				
			injury = Case.get_injury(latNW=latNW, latSE=latSE, lonNW=lonNW, lonSE=lonSE, untilTime=untilTime)
		
		elif fromTime is not None:
			try:
				fromTime = convert_timestamp(int(fromTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400	
				
			injury = Case.get_injury(latNW=latNW, latSE=latSE, lonNW=lonNW, lonSE=lonSE, fromTime=fromTime)
		
		else:
			injury = Case.get_injury(latNW=latNW, latSE=latSE, lonNW=lonNW, lonSE=lonSE)
			
		return jsonify(injury), 200

def convert_timestamp(unix):
	return utils.rfcformat(datetime.fromtimestamp(unix))