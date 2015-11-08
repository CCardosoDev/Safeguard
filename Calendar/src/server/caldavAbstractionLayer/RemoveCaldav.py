import caldav
from caldav.lib import error
from flask import jsonify, make_response

from server.caldavAbstractionLayer.url import makeURL


def removeCaldavEvent(username, password, collectionName, calendarName, eventID):
    url = makeURL(username, password, collectionName, calendarName);
    client = caldav.DAVClient(url)
    principal = caldav.Principal(client, url)

    calendars = principal.calendars() 
    
    if len(calendars) == 0:
        return make_response(jsonify( { 'error': 'calendar not found!' } ), 404)
    
    try:
        calendars[0].event(eventID).delete()
    except error.NotFoundError:
        return make_response(jsonify( { 'error': 'event uid: \'' + eventID + '\' not found!' } ), 404)
    return make_response("ok", 200)



def removeCaldavCalendar(username, password, collectionName, calendarName):
    url = makeURL(username, password, collectionName, calendarName);
    client = caldav.DAVClient(url)
    principal = caldav.Principal(client, url)

    calendars = principal.calendars()
    
    if len(calendars) == 0:
        return make_response(jsonify( { 'error': 'calendar not found!' } ), 404)
    
    calendars[0].delete();
    return  make_response("ok", 200)



def removeCaldavCollection(username, password, collectionName):
    url = makeURL(username, password, collectionName);
    client = caldav.DAVClient(url)
    principal = caldav.Principal(client, url)

    calendars = principal.calendars()
    
    if len(calendars) == 0:
        return make_response(jsonify( { 'error': 'collection not found!' } ), 404)
    
    calendars[0].delete();
    return  make_response("ok", 200)

