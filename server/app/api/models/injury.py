from api import db, ma

class Injury(db.Model):
    __tablename__ = "injury"
    caseID = db.Column(db.Integer, db.ForeignKey("case.caseID"), primary_key=True)
    footOrLeg = db.Column(db.Boolean, nullable=False)
    wing = db.Column(db.Boolean, nullable=False)
    headOrEye = db.Column(db.Boolean, nullable=False)
    openWound = db.Column(db.Boolean, nullable=False)
    paralyzedOrFlightless = db.Column(db.Boolean, nullable=False)
    fledgling = db.Column(db.Boolean, nullable=False)
    other = db.Column(db.Boolean, nullable=False)

    def __init__(self, footOrLeg, wing, headOrEye, openWound, paralyzedOrFlightless, fledgling, other):
        self.footOrLeg = footOrLeg
        self.wing = wing
        self.headOrEye = headOrEye
        self.openWound = openWound
        self.paralyzedOrFlightless = paralyzedOrFlightless
        self.fledgling = fledgling
        self.other = other

    def save(self):
        db.session.add(self)
        db.session.commit()

    def delete(self):
        db.session.delete(self)
        db.session.commit()

    def __repr__(self):
        return "<Injury: {}>".format(self.caseID)

    @staticmethod
    def all():
        return Injury.query.all()

    @staticmethod
    def get(caseID):
        return Injury.query.get(caseID)

class InjurySchema(ma.ModelSchema):
    class Meta:
        model = Injury
        sqla_session = db.session

injury_schema = InjurySchema()
injuries_schema = InjurySchema(many=True)