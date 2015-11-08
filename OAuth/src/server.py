from flask import Flask, redirect
from OpenSSL import SSL
from oauth2client.client import flow_from_clientsecrets
from flask.globals import request
from flask.helpers import make_response
from flask.json import jsonify
from urlparse import urlparse
from databaseLayer import registerToken, getServiceName, getClientData,\
    serviceExists
import httplib2
from apiclient.discovery import build
import logging
from logging import Formatter, FileHandler
from authDecorator import requires_auth
import urllib2

#from apiclient.discovery import build 
#from flask.helpers import make_response


context = SSL.Context(SSL.SSLv23_METHOD)
context.use_privatekey_file('../calSafeguard.pem')
context.use_certificate_file('../CalSafeguard.crt')
tokenJSONPath ='../clientSecretGoogle'

app = Flask(__name__)

@app.route("/")
def hello():
    return "SAFEGUARD"

#login
@app.route("/login")
def login1():    
    redirectURL = request.args.get('redirecturl')
    if not redirectURL:
        return make_response("No redirecturl query parameter", 404)
    
    flow = flow_from_clientsecrets(tokenJSONPath,
                                   scope='openID https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email',
                                   redirect_uri='https://safeguard.192.168.8.217.xip.io:7704/loginCode') 
    
    auth_uri = flow.step1_get_authorize_url() + "&state=" + redirectURL
    
    androidID = request.args.get('androidid')
    if androidID: 
        auth_uri += "_androidid_" + androidID
    
    return redirect(auth_uri, code=302)

@app.route("/loginCode")
def login2():
    redirectURL = request.args.get('state')
    code = request.args.get('code')
    
    if not redirectURL or not code:
        return make_response("Make sure to include state and code query parameters", 404) 
    
    flow = flow_from_clientsecrets(tokenJSONPath, 
                                   scope='openID https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email',
                                   redirect_uri='https://safeguard.192.168.8.217.xip.io:7704/loginCode')
    
    try:
        credentials = flow.step2_exchange(code)
    except:
        return make_response("Failed authentication @ google", 401)
    
    
    http = httplib2.Http()
    http = credentials.authorize(http)
    #service = build("plus", "v1", http=http)
    #data = service.people().get(userId='me').execute()
    
    service = build("oauth2", "v2", http=http)
    data = service.userinfo().get().execute()
    
    androidID = None
    redirectSplit = redirectURL.split("_androidid_")
    if len(redirectSplit) > 1:
        redirectURL = redirectSplit[0]  
        androidID   = redirectSplit[1]
    
    if androidID:
        accesstoken = registerToken("mobile", "composition", data, androidID)
    else:
        accesstoken = registerToken("composition", "composition", data, androidID)
    if not accesstoken:
        return make_response("Failed to register token", 401)
    
    if not androidID:
        if urlparse(redirectURL)[4]:
            redirectURL += '&token=' + accesstoken
        else:
            redirectURL += '?token=' + accesstoken
    else:
        redirectURL = urllib2.unquote(redirectURL) + accesstoken
    
    #criar uma entrada para composicao {clientID, retrive_code} 
    return redirect(redirectURL, code=302)
    
#request ser
@app.route("/getData")
@requires_auth
def getData():
    #id/pass cabechalho http
    #token query string
    #eu sou o servico id/pass e tenho este token para acesso
    #da-me um clientID
    
    token = request.args.get('token')
    if not token:
        return make_response("No token query parameter", 404)
    
    #verificar auth do server
    #recuperar nome do servico
    service = getServiceName(request.authorization.username, request.authorization.password)
    if not service:
        return make_response("failed authentication", 401)
    
    #recuperar o clientID
    
    #print service
    userData = getClientData(service, token)
    if not userData:
        return make_response("Client not found", 404)
    
    body = {} 
    body["userData"] = userData
    return make_response(jsonify(body), 200)

@app.route("/getServerAccess")
@requires_auth
def serviceAccess():
    #id/pass cabechalho http
    #clientID cabecalho
    #service query
    token = request.args.get('token')
    if not token:
        return make_response("No token query parameter", 404)
    serviceRequested = request.args.get('service')
    
    if not serviceExists(serviceRequested):
        return make_response("Service not found", 404)   
    
    if not token:
        return make_response("No service query parameter", 404)
    
    #verificar auth do server
    #recuperar nome do servico
    service = getServiceName(request.authorization.username, request.authorization.password)
    if not service:
        return make_response("Failed authentication", 401)
    
    #verificar -> NOT
    userData = getClientData(service, token)
    if not userData:
        return make_response("Client id not found", 404)
    

    clientId = {"id" : userData["id"]} 
    accessToken =  registerToken(service,serviceRequested, clientId)
    
     
    body = {"token":accessToken}
    return make_response(jsonify(body), 200)

#proxy_pass http://127.0.0.1:8000/;
if __name__ == "__main__":
    app.run(host='192.168.215.216', port = 5000,debug = True , ssl_context=context) #, ssl_context=context

if not app.debug:
    file_handler = FileHandler("/var/log/oauth");
    file_handler.setLevel(logging.INFO)
    file_handler.setFormatter(Formatter(
    '%(asctime)s %(levelname)s: %(message)s '
    '[in %(pathname)s:%(lineno)d]'
    ))
    app.logger.addHandler(file_handler)
    
    
    