from flask import Flask,render_template,request
from flask_login import LoginManager
from flask_sqlalchemy import SQLAlchemy

from .login_register import lr_blueprint
from .post_operation import po_blueprint
from .user_operation import uo_blueprint
from .resource_operation import ro_blueprint
