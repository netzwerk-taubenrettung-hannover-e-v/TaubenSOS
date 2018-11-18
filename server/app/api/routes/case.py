from flask import (Blueprint, request)

from api.models.case import (Case, case_schema, cases_schema)

bp = Blueprint("case", __name__, url_prefix="/api")

@bp.route("/case", methods=["GET"], strict_slashes=False)
def cases():
    if request.method == "GET":
        cases = Case.all()
        return cases_schema.jsonify(cases)

@bp.route("/case/<caseID>", methods=["GET"], strict_slashes=False)
def case_detail(caseID):
    if request.method == "GET":
        case = Case.get(caseID)
        return case_schema.jsonify(case)