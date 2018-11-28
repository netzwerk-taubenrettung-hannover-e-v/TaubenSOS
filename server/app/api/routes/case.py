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
		case, errors = case_schema.load(request.get_json())
		if errors:
			return str(errors), 500
		else:
			case.save()
			return case_schema.jsonify(case), 201

@bp.route("/case/<caseID>", methods=["GET"], strict_slashes=False)
def read_case(caseID):
    if request.method == "GET":
        case = Case.get(caseID)
        return case_schema.jsonify(case)