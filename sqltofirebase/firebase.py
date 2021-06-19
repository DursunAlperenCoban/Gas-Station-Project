import pyodbc
import time
import firebase_admin
from firebase_admin import credentials
import locale
from datetime import datetime
import json
import bp
import future
import opet
import shell
import total
import tp

locale.setlocale(locale.LC_ALL, 'tr_TR')
with open("config.json") as json_data_file:
    cfg=json.load(json_data_file)
json_data_file.close()
date = datetime.now()
date = date.strftime("%x")
cred = credentials.Certificate(cfg['googleapi']['cred'])
firebase_admin.initialize_app(cred)
from firebase_admin import firestore
db = firestore.client()
conn = pyodbc.connect("DRIVER={SQL Server}; Server="+cfg['sqlserver']['server']+";User="+cfg['sqlserver']['user']+"; Password=" +cfg['sqlserver']['passwd']+";Database="+cfg['sqlserver']['db'])
cursor=conn.cursor()
try:
    with open('logSyncfirebase.json')as json_file:
       dataj=json.load(json_file)
except:
   dataj={}
data={}
data['firebaseSync'] = []
try:
    opet.opettofirebase(cursor,db,time)
    data['firebaseSync'].append({
         'brand':'Opet',
        'date':str(date),
        'description':'synctofirebase',
          'status':'success'
    })
except:
    print("Something went wrong on sync Opet to firebase")
    data['firebaseSync'].append({
        'brand': 'Opet',
        'date': str(date),
        'description': 'synctofirebase',
        'status': 'something went wrong'
    })
try:
    bp.bptofirebase(cursor,db,time)
    data['firebaseSync'].append({
        'brand':'BP',
        'date': str(date),
        'description':'synctofirebase',
        'status':'success'
    })
except:
    print("Something went wrong on sync BP to firebase")
    data['firebaseSync'].append({
        'brand': 'BP',
        'date': str(date),
        'description': 'synctofirebase',
        'status': 'something went wrong'
    })
try:
    tp.turkPetroltofirebase(cursor,db,time)
    data['firebaseSync'].append({
        'brand': 'Turkpetrol',
        'date': str(date),
        'description': 'synctofirebase',
        'status': 'success'
    })
except:
    print("Something went wrong on sync TürkPetrol to firebase")
    data['firebaseSync'].append({
        'brand': 'Turkpetrol',
        'date': str(date),
        'description': 'synctofirebase',
        'status': 'something went wrong'
    })
try:
    total.totaltofirebase(cursor,db,time)
    data['firebaseSync'].append({
        'brand': 'Total',
        'date': str(date),
        'description': 'synctofirebase',
        'status': 'success'
    })
except:
    print("Something went wrong on sync Total to firebase")
    data['firebaseSync'].append({
        'brand': 'Total',
        'date': str(date),
        'description': 'synctofirebase',
        'status': 'something went wrong'
    })
try:
    shell.shelltofirebase(cursor,db,time)
    data['firebaseSync'].append({
        'brand': 'Shell',
        'date': str(date),
        'description': 'synctofirebase',
        'status': 'success'
    })
except:
    print("Something went wrong on sync Shell to firebase")
    data['firebaseSync'].append({
        'brand': 'Shell',
        'date': str(date),
        'description': 'synctofirebase',
        'status': 'something went wrong'
    })

future.predtofirebase(cursor,db,time)

dataj.update(data)
with open("logSyncfirebase.json","w") as json_log_file:
    json.dump(dataj,json_log_file)
conn.close()