import time
import requests
from bs4 import BeautifulSoup
import pypyodbc
import datetime
import json
import pandas as pd
def translate(text):
        i = 0
        temp = ""
        for char in text:

            if char == 'ı' or char == 'I' or char == 'İ':
                char = 'i'
            elif char == 'ğ' or char == 'Ğ':
                char = 'g'
            elif char == 'ü' or char == 'Ü':
                char = 'u'
            elif char == 'ş' or char == 'Ş':
                char = 's'
            elif char == 'ö' or char == 'Ö':
                char = 'o'
            elif char == 'ç' or char == 'Ç':
                char = 'c'
            elif char == '.':
                char = '%'
            temp += str.lower(char)
            i += 1
        return temp


iller = ["", "Adana", 'Adıyaman', 'Afyon', 'Ağrı', 'Amasya', 'Ankara', 'Antalya', 'Artvin',
             'Aydın', 'Balıkesir', 'Bilecik', 'Bingöl', 'Bitlis', 'Bolu', 'Burdur', 'Bursa', 'Çanakkale',
             'Çankırı', 'Çorum', 'Denizli', 'Diyarbakır', 'Edirne', 'Elazığ', 'Erzincan', 'Erzurum', 'Eskişehir',
             'Gaziantep', 'Giresun', 'Gümüşhane', 'Hakkari', 'Hatay', 'Isparta', 'Mersin', 'İstanbul', 'İzmir',
             'Kars', 'Kastamonu', 'Kayseri', 'Kırklareli', 'Kırşehir', 'Kocaeli', 'Konya', 'Kütahya', 'Malatya',
             'Manisa', 'Kahramanmaraş', 'Mardin', 'Muğla', 'Muş', 'Nevşehir', 'Niğde', 'Ordu', 'Rize', 'Sakarya',
             'Samsun', 'Siirt', 'Sinop', 'Sivas', 'Tekirdağ', 'Tokat', 'Trabzon', 'Tunceli', 'Şanlıurfa', 'Uşak',
             'Van', 'Yozgat', 'Zonguldak', 'Aksaray', 'Bayburt', 'Karaman', 'Kırıkkale', 'Batman', 'Şırnak',
             'Bartın', 'Ardahan', 'Iğdır', 'Yalova', 'Karabük', 'Kilis', 'Osmaniye', 'Düzce']

illerop = ["Adana", 'Adıyaman', 'Afyon', 'Ağrı', 'Amasya', 'Ankara', 'Antalya', 'Artvin',
               'Aydın', 'Balıkesir', 'Bilecik', 'Bingöl', 'Bitlis', 'Bolu', 'Burdur', 'Bursa', 'Çanakkale',
               'Çankırı', 'Çorum', 'Denizli', 'Diyarbakır', 'Edirne', 'Elazığ', 'Erzincan', 'Erzurum', 'Eskişehir',
               'Gaziantep', 'Giresun', 'Gümüşhane', 'Hakkari', 'Hatay', 'Isparta', 'Mersin', 'Avrupa', 'Anadolu',
               'İzmir','Kars', 'Kastamonu', 'Kayseri', 'Kırklareli', 'Kırşehir', 'Kocaeli', 'Konya', 'Kütahya', 'Malatya',
               'Manisa', 'Kahramanmaraş', 'Mardin', 'Muğla', 'Muş', 'Nevşehir', 'Niğde', 'Ordu', 'Rize', 'Sakarya',
               'Samsun', 'Siirt', 'Sinop', 'Sivas', 'Tekirdağ', 'Tokat', 'Trabzon', 'Tunceli', 'Şanlıurfa', 'Uşak',
               'Van', 'Yozgat', 'Zonguldak', 'Aksaray', 'Bayburt', 'Karaman', 'Kırıkkale', 'Batman', 'Şırnak',
               'Bartın', 'Ardahan', 'Iğdır', 'Yalova', 'Karabük', 'Kilis', 'Osmaniye', 'Düzce']



x = datetime.datetime.now()
x = x.strftime("%x")
log = open("C:\\temp\\logsyncopet.txt", "a")
with open("config.json") as json_data_file:
    cfg=json.load(json_data_file)
    json_data_file.close()
conn = pypyodbc.connect("DRIVER={SQL Server}; Server="+cfg['sqlserver']['server']+";User="+cfg['sqlserver']['user']+"; Password="+cfg['sqlserver']['passwd']+";Database="+cfg['sqlserver']['db'])

cursor = conn.cursor()
guncel = cursor.execute("exec getLastDay 'Opet'").fetchone()
sday = guncel[0]
log.write("######tarih: " + str(x) + "#######\n")

#if sday != x:
for il in illerop:
    try:
        url= "https://www.opet.com.tr/AjaxProcess/GetFuelPricesList?Cityname="+il
        R= requests.post(url)
        soup = BeautifulSoup(R.text, "html.parser")
        script_text = soup.findAll('script')
        relevant = script_text
        text=soup.text.strip()[8:-1]
        data= json.loads(text)
        df= pd.read_json(json.dumps(data))

        for x in df.index:

            print(translate(df['_IlceAd'][x])+","+str(df["_Kursunsuz95"][x])+","+str(df["_Motorin"][x]))
            cursor.execute("exec AddtoOpet @il='"+il+"',@ilce='"+translate(df['_IlceAd'][x])+"',@benzin='"+str(df['_Kursunsuz95'][x])+"',@motorin='"+str(df['_Motorin'][x])+"';").commit()
            time.sleep(0.1)
        time.sleep(0.3)
    except:
        log.write(str(datetime.datetime.now())+" When fetching"+ str(il)+", "+ df['_IlceAd']+" something went wrong.")
conn.close()
log.close()

