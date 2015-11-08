
import caldav
from flask import jsonify, make_response
from server.caldavAbstractionLayer.url import makeURL
from server.caldavAbstractionLayer.jsonHandler import makeEventFromJson

def insertCaldavEvent(username, password, collectionName, calendarName,data):
    
    url = makeURL(username, password, collectionName, calendarName)

    client = caldav.DAVClient(url)    
    principal = caldav.Principal(client, url)
    calendars = principal.calendars()  
    
    if len(calendars) == 0: 
        return make_response(jsonify( { 'error': 'calendar not found!' } ), 404)
   
      
    eventData = makeEventFromJson(data) 
    caldav.Event(client, data = eventData, parent = calendars[0]).save();
    
    return  make_response("ok", 200)

def insertCaldavCalendar(username, password, collectionName, calendarName): 
     
    url = makeURL(username, password, collectionName); 
    client = caldav.DAVClient(url) 
    principal = caldav.Principal(client, url)
    caldav.Calendar(client, parent=principal, id=calendarName, name=calendarName).save()
    #calendar.set_properties([ cdav.Calendar(), cdav.CalendarData(),cdav.CalendarDescription("calDesc")])

    return  make_response("ok", 200)


def insertCaldavCollection(username, password, collectionName): 
     
    url = makeURL(username, password, None); 
    client = caldav.DAVClient(url) 
    principal = caldav.Principal(client, url)
    caldav.Collection(client, parent=principal, id=collectionName, name=collectionName).save()
    #calendar.set_properties([ cdav.Calendar(), cdav.CalendarData(),cdav.CalendarDescription("calDesc")])
    
    return  make_response("ok", 200)