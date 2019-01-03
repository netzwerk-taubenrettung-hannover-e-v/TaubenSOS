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
		data = request.get_json()
		if data.get("lastUpdate") is not None:
			cases = Case.get_newly_closed_cases(convert_timestamp(int(data.get("lastUpdate"))))
		else:
			cases = Case.get_all_closed_cases()
		result = [make_json_case(case = c) for c in cases]
		return jsonify(result)
	

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

def make_json_case(case):
	result = case_schema.dump(case).data
	return result