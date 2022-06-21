from ..app import db
from sqlalchemy import ForeignKey
class P_resource(db.Model):
    __tablename__="P_resource"
    index = db.Column(db.Integer,primary_key=True)
    fid = db.Column(db.Integer,ForeignKey("Floor.fid",ondelete='CASCADE'),primary_key=True)
    pid = db.Column(db.Integer,primary_key=True)
    url = db.Column(db.String(256))
    
    def to_dict(self):
            return {c.name: getattr(self, c.name) for c in self.__table__.columns}
        
    def __repr__(self):
        return '<P_resource >' 
    
class VS_resource(db.Model):
    __tablename__="VS_resource"
    fid = db.Column(db.Integer,ForeignKey("Floor.fid",ondelete='CASCADE'),primary_key=True)
    pid = db.Column(db.Integer,primary_key=True)
    url = db.Column(db.String(256))
    
    def to_dict(self):
            return {c.name: getattr(self, c.name) for c in self.__table__.columns}
        
    def __repr__(self):
        return '<P_resource >' 