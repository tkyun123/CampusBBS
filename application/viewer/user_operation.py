import json
import time
from flask import Blueprint,request,send_from_directory,jsonify
from flask_login import login_required
from ..model import User,UserRelation,Post,Notice,PostRecord
from ..app import db
from sqlalchemy import and_,func,desc,or_
from sqlalchemy.orm import aliased
uo_blueprint = Blueprint('uo_blueprint',__name__)

@uo_blueprint.route('/API/user_info/<uid>/<uid2>',methods=['GET'])
def user_info(uid,uid2):
    uid = int(uid)
    uid2= int(uid2)
    if(uid == uid2):
        user = db.session.query(User).filter(User.uid==uid).first()
        sub_count = db.session.query(func.count(UserRelation.uid_2)).filter(and_(UserRelation.uid_1==uid,UserRelation.relation==1)).scalar()
        block_count = db.session.query(func.count(UserRelation.uid_2)).filter(and_(UserRelation.uid_1==uid,UserRelation.relation==0)).scalar()
        _sub_count = db.session.query(func.count(UserRelation.uid_1)).filter(and_(UserRelation.uid_2==uid,UserRelation.relation==1)).scalar()
        post_count = db.session.query(func.count(Post.pid)).filter(Post.uid==uid).scalar()
        if user:
            new_dict = {"nickname":user.nickname,"email":user.email,"intro":user.intro,"pic_url":user.pic_url,'sub_count':sub_count,'block_count':block_count,"_sub_count":_sub_count,"post_count":post_count}
            print(new_dict)
            return jsonify(new_dict)
        else:
            return jsonify({})
    else:
        user = db.session.query(User).filter(User.uid==uid2).first()
        sub_count = db.session.query(func.count(UserRelation.uid_2)).filter(and_(UserRelation.uid_1==uid2,UserRelation.relation==1)).scalar()
        block_count = db.session.query(func.count(UserRelation.uid_2)).filter(and_(UserRelation.uid_1==uid2,UserRelation.relation==0)).scalar()
        _sub_count = db.session.query(func.count(UserRelation.uid_1)).filter(and_(UserRelation.uid_2==uid2,UserRelation.relation==1)).scalar()
        post_count = db.session.query(func.count(Post.pid)).filter(Post.uid==uid2).scalar()
        relation = db.session.query(UserRelation).filter(and_(UserRelation.uid_1==uid,UserRelation.uid_2==uid2)).first()
        if user:
            new_dict = {"nickname":user.nickname,"email":user.email,"intro":user.intro,"pic_url":user.pic_url,'sub_count':sub_count,'block_count':block_count,"_sub_count":_sub_count,"post_count":post_count}
            if relation is not None:
                new_dict['relation']=relation.relation
            else:
                new_dict['relation']=-1
            print(new_dict)
            return jsonify(new_dict)
        else:
            return jsonify({})
        
@uo_blueprint.route('/API/get_sub',methods=['POST'])
def get_sub():
    tmp_dict = request.form
    if "uid" in request.form:
        tmp_dict = request.form
        print(tmp_dict)
    else:
        tmp_dict = request.json
        print(tmp_dict)
    uid = int(tmp_dict['uid'])
    uid2 = int(tmp_dict['uid2'])
    page_size = int(tmp_dict['page_size'])
    page_index = int(tmp_dict['page_index'])
    Ur = aliased(UserRelation)
    u = db.session.query(User,Ur.relation).join(UserRelation,UserRelation.uid_2==User.uid).filter(and_(UserRelation.relation==1,UserRelation.uid_1 == uid)).outerjoin(Ur,and_(Ur.uid_2==UserRelation.uid_2,Ur.uid_1==uid2)).limit(page_size).offset((page_index)*page_size)
    tmp_list = []
    for user in u:
        tmp_dict = user['User'].to_dict()
        tmp_dict.pop('password_hash')
        if user['relation'] is not None:
            tmp_dict['relation'] = user['relation']
        else:
            tmp_dict['relation'] = -1
        print(tmp_dict)
        tmp_list.append(tmp_dict)
    return jsonify({'data':tmp_list})

