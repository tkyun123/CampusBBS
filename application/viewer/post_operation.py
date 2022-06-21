from flask import Blueprint,request,send_from_directory,jsonify
from flask_login import login_required
from ..model import Post,Floor,Comment,Agreement,User,UserRelation,Notice,PostRecord,P_resource,VS_resource
from ..app import db
import time
import json
from sqlalchemy import desc,and_,or_
from sqlalchemy.orm import aliased

po_blueprint = Blueprint('po_blueprint',__name__)

@po_blueprint.route('/API/get_posts',methods=['GET'])
def get_posts():
    posts = db.session.query(Post).limit(100).all()
    post_list = []
    for post in posts:
        post_list.append(post.to_dict())
    return jsonify(post_list)

@po_blueprint.route('/API/new_post',methods=['POST'])
def new_post():
    try:
        tmp_dict = request.form
        if "uid" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)
        post = Post(uid=int(tmp_dict['uid']),title=tmp_dict['title'],theme=['theme'],position=tmp_dict['position'],post_time = int(time.mktime(time.localtime(time.time()))))
        db.session.add(post)
        db.session.flush()
        floor = Floor(fid=0,pid=post.pid,uid=int(tmp_dict['uid']),text=tmp_dict['text'],position=tmp_dict['position'],type = tmp_dict['type'],time = int(time.mktime(time.localtime(time.time()))))
        db.session.add(floor)
        tmp_text = ""
        uid = int(tmp_dict['uid'])
        user = db.session.query(User).filter(User.uid==floor.uid).first()
        tmp_text = tmp_text + user.nickname + " 发布了新帖子：" + tmp_dict['title']
        post_record = PostRecord(uid=uid,pid=post.pid,text = tmp_text)
        db.session.add(post_record)
        db.session.commit()
        return jsonify({"pid":post.pid,"new_post_state":1})
    except Exception as e:
        print(str(e))
        return jsonify({'new_post_state':0})
    
@po_blueprint.route('/API/del_post',methods=['POST'])
def del_post():
    tmp_dict = request.form
    if "pid" in request.form:
        tmp_dict = request.form
        print(tmp_dict)
    else:
        tmp_dict = request.json
        print(tmp_dict)
    db.session.query(Post).filter(Post.pid==tmp_dict['pid']).delete()
    db.session.commit()

