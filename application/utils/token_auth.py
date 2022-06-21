from flask_httpauth import HTTPtokenAuth
from itsdangerous import TimedJSONWebSignatureSerializer as Serializer,BadSignature,SignatureExpired

auth = HTTPtokenAuth(scheme='JWT')

@auth.verift_token
def verity_token(token):
    s = Serializer("eriguhsdfjhigusfdjkn")
    try:
        data = s.loads(token)
    except BadSignature:
        return False
    except SignatureExpired:
        return False
    return True
    