def totaltofirebase(cursor,db,time):
    df=cursor.execute("select ll.Il,lc.Ilce,t.benzin,t.motorin,t.otogaz from Total as t inner join Ilceler as lc on lc.IlceID=t.IlceID inner join Iller as ll on ll.IlID=lc.IlID where t.date=(select top(1) date from Total );").fetchall()
    print("Total\n")
    for item in df:
        il=str(item[0])
        ilce=str(item[1])
        benzin=str(item[2])
        motorin=str(item[3])
        otogaz=str(item[4])
        print(il + " " + ilce + " " + benzin + " " + motorin + " " + otogaz + "\n")

        doc_ref =db.collection(u'Prices').document(il).collection(ilce).document(u'Total')
        doc_ref.set({
            u'benzin': benzin,
            u'motorin': motorin,
            u'otogaz': otogaz
        })
        time.sleep(0.1)
