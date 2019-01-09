import json
from flask import (Blueprint, request, jsonify)
from datetime import datetime
from marshmallow import utils
from api.models.case import (Case, case_schema, cases_schema)

bp = Blueprint("stats", __name__, url_prefix="/api")

@bp.route("/stats", methods=["GET"], strict_slashes=False)
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

@bp.route("/stats/pigeonsSaved", methods=["GET"], strict_slashes=False)
def read_stats_pigeons_saved():
	if request.method == "GET":
		startTime = request.json["startTime"]
		untilTime = request.json["untilTime"]

		pigeonsSavedStat = Case.get_pigeons_saved_stat(startTime, untilTime)
		return str(pigeonsSavedStat)

@bp.route("/stats/pigeonsNotFound", methods=["GET"], strict_slashes=False)
def read_stats_pigeons_not_found():
	if request.method == "GET":
		startTime = request.json["startTime"]
		untilTime = request.json["untilTime"]

		pigeonsNotFoundStat = Case.get_pigeons_not_found_stat(startTime, untilTime)
		return str(pigeonsNotFoundStat)

@bp.route("/stats/pigeonsFoundDead", methods=["GET"], strict_slashes=False)
def read_stats_pigeons_found_dead():
	if request.method == "GET":
		startTime = request.json["startTime"]
		untilTime = request.json["untilTime"]

		pigeonsFoundDeadStat = Case.get_pigeons_found_dead_stat(startTime, untilTime)
		return str(pigeonsFoundDeadStat)

def convert_timestamp(unix):
	return utils.rfcformat(datetime.fromtimestamp(unix))