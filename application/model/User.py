from flask_login import UserMixin
from werkzeug.security import generate_password_hash,check_password_hash
from ..app import db
from sqlalchemy import ForeignKey,and_
from itsdangerous import TimedJSONWebSignatureSerializer as Serializer
"""
User 类{
    id:用户的唯一 id标识
    username:用户的用户名
    password:用户的密码，存储时采用generate_password_hash方法进行加密
方法说明:
    query_by_id:参数为id，返回值为查找得到的用户对象
    query_by_username:参数为用户名，返回值为查找得到的用户对象
    register:参数为用户对象，在数据库中添加对应的用户信息
    user_del:参数为用户名，在数据库中删除对应用户信息。
    user_alter:参数为用户对象，在数据库中更新同名用户的用户信息。
}
"""
class User(db.Model,UserMixin):
    __tablename__= 'User'
    uid = db.Column(db.Integer, autoincrement=True, primary_key=True)
    email = db.Column(db.String(256))
    password_hash = db.Column(db.String(256))
    pic_url = db.Column(db.String(100))
    intro = db.Column(db.String(256))
    nickname = db.Column(db.String(20))
    
    def __init__(self,email,pwd,nickname):
        self.email = email
        self.nickname = nickname
        self.__pwd = pwd
        self.__password__()
    
    def __password__(self):
        self.password_hash = generate_password_hash(self.__pwd)

    def get_id(self):
        jwt = Serializer(secret_key='eriguhsdfjhigusfdjkn',expires_in=3600)
        return jwt.dumps(str(self.uid)).decode()
    
    def to_dict(self):
        return {c.name: getattr(self, c.name) for c in self.__table__.columns}
    
    def verify_password(self,pwd):
        return check_password_hash(self.password_hash,pwd)
    
    def __repr__(self):
        return '<User %r>' % self.nickname
    
    

class UserRelation(db.Model):
    
    __tablename__="UserRelation"
    uid_1 = db.Column(db.Integer, primary_key=True)
    uid_2 = db.Column(db.Integer, primary_key=True)
    relation = db.Column(db.Integer)   
    
    def to_dict(self):
        return {c.name: getattr(self, c.name) for c in self.__table__.columns}
        
    def __repr__(self):
        return '<UserRelation >' 

class Notice(db.Model):
    __tablename__='Notice'
    nid = db.Column(db.Integer,autoincrement=True, primary_key=True)
    uid = db.Column(db.Integer, ForeignKey('User.uid',ondelete='CASCADE'))
    pid = db.Column(db.Integer, ForeignKey('Post.pid',ondelete='CASCADE'))
    type = db.Column(db.Integer)
    text = db.Column(db.Text)
    time = db.Column(db.Integer,nullable=False)
    check = db.Column(db.Integer,default=0)
    send = db.Column(db.Integer,default=0)
    def to_dict(self):
        return {c.name: getattr(self, c.name) for c in self.__table__.columns}
    def __repr__(self):
        return '<Notice >' 