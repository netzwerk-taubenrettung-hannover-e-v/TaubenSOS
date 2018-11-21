from flask import (Blueprint, request)
from api.models.population import (Population, population_schema, populations_schema)

bp = Blueprint("population", __name__, url_prefix="/api")

@bp.route("/population", methods=["GET"], strict_slashes=False)
def read_stats():
	if request.method == "GET":
		populations = Population.all()
		return populations_schema.jsonify(populations)

@bp.route("population", methods=["POST"], strict_slashes=False)
def create_stat():
	if request.method == "POST":
		pigeoncount = request.json["pigeoncount"]
		latitude = request.json["latitude"]
		longitude = request.json["longitude"]

		population = Population(pigeoncount=pigeoncount,
					latitude=latitude,
					longitude=longitude)
		population.save()
		print(population)
		return population_schema.jsonify(population), 201
