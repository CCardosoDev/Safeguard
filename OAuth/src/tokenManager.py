from M2Crypto import EVP
from base64 import b64encode
import os


def makeToken(keyLocation, db, clientId, service, androidId=None):
    key = EVP.load_key(keyLocation)

    token = {}
    token["Id"] = clientId
    toSign = clientId
    
    if androidId is not None:
        token["androidId"] = androidId
        toSign += androidId
    
    token["service"] = service
    toSign += service
    
    token["nonce"] = b64encode(os.urandom (64))
    toSign += token["nonce"]
    
    key.sign_init()
    key.sign_update(toSign)
    signature = key.sign_final()
    
    token["signature"] = b64encode(signature)
    
    return token