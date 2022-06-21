from ..app import db
from sqlalchemy import ForeignKey,and_
class Post(db.Model):
    __tablename__="Post"
    pid = db.Column(db.Integer,autoincrement=True,primary_key=True)
    uid = db.Column(db.Integer,ForeignKey("User.uid"))
    title = db.Column(db.String(40))
    theme = db.Column(db.String(20))
    position = db.Column(db.Text)
    post_time = db.Column(db.Integer,nullable=False)
    agree_count = db.Column(db.Integer,default=0)
    
    def to_dict(self):
        return {c.name: getattr(self, c.name) for c in self.__table__.columns}
        
    def __repr__(self):
        return '<Post >' 

class Floor(db.Model):
    __tablename__="Floor"
    fid = db.Column(db.Integer,primary_key=True)
    pid = db.Column(db.Integer,ForeignKey('Post.pid',ondelete='CASCADE'),primary_key=True)
    uid = db.Column(db.Integer)
    text = db.Column(db.Text)
    time = db.Column(db.Integer,nullable=False)
    agree_count = db.Column(db.Integer,default=0)
    position = db.Column(db.Text)
    type = db.Column(db.Integer)
    
    def to_dict(self):
            return {c.name: getattr(self, c.name) for c in self.__table__.columns}
        
    def __repr__(self):
        return '<Floor >' 
    
class Comment(db.Model):
    __tablename__="Comment"
    cid = db.Column(db.Integer,primary_key=True)
    pid = db.Column(db.Integer,primary_key=True)
    fid = db.Column(db.Integer,ForeignKey('Floor.fid',ondelete='CASCADE'),primary_key=True)
    uid = db.Column(db.Integer)
    text = db.Column(db.Text)
    time = db.Column(db.Integer,nullable=False)
    position = db.Column(db.Text)
    def to_dict(self):
            return {c.name: getattr(self, c.name) for c in self.__table__.columns}
        
    def __repr__(self):
        return '<Comment >' 

class Agreement(db.Model):
    __tablename__='Agreement'
    fid = db.Column(db.Integer,ForeignKey('Floor.fid',ondelete='CASCADE'),primary_key=True)
    pid = db.Column(db.Integer,ForeignKey('Floor.pid',ondelete='CASCADE'),primary_key=True)
    uid = db.Column(db.Integer,primary_key=True)
    
    def to_dict(self):
            return {c.name: getattr(self, c.name) for c in self.__table__.columns}
        
    def __repr__(self):
        return '<Agreement >' 

class PostRecord(db.Model):
    __tablename__='PostRecord'
    uid = db.Column(db.Integer,ForeignKey('Post.uid',ondelete='CASCADE'),primary_key=True)
    pid = db.Column(db.Integer,ForeignKey('Post.pid',ondelete='CASCADE'),primary_key=True)
    text = db.Column(db.Text)
    