vcal = """BEGIN:VCALENDAR
VERSION:2.0
PRODID:ES_Cal_POWER
BEGIN:VEVENT
LOCATION:%s
DTSTART:%s
RRULE:%s
DURATION:%s
SUMMARY:%s
DESCRIPTION:%s
END:VEVENT
END:VCALENDAR
"""



def makeEventFromJson(jsonData):

    location = jsonData["event"]["location"]

    dtstart  = jsonData["event"]["dtstart"]["year"]
    dtstart += jsonData["event"]["dtstart"]["month"]
    dtstart += jsonData["event"]["dtstart"]["day"]
    dtstart += "T"
    dtstart += jsonData["event"]["dtstart"]["hour"]
    dtstart += jsonData["event"]["dtstart"]["minute"]
    dtstart += jsonData["event"]["dtstart"]["second"]
    dtstart += jsonData["event"]["dtstart"]["timezone"]
    dtstart = dtstart.upper()
    
    
    rrule  = ""
    rrule  += "FREQ="  + jsonData["event"]["rrule"]["freq"]  
    rrule  +=  ";COUNT=" + jsonData["event"]["rrule"]["count"]
    if "interval" in jsonData["event"]["rrule"]:
        rrule  +=  ";INTERVAL=" + jsonData["event"]["rrule"]["interval"]
    if "byday" in jsonData["event"]["rrule"]:
        rrule  +=  ";BYDAY=" + jsonData["event"]["rrule"]["byday"]
    rrule = rrule.upper()
    
    
    duration  = "P"
    duration += jsonData["event"]["duration"]["week"]   + "W"
    duration += jsonData["event"]["duration"]["day"]    + "D"
    duration += jsonData["event"]["duration"]["hour"]  + "H"
    duration += jsonData["event"]["duration"]["minute"] + "M"
    
    summary  = jsonData["event"]["summary"]
    
    description = ""
    if "description" in jsonData["event"]:
        description = jsonData["event"]["description"]

    
    return vcal % (location, dtstart, rrule, duration, summary, description)
