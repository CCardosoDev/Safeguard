from flask import Flask, request

from dateutil import parser

from caldavAbstractionLayer.GetCaldav import getCaldavEvents
from caldavAbstractionLayer.RemoveCaldav import removeCaldavEvent, removeCaldavCalendar, removeCaldavCollection
from caldavAbstractionLayer.InsertCaldav import insertCaldavEvent, insertCaldavCalendar, insertCaldavCollection

import logging
from logging import Formatter, FileHandler
from flask.helpers import make_response
from auth import getUserId

'''                
http://flask.pocoo.org/snippets/111/'''                         
                             
app = Flask(__name__)
caldavUser = "daba"
caldavPassword = "daba"

@app.route("/")
def hello(): 
    return "SAFEGUARD"

#collection <-> user management
@app.route('/user', methods = ['POST'], strict_slashes=False)
def insertUser():
    token = request.args.get('token')
    if not token:
        return make_response("No token query parameter", 404)
    
    userID = getUserId(token)
    if not userID:
        return make_response("User not found", 404)   
    
    return insertCaldavCollection(caldavUser, caldavPassword, userID);

#NOT OK
@app.route('/user', methods = ['DELETE'], strict_slashes=False)
def removeUser():

    token = request.args.get('token')
    if not token:
        return make_response("No token query parameter", 404)
    
    userID = getUserId(token)
    if not userID:
        return make_response("User not found", 404)  
    return removeCaldavCollection(caldavUser, caldavPassword, userID);



#Calendar <-> profile management


@app.route('/profile/<string:profileID>', methods = ['POST'], strict_slashes=False)
def insertProfile(profileID):
    token = request.args.get('token')
    if not token:
        return make_response("No token query parameter", 404)
    
    userID = getUserId(token)
    if not userID:
        return make_response("User not found", 404)  
    return insertCaldavCalendar(caldavUser, caldavPassword, userID, profileID);

@app.route('/profile/<string:profileID>', methods = ['DELETE'], strict_slashes=False)
def removeProfile(profileID):
    token = request.args.get('token')
    if not token:
        return make_response("No token query parameter", 404)
    
    userID = getUserId(token)
    if not userID:
        return make_response("User not found", 404) 
    return removeCaldavCalendar(caldavUser, caldavPassword, userID, profileID); 



@app.route('/event/<string:profileID>/<string:eventID>', methods = ['DELETE'], strict_slashes=False)
def removeEvent(profileID, eventID):
    
    token = request.args.get('token')
    if not token:
        return make_response("No token query parameter", 404)
    
    userID = getUserId(token)
    if not userID:
        return make_response("User not found", 404) 
    
    return removeCaldavEvent(caldavUser, caldavPassword, userID,profileID, eventID)


@app.route('/event/<string:profileID>', methods = ['POST'], strict_slashes=False)
def insertEvent(profileID):
    token = request.args.get('token')
    if not token:
        return make_response("No token query parameter", 404)
    
    userID = getUserId(token)
    if not userID:
        return make_response("User not found", 404) 
    
    return insertCaldavEvent(caldavUser, caldavPassword, userID, profileID,request.json);


'''Depending on the inputs will filter'''
@app.route('/event/', methods = ['GET'], strict_slashes=False)
@app.route('/event/<string:profileID>', methods = ['GET'], strict_slashes=False)
@app.route('/event/<string:profileID>/<string:startDateString>', methods = ['GET'], strict_slashes=False)
@app.route('/event/<string:profileID>/<string:startDateString>/<string:endDateString>', methods = ['GET'], strict_slashes=False)
def get(profileID = None, startDateString = None, endDateString = None):
    
    token = request.args.get('token')
    if not token:
        return make_response("No token query parameter", 404)
    
    userID = getUserId(token)
    if not userID:
        return make_response("User not found", 404)   
    
    if startDateString:
        startDate = parser.parse(startDateString)
    else:
        startDate = None    
    if endDateString:
        endDate = parser.parse(endDateString)
    else:
        endDate = None
    return getCaldavEvents(caldavUser, caldavPassword,userID, profileID, startDate, endDate)


if __name__ == "__main__":
    app.run(host='localhost', port = 5014,debug = True) #, ssl_context=context



if not app.debug: 
    file_handler = FileHandler("/var/log/calendar");
    file_handler.setLevel(logging.INFO)
    file_handler.setFormatter(Formatter(
    '%(asctime)s %(levelname)s: %(message)s '
    '[in %(pathname)s:%(lineno)d]'
    ))
    app.logger.addHandler(file_handler)
    
#10.10.0.37    
#127.0.0.1    
    