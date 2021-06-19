def predtofirebase(cursor,db,time):
    df=cursor.execute("select * from future where date= (select max(date) from future);").fetchall()
    print("Predictions\n")
    for item in df:
        date=str(item[1])
        benzin=str(item[2])
        motorin=str(item[3])
        otogaz =str(item[4])
        rmse_b = str(item[5])
        rmse_m =str(item[6])
        rmse_o = str(item[7])
        print(date + " " + benzin + " " + motorin +" "+otogaz+ " " +rmse_b+" "+rmse_m+" "+rmse_o+"\n")

        doc_ref = db.collection(u'Predictions').document(date)
        doc_ref.set({
            u'benzin': benzin,
            u'motorin': motorin,
            u'otogaz': otogaz,
            u'rmse_b':rmse_b,
            u'rmse_m':rmse_m,
            u'rmse_o':rmse_o
        })
        time.sleep(0.1)