def makeURL(clientID, password, collection = None,calendar = None):
#http://192.168.8.217:7701/davical/admin.php
    link = "localhost:80/davical/caldav.php"
    #link = "192.168.8.217:7701/davical/caldav.php"
    url = "http://%s:%s@%s/%s" % (clientID, password, link, clientID)
    if collection:
        url += "/" + collection
        if calendar:
            url +=  "/" + calendar
    return url   