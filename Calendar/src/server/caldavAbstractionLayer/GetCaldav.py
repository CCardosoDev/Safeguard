import os
import caldav
from caldav.elements import dav
from caldav.lib import error
from flask import jsonify, make_response
from server.caldavAbstractionLayer.costumProperties import availableProps
from server.caldavAbstractionLayer.url import makeURL
from server.caldavAbstractionLayer.costumProperties import Dtstart, Location, Summary, Duration, RRule, Description
from datetime import datetime


def weeks_days_hours_minutes(td):
    return td.days // 7, td.days % 7, td.seconds // 3600, (td.seconds // 60) % 60

def getCaldavEvents(username, password, collectionName=None, calendarName=None , dateStart=None, dateEnd=None):

    url = makeURL(username, password, collectionName, calendarName);
    client = caldav.DAVClient(url)
    principal = caldav.Principal(client, url)
    try: 
        calendars = principal.calendars()
    except error.AuthorizationError:
        return make_response(jsonify({ 'error': 'Authentication failed' }), 401)
     
    result = {}   
    result['eventList'] = {} 
    
    for calendar in calendars:
        fullName = calendar.get_properties(props=[dav.Href()], depth=10)['{DAV:}href']
        if fullName[-1] == '/':
            basename = os.path.basename(fullName[:-1])
        else:
            basename = os.path.basename(fullName)
            
        result['eventList'][basename] = []
        
        if dateStart:
            events = calendar.date_search(dateStart, dateEnd)
        else:
            events = calendar.events()
        
        for event in events:
            event.load();
            propMap = event.get_properties(props=availableProps, depth=200)
            # event.instance.vevent.uid.value
            result['eventList'][basename].append({})
            result['eventList'][basename][-1]["uid"] = event.instance.vevent.uid.value
            
            for prop in availableProps:
                if prop.tag in propMap:
                    result['eventList'][basename][-1][prop.serviceName] = propMap[prop.tag]
            
            if Location().tag in propMap:
                result['eventList'][basename][-1][Location().serviceName] = propMap[Location().tag]
            
            if Summary().tag in propMap:
                result['eventList'][basename][-1][Summary().serviceName] = propMap[Summary().tag]

            # result['eventList'][basename][-1][Dtstart().serviceName]
              
            if Dtstart().tag in propMap:
                date = datetime.strptime(propMap[Dtstart().tag][:-3], '%Y-%m-%d %H:%M:%S')
                tempDateMap = {}
                tempDateMap["year"] = str(date.year) 
                tempDateMap["month"] = str(date.month)  if len(str(date.month)) == 2 else "0" + str(date.month)
                tempDateMap["day"] = str(date.day)    if len(str(date.day)) == 2 else "0" + str(date.day)
                tempDateMap["hour"] = str(date.hour)   if len(str(date.hour)) == 2 else "0" + str(date.hour)
                tempDateMap["minute"] = str(date.minute) if len(str(date.minute)) == 2 else "0" + str(date.minute)
                tempDateMap["second"] = str(date.second) if len(str(date.second)) == 2 else "0" + str(date.second)
                tempDateMap["timezone"] = "WET"
                result['eventList'][basename][-1][Dtstart().serviceName] = tempDateMap
           
                # print event.data 
                 
            if RRule().tag in propMap:
                rruleString = propMap[RRule().tag]
                if rruleString:
                    rruleMap = {}
                    for param in rruleString.split(";"):
                        (name, value) = param.split("=")
                        rruleMap[name.lower()] = value
                    result['eventList'][basename][-1][RRule().serviceName] = rruleMap
                    
            if Description().tag in propMap:
                result['eventList'][basename][-1][Description().serviceName] = propMap[Description().tag]   
              
            try:      
                (weeks, days, hours, minutes) = weeks_days_hours_minutes(event.instance.vevent.duration.value)
                            
                result['eventList'][basename][-1][Duration().serviceName] = {}  # str(event.instance.vevent)
                result['eventList'][basename][-1][Duration().serviceName]["week"] = str(weeks)
                result['eventList'][basename][-1][Duration().serviceName]["day"] = str(days)
                result['eventList'][basename][-1][Duration().serviceName]["hour"] = str(hours)
                result['eventList'][basename][-1][Duration().serviceName]["minute"] = str(minutes)
            except:
                pass
            
    return make_response(jsonify(result), 200)