@po_blueprint.route('/API/get_page_posts',methods=['POST'])
def get_page_posts():
    try:
        tmp_dict = request.form
        if "order_type" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)
        page_size = int(tmp_dict['page_size'])
        page_index = int(tmp_dict['page_index'])
        order_type = int(tmp_dict['order_type'])
        uid = int(tmp_dict['uid'])
        sort_type = int(tmp_dict['sort_type'])
        posts = ""
        tmp_relation=""
        ONLY_ATTENTION = 1
        tmp_relation = (db.session.query(UserRelation.uid_2,UserRelation.relation).filter(UserRelation.uid_1 == uid)).subquery('tmp_relation')
        tmp_user = (db.session.query(Agreement.uid,Agreement.pid).filter(and_(Agreement.uid == uid,Agreement.fid == 0))).subquery('tmp_user')
        if order_type == 0:
            if sort_type== ONLY_ATTENTION:
                posts = db.session.query(Post,User.nickname,User.pic_url,tmp_user.c.uid,tmp_relation.c.relation).outerjoin(tmp_relation,tmp_relation.c.uid_2==Post.uid).filter(or_(tmp_relation.c.relation == 1, tmp_relation.c.relation==None)).join(User,User.uid==Post.uid).outerjoin(tmp_user,tmp_user.c.pid==Post.pid).order_by(Post.post_time).limit(page_size).offset((page_index)*page_size)
            else:
                posts = db.session.query(Post,User.nickname,User.pic_url,tmp_user.c.uid,tmp_relation.c.relation).outerjoin(tmp_relation,tmp_relation.c.uid_2==Post.uid).filter(tmp_relation.c.relation == 1).join(User,User.uid==Post.uid).outerjoin(tmp_user,tmp_user.c.pid==Post.pid).order_by(Post.post_time).limit(page_size).offset((page_index)*page_size)
        elif order_type == 1:
            if sort_type== ONLY_ATTENTION:
                posts = db.session.query(Post,User.nickname,User.pic_url,tmp_user.c.uid,tmp_relation.c.relation).outerjoin(tmp_relation,tmp_relation.c.uid_2==Post.uid).filter(or_(tmp_relation.c.relation == 1, tmp_relation.c.relation==None)).join(User,User.uid==Post.uid).outerjoin(tmp_user,tmp_user.c.pid==Post.pid).order_by(desc(Post.post_time)).limit(page_size).offset((page_index)*page_size)
            else:
                posts = db.session.query(Post,User.nickname,User.pic_url,tmp_user.c.uid,tmp_relation.c.relation).outerjoin(tmp_relation,tmp_relation.c.uid_2==Post.uid).filter(tmp_relation.c.relation == 1).join(User,User.uid==Post.uid).outerjoin(tmp_user,tmp_user.c.pid==Post.pid).order_by(desc(Post.post_time)).limit(page_size).offset((page_index)*page_size)
        elif order_type == 2:
            if sort_type== ONLY_ATTENTION:
                posts = db.session.query(Post,User.nickname,User.pic_url,tmp_user.c.uid,tmp_relation.c.relation).outerjoin(tmp_relation,tmp_relation.c.uid_2==Post.uid).filter(or_(tmp_relation.c.relation == 1, tmp_relation.c.relation==None)).join(User,User.uid==Post.uid).outerjoin(tmp_user,tmp_user.c.pid==Post.pid).order_by(Post.agree_count).limit(page_size).offset((page_index)*page_size)
            else:
                posts = db.session.query(Post,User.nickname,User.pic_url,tmp_user.c.uid,tmp_relation.c.relation).outerjoin(tmp_relation,tmp_relation.c.uid_2==Post.uid).filter(tmp_relation.c.relation == 1).join(User,User.uid==Post.uid).outerjoin(tmp_user,tmp_user.c.pid==Post.pid).order_by(Post.agree_count).limit(page_size).offset((page_index)*page_size)
        elif order_type == 3:
            if sort_type== ONLY_ATTENTION:
                posts = db.session.query(Post,User.nickname,User.pic_url,tmp_user.c.uid,tmp_relation.c.relation).outerjoin(tmp_relation,tmp_relation.c.uid_2==Post.uid).filter(or_(tmp_relation.c.relation == 1, tmp_relation.c.relation==None)).join(User,User.uid==Post.uid).outerjoin(tmp_user,tmp_user.c.pid==Post.pid).order_by(desc(Post.agree_count)).limit(page_size).offset((page_index)*page_size)

            else:
                posts = db.session.query(Post,User.nickname,User.pic_url,tmp_user.c.uid,tmp_relation.c.relation).outerjoin(tmp_relation,tmp_relation.c.uid_2==Post.uid).filter(tmp_relation.c.relation == 1).join(User,User.uid==Post.uid).outerjoin(tmp_user,tmp_user.c.pid==Post.pid).order_by(desc(Post.agree_count)).limit(page_size).offset((page_index)*page_size)
        post_list=[]
        for row in posts:
            tmp_dict = dict(row)
            tmp = tmp_dict['Post'].to_dict()
            tmp['nickname'] = tmp_dict['nickname']
            tmp['pic_url'] = tmp_dict['pic_url']
            if tmp_dict['relation'] is not None:
                tmp['relation'] = tmp_dict['relation']
            else:
                tmp['relation'] = -1
            if tmp_dict['uid']:
                tmp['agree_state'] = 1
            else:
                tmp['agree_state'] = 0
            
            post_list.append(tmp)
        # print(post_list)
        return jsonify({'data':post_list})
    except Exception as e:
        print(str(e))
        return jsonify({'data':[],'error':str(e)})
    
