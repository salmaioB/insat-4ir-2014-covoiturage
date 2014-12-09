################################################################################
# server/test/test_droid.py                                         2014-12-09 #
# Covoiturage Sopra - INSA Toulouse                               Félix Poisot #
################################################################################

# Simule rapidement les requêtes envoyées par l'application android.

# testé sous Python 3.4.1

from urllib.request import *

host = "http://localhost/"



def mkReq(command, payload):
   """command: url à taper sur le serveur.
      payload: données JSON à mettre dans le POST."""
   # encode(): utf-8 semble être l'encodage par défaut
   return Request(host+"android/"+command, payload.encode(),
                  {"Content-Type":"application/json"}, method="POST")


if __name__ == "__main__":

   if not host.endswith("/"):
      host += "/"

   print("login...")
   req = mkReq("login", '{"name":"whatsthepassword","pasword":"password"}')
   resp = urlopen(req)
   print(resp)
   print(resp.getheaders())
   
