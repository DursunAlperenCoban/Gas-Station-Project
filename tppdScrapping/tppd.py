import locale
import time
import requests
from bs4 import BeautifulSoup
import pypyodbc
import datetime
locale.setlocale(locale.LC_ALL, 'nl_NL')
def translate(text):
    i=0
    temp = ""
    for char in text:

        if char == 'ı' or char =='I' or char =='İ':
            char = 'i'
        elif char == 'ğ' or char == 'Ğ':
            char = 'g'
        elif char == 'ü' or char == 'Ü':
            char = 'u'
        elif char == 'ş'or char == 'Ş':
            char = 's'
        elif char == 'ö' or char == 'Ö':
            char = 'o'
        elif char == 'ç' or char == 'Ç':
              char = 'c'
        elif char == '.':
            char='%'
        temp+=char
        i+=1
    return temp

iller= ["","Adana", 'Adıyaman', 'Afyon', 'Ağrı', 'Amasya', 'Ankara', 'Antalya', 'Artvin',
'Aydın', 'Balıkesir', 'Bilecik', 'Bingöl', 'Bitlis', 'Bolu', 'Burdur', 'Bursa', 'Çanakkale',
'Çankırı', 'Çorum', 'Denizli', 'Diyarbakır', 'Edirne', 'Elazığ', 'Erzincan', 'Erzurum', 'Eskişehir',
'Gaziantep', 'Giresun', 'Gümüşhane', 'Hakkari', 'Hatay', 'Isparta', 'Mersin', 'İstanbul', 'İzmir',
'Kars', 'Kastamonu', 'Kayseri', 'Kırklareli', 'Kırşehir', 'Kocaeli', 'Konya', 'Kütahya', 'Malatya',
'Manisa', 'Kahramanmaraş', 'Mardin', 'Muğla', 'Muş', 'Nevşehir', 'Niğde', 'Ordu', 'Rize', 'Sakarya',
'Samsun', 'Siirt', 'Sinop', 'Sivas', 'Tekirdağ', 'Tokat', 'Trabzon', 'Tunceli', 'Şanlıurfa', 'Uşak',
'Van', 'Yozgat', 'Zonguldak', 'Aksaray', 'Bayburt', 'Karaman', 'Kırıkkale', 'Batman', 'Şırnak',
'Bartın', 'Ardahan', 'Iğdır', 'Yalova', 'Karabük', 'Kilis', 'Osmaniye', 'Düzce']

illerop= ["Adana", 'Adıyaman', 'Afyon', 'Ağrı', 'Amasya', 'Ankara', 'Antalya', 'Artvin',
'Aydın', 'Balıkesir', 'Bilecik', 'Bingöl', 'Bitlis', 'Bolu', 'Burdur', 'Bursa', 'Çanakkale',
'Çankırı', 'Çorum', 'Denizli', 'Diyarbakır', 'Edirne', 'Elazığ', 'Erzincan', 'Erzurum', 'Eskişehir',
'Gaziantep', 'Giresun', 'Gümüşhane', 'Hakkari', 'Hatay', 'Isparta', 'Mersin', 'Avrupa','Anadolu', 'İzmir',
'Kars', 'Kastamonu', 'Kayseri', 'Kırklareli', 'Kırşehir', 'Kocaeli', 'Konya', 'Kütahya', 'Malatya',
'Manisa', 'Kahramanmaraş', 'Mardin', 'Muğla', 'Muş', 'Nevşehir', 'Niğde', 'Ordu', 'Rize', 'Sakarya',
'Samsun', 'Siirt', 'Sinop', 'Sivas', 'Tekirdağ', 'Tokat', 'Trabzon', 'Tunceli', 'Şanlıurfa', 'Uşak',
'Van', 'Yozgat', 'Zonguldak', 'Aksaray', 'Bayburt', 'Karaman', 'Kırıkkale', 'Batman', 'Şırnak',
'Bartın', 'Ardahan', 'Iğdır', 'Yalova', 'Karabük', 'Kilis', 'Osmaniye', 'Düzce']



x = datetime.datetime.now()
x = x.strftime("%x")
log=open("C:\\temp\\logsyncturkpetrol.txt","a")

conn = pypyodbc.connect("DRIVER={SQL Server}; Server=DSK6;User=sa;Password=1234;Database=GasStation")
cursor = conn.cursor()
guncel = cursor.execute("exec getLastDay 'turkpetrol'").fetchone()
if guncel:
    print(guncel[0])
    sday=guncel[0]
else:
    sday=""
log.write("######tarih: "+str(x)+"#######\n")
print(x)
if sday!=x:
    ilID=1
    log.write("\nSync Started\n")
    while ilID < 82:
        Url = "https://www.tppd.com.tr/tr/akaryakit-fiyatlari?id=" + str(ilID)
        R = requests.get(Url)
        Soup = BeautifulSoup(R.text, "html.parser")
        List = Soup.find_all("td", {"class": "numeric"})
        ctr = 0
        i = 1
        total = 0
        List2 = Soup.find_all("td", {"data-title": "İLÇE"})
        j = 0


        while len(List) > ctr:
            ilce = translate(str.lower(List2[j].text))
            benzin= str(locale.atof(List[0 + ctr].text[2:6]))
            motorin=str(locale.atof(List[3 + ctr].text[2:6]))
            otogaz= str(locale.atof(List[7 + ctr].text[2:6]))
            cursor.execute("exec AddtoTurkPetrol @il='" + str(ilID) + "', @ilce='" + ilce + "', @benzin='" + benzin + "', @motorin='" + motorin + "', @otogaz='" + otogaz + "';").commit()
            log.write(ilce+" "+benzin+" "+motorin+" "+otogaz)
            print("Tarih :"+x + " ilID: "+str(ilID)+" ilçe: "+ilce+" benzin: "+benzin+" motorin: "+motorin+" otogaz: "+otogaz)
            i += 1
            j += 1
            ctr += 8
            time.sleep(0.1)
        ilID += 1

    log.write("\nSync Finished\n")
else:
    log.write("\nSync Already done\n")
conn.close()
log.write("\nEnd of Date\n")
log.close()