@po_blueprint.route('/API/get_page_user_posts',methods=['POST'])
def get_page_user_posts():
    # try:
        tmp_dict = request.form
        if "type" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)
        page_size = int(tmp_dict['page_size'])
        page_index = int(tmp_dict['page_index'])
        order_type = int(tmp_dict['type'])
        uid = int(tmp_dict['uid'])
        posts = ""
        tmp_relation = (db.session.query(UserRelation.uid_2,UserRelation.relation).filter(UserRelation.uid_1 == uid)).subquery('tmp_relation')
        tmp_user = (db.session.query(Agreement.uid,Agreement.pid).filter(and_(Agreement.uid == uid,Agreement.fid == 0))).subquery('tmp_user')
        if order_type == 0:
            posts = db.session.query(Post,User.nickname,User.pic_url,tmp_user.c.uid,tmp_relation.c.relation).outerjoin(tmp_relation,tmp_relation.c.uid_2==Post.uid).filter(and_(or_(tmp_relation.c.relation == 1, tmp_relation.c.relation==None),Post.uid==uid)).join(User,User.uid==Post.uid).outerjoin(tmp_user,tmp_user.c.pid==Post.pid).order_by(Post.post_time).limit(page_size).offset((page_index)*page_size)
        elif order_type == 1:
            posts = db.session.query(Post,User.nickname,User.pic_url,tmp_user.c.uid,tmp_relation.c.relation).outerjoin(tmp_relation,tmp_relation.c.uid_2==Post.uid).filter(and_(or_(tmp_relation.c.relation == 1, tmp_relation.c.relation==None),Post.uid==uid)).join(User,User.uid==Post.uid).outerjoin(tmp_user,tmp_user.c.pid==Post.pid).order_by(desc(Post.post_time)).limit(page_size).offset((page_index)*page_size)
        elif order_type == 2:
            posts = db.session.query(Post,User.nickname,User.pic_url,tmp_user.c.uid,tmp_relation.c.relation).outerjoin(tmp_relation,tmp_relation.c.uid_2==Post.uid).filter(and_(or_(tmp_relation.c.relation == 1, tmp_relation.c.relation==None),Post.uid==uid)).join(User,User.uid==Post.uid).outerjoin(tmp_user,tmp_user.c.pid==Post.pid).order_by(Post.agree_count).limit(page_size).offset((page_index)*page_size)
        elif order_type == 3: 
            posts = db.session.query(Post,User.nickname,User.pic_url,tmp_user.c.uid,tmp_relation.c.relation).outerjoin(tmp_relation,tmp_relation.c.uid_2==Post.uid).filter(and_(or_(tmp_relation.c.relation == 1, tmp_relation.c.relation==None),Post.uid==uid)).join(User,User.uid==Post.uid).outerjoin(tmp_user,tmp_user.c.pid==Post.pid).order_by(desc(Post.agree_count)).limit(page_size).offset((page_index)*page_size)
           
        post_list=[]

        for row in posts:
            tmp_dict = dict(row)
            tmp = tmp_dict['Post'].to_dict()
            tmp['nickname'] = tmp_dict['nickname']
            tmp['pic_url'] = tmp_dict['pic_url']
            if tmp_dict['relation'] is not None:
                tmp['relation'] = tmp_dict['relation']
            else:
                tmp['relation'] = -1
            if tmp_dict['uid']:
                tmp['agree_state'] = 1
            else:
                tmp['agree_state'] = 0
            post_list.append(tmp)
         
        return jsonify({'data':post_list})
    # except Exception as e:
    #     print(str(e))
    #     return jsonify({'data':[],'error':str(e)})
    
@po_blueprint.route('/API/get_floors',methods=['POST'])
def get_floors():
    tmp_dict = request.form
    if "pid" in request.form:
        tmp_dict = request.form
        print(tmp_dict)
    else:
        tmp_dict = request.json
        print(tmp_dict)
    floors = db.session.query(Floor).filter(Floor.pid==tmp_dict['pid']).limit(100).all()
    floor_list = []
    for floor in floors:
        floor_list.append(floor.to_dict())
    return jsonify(floor_list)

@po_blueprint.route('/API/get_page_floors',methods=['POST'])
def get_page_floors():
    # try:
        tmp_dict = request.form
        if "order_type" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)
        page_size = int(tmp_dict['page_size'])
        page_index = int(tmp_dict['page_index'])
        order_type = int(tmp_dict['order_type'])
        uid = int(tmp_dict['uid'])
       
        pid = int(tmp_dict['pid'])
        floors = ""
        tmp_relation = (db.session.query(UserRelation.uid_2,UserRelation.relation).filter(UserRelation.uid_1 == uid)).subquery('tmp_relation')
        tmp_user = (db.session.query(Agreement.uid,Agreement.fid).filter(and_(Agreement.uid == uid,Agreement.pid == pid))).subquery('tmp_user')
        if order_type == 0:
            floors = db.session.query(Floor,User.nickname,User.pic_url,tmp_user.c.uid).filter(and_(Floor.fid>0,Floor.pid==pid)).join(User,User.uid==Floor.uid).outerjoin(tmp_user,tmp_user.c.fid==Floor.fid).order_by(Floor.time).limit(page_size).offset((page_index)*page_size)
        elif order_type == 1:
            floors = db.session.query(Floor,User.nickname,User.pic_url,tmp_user.c.uid).filter(and_(Floor.fid>0,Floor.pid==pid)).join(User,User.uid==Floor.uid).outerjoin(tmp_user,tmp_user.c.fid==Floor.fid).order_by(desc(Floor.time)).limit(page_size).offset((page_index)*page_size)
        floor_list = []
        if page_index==0:
            zero_floor = db.session.query(Floor,User.nickname,User.pic_url,tmp_user.c.uid).filter(and_(Floor.fid==0,Floor.pid==pid)).join(User,User.uid==Floor.uid).outerjoin(tmp_user,tmp_user.c.fid==Floor.fid).first()
            tmp_dict = dict(zero_floor)
            tmp = tmp_dict['Floor'].to_dict()
            urls = ""
            if (tmp['type']==0):
                urls = db.session.query(P_resource).filter(and_(P_resource.pid==int(tmp['pid']),P_resource.fid==int(tmp['fid'])))
                tmp['urls']=[]
                if urls:
                    for url in urls:
                        tmp_url = url.to_dict()
                        print(tmp_url)
                        tmp['urls'].append(tmp_url['url'])
            else:
                url = db.session.query(VS_resource).filter(and_(VS_resource.pid==int(tmp['pid']),VS_resource.fid==int(tmp['fid']))).first()
                tmp['urls']=[]
                
                if url:
                    tmp_url = url.to_dict()
                    print(tmp_url)
                    tmp['urls'].append(tmp_url['url'])
            
            tmp['nickname'] = tmp_dict['nickname']
            tmp['pic_url'] = tmp_dict['pic_url']
            if tmp_dict['uid']:
                tmp['agree_state'] = 1
            else:
                tmp['agree_state'] = 0
            floor_list.append(tmp)
        if floors:
            for row in floors:
                tmp_dict = dict(row)
                tmp = tmp_dict['Floor'].to_dict()
                tmp['nickname'] = tmp_dict['nickname']
                tmp['pic_url'] = tmp_dict['pic_url']
                if tmp_dict['uid']:
                    tmp['agree_state'] = 1
                else:
                    tmp['agree_state'] = 0
                floor_list.append(tmp)
        print(floor_list)
        return jsonify({'data':floor_list})
    # except Exception as e:
    #     print(str(e))
    #     return jsonify({'data':[]})

@po_blueprint.route('/API/new_floor',methods=['POST'])
def new_floor():
    try:
        tmp_dict = request.form
        if "pid" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)
        pid = int(tmp_dict['pid'])
        uid = int(tmp_dict['uid'])
        _type = int(tmp_dict['type'])

        latest_floor = db.session.query(Floor).filter(Floor.pid==pid).order_by(desc(Floor.fid)).first()
        new_floor = Floor(pid = pid,fid=latest_floor.fid+1,uid=uid,text=tmp_dict['text'],type=_type,position=tmp_dict['position'],time=int(time.mktime(time.localtime(time.time()))))
        db.session.add(new_floor)
        db.session.commit()
        return jsonify({"state":1})
    except Exception as e:
        print(str(e))
        return jsonify({'state':0})

@po_blueprint.route('/API/get_comments',methods=['POST'])
def get_comments():
    tmp_dict = request.form
    if "pid" in request.form:
        tmp_dict = request.form
        print(tmp_dict)
    else:
        tmp_dict = request.json
        print(tmp_dict)
    comments = db.session.query(Comment).filter(and_(Comment.pid==tmp_dict['pid'],Comment.fid==tmp_dict['fid'])).limit(100).all()
    comment_list = []
    for comment in comments:
        comment_list.append(comment.to_dict())
    return jsonify(comment_list)

@po_blueprint.route('/API/get_page_comments',methods=['POST'])
def get_page_comments():
    try:
        tmp_dict = request.form
        if "page_size" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)
        page_size = int(tmp_dict['page_size'])
        page_index = int(tmp_dict['page_index'])
        pid = int(tmp_dict['pid'])
        fid = int(tmp_dict['fid'])
        comments = db.session.query(Comment,User.nickname,User.pic_url).filter(and_(Comment.fid==fid,Comment.pid==pid)).join(User,User.uid==Comment.uid).order_by(Comment.time).limit(page_size).offset((page_index)*page_size)
        comment_list = []
        if comments:
            for row in comments:
                tmp_dict = dict(row)
                tmp = tmp_dict['Comment'].to_dict()
                tmp['nickname'] = tmp_dict['nickname']
                tmp['pic_url'] = tmp_dict['pic_url']
                comment_list.append(tmp)
                
        return jsonify({'data':comment_list})
    except Exception as e:
        print(str(e))
        return jsonify({'data':[]})


@po_blueprint.route('/API/get_page_user_comments',methods=['POST'])
def get_page_user_comments():
    try:
        tmp_dict = request.form
        if "page_size" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)
        page_size = int(tmp_dict['page_size'])
        page_index = int(tmp_dict['page_index'])
        uid = int(tmp_dict['uid'])
        comments = db.session.query(Comment,User.nickname,User.pic_url).join(User,User.uid==Comment.uid).filter(Comment.uid==uid).order_by(desc(Comment.time)).limit(page_size).offset((page_index)*page_size)
        comment_list = []
        if comments:
            for row in comments:
                tmp_dict = dict(row)
                tmp = tmp_dict['Comment'].to_dict()
                tmp['nickname'] = tmp_dict['nickname']
                tmp['pic_url'] = tmp_dict['pic_url']
                comment_list.append(tmp)
        print(comment_list)
        return jsonify({'data':comment_list})
    except Exception as e:
        print(str(e))
        return jsonify({'data':[]})



@po_blueprint.route('/API/new_comment',methods=['POST'])
def new_comment():
    try:
        tmp_dict = request.form
        if "pid" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)
        uid = int(tmp_dict['uid'])
        pid = int(tmp_dict['pid'])
        fid = int(tmp_dict['fid'])
        latest_comment = db.session.query(Comment).filter(and_(Comment.pid==pid,Comment.fid==fid)).order_by(desc(Comment.cid)).first()
        cid = 0
        if latest_comment:
            cid = latest_comment.cid+1
        comment = Comment(pid = pid,fid=fid,uid=uid,cid=cid,text=tmp_dict['text'],position=tmp_dict['position'],time=int(time.mktime(time.localtime(time.time()))))
        floor = db.session.query(Floor).filter(and_(Floor.pid==pid,Floor.fid==fid)).first()
        user = db.session.query(User).filter(User.uid == uid).first()
        
        text = ""
        text = text + user.nickname + " 评论你的回复("+floor.text+"):" + comment.text 
        
        notice = Notice(uid=floor.uid,type=1,pid=pid,text = text,time=int(time.mktime(time.localtime(time.time()))))
        db.session.add(notice)
        db.session.add(comment)
        db.session.commit()
        return jsonify({"state":1})
    except Exception as e:
        print(str(e))
        return jsonify({"state":0})
    

@po_blueprint.route('/API/del_comment',methods=['POST'])
def del_comment():
    try:
        tmp_dict = request.form
        if "pid" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)
        db.session.query(Comment).filter(and_(Comment.pid==int(tmp_dict['pid']),Comment.fid==int(tmp_dict['fid']),Comment.cid==int(tmp_dict['cid']))).delete()
        db.session.commit()
        return jsonify({"state":1})
    except Exception as e:
        print(e)
        return jsonify({'state':0})

    
@po_blueprint.route('/API/new_agreement',methods=['POST'])
def new_agreement():
    try:
        tmp_dict = request.form
        if "pid" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)
        uid = int(tmp_dict['uid'])
        fid = int(tmp_dict['fid'])
        pid = int(tmp_dict['pid'])
        new_agreement = Agreement(uid=uid,fid=fid,pid=pid)
        db.session.add(new_agreement)
        db.session.flush()
        floor = db.session.query(Floor).filter(and_(Floor.fid==fid,Floor.pid==pid)).first()
        floor.agree_count += 1
        if fid==0:
            post = db.session.query(Post).filter(Post.pid==pid).first()
            post.agree_count += 1
        
        user = db.session.query(User).filter(User.uid == uid).first()
        text = ""
        if fid == 0:
            post = db.session.query(Post).filter(Post.pid==pid).first()
            text = text + user.nickname + " 赞了你的帖子:" + post.title
        else:
            text = text + user.nickname + " 赞了你的回复:" + floor.text
        notice = Notice(uid=floor.uid,type=1,pid=pid,text = text,time=int(time.mktime(time.localtime(time.time()))))
        db.session.add(notice)
        db.session.commit()
        return jsonify({'agree_state':1})
    except Exception as e:
        print(str(e))
        return jsonify({'agree_state':0})

