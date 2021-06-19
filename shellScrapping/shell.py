import pyodbc
import time
import locale
from selenium import webdriver
from bs4 import BeautifulSoup as soup
import json



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

PATH= "C:\Program Files (x86)\chromedriver.exe"
driver = webdriver.Chrome(PATH)

driver.get("https://www.turkiyeshell.com/pompatest/")
print(driver.title)
time.sleep(0.5)
i=35
count=1
with open("config.json") as json_data_file:
    cfg=json.load(json_data_file)
    json_data_file.close()
conn = pyodbc.connect("DRIVER={SQL Server}; Server="+cfg['sqlserver']['server']+";User="+cfg['sqlserver']['user']+"; Password="+cfg['sqlserver']['passwd']+";Database="+cfg['sqlserver']['db'])
cursor=conn.cursor()
while i<76:
    ddcrs = driver.find_element_by_id("cb_all_cb_province_B-1").click()
    ddl = driver.find_element_by_xpath('//*[@id="cb_all_cb_province_DDD_L_LBT"]/tbody/tr[' + str(i) + ']')
    time.sleep(0.2)
    il=translate(str(ddl.text))
    print(il)
    ddl.click()
    i += 1
    time.sleep(0.5)
    page= driver.page_source
    liste = driver.find_elements_by_id('cb_all_grdPrices_DXMainTable')
    time.sleep(0.3)
    liste= soup(page,"html.parser")
    liste=liste.find("table",{"id":"cb_all_grdPrices_DXMainTable"})
    liste= liste.find_all('td')
    time.sleep(0.3)
    print(len(liste))
    item =29

    while item <len(liste):
        ilce=translate(str(liste[item-9].text))
        benzin=str(liste[item-8].text)
        motorin=str(liste[item-6].text)
        otogaz=str(liste[29].text)
        print(ilce+", "+benzin+", "+motorin+", "+otogaz+"\n")
        item+=10
        cursor.execute("exec AddtoShell @il='"+il+"', @ilce='"+ilce+"', @benzin='"+benzin+"', @motorin='"+motorin+"', @otogaz='"+otogaz+"';").commit()
        time.sleep(0.3)
        count+=1

print("count= "+str(count))
conn.close()
driver.quit()


'''
##Test Code
ddcrs = driver.find_element_by_id("cb_all_cb_province_B-1").click()
ddl = driver.find_element_by_xpath('//*[@id="cb_all_cb_province_DDD_L_LBT"]/tbody/tr[' + str(i) + ']')
time.sleep(0.2)
print(ddl.text)
ddl.click()
i += 1
time.sleep(0.5)
page= driver.page_source
liste = driver.find_elements_by_id('cb_all_grdPrices_DXMainTable')
time.sleep(0.3)
liste= soup(page,"html.parser")
liste=liste.find("table",{"id":"cb_all_grdPrices_DXMainTable"})
liste= liste.find_all('td')
time.sleep(0.3)
print(len(liste))
item =29
while item <len(liste):
    print(str(liste[item-9].text)+", "+str(parsenumber(liste[item-8].text=)+", "+str(liste[item-6].text)+", "+str(liste[item].text)+"\n")
    item+=10
time.sleep(1)


'''

