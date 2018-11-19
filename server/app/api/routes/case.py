from flask import (Blueprint, request)

from api.models.case import (Case, case_schema, cases_schema)

bp = Blueprint("case", __name__, url_prefix="/api")

@bp.route("/case", methods=["GET"], strict_slashes=False)
def read_cases():
    if request.method == "GET":
        cases = Case.all()
        return cases_schema.jsonify(cases)

@bp.route("/case", methods=["POST"], strict_slashes=False)
def create_case():
	if request.method == "POST":
		timestamp = request.json["timestamp"]
		priority = request.json["priority"]
		isCarrierPigeon = request.json["isCarrierPigeon"]
		isWeddingPigeon = request.json["isWeddingPigeon"]
		additionalInfo = request.json["additionalInfo"]
		phone = request.json["phone"]
		latitude = request.json["latitude"]
		longitude = request.json["longitude"]

		case = Case(timestamp=timestamp,
					priority=priority,
					isCarrierPigeon=isCarrierPigeon,
					isWeddingPigeon=isWeddingPigeon,
					additionalInfo=additionalInfo,
					phone=phone,
					latitude=latitude,
					longitude=longitude,
					isClosed=False,
					media1=None,
					media2=None,
					media3=None,
					rescuer=None,
					wasFoundDead=None)
		case.save()
		return case_schema.jsonify(case), 201

@bp.route("/case/<caseID>", methods=["GET"], strict_slashes=False)
def read_case(caseID):
    if request.method == "GET":
        case = Case.get(caseID)
        return case_schema.jsonify(case)