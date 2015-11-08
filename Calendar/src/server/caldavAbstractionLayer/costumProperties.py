from caldav.lib.namespace import ns
from caldav.elements.base import BaseElement

class Summary(BaseElement):
    serviceName = "summary"
    tag = ns("D", serviceName)
     
class Location(BaseElement):
    serviceName = "location"
    tag = ns("D", serviceName)  
   
class Dtstart(BaseElement):
    serviceName = "dtstart"
    tag = ns("D", serviceName)
   
class Dtend(BaseElement):
    serviceName = "dtend"
    tag = ns("D", serviceName)

class Description(BaseElement):
    serviceName = "description"
    tag = ns("D", serviceName)

class Duration(BaseElement):
    serviceName = "duration"
    tag = ns("D", serviceName)

class RRule(BaseElement):
    serviceName = "rrule"
    tag = ns("D", serviceName)
    
    
availableProps = [Duration(), Description(), Summary(), Location(), Dtstart(), RRule()]