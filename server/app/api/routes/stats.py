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
		latNE = float(request.args.get("latNE"))
		lonNE = float(request.args.get("lonNE"))
		latSW = float(request.args.get("latSW"))
		lonSW = float(request.args.get("lonSW"))
		if untilTime is not None and fromTime is not None:
			try:
				untilTime = convert_timestamp(int(untilTime))
				fromTime = convert_timestamp(int(fromTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400
		values = PopulationMarker.get_stats(latNE=latNE, lonNE=lonNE, latSW=latSW, lonSW=lonSW, untilTime=untilTime, fromTime=fromTime)
		return jsonify(values), 200

@bp.route("/stats/pigeonNumbers", methods=["GET"], strict_slashes=False)
def read_pigeon_numbers():
	if request.method == "GET":
		untilTime = request.args.get("untilTime")
		fromTime = request.args.get("fromTime")
		latNE = float(request.args.get("latNE"))
		lonNE = float(request.args.get("lonNE"))
		latSW = float(request.args.get("latSW"))
		lonSW = float(request.args.get("lonSW"))

		if latNE is None or lonNE is None or latSW is None or lonSW is None:
			return jsonify(message="Provide coordinates correctly"), 400

		if untilTime is not None and fromTime is not None:
			try:
				untilTime = convert_timestamp(int(untilTime))
				fromTime = convert_timestamp(int(fromTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400

			pigeonNumbers = Case.get_pigeon_numbers(fromTime=fromTime, untilTime=untilTime, latNE=latNE, lonNE=lonNE, latSW=latSW, lonSW=lonSW)

		elif untilTime is not None:
			try:
				untilTime = convert_timestamp(int(untilTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400

			pigeonNumbers = Case.get_pigeon_numbers(untilTime=untilTime, latNE=latNE, lonNE=lonNE, latSW=latSW, lonSW=lonSW)

		elif fromTime is not None:
			try:
				fromTime = convert_timestamp(int(fromTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400

			pigeonNumbers = Case.get_pigeon_numbers(fromTime=fromTime, latNE=latNE, lonNE=lonNE, latSW=latSW, lonSW=lonSW)

		else:
			pigeonNumbers = Case.get_pigeon_numbers(latNE=latNE, lonNE=lonNE, latSW=latSW, lonSW=lonSW)

		return jsonify(pigeonNumbers), 200

@bp.route("/stats/breed", methods=["GET"], strict_slashes=False)
def read_breed():
	if request.method == "GET":
		untilTime = request.args.get("untilTime")
		fromTime = request.args.get("fromTime")
		latNE = float(request.args.get("latNE"))
		lonNE = float(request.args.get("lonNE"))
		latSW = float(request.args.get("latSW"))
		lonSW = float(request.args.get("lonSW"))

		if latNE is None or lonNE is None or latSW is None or lonSW is None:
			return jsonify(message="Provide coordinates correctly"), 400

		if untilTime is not None and fromTime is not None:
			try:
				untilTime = convert_timestamp(int(untilTime))
				fromTime = convert_timestamp(int(fromTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400

			breed = Case.get_breed(latNE=latNE, latSW=latSW, lonNE=lonNE, lonSW=lonSW, fromTime=fromTime, untilTime=untilTime)

		elif untilTime is not None:
			try:
				untilTime = convert_timestamp(int(untilTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400

			breed = Case.get_breed(latNE=latNE, latSW=latSW, lonNE=lonNE, lonSW=lonSW, untilTime=untilTime)

		elif fromTime is not None:
			try:
				fromTime = convert_timestamp(int(fromTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400

			breed = Case.get_breed(latNE=latNE, latSW=latSW, lonNE=lonNE, lonSW=lonSW, fromTime=fromTime)

		else:
			breed = Case.get_breed(latNE=latNE, latSW=latSW, lonNE=lonNE, lonSW=lonSW)

		return jsonify(breed), 200

@bp.route("/stats/injury", methods=["GET"], strict_slashes=False)
def read_injury():
	if request.method == "GET":
		untilTime = request.args.get("untilTime")
		fromTime = request.args.get("fromTime")
		latNE = float(request.args.get("latNE"))
		lonNE = float(request.args.get("lonNE"))
		latSW = float(request.args.get("latSW"))
		lonSW = float(request.args.get("lonSW"))

		if latNE is None or lonNE is None or latSW is None or lonSW is None:
			return jsonify(message="Provide coordinates correctly"), 400

		if untilTime is not None and fromTime is not None:
			try:
				untilTime = convert_timestamp(int(untilTime))
				fromTime = convert_timestamp(int(fromTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400	

			injury = Case.get_injury(latNE=latNE, latSW=latSW, lonNE=lonNE, lonSW=lonSW, fromTime=fromTime, untilTime=untilTime)

		elif untilTime is not None:
			try:
				untilTime = convert_timestamp(int(untilTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400	
				
			injury = Case.get_injury(latNE=latNE, latSW=latSW, lonNE=lonNE, lonSW=lonSW, untilTime=untilTime)
		
		elif fromTime is not None:
			try:
				fromTime = convert_timestamp(int(fromTime))
			except ValueError:
				return jsonify(message="Unix timestamp out of range"), 400	
				
			injury = Case.get_injury(latNE=latNE, latSW=latSW, lonNE=lonNE, lonSW=lonSW, fromTime=fromTime)
		
		else:
			injury = Case.get_injury(latNE=latNE, latSW=latSW, lonNE=lonNE, lonSW=lonSW)
			
		return jsonify(injury), 200

def convert_timestamp(unix):
	return utils.rfcformat(datetime.fromtimestamp(unix))