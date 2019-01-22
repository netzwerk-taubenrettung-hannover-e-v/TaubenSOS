from api import db, ma, spec
from api.models import injury, medium, user, breed
from api.models.breed import Breed
from api.models.injury import Injury
from datetime import datetime, timedelta
from sqlalchemy import text, bindparam
from marshmallow import post_dump, pre_load, post_load, utils, validate

class Case(db.Model):
    __tablename__ = "case"
    caseID = db.Column(db.Integer, primary_key=True)
    timestamp = db.Column(db.DateTime, nullable=False, default=datetime.utcnow)
    lastEdited = db.Column(db.DateTime, nullable=False, default=datetime.utcnow)
    priority = db.Column(db.Integer, nullable=False)
    reporter = db.Column(db.String(20), db.ForeignKey("user.username"), nullable=True)
    rescuer = db.Column(db.String(20), db.ForeignKey("user.username"), nullable=True)
    breed = db.Column(db.String(20), db.ForeignKey("breed"), nullable=True)
    additionalInfo = db.Column(db.String, nullable=True)
    phone = db.Column(db.String(20), nullable=False)
    latitude = db.Column(db.Float, nullable=False)
    longitude = db.Column(db.Float, nullable=False)
    wasFoundDead = db.Column(db.Boolean, nullable=True)
    wasNotFound = db.Column(db.Boolean, nullable=True)
    isClosed = db.Column(db.Boolean, nullable=False)
    injury = db.relationship("Injury", cascade="all, delete-orphan", backref="case", lazy=True, uselist=False)
    media = db.relationship("Medium", cascade="all, delete-orphan", backref="case", lazy=True, uselist=True)

    def __init__(self, timestamp, priority, reporter, rescuer, breed, additionalInfo, phone, latitude, longitude, wasFoundDead, wasNotFound, isClosed, injury):
        self.timestamp = timestamp
        self.priority = priority
        self.reporter = reporter
        self.rescuer = rescuer
        self.breed = breed
        self.additionalInfo = additionalInfo
        self.phone = phone
        self.latitude = latitude
        self.longitude = longitude
        self.wasFoundDead = wasFoundDead
        self.wasNotFound = wasNotFound
        self.isClosed = isClosed
        self.injury = injury

    def save(self):
        db.session.add(self)
        db.session.commit()

    def update(self, **kwargs):
        kwargs.pop("caseID", None)
        kwargs.pop("media", None)
        for key, value in kwargs.items():
            if key == "injury":
                value = injury.injury_schema.load(value).data
            setattr(self, key, value)
        self.lastEdited = datetime.utcnow()
        db.session.commit()

    def delete(self):
        db.session.delete(self)
        db.session.commit()

    def __repr__(self):
        return "<Case: {}>".format(self.caseID)

    @staticmethod
    def all():
        return Case.query.order_by(Case.timestamp.desc())

    @staticmethod
    def recents():
        since = datetime.utcnow() - timedelta(hours=24)
        except_query = Case.query.filter(Case.isClosed == True, Case.lastEdited < since)
        return Case.query.except_(except_query).order_by(Case.timestamp.desc())

    @staticmethod
    def get(caseID):
        return Case.query.get(caseID)

    @staticmethod
    def get_closed_cases(fromTime=None, untilTime=None):
        if fromTime is not None and untilTime is not None:
            return db.session.query(Case).filter(db.and_(Case.timestamp > fromTime, Case.timestamp < untilTime, Case.isClosed == True))
        elif fromTime is not None:
            return db.session.query(Case).filter(db.and_(Case.timestamp > fromTime, Case.isClosed == True))
        elif untilTime is not None:
            return db.session.query(Case).filter(db.and_(Case.timestamp < untilTime, Case.isClosed == True))
        else:
            return db.session.query(Case).filter(Case.isClosed == True)

    @staticmethod
    def get_pigeon_numbers(latNE, lonNE, latSW, lonSW, fromTime=None, untilTime=None):
        if fromTime is not None and untilTime is not None:
            sql = text('select date("timestamp") as "day", sum(case when "wasFoundDead" = TRUE then 1 else 0 end) as "sumFoundDead", sum(case when "wasNotFound" = TRUE then 1 else 0 end) as "sumNotFound", count("caseID") from "case" where "isClosed" = TRUE and "latitude" between :latSW and :latNE and "longitude" between :lonSW and :lonNE and "timestamp" between :fromTime and :untilTime group by "day" order by "day"')
            sql = sql.bindparams(latSW=latSW, latNE=latNE, lonSW=lonSW, lonNE=lonNE, fromTime=fromTime, untilTime=untilTime)
        elif fromTime is not None:
            sql = text('select date("timestamp") as "day", sum(case when "wasFoundDead" = TRUE then 1 else 0 end) as "sumFoundDead", sum(case when "wasNotFound" = TRUE then 1 else 0 end) as "sumNotFound", count("caseID") from "case" where "isClosed" = TRUE and "latitude" between :latSW and :latNE and "longitude" between :lonSW and :lonNE and "timestamp" > :fromTime group by "day" order by "day"')
            sql = sql.bindparams(latSW=latSW, latNE=latNE, lonSW=lonSW, lonNE=lonNE, fromTime=fromTime)
        elif untilTime is not None:
            sql = text('select date("timestamp") as "day", sum(case when "wasFoundDead" = TRUE then 1 else 0 end) as "sumFoundDead", sum(case when "wasNotFound" = TRUE then 1 else 0 end) as "sumNotFound", count("caseID") from "case" where "isClosed" = TRUE and "latitude" between :latSW and :latNE and "longitude" between :lonSW and :lonNE and "timestamp" < :untilTime group by "day" order by "day"')
            sql = sql.bindparams(latSW=latSW, latNE=latNE, lonSW=lonSW, lonNE=lonNE, untilTime=untilTime)
        else:
            sql = text('select date("timestamp") as "day", sum(case when "wasFoundDead" = TRUE then 1 else 0 end) as "sumFoundDead", sum(case when "wasNotFound" = TRUE then 1 else 0 end) as "sumNotFound", count("caseID") from "case" where "isClosed" = TRUE and "latitude" between :latSW and :latNE and "longitude" between :lonSW and :lonNE group by "day" order by "day"')
            sql = sql.bindparams(latSW=latSW, latNE=latNE, lonSW=lonSW, lonNE=lonNE)
        result = db.engine.execute(sql)
        res = result.fetchall()
        x = []
        for i in res:
            x.append(dict(i.items()))
        for i in x:
            i['day'] = utils.from_rfc(str(i['day'])).strftime("%s")
        return x

    @staticmethod
    def get_breed(latNE, lonNE, latSW, lonSW, fromTime=None, untilTime=None):
        if fromTime is not None and untilTime is not None:
            sql = text('select sum(case when breed = :feralPigeon then 1 else 0 end) as "feralPigeon", sum(case when breed = :fancyPigeon then 1 else 0 end) as "fancyPigeon", sum(case when breed = :carrierPigeon then 1 else 0 end) as "carrierPigeon", sum(case when breed = :commonWoodPigeon then 1 else 0 end) as "commonWoodPigeon", sum(case when breed is Null then 1 else 0 end) as "undefined" from "case" where "isClosed" = TRUE and latitude between :latSW and :latNE and longitude between :lonSW and :lonNE and timestamp between :fromTime and :untilTime')
            sql = sql.bindparams(latSW=latSW, latNE=latNE, lonSW=lonSW, lonNE=lonNE, fromTime=fromTime, untilTime=untilTime, feralPigeon="Feral Pigeon", fancyPigeon="Fancy Pigeon", commonWoodPigeon="Common Wood Pigeon", carrierPigeon="Carrier Pigeon")
        elif fromTime is not None:
            sql = text('select sum(case when breed = :feralPigeon then 1 else 0 end) as "feralPigeon", sum(case when breed = :fancyPigeon then 1 else 0 end) as "fancyPigeon", sum(case when breed = :carrierPigeon then 1 else 0 end) as "carrierPigeon", sum(case when breed = :commonWoodPigeon then 1 else 0 end) as "commonWoodPigeon", sum(case when breed is Null then 1 else 0 end) as "undefined" from "case" where "isClosed" = TRUE and latitude between :latSW and :latNE and longitude between :lonSW and :lonNE and timestamp > :fromTime')
            sql = sql.bindparams(latSW=latSW, latNE=latNE, lonSW=lonSW, lonNE=lonNE, fromTime=fromTime, feralPigeon="Feral Pigeon", fancyPigeon="Fancy Pigeon", commonWoodPigeon="Common Wood Pigeon", carrierPigeon="Carrier Pigeon")
        elif untilTime is not None:
            sql = text('select sum(case when breed = :feralPigeon then 1 else 0 end) as "feralPigeon", sum(case when breed = :fancyPigeon then 1 else 0 end) as "fancyPigeon", sum(case when breed = :carrierPigeon then 1 else 0 end) as "carrierPigeon", sum(case when breed = :commonWoodPigeon then 1 else 0 end) as "commonWoodPigeon", sum(case when breed is Null then 1 else 0 end) as "undefined" from "case" where "isClosed" = TRUE and latitude between :latSW and :latNE and longitude between :lonSW and :lonNE and timestamp < :untilTime')
            sql = sql.bindparams(latSW=latSW, latNE=latNE, lonSW=lonSW, lonNE=lonNE, fromTime=fromTime, untilTime=untilTime, feralPigeon="Feral Pigeon", fancyPigeon="Fancy Pigeon", commonWoodPigeon="Common Wood Pigeon", carrierPigeon="Carrier Pigeon")
        else:
            sql = text('select sum(case when breed = :feralPigeon then 1 else 0 end) as "feralPigeon", sum(case when breed = :fancyPigeon then 1 else 0 end) as "fancyPigeon", sum(case when breed = :carrierPigeon then 1 else 0 end) as "carrierPigeon", sum(case when breed = :commonWoodPigeon then 1 else 0 end) as "commonWoodPigeon", sum(case when breed is Null then 1 else 0 end) as "undefined" from "case" where "isClosed" = TRUE and latitude between :latSW and :latNE and longitude between :lonSW and :lonNE')
            sql = sql.bindparams(latSW=latSW, latNE=latNE, lonSW=lonSW, lonNE=lonNE, feralPigeon="Feral Pigeon", fancyPigeon="Fancy Pigeon", commonWoodPigeon="Common Wood Pigeon", carrierPigeon="Carrier Pigeon")
        result = db.engine.execute(sql)
        res = result.fetchall()
        for i in res:
            x =dict(i.items())
        return x

    @staticmethod
    def get_injury(latNE, lonNE, latSW, lonSW, fromTime=None, untilTime=None):
        if fromTime is not None and untilTime is not None:
            sql = text('select sum(case when "footOrLeg" = TRUE then 1 else 0 end) as "sumFootOrLeg", sum(case when "strappedFeet" = TRUE then 1 else 0 end) as "sumStrappedFeet", sum(case when "wing" = TRUE then 1 else 0 end) as "sumWing", sum(case when "headOrEye" = TRUE then 1 else 0 end) as "sumHeadOrEye", sum(case when "openWound" = TRUE then 1 else 0 end) as "sumOpenWound", sum(case when "paralyzedOrFlightless" = TRUE then 1 else 0 end) as "sumParalyzedOrFlightless", sum(case when "fledgling" = TRUE then 1 else 0 end) as "sumFledgling", sum(case when "other" = TRUE then 1 else 0 end) as "sumOther" from "injury" join "case" using ("caseID") where "isClosed" = TRUE and "latitude" between :latSW and :latNE and "longitude" between :lonSW and :lonNE and "timestamp" between :fromTime and :untilTime')
            sql = sql.bindparams(latSW=latSW, latNE=latNE, lonSW=lonSW, lonNE=lonNE, fromTime=fromTime, untilTime=untilTime)
        elif fromTime is not None:
            sql = text('select sum(case when "footOrLeg" = TRUE then 1 else 0 end) as "sumFootOrLeg", sum(case when "strappedFeet" = TRUE then 1 else 0 end) as "sumStrappedFeet", sum(case when "wing" = TRUE then 1 else 0 end) as "sumWing", sum(case when "headOrEye" = TRUE then 1 else 0 end) as "sumHeadOrEye", sum(case when "openWound" = TRUE then 1 else 0 end) as "sumOpenWound", sum(case when "paralyzedOrFlightless" = TRUE then 1 else 0 end) as "sumParalyzedOrFlightless", sum(case when "fledgling" = TRUE then 1 else 0 end) as "sumFledgling", sum(case when "other" = TRUE then 1 else 0 end) as "sumOther" from "injury" join "case" using ("caseID") where "isClosed" = TRUE and "latitude" between :latSW and :latNE and "longitude" between :lonSW and :lonNE and "timestamp" > :fromTime')
            sql = sql.bindparams(latSW=latSW, latNE=latNE, lonSW=lonSW, lonNE=lonNE, fromTime=fromTime)
        elif untilTime is not None:
            sql = text('select sum(case when "footOrLeg" = TRUE then 1 else 0 end) as "sumFootOrLeg", sum(case when "strappedFeet" = TRUE then 1 else 0 end) as "sumStrappedFeet", sum(case when "wing" = TRUE then 1 else 0 end) as "sumWing", sum(case when "headOrEye" = TRUE then 1 else 0 end) as "sumHeadOrEye", sum(case when "openWound" = TRUE then 1 else 0 end) as "sumOpenWound", sum(case when "paralyzedOrFlightless" = TRUE then 1 else 0 end) as "sumParalyzedOrFlightless", sum(case when "fledgling" = TRUE then 1 else 0 end) as "sumFledgling", sum(case when "other" = TRUE then 1 else 0 end) as "sumOther" from "injury" join "case" using ("caseID") where "isClosed" = TRUE and "latitude" between :latSW and :latNE and "longitude" between :lonSW and :lonNE and "timestamp" < :untilTime')
            sql = sql.bindparams(latSW=latSW, latNE=latNE, lonSW=lonSW, lonNE=lonNE, untilTime=untilTime)
        else:
            sql = text('select sum(case when "footOrLeg" = TRUE then 1 else 0 end) as "sumFootOrLeg", sum(case when "strappedFeet" = TRUE then 1 else 0 end) as "sumStrappedFeet", sum(case when "wing" = TRUE then 1 else 0 end) as "sumWing", sum(case when "headOrEye" = TRUE then 1 else 0 end) as "sumHeadOrEye", sum(case when "openWound" = TRUE then 1 else 0 end) as "sumOpenWound", sum(case when "paralyzedOrFlightless" = TRUE then 1 else 0 end) as "sumParalyzedOrFlightless", sum(case when "fledgling" = TRUE then 1 else 0 end) as "sumFledgling", sum(case when "other" = TRUE then 1 else 0 end) as "sumOther" from "injury" join "case" using ("caseID") where "isClosed" = TRUE and "latitude" between :latSW and :latNE and "longitude" between :lonSW and :lonNE')
            sql = sql.bindparams(latSW=latSW, latNE=latNE, lonSW=lonSW, lonNE=lonNE)
        result = db.engine.execute(sql)
        res = result.fetchall()
        for i in res:
            x = dict(i.items())
        return x


class CaseSchema(ma.Schema):
    caseID = ma.Integer(dump_only=True)
    timestamp = ma.DateTime("rfc", missing=None)
    priority = ma.Integer(required=True, validate=validate.Range(min=1, max=3))
    reporter = ma.String(missing=None, validate=user.User.exists)
    rescuer = ma.String(missing=None, validate=user.User.exists)
    breed = ma.String(missing=None, validate=breed.Breed.exists)
    additionalInfo = ma.String(missing=None)
    phone = ma.String(required=True)
    latitude = ma.Float(required=True)
    longitude = ma.Float(required=True)
    wasFoundDead = ma.Boolean(missing=None)
    wasNotFound = ma.Boolean(missing=None)
    isClosed = ma.Boolean(missing=False)
    injury = ma.Nested(injury.InjurySchema, required=True)
    media = ma.Nested(medium.MediumSchema, many=True, dump_only=True)

    @post_dump
    def wrap(self, data):
        d = utils.from_rfc(data["timestamp"])
        data["timestamp"] = d.strftime("%s")
        return data

    @pre_load
    def process_input(self, data):
        if data.get("timestamp") is not None:
            d = datetime.fromtimestamp(int(data["timestamp"]))
            data["timestamp"] = utils.rfcformat(d)
        return data

    @post_load
    def make_case(self, data):
        return Case(**data)

case_schema = CaseSchema()
cases_schema = CaseSchema(many=True)

spec.definition("Case", schema=CaseSchema)