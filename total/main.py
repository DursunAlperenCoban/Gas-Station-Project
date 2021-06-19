import requests
from bs4 import BeautifulSoup as s
import pyodbc
import time
import locale
locale.setlocale(locale.LC_ALL, 'tr_TR')




def translate(text):
  i = 0
  temp = ""
  for char in text:

    if char == '?' or char == 'I' or char == '?' or char =='Ý':
      char = 'i'
    elif char == '?' or char == '?' or char=='Ð':
      char = 'g'
    elif char == 'ü' or char == 'Ü':
      char = 'u'
    elif char == '?' or char == '?' or char=='Þ':
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

conn = pyodbc.connect("DRIVER={SQL Server}; Server=DSK6;User=sa;Password=1234;Database=GasStation")
log=open("c:\\temp\\total.txt","w",encoding="iso8859-9")

cityID=1
while cityID <82:
  Url= "http://195.216.232.22/product_price.asp?cityID="+str(cityID)
  R= requests.get(Url)
  Soup=s(R.text,"html.parser")
  List=Soup.find_all("table",{"border":"1"})
  i=1
  ctr=9
  cursor = conn.cursor()
  soup = s(str(List),"html.parser")
  List = soup.find_all("td")


  while len(List)/9>i:
    il= translate(List[0].text)
    ilce= translate(List[ctr*i].text)
    if (List[1+(ctr*i)].text) != '-':
      benzin= locale.atof(List[1+(ctr*i)].text)
    else:
      benzin = None
    if (List[3+(ctr*i)].text)!='-':
      motorin= locale.atof(List[3+(ctr*i)].text)
    else:
      motorin=None
    if (List[8+(ctr*i)].text)!= '-':
      otogaz=locale.atof(List[8+(ctr*i)].text)
    else:
      otogaz=None

    print(il+" "+ilce+" "+str(benzin)+" "+str(motorin)+" "+str(otogaz)+"\n")
    log.write(il+" "+ilce+" "+str(benzin)+" "+str(motorin)+" "+str(otogaz)+"\n")
    cursor.execute("exec AddtoTotal @il=N'"+il+"', @ilce=N'"+ilce+"', @benzin=N'"+str(benzin)+"', @motorin=N'"+str(motorin)+"', @otogaz=N'"+str(otogaz)+"';").commit()
    time.sleep(0.1)
    i+=1
  cityID+=1
conn.close()
