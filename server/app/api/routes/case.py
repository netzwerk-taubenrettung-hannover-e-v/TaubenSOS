from flask import (Blueprint, request, jsonify)
import boto3, uuid

from api.models.case import (Case, case_schema, cases_schema)
from api.models.injury import (Injury, injury_schema)
from api.models.medium import (Medium, medium_schema)

bp = Blueprint("case", __name__, url_prefix="/api")

@bp.route("/case", methods=["GET"], strict_slashes=False)
def read_cases():
	"""
	file: ../../docs/case/read_all.yml
	"""
	if request.method == "GET":
		cases = Case.all()
		result = [generate_media_urls(Case=c, ClientMethod="get_object") for c in cases]
		return jsonify(result)

@bp.route("/case", methods=["POST"], strict_slashes=False)
def create_case():
	"""
	file: ../../docs/case/create.yml
	"""
	if request.method == "POST":
		json = request.get_json()
		if json.get("media") is not None:
			json.get("media")[:] = [medium_schema.dump(Medium("photos/" + str(uuid.uuid4()) + "-" + m)).data for m in json.get("media")]
		case, errors = case_schema.load(json)
		if errors:
			return jsonify(errors), 400
		else:
			case.save()
			result = generate_media_urls(Case=case, ClientMethod="put_object")
			return jsonify(result), 201

@bp.route("/case/<caseID>", methods=["GET"], strict_slashes=False)
def read_case(caseID):
	"""
	file: ../../docs/case/read.yml
	"""
	if request.method == "GET":
		case = Case.get(caseID)
		if case is None:
			return jsonify({"message": "The case to be shown could not be found"}), 404
		result = generate_media_urls(Case=case, ClientMethod="get_object")
		return jsonify(result)

@bp.route("/case/<caseID>", methods=["PUT"], strict_slashes=False)
def update_case(caseID):
	"""
	file: ../../docs/case/update.yml
	"""
	if request.method == "PUT":
		json = request.get_json()
		if json.get("media") is not None:
			json.get("media")[:] = [medium_schema.dump(Medium("photos/" + str(uuid.uuid4()) + "-" + m)).data for m in json.get("media")]
		case = Case.get(caseID)
		if case is None:
			return jsonify({"message": "The case to be updated could not be found"}), 404
		errors = case_schema.validate(json, partial=True)
		if errors:
			return jsonify(errors), 400
		case.update(**json)
		result = generate_media_urls(Case=case, ClientMethod="put_object")
		return jsonify(result), 200


@bp.route("/case/<caseID>", methods=["DELETE"], strict_slashes=False)
def delete_case(caseID):
	"""
	file: ../../docs/case/delete.yml
	"""
	if request.method == "DELETE":
		case = Case.get(caseID)
		if case is None:
			return jsonify({"message": "The case to be deleted could not be found"}), 404
		case.delete()
		s3 = boto3.client("s3")
		for m in case.media:
			s3.delete_object(Bucket="tauben2", Key=m.uri)
		return "", 204, {"Content-Type": "application/json"}

def generate_media_urls(Case, ClientMethod):
	result = case_schema.dump(Case).data
	s3 = boto3.client("s3")
	result.get("media")[:] = [s3.generate_presigned_url(ClientMethod=ClientMethod, Params={"Bucket": "tauben2", "Key": m.get("uri")}, ExpiresIn=600) for m in result.get("media")]
	return result