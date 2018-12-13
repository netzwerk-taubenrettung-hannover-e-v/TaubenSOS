from api import db

class Breed(db.Model):
    __tablename__ = "breed"
    breed = db.Column(db.String(20), primary_key=True)

    @staticmethod
    def exists(breed):
        return db.session.query(Breed.query.filter(Breed.breed == breed).exists()).scalar()