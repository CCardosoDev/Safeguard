import requests
from requests.auth import HTTPBasicAuth

username = 'OLSADMASLD'
password = 'ASDASKGHVCBTRYY'
link =  'https://127.0.0.1:5000/getData'
#link2 = 'https://192.168.8.217:7704/getData'
def getUserId(token):
    payload = {'token': token} 
    
    r = requests.get(link, auth=HTTPBasicAuth(username, password), params = payload, verify=False)
    
    if r.status_code == requests.codes.ok:  
        return r.json()["userData"]["id"]; 
    else:
        return None