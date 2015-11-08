import pymongo
import shortuuid
urlServer= "safeguard:safeguard@localhost"
url = "localhost"
port = 27017

compositionService = {"service":"composition"   , "consumerKey":"ADSLKDJSCD", "consumerSecret":"ASECSGJKMJHJHJH"}
calendarService =    {"service":"calendar"      , "consumerKey":"OLSADMASLD", "consumerSecret":"ASDASKGHVCBTRYY"}
geoRefService =      {"service":"georeferencing", "consumerKey":"LFGKDLFKGJ", "consumerSecret":"RETWERETYTUYGHN"}
mobileService =      {"service":"mobile"        , "consumerKey":"KSDJFHDSJF", "consumerSecret":"SKDJFNKDJFNSDKJ"}
profilesService =    {"service":"profiles"      , "consumerKey":"SDOIFIDNNA", "consumerSecret":"JJSHFPJDFOSIDFM"}
#connect          
connection = pymongo.Connection()

def resetDB():

    #clear old data
    connection.drop_database('safeguard')
    #create new database
    database = connection.safeguard
    
    #create the services collection
    serviceCollection = database.services
    
    #fill it
    serviceCollection.insert([compositionService, calendarService, geoRefService, mobileService, profilesService]);
    
    #INDEX services collection
    serviceCollection.ensure_index([('consumerKey', 1), ("consumerSecret", 1)])
    serviceCollection.ensure_index("service")
    
    #create token collection
    userData = database.userData
    
    #INDEX token collection
    userData.ensure_index("token")
    


def registerToken(requester, service, data, androidID = None):
    uuid = shortuuid.uuid()
    
    database = connection.safeguard
    userData = database.userData
    
    #delete old token
    #print data
    
    #print 'service', service
    #print 'requester', requester
    #print "data", "{'id':" ,data["id"] ,"}}"
    
    query = {'service': service, 'requester': requester,"data.id": data["id"], "data.androidid" : androidID}
    userData.remove(query)
    
    while list(userData.find({'token': uuid}).limit(1)):
        uuid = shortuuid.uuid()
    
    if androidID:
        data["androidid"] = androidID
    
    userData.insert({"token":uuid, "service" : service, 'requester': requester,"data" : data})
    
    return uuid

def getServiceName(consumerKey, consumerSecret):
    database = connection.safeguard 
    serviceCollection = database.services
    service = list(serviceCollection.find({'consumerKey': consumerKey, "consumerSecret":consumerSecret}).limit(1))
    
    if service:
        return service[0]["service"]
    
    return None
    

def getClientData(service, token):
    database = connection.safeguard
    serviceCollection = database.userData
    service = list(serviceCollection.find({'token': token, "service":service}).limit(1))
    
    if service:
        return service[0]["data"]
    
    return None    
    
def serviceExists(service):
    database = connection.safeguard
       
    serviceCollection = database.services
    serviceName = list(serviceCollection.find({"service":service}).limit(1))
    
    if serviceName:
        return True
    
    return False 
    
#resetDB()
#print registerToken("service", {"ola": "ola"})     
#print    getServiceName("OLSADMASLD", "ASDASKGHVCBTRYY")
#print getClientData("service", "4BqV6gnkXUSQuHaxdELkLN")




