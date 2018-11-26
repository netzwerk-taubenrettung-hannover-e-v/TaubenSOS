from flask import (Blueprint, request)

from api.models.case import (Case, case_schema, cases_schema)
from api.models.injury import (Injury, injury_schema)

bp = Blueprint("case", __name__, url_prefix="/api")

@bp.route("/case", methods=["GET"], strict_slashes=False)
def read_cases():
    if request.method == "GET":
        cases = Case.all()
        return cases_schema.jsonify(cases)

@bp.route("/case", methods=["POST"], strict_slashes=False)
def create_case():
	if request.method == "POST":
		timestamp = request.json.get("timestamp")
		priority = request.json.get("priority")
		isCarrierPigeon = request.json.get("isCarrierPigeon")
		isWeddingPigeon = request.json.get("isWeddingPigeon")
		additionalInfo = request.json.get("additionalInfo")
		phone = request.json.get("phone")
		latitude = request.json.get("latitude")
		longitude = request.json.get("longitude")

		case = Case(timestamp=timestamp,
					priority=priority,
					isCarrierPigeon=isCarrierPigeon,
					isWeddingPigeon=isWeddingPigeon,
					additionalInfo=additionalInfo,
					phone=phone,
					latitude=latitude,
					longitude=longitude,
					injury=injury_schema.load(request.get_json().get("injury")).data,
					isClosed=None,
					rescuer=None,
					wasFoundDead=None)
		case.save()
		return case_schema.jsonify(case), 201

@bp.route("/case/<caseID>", methods=["GET"], strict_slashes=False)
def read_case(caseID):
    if request.method == "GET":
        case = Case.get(caseID)
        return case_schema.jsonify(case)