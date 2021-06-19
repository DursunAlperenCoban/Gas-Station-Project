def opettofirebase(cursor,db,time):
    df = cursor.execute("select ll.Il,lc.Ilce,t.benzin,t.motorin from Opet as t inner join Ilceler as lc on lc.IlceID=t.IlceID inner join Iller as ll on ll.IlID=lc.IlID where t.date=(select top(1) date from Opet );").fetchall()
    print("Opet\n")
    for item in df:
        il = str(item[0])
        ilce = str(item[1])
        benzin = str(item[2])
        motorin = str(item[3])

        print(il + " " + ilce + " " + benzin + " " + motorin + "\n")

        doc_ref = db.collection(u'Prices').document(il).collection(ilce).document(u'Opet')
        doc_ref.set({
            u'benzin': benzin,
            u'motorin': motorin
        })
        time.sleep(0.1)