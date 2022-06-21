from flask import Blueprint,request,send_from_directory,jsonify
from flask_login import login_required
from ..model import Post,Floor,Comment,Agreement,P_resource,VS_resource,User
from ..app import db
import time
import json
import os
import base64
source_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ro_blueprint = Blueprint('ro_blueprint',__name__)

# @ro_blueprint.route('/API/source/<mul_type>/<filename>')
# def source(mul_type,filename):
#     try:
#         print(mul_type,filename)
#         return send_from_directory(os.path.join(source_root,'resource/%s')%mul_type,filename)
#     except Exception as e:
#         return send_from_directory(os.path.join(source_root,'resource/pic')%mul_type,'0.jpg')

@ro_blueprint.route('/API/update',methods=['POST'])
def update():
    # try:
        if "source_data" in request.form:
            tmp_dict = request.form
            # print(tmp_dict)
        else:
            tmp_dict = request.json
            # print(tmp_dict)
        index = 0
        for source in tmp_dict['source_data']:
            
            if 'pid' not in tmp_dict:
                fp=open(os.path.join(source_root,'resource/%s/%s')%(source['mul_type'],source['filename']),'wb')
                source_data = source['data'].split(',')[-1]
                fp.write(base64.b64decode(source_data))
                new_data = {'pic_url':"/resource/pic/"+source['filename']}
                db.session.query(User).filter(User.uid==int(tmp_dict['uid'])).update(new_data)
            elif 'pid' in tmp_dict:
                if source['mul_type'] == 'pic':
                    fp=open(os.path.join(source_root,'resource/%s/%s')%(source['mul_type'],str(index)+source['filename']),'wb')
                    source_data = source['data'].split(',')[-1]
                    fp.write(base64.b64decode(source_data))
                    new_data = {'pic_url':"/resource/pic/"+source['filename']}
                    p_resource = P_resource(index=index,pid = int(tmp_dict['pid']),fid=int(tmp_dict['fid']),url='/resource/pic/'+str(index)+source['filename'])
                    db.session.add(p_resource)
                elif source['mul_type'] == 'video':
                    fp=open(os.path.join(source_root,'resource/%s/%s')%(source['mul_type'],source['filename']),'wb')
                    source_data = source['data'].split(',')[-1]
                    fp.write(base64.b64decode(source_data))
                    new_data = {'pic_url':"/resource/pic/"+source['filename']}
                    vs_resource = VS_resource(pid=int(tmp_dict['pid']),fid= int(tmp_dict['fid']),url='/resource/video/'+source['filename'])
                    db.session.add(vs_resource)
            index+=1
        db.session.commit()
        return jsonify({'update_state':1})
    # except Exception as e:
    #     print(str(e))
    #     return jsonify({'update_state':0})
        
 
