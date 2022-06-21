from flask import Flask,request
from flask_login import LoginManager
from .app_extension import db
from application.viewer import lr_blueprint,po_blueprint,uo_blueprint,ro_blueprint

from .model import User
from flask_cors import CORS
import itsdangerous
from itsdangerous import TimedJSONWebSignatureSerializer as Serializer
import json
import pymysql

def create_app():
    app = Flask(__name__,static_folder="resource",static_url_path="/resource")
    
    app.config['SECRET_KEY']='98746513279846513'
    app.config['SQLALCHEMY_DATABASE_URI']='mysql+pymysql://tk:tk011317@localhost:3306/campus'
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS']=True
    db.init_app(app)
    app.app_context().push()
    # db.drop_all()
    db.create_all()
    return app
app = create_app()
CORS(app)
app.register_blueprint(lr_blueprint)
app.register_blueprint(po_blueprint)
app.register_blueprint(uo_blueprint)
app.register_blueprint(ro_blueprint)
login_manager = LoginManager()
login_manager.session_protection='strong'
login_manager.login_view = 'lr_blueprint.login'
login_manager.init_app(app)


@login_manager.request_loader
def load_user(request):
    jwt = Serializer(secret_key='eriguhsdfjhigusfdjkn',expires_in=3600)
    _dict = request.form
    # print(_dict)
    token=""
    if 'token' in _dict:
        token = _dict['token']
    else:
        __dict = request.json
        token = __dict['token']
    try:
        id = int(jwt.loads(token))
    except itsdangerous.exc.SignatureExpired:
        return None
    except itsdangerous.exc.BadSignature:
        return None
    user = db.session.query(User.uid==id).first()
    if user:
        return user
    return None


