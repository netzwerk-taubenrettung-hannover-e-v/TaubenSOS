from flask import (Blueprint, request)
from api.models.case import (Case, case_schema, cases_schema)
from api.models.population import(Population, population_schema, populations_schema)

bp = Blueprint("stats", __name__, url_prefix="/api")

@bp.route("/stats/pigeonsSaved", methods=["GET"], strict_slashes=False)
def read_stats_pigeons_saved():
    if request.method == "GET":
        startTime = request.json["startTime"]
        untilTime = request.json["untilTime"]

        pigeonsSavedStat = Case.get_pigeons_saved_stat(startTime, untilTime)
        return cases_schema.jsonify(pigeonsSavedStat)

@bp.route("/stats/pigeonsNotFound", methods=["GET"], strict_slashes=False)
def read_stats_pigeons_not_found():
    if request.method == "GET":
        startTime = request.json["startTime"]
        untilTime = request.json["untilTime"]

        pigeonsNotFoundStat = Case.get_pigeons_not_found_stat(startTime, untilTime)
        return cases_schema.jsonify(pigeonsNotFoundStat)

@bp.route("/stats/pigeonsFoundDead", methods=["GET"], strict_slashes=False)
def read_stats_pigeons_found_dead():
    if request.method == "GET":
        startTime = request.json["startTime"]
        untilTime = request.json["untilTime"]

        pigeonsFoundDeadStat = Case.get_pigeons_found_dead_stat(startTime, untilTime)
        return cases_schema.jsonify(pigeonsFoundDeadStat)

@bp.route("/stats/population", methods=["GET"], strict_slashes=False)
def calculate_population():
    if request.method == "GET":
        startTime = request.json["startTime"]
        untilTime = request.json["untilTime"]
        latTopLeft = request.json["latTopLeft"]
        lonTopLeft = request.json["lonTopLeft"]
        latBotRight = request.json["latBotRight"]
        lonBotRight = request.json["lonBotRight"]

        pigeonCountStats = Population.get_pigeon_count(startTime, untilTime, latTopLeft, lonTopLeft, latBotRight, lonBotRight)
        return populations_schema.jsonify(pigeonCountStats)