@uo_blueprint.route('/API/get__sub',methods=['POST'])
def get__sub():
    try:
        tmp_dict = request.form
        if "uid" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)
        uid = int(tmp_dict['uid'])
        uid2 = int(tmp_dict['uid2'])
        page_size = int(tmp_dict['page_size'])
        page_index = int(tmp_dict['page_index'])
        Ur = aliased(UserRelation)
        
        u = db.session.query(User,Ur.relation).join(UserRelation,UserRelation.uid_1==User.uid).filter(and_(UserRelation.relation==1,UserRelation.uid_2 == uid)).outerjoin(Ur,and_(Ur.uid_2==UserRelation.uid_1,Ur.uid_1==uid2)).limit(page_size).offset((page_index)*page_size)
        
        tmp_list = []
        for user in u:
            
            tmp_dict = user['User'].to_dict()
            tmp_dict.pop('password_hash')
            if user['relation'] is not None:
                tmp_dict['relation'] = user['relation']
            else:
                tmp_dict['relation'] = -1
            tmp_list.append(tmp_dict)
        return jsonify({'data':tmp_list})
    except Exception as e:
        print(e)
        return jsonify({})


@uo_blueprint.route('/API/sub',methods=['POST'])
def sub():
    try:
        tmp_dict = request.form
        if "uid_1" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)
        uid_1 = int(tmp_dict['uid_1'])
        uid_2 = int(tmp_dict['uid_2'])
        ur = UserRelation(uid_1=uid_1,uid_2=uid_2,relation=1)
        db.session.add(ur)
        db.session.commit()
        return jsonify({"state":1})
    except Exception as e:
        print(e)
        return jsonify({"state":0})
    

@uo_blueprint.route('/API/get_block',methods=['POST'])
def get_block():
    tmp_dict = request.form
    if "uid" in request.form:
        tmp_dict = request.form
        print(tmp_dict)
    else:
        tmp_dict = request.json
        print(tmp_dict)
    uid = int(tmp_dict['uid'])
    page_size = int(tmp_dict['page_size'])
    page_index = int(tmp_dict['page_index'])
    u = db.session.query(User).join(UserRelation,UserRelation.uid_2==User.uid).filter(and_(UserRelation.relation==0,UserRelation.uid_1 == uid)).limit(page_size).offset((page_index)*page_size)
    tmp_list = []
    for user in u:
        tmp_dict = user.to_dict()
        tmp_dict.pop('password_hash')
        tmp_dict['relation']=0
        tmp_list.append(tmp_dict)
    return jsonify({'data':tmp_list})

@uo_blueprint.route('/API/block',methods=['POST'])
def block():
    try:
        tmp_dict = request.form
        if "uid_1" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)
        uid_1 = int(tmp_dict['uid_1'])
        uid_2 = int(tmp_dict['uid_2'])
        ur = UserRelation(uid_1=uid_1,uid_2=uid_2,relation=0)
        db.session.add(ur)
        db.session.commit()
        return jsonify({"state":1})
    except Exception as e:
        
        return jsonify({"state":0})
    
    

@uo_blueprint.route('/API/cancel_relation',methods=['POST'])
def cancel_relation():
    try:
        tmp_dict = request.form
        if "uid_1" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)
        db.session.query(UserRelation).filter(and_(UserRelation.uid_1 == int(tmp_dict['uid_1']),UserRelation.uid_2 == int(tmp_dict['uid_2']))).delete()
        db.session.commit()
        return jsonify({"state":1})
    except Exception as e:
        return jsonify({"state":0})

@uo_blueprint.route('/API/info_change',methods=['POST'])
def info_change():
    try:    
        tmp_dict = request.form
        if "uid" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)  
        new_info = {"nickname":tmp_dict['nickname'],"intro":tmp_dict['intro']}
        db.session.query(User).filter(User.uid==tmp_dict['uid']).update(new_info)
        db.session.commit()
        return jsonify({'user_info_state':1})
    except Exception as e:
        print(str(e))
        return jsonify({'user_info_state':0})

@uo_blueprint.route('/API/search_page_users',methods=['POST'])
def search_page_users():
    try:    
        tmp_dict = request.form
        if "uid" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)
        uid = int(tmp_dict['uid'])
        keyword = tmp_dict['keyword']
        page_size = int(tmp_dict['page_size'])
        page_index = int(tmp_dict['page_index'])
        tmp_relation = (db.session.query(UserRelation.uid_2,UserRelation.relation).filter(UserRelation.uid_1 == uid)).subquery('tmp_relation')
        users = db.session.query(User,tmp_relation.c.relation).outerjoin(tmp_relation,tmp_relation.c.uid_2==User.uid).filter(and_(User.nickname.like("%"+keyword+"%"),or_(tmp_relation.c.relation == 1, tmp_relation.c.relation==None))).limit(page_size).offset((page_index)*page_size)
        user_list = []
        for user in users:
            tmp_dict = dict(user)
            tmp = tmp_dict['User'].to_dict()
            if tmp_dict['relation'] is not None:
                tmp['relation'] = tmp_dict['relation']
            else:
                tmp['relation'] = -1
            user_list.append(tmp)
        return jsonify({'data':user_list})
    except Exception as e:
        print(str(e))  
        return jsonify({'data':[]})

@uo_blueprint.route('/API/new_notice',methods=['Post'])
def new_notice():
    try:    
        tmp_dict = request.form
        if "uid" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)
        uid = int(tmp_dict['uid'])
        text = tmp_dict['text']
        type = int(tmp_dict['type'])
        notice = Notice(uid=uid,type=type,text=text,time=int(time.mktime(time.localtime(time.time()))))
        db.session.add(notice)
        db.session.commit()
        return jsonify({"state":1})
    except Exception as e:
        print(str(e))  
        return jsonify({'state':0})
    


@uo_blueprint.route('/API/get_notice',methods=['POST'])
def get_notice():
    try:    
        tmp_dict = request.form
        if "uid" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)
        uid = int(tmp_dict['uid'])
        page_size = int(tmp_dict['page_size'])
        page_index = int(tmp_dict['page_index'])
        type = int(tmp_dict['type'])
        if type == 1:
            notices = db.session.query(Notice).filter(and_(Notice.uid==uid,Notice.type==1)).order_by(desc(Notice.time)).limit(page_size).offset((page_index)*page_size)
            notice_list = []
            uncheck_count = db.session.query(func.count(Notice.uid)).filter(and_(Notice.uid==uid,Notice.check==0)).scalar()
            
            for notice in notices:
                # print(notice)
                # print(notice.to_dict())
                notice_list.append(notice.to_dict())
                notice.check=1;
            db.session.flush()
            db.session.commit()
            return jsonify({'data':notice_list,'uncheck':uncheck_count})
        if type == 0:
            print(0)
            tmp_record = db.session.query(Notice.pid).filter(and_(Notice.uid==uid,Notice.type==0)).subquery('tmp_record')
            post_records = db.session.query(PostRecord).join(UserRelation,UserRelation.uid_2==PostRecord.uid).filter(and_(UserRelation.uid_1==uid,UserRelation.relation==1,PostRecord.pid.notin_(tmp_record)))
            notice_list = []
            # print("type_0")
            
            if post_records:
                for post_record in post_records:
                    tmp_notice = Notice(uid=uid,text=post_record.text,pid=post_record.pid,type=0,time=int(time.mktime(time.localtime(time.time()))))
                    print(tmp_notice)
                    db.session.add(tmp_notice)
            db.session.commit()
            notices = db.session.query(Notice).filter(and_(Notice.uid==uid,Notice.type==0)).order_by(desc(Notice.time)).limit(page_size).offset((page_index)*page_size)
            notice_list = []
            uncheck_count = db.session.query(func.count(Notice.uid)).filter(and_(Notice.uid==uid,Notice.check==0)).scalar()
            for notice in notices:
                notice_list.append(notice.to_dict())
                notice.check = 1;
            
            db.session.flush()
            db.session.commit()
            return jsonify({'data':notice_list,'uncheck':uncheck_count})
    except Exception as e:
        print(str(e))  
        return jsonify({'data':[]})
    
@uo_blueprint.route('/API/send_notice',methods=['POST'])
def send_notice():
    try:    
        tmp_dict = request.form
        if "uid" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)
        uid = int(tmp_dict['uid'])
        tmp_record = db.session.query(Notice.pid).filter(and_(Notice.uid==uid,Notice.type==0)).subquery('tmp_record')
        post_records = db.session.query(PostRecord).join(UserRelation,UserRelation.uid_2==PostRecord.uid).filter(and_(UserRelation.uid_1==uid,UserRelation.relation==1,PostRecord.pid.notin_(tmp_record)))
        notice_list = []
        
        for post_record in post_records:
            print(1)
            tmp_notice = Notice(uid=uid,text=post_record.text,pid=post_record.pid,type=0,time=int(time.mktime(time.localtime(time.time()))))
            db.session.add(tmp_notice)
        db.session.commit()
        notices = db.session.query(Notice).filter(and_(Notice.uid==uid,Notice.send==0)).order_by(desc(Notice.time))
        notice_list = []
        for notice in notices:
            notice_list.append(notice.to_dict())
        db.session.query(Notice).filter(and_(Notice.uid==uid)).update({'send':1})
        db.session.commit()
        return jsonify({'data':notice_list})
    except Exception as e:
        print(str(e))  
        return jsonify({'data':[]})


@uo_blueprint.route('/API/check_notice',methods=['POST'])
def check_notice():
    try:    
        tmp_dict = request.form
        if "nid" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)
        nid = int(tmp_dic['nid'])
        db.session.query(Notice).filter(Notice.nid==nid).update({'check':1})
        db.session.commit()
        return jsonify({"state":1})
    except Exception as e:
        print(str(e))  
        return jsonify({'state':0})