@po_blueprint.route('/API/del_agreement',methods=['POST'])
def del_agreement():
    try:
        tmp_dict = request.form
        if "pid" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)
        uid = int(tmp_dict['uid'])
        fid = int(tmp_dict['fid'])
        pid = int(tmp_dict['pid'])
        db.session.query(Agreement).filter(Agreement.uid==uid,Agreement.fid==fid,Agreement.pid==pid).delete()
        db.session.flush()
        floor = db.session.query(Floor).filter(and_(Floor.fid==fid,Floor.pid==pid)).first()
        floor.agree_count -= 1
        if fid==0:
            post = db.session.query(Post).filter(Post.pid==pid).first()
            post.agree_count -= 1
        db.session.commit()
        return jsonify({'agree_state':1})
    except Exception as e:
        print(str(e))
        return jsonify({'agree_state':0})
    
@po_blueprint.route('/API/search_page_posts',methods=['POST'])
def search_page_posts():
    try:
        tmp_dict = request.form
        if "keyword" in request.form:
            tmp_dict = request.form
            print(tmp_dict)
        else:
            tmp_dict = request.json
            print(tmp_dict)
        uid = int(tmp_dict['uid'])
        keyword = tmp_dict['keyword']
        page_size = int(tmp_dict['page_size'])
        page_index = int(tmp_dict['page_index'])
        ptime = 2147483646
        type = int(tmp_dict['type'])
        # order_type = int(tmp_dict['order_type'])
        tmp_relation = (db.session.query(UserRelation.uid_2,UserRelation.relation).filter(UserRelation.uid_1 == uid)).subquery('tmp_relation')
        tmp_user = (db.session.query(Agreement.uid,Agreement.pid).filter(and_(Agreement.uid == uid,Agreement.fid == 0))).subquery('tmp_user')
        if type == -1:
            posts = db.session.query(Post,User.nickname,User.pic_url,tmp_user.c.uid,tmp_relation.c.relation).outerjoin(tmp_relation,tmp_relation.c.uid_2==Post.uid).join(Floor,and_(Floor.fid==0,Floor.pid==Post.pid)).filter(and_(or_(tmp_relation.c.relation == 1, tmp_relation.c.relation==None),or_(Floor.text.like("%"+keyword+"%"),Post.title.like("%"+keyword+"%")),Post.post_time<=ptime)).join(User,User.uid==Post.uid).outerjoin(tmp_user,tmp_user.c.pid==Post.pid).order_by(Post.post_time).limit(page_size).offset((page_index)*page_size)
        else:
            posts = db.session.query(Post,User.nickname,User.pic_url,tmp_user.c.uid,tmp_relation.c.relation).outerjoin(tmp_relation,tmp_relation.c.uid_2==Post.uid).join(Floor,and_(Floor.fid==0,Floor.pid==Post.pid)).filter(and_(or_(tmp_relation.c.relation == 1, tmp_relation.c.relation==None),or_(Floor.text.like("%"+keyword+"%"),Post.title.like("%"+keyword+"%")),Post.post_time<=ptime)).join(User,User.uid==Post.uid).outerjoin(tmp_user,tmp_user.c.pid==Post.pid).filter(Floor.type==type).order_by(Post.post_time).limit(page_size).offset((page_index)*page_size)
        post_list=[]
        print (posts)
        for row in posts:
            tmp_dict = dict(row)
            tmp = tmp_dict['Post'].to_dict()
            tmp['nickname'] = tmp_dict['nickname']
            tmp['pic_url'] = tmp_dict['pic_url']
            if tmp_dict['uid']:
                tmp['agree_state'] = 1
            else:
                tmp['agree_state'] = 0
            if tmp_dict['relation'] is not None:
                tmp['relation'] = tmp_dict['relation']
            else:
                tmp['relation'] = -1
            post_list.append(tmp)
            
        return jsonify({'data':post_list})
    except Exception as e:
        print(str(e))
        return jsonify({'data':[]})