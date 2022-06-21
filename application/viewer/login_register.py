from flask import Blueprint,request,render_template,redirect,jsonify
from werkzeug.security import generate_password_hash,check_password_hash
from ..model import User
from flask_login import login_user,login_required,logout_user
from ..app import db
import json
lr_blueprint = Blueprint('lr_blueprint',__name__)

@lr_blueprint.route('/API/login',methods=['POST','GET'])
def login():
    if request.method == 'GET':
        return 0
    if request.method == 'POST':
        email = ''
        password = ''
        print(request.form)
        tmp_dict = {}
        if "email" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)
        try:
            email = tmp_dict['email']
            password = tmp_dict['password']
        except:
            return jsonify({'login_state':0})
        user = db.session.query(User).filter(User.email==email).first()
        
        if user and user.verify_password(password):
            login_user(user,remember=True)
            print("login_success")
            token = user.get_id()        
            # return jsonify({'login_state':1})
            print(token)
            return jsonify({'login_state':1,'token':token,'uid':user.uid})
        else:
            return jsonify({'login_state':0})
        

@lr_blueprint.route('/API/register',methods=['GET','POST'])
def register():
    try:
        if request.method == 'GET':
            form = LoginForm()
            return render_template('register.html',form=form)
        
        if request.method == 'POST':
            _dict = request.form
            
            email = _dict['email']
            password = _dict['password']
            nickname = _dict['nickname']
            user = User(email=email,pwd=password,nickname=nickname)
            last_user = db.session.query(User).filter(User.email==user.email).first()
            
            if last_user:
                return {'register_state':0,'error':'email has existed'}
            db.session.add(user)
            db.session.commit()
            return {'register_state':1}
        else:
                
            return jsonify({'register_state':0})
    except Exception as e:
            return jsonify({'register_state':0,'error':str(e)})
    

@lr_blueprint.route('/API/logout',methods=['POST'])
# @login_required
def logout():
    try:
        
        # tmp_dict = json.loads(request.data)
        # user = User.query_by_username(tmp_dict['username'])
        logout_user()
        return jsonify({'logout_state':1})
    except:
        return jsonify({'logout_state':0})

# @lr_blueprint.route('/API/web/user_del',methods=['GET','POST'])
# # @login_required
# def user_del():
#     if request.method == 'GET':
#         form = DelForm()
#         return render_template('user_del.html',form=form)
#     if request.method == 'POST':
#         tmp_dict = request.form
#         username = tmp_dict['username']
#         User.user_del(username)
#         return "user_del success"
#     else:
#         form = DelForm()
#         return render_template('user_del.html',form=form)

@lr_blueprint.route('/API/password_change',methods=['GET','POST'])
def password_change():
    if request.method == 'POST':
        try:
            tmp_dict = request.form
            if "uid" in request.form:
                tmp_dict = request.form
                print(tmp_dict)
            else:
                tmp_dict = request.json
                print(tmp_dict)  
            
            uid = int(tmp_dict['uid'])
            password = tmp_dict['new_password']
            old_password = tmp_dict['old_password']
            user = db.session.query(User).filter(User.uid==uid).first()
            if user and user.verify_password(old_password):   
                db.session.query(User).filter(User.uid==uid).update({'password_hash':generate_password_hash(password)})
                db.session.commit()
                return jsonify({'state':1})
            else:
                return jsonify({'state':0})
        except Exception as e:
            print(e)
            return jsonify({'state':0})
    else:
        
        return jsonify({'pc_state':0})

# @lr_blueprint.route('/API/web/user_alter',methods=['GET','POST'])
# # @login_required
# def user_alter():
#     if request.method == 'GET':
#         form = LoginForm()
#         return render_template('user_alter.html',form=form)
#     if request.method == 'POST':
#         tmp_dict = request.form
#         username = tmp_dict['username']
#         tel = tmp_dict['tel']
#         wechat = tmp_dict['wechat']
#         user = User(username = username,pwd = "0",tel = tel,wechat =wechat)
#         User.user_alter(user)
#         return "alter success"
#     else:
#         form = LoginForm()
#         return render_template('user_alter.html',form=form)

# @lr_blueprint.route('/API/web/user_list',methods=['GET','POST'])
# # @login_required
# def user_list():
#     users = User.all_Users()
#     result_dict={"data":{"total":len(users),"users":[]},"meta":1}
#     if len(users)==0:
#         result_dict["data"]["meta"]=0
#     for user in users:
#         result_dict["data"]["users"].append(user.to_dict())
#     # print(result_dict)
#     return jsonify(result_dict)

# @lr_blueprint.route('/API/web/query_user_id',methods=['GET','POST'])
# # @login_required
# def query_user_id():
#     # print(request.form)
#     tmp_list = request.form
#     id = tmp_list['id']
#     try:
#         return jsonify(User.query_by_id(id).to_dict())
#     except:
#         return jsonify({})

# @lr_blueprint.route('/API/web/query_user_name',methods=['GET','POST'])
# # @login_required
# def query_user_name():
#     print(request.form)
#     tmp_list = request.form
#     username = tmp_list['username']
#     try:
#         print(User.query_by_username(username).to_dict())
#         return jsonify(User.query_by_username(username).to_dict())
#     except:
#         return jsonify({})



