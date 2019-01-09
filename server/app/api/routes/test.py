from flask import (Blueprint, request)

from api.models.test import (Test, test_schema, tests_schema)

bp = Blueprint("test", __name__, url_prefix="/api")

@bp.route("/test", methods=["GET"])
def tests():
    if request.method == "GET":
        tests = Test.all()
        return tests_schema.jsonify(tests)

@bp.route("/test/<number>", methods=["GET"])
def test_detail(number):
    if request.method == "GET":
        test = Test.get(number)
        return test_schema.jsonify(test)