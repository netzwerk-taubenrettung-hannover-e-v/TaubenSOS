from flask import Blueprint, request, jsonify
import boto3, uuid, os, filetype, tempfile, cv2, threading

from api.models.case import Case, case_schema, cases_schema
from api.models.injury import Injury, injury_schema
from api.models.medium import Medium, medium_schema, media_schema

bp = Blueprint("case", __name__, url_prefix="/api")

media_bucket_name = os.getenv("AWS_S3_MEDIA_BUCKET_NAME", default="media")
media_bucket_location = boto3.client("s3").get_bucket_location(Bucket=media_bucket_name)
s3 = boto3.client("s3", region_name=media_bucket_location["LocationConstraint"])

@bp.route("/case", methods=["GET"], strict_slashes=False)
def read_cases():
	"""
	file: ../../docs/case/read_all.yml
	"""
	cases = Case.all()
	return cases_schema.jsonify(cases), 200

@bp.route("/case", methods=["POST"], strict_slashes=False)
def create_case():
	"""
	file: ../../docs/case/create.yml
	"""
	json = request.get_json()
	case, errors = case_schema.load(json)
	if errors:
		return jsonify(errors), 400
	case.save()
	return case_schema.jsonify(case), 201

@bp.route("/case/<int:caseID>", methods=["GET"], strict_slashes=False)
def read_case(caseID):
	"""
	file: ../../docs/case/read.yml
	"""
	case = Case.get(caseID)
	if case is None:
		return jsonify(message="The case to be shown could not be found"), 404
	return case_schema.jsonify(case), 200

@bp.route("/case/<int:caseID>", methods=["PUT"], strict_slashes=False)
def update_case(caseID):
	"""
	file: ../../docs/case/update.yml
	"""
	case = Case.get(caseID)
	if case is None:
		return jsonify(message="The case to be updated could not be found"), 404
	json = request.get_json()
	errors = case_schema.validate(json, partial=True)
	if errors:
		return jsonify(errors), 400
	case.update(**json)
	return case_schema.jsonify(case), 200

@bp.route("/case/<int:caseID>", methods=["DELETE"], strict_slashes=False)
def delete_case(caseID):
	"""
	file: ../../docs/case/delete.yml
	"""
	case = Case.get(caseID)
	if case is None:
		return jsonify(message="The case to be deleted could not be found"), 404
	for medium in case.media:
		s3.delete_object(Bucket=media_bucket_name, Key=medium.uri)
		if medium.thumbnail is not None:
			s3.delete_object(Bucket=media_bucket_name, Key=medium.thumbnail)
	case.delete()
	return "", 204, {"Content-Type": "application/json"}

@bp.route("/case/<int:caseID>/media", methods=["GET"], strict_slashes=False)
def get_media_for_case(caseID):
	"""
	file: ../../docs/case/get_media.yml
	"""
	case = Case.get(caseID)
	if case is None:
		return jsonify(message="The case you referred to could not be found"), 404
	return media_schema.jsonify(case.media), 200

@bp.route("/case/<int:caseID>/media", methods=["POST"], strict_slashes=False)
def add_medium_to_case(caseID):
	"""
	file: ../../docs/case/add_medium.yml
	"""
	case = Case.get(caseID)
	if case is None:
		return jsonify(message="The case to add media to could not be found"), 404
	if not request.data:
		return jsonify(message="Empty body"), 400
	if request.content_length > request.max_content_length:
		return jsonify(message="Object to be added is too large"), 413
	data = request.get_data()
	medium = Medium(caseID=caseID, mimeType=filetype.guess_mime(data))
	if filetype.image(data) is not None:
		medium.uri = "photos/" + str(uuid.uuid4()) + "." + filetype.guess_extension(data)
	elif filetype.video(data) is not None:
		medium.uri = "videos/" + str(uuid.uuid4()) + "." + filetype.guess_extension(data)
		medium.thumbnail = "thumbnails/" + str(uuid.uuid4()) + ".png"
		threading.Thread(target=generate_thumbnail_for_video, args=(data, medium.thumbnail)).start()
	else:
		return jsonify(message="Media format not supported"), 415
	threading.Thread(target=s3.put_object, kwargs=dict(Body=data, Bucket=media_bucket_name, Key=medium.uri)).start()
	medium.save()
	return medium_schema.jsonify(medium), 200

@bp.route("/case/<int:caseID>/media/<int:mediaID>", methods=["PUT"], strict_slashes=False)
def update_medium_for_case(caseID, mediaID):
	"""
	file: ../../docs/case/update_medium.yml
	"""
	medium = Medium.get(caseID, mediaID)
	if medium is None:
		return jsonify(message="The medium to be updated could not be found"), 404
	if not request.data:
		return jsonify(message="Empty body"), 400
	if request.content_length > request.max_content_length:
		return jsonify(message="Object you were about to upload is too large"), 413
	data = request.get_data()
	old_uri = medium.uri
	old_thumbnail = medium.thumbnail
	if filetype.image(data) is not None:
		medium.uri = "photos/" + str(uuid.uuid4()) + "." + filetype.guess_extension(data)
		medium.thumbnail = None
	elif filetype.video(data) is not None:
		medium.uri = "videos/" + str(uuid.uuid4()) + "." + filetype.guess_extension(data)
		medium.thumbnail = "thumbnails/" + str(uuid.uuid4()) + ".png"
		threading.Thread(target=generate_thumbnail_for_video, args=(data, medium.thumbnail)).start()
	else:
		return jsonify(message="Media format not supported"), 415
	medium.mimeType = filetype.guess_mime(data)
	s3.delete_object(Bucket=media_bucket_name, Key=old_uri)
	if old_thumbnail is not None:
		s3.delete_object(Bucket=media_bucket_name, Key=old_thumbnail)
	threading.Thread(target=s3.put_object, kwargs=dict(Body=data, Bucket=media_bucket_name, Key=medium.uri)).start()
	medium.update()
	return medium_schema.jsonify(medium), 200

@bp.route("/case/<int:caseID>/media/<int:mediaID>", methods=["GET"], strict_slashes=False)
def get_medium_for_case(caseID, mediaID):
	"""
	file: ../../docs/case/get_medium.yml
	"""
	medium = Medium.get(caseID, mediaID)
	if medium is None:
		return jsonify(message="The medium to be shown could not be found"), 404
	return s3.get_object(Bucket=media_bucket_name, Key=medium.uri).get("Body").read(), 200, {"Content-Type": medium.mimeType}

@bp.route("/case/<int:caseID>/media/<int:mediaID>", methods=["DELETE"], strict_slashes=False)
def remove_medium_from_case(caseID, mediaID):
	"""
	file: ../../docs/case/remove_medium.yml
	"""
	medium = Medium.get(caseID, mediaID)
	if medium is None:
		return jsonify(message="The medium to be removed could not be found"), 404
	s3.delete_object(Bucket=media_bucket_name, Key=medium.uri)
	if medium.thumbnail is not None:
		s3.delete_object(Bucket=media_bucket_name, Key=medium.thumbnail)
	medium.delete()
	return "", 204, {"Content-Type": "application/json"}

@bp.route("/case/<int:caseID>/media/<int:mediaID>/thumbnail", methods=["GET"], strict_slashes=False)
def get_thumbnail_for_video(caseID, mediaID):
	"""
	file: ../../docs/case/get_thumbnail.yml
	"""
	medium = Medium.get(caseID, mediaID)
	if medium is None:
		return jsonify(message="The medium to show the thumbnail for could not be found"), 404
	if medium.thumbnail is None:
		return jsonify(message="The medium you referred to is not a video and thus does not have a thumbnail associated with it"), 404
	return s3.get_object(Bucket=media_bucket_name, Key=medium.thumbnail).get("Body").read(), 200, {"Content-Type": "image/png"}

def generate_thumbnail_for_video(video, uri):
	with tempfile.NamedTemporaryFile() as fp:
		fp.write(video)
		vcap = cv2.VideoCapture(fp.name)
		ret, img = vcap.read()
		ret, buf = cv2.imencode(".png", img)
		s3.put_object(Body=buf.tostring(), Bucket=media_bucket_name, Key=uri)
		vcap.release()