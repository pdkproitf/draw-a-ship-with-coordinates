/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package da_java;

import static da_java.InitMessageCls.sms;
import java.net.CookieHandler;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import oracle.jdbc.driver.Message;
import org.apache.log4j.Logger;

/**
 *
 * @author pdkpro
 */
public class ServerThreadCls extends Thread {

    public boolean fcReplace = true;
    Logger log;
    DatagramSocket sk;
    DatagramPacket data;
    int countBoat = 0;

    public ServerThreadCls(String name) {
        super(name);
        try {
            sk = new DatagramSocket(5541);
            //log = taoLog.khoiTaoLog();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                createReceive();
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void createReceive() {
        while (true) {
            try {
                //log.info("da mo ket noi");
                byte[] buff = new byte[400];
                data = new DatagramPacket(buff, 0, buff.length);
                //nhan ve
                sk.receive(data);
                //log.info("nhan dc ket noi");
                //boc tach chuyen sang string
                String Sdata = new String(data.getData());
                InetAddress inet = data.getAddress();
                int port = data.getPort();
                SetHash(Sdata, inet, port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void SetHash(String line, InetAddress inet, int port) {
        String[] data = line.split("\t");
        System.out.println("line = "+line);
        try {
            //for (String s : data) {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            if (!ConstantsCls.hashFirstData.containsKey(data[0]) || !ConstantsCls.CheckDB(data[0])) {//neu la tin dau tien cua clien thi luu toan bo thong tin cua no 
                InitMessageCls.setMessage(data[0], data[1], Double.parseDouble(data[2]), Double.parseDouble(data[3]),
                        Double.parseDouble(data[4]), Double.parseDouble(data[5]),
                        Double.parseDouble(data[6]), Double.parseDouble(data[7]), Double.parseDouble(data[8]),
                        format.parse(data[9]), inet, port);
                //phan nay do chinh sua ma thanh
            } else {//neu da co roi hi chi lay ma,x,y,date
                MessageCls sms = ConstantsCls.hashFirstData.get(data[0]);
                if (sms.getName().equals(data[1])) {//neu clien mat ket noi va ket noi lai thi kiem tra xem neu no ket noi lai
                    //tin dau tien cua no gom 9 thong tin nen hung ve phai chon loc ra
                    InitMessageCls.setMessage(data[0], Double.parseDouble(data[2]), Double.parseDouble(data[3]), format.parse(data[9]));
                } else {
                    InitMessageCls.setMessage(data[0], Double.parseDouble(data[1]), Double.parseDouble(data[2]), format.parse(data[3]));
                }
            }
            MessageCls sms = InitMessageCls.getMessage();
            if (!ConstantsCls.hashFirstData.containsKey(sms.getMa())) {//neu tau cho co trong hash thi dua vao hashFirst
                ConstantsCls.hashFirstData.put(sms.getMa(), sms);
            }
            String key = sms.getMa();
            if (ConstantsCls.hashData.containsKey(key)) {
                TreeSet<MessageCls> arr = ConstantsCls.hashData.get(key);
                arr.add(sms);
                ConstantsCls.hashData.put(key, arr);
            } else {
                TreeSet<MessageCls> arr = new TreeSet<MessageCls>();
                arr.add(sms);
                ConstantsCls.hashData.put(key, arr);
                ConstantsCls.distionaries.add(key);
            }
            FindJarringPointDefault();
            newestMessage2(sms);
            System.out.println("hashFirst Size =  " + ConstantsCls.hashFirstData.size());
            System.out.println("hash Size =  " + ConstantsCls.hashData.size());
            CheckFinish(sms);
            
            //log.info("da dua vao hash" + sms);
        } catch (Exception e) {
            System.out.println("loi dua vao hash");
            e.printStackTrace();
            //log.error("loi dua vao hash");
        }
    }

    public void newestMessage() {
        try {
            for (int i = 0; i < ConstantsCls.distionaries.size(); i++) {
                String key = ConstantsCls.distionaries.get(i);
                if (!ConstantsCls.arrItineraryFinish.contains(key)) {
                    TreeSet<MessageCls> arr = ConstantsCls.hashData.get(key);
                    MessageCls sms = arr.first();
                    if (ConstantsCls.hashNewest.containsKey(key)) {//neu da co trong mapVaCham thi update
                        ConstantsCls.hashNewest.replace(key, sms);
                    } else {//neu chua co thi them vao cai moi
                        ConstantsCls.hashNewest.put(key, sms);
                        ConstantsCls.distionMapNewest.add(key);
                    }
                    System.out.println("MAPNEWEST => > ma " + sms.getMa() + "  " + sms.getFirst().getX() + "  " + sms.getFirst().getY());
                    CheckAndHandleJarring(sms);
                    //CheckFinish(sms);
                    NoticeJarring();
                }
            }
        } catch (Exception e) {
            //log.error("loi tim tim moi nhat");
            e.printStackTrace();
        }
    }

        public void newestMessage2(MessageCls sms) {
        try {
                if (!ConstantsCls.arrItineraryFinish.contains(sms.getMa())) {
                    if (ConstantsCls.hashNewest.containsKey(sms.getMa())) {//neu da co trong mapVaCham thi update
                       if(ConstantsCls.hashNewest.get(sms.getMa()).getTime().getTime()<sms.getTime().getTime()){
                            ConstantsCls.hashNewest.replace(sms.getMa(), sms);
                       }
                    } else {//neu chua co thi them vao cai moi
                        ConstantsCls.hashNewest.put(sms.getMa(), sms);
                        ConstantsCls.distionMapNewest.add(sms.getMa());
                    }
                    System.out.println("MAPNEWEST => > ma " + sms.getMa() + "  " + sms.getFirst().getX() + "  " + sms.getFirst().getY());
                }
                CheckAndHandleJarring(sms);
            NoticeJarring();
        } catch (Exception e) {
            //log.error("loi tim tim moi nhat");
            e.printStackTrace();
        }
    }
    
    
    public void FindJarringPointDefault() {//tim diem va cham mac dinh
        if (ConstantsCls.distionaries.size() > this.countBoat) {
            this.countBoat = ConstantsCls.distionaries.size();
            for (int i = 0; i < this.countBoat; i++) {
                String key = ConstantsCls.distionaries.get(i);
                MessageCls sms1 = ConstantsCls.hashFirstData.get(key);//doi tuong dem di so sanh
                for (int j = 0; j < this.countBoat; j++) {
                    String key2 = ConstantsCls.distionaries.get(j);
                    MessageCls sms2 = ConstantsCls.hashFirstData.get(key2);//doi tuong dc so sanh
                    if (!sms1.getMa().equalsIgnoreCase(sms2.getMa())) {//bat dau so sanh
                        ItineraryCls ht1 = new ItineraryCls(sms1.getFirst(), sms1.getLast());
                        ItineraryCls ht2 = new ItineraryCls(sms2.getFirst(), sms2.getLast());
                        String key3 = sms1.getMa() + "//" + sms2.getMa();
                        ConstantsCls.hashJarringPoint.put(key3, SolveSystemEquation(ht1, ht2));
                        System.out.println("DIEM VA CHAM " + key3 + "   " + SolveSystemEquation(ht1, ht2).toString());
                    }
                }
            }
        }
    }

    public void CheckAndHandleJarring(MessageCls sms0) {//kiem tra va xu ly va cham
        if (ConstantsCls.distionMapNewest.size() > 1) {
            String key0 = sms0.getMa();
            for (int i = 0; i < ConstantsCls.distionMapNewest.size(); i++) {
                String key = ConstantsCls.distionMapNewest.get(i);
                if (!key0.equals(key)) {//neu khac chinh no thi so sanh
                    MessageCls sms1 = ConstantsCls.hashNewest.get(key);//tau2
                    MessageCls sms1a = ConstantsCls.hashFirstData.get(key);//lay  thong tin dai rong tau2
                    MessageCls sms = ConstantsCls.hashNewest.get(key0);
                    MessageCls smsa = ConstantsCls.hashFirstData.get(key0);//lay  thong tin dai rong tau1 
                    //neu 2 tau co hanh trinh gan nhu trung nhau thi lua tau nao co van toc lon hon ma tim 8 diem an toan
                    double a = (double) (sms.getFirst().getX() - sms1.getFirst().getX() - sms1a.getLon() / 2 - smsa.getLon() / 2);
                    double b = (double) (sms.getFirst().getY() - sms1.getFirst().getY() - sms1a.getLag() / 2 - smsa.getLag() / 2);
                    double delta = (double) Math.sqrt(a * a + b * b);//tinh khoang cach giua 2 tau
                    if (delta <= 70) {//vi pham khoan cach an toan. 
                        //th00: neu 2 duong thang gan nhu song song hoặc có điểm va  chạm ngoài bản đồ
                        CoordinateCls diemVC = ConstantsCls.hashJarringPoint.get(sms.getMa() + "//" + sms1.getMa());
                        double dx = Math.abs(smsa.getFirst().getX() - sms1a.getLast().getX());
                        double dy = Math.abs(smsa.getFirst().getY() - sms1a.getLast().getY());
                        double dx1 = Math.abs(sms1a.getFirst().getX() - smsa.getLast().getX());
                        double dy1 = Math.abs(sms1a.getFirst().getY() - smsa.getLast().getY());
                        if ((((dx <= 20 && dy <= 20) || (dx1 <= 20 && dy1 <= 20)))) {
                            System.out.println("*********************************************");
                            System.out.println("tau "+sms.getMa()+"  "+sms1.getMa());
                            if (smsa.getAverageVelocity() >= sms1a.getAverageVelocity()) {
                                //ToaDo diemVC;
                                HandleSecurityPoint(sms, sms1, diemVC = new CoordinateCls(Double.NaN, Double.NaN));
                            } else {
                                // ToaDo diemVC;
                                HandleSecurityPoint(sms1, sms, diemVC = new CoordinateCls(Double.NaN, Double.NaN));
                            }
                        } else //tinh kha nang va cham
                        if (CheckDegreOfSecurity(sms, smsa, sms1, sms1a) > 5) {//kha nang va cham nho xem nhu k co
                            ConstantsCls.hashSmsWarn.put(sms.getMa() + "//" + sms1.getMa(), "can than co kha nang va cham voi tau " + "\t" + "null" + "\t" + "null"+"//null\tnull");
                            ConstantsCls.distionWarn.add(sms.getMa() + "//" + sms1.getMa());
                        } else {//co kha nang va cham tien hanh giai quyet
                            //xet gocphan tu 
                            int g1 = LookQuadran(sms, diemVC);
                            int g2 = LookQuadran(sms1, diemVC);
                            //neu 2 tau co hanh trinh gan nhu trung nhau 
                            if (Math.abs(g1 - g2) != 2) {//neu cung 1 goc phan tu
                                //neu 1 trong 2 da qua thi thong bao la xg, k có khả năng đụng chạm
                                // .....................tim 8 diem an toan
                                System.out.println("truong hop 1");
                                HandleSecurityPoint(sms, sms1, diemVC);
                            } else {//truong hop nguoc phia
                                ///th1: kiem tra neu 1 tau da qua diem va cham thi dung thuat toan tim 8 diem
                                System.out.println("truong hop 2");
                                CoordinateCls dvc = ConstantsCls.hashJarringPoint.get(sms.getMa() + "//" + sms1.getMa());
                                boolean tau1 = LookJarringPoint(smsa.getFirst(), sms.getFirst(), diemVC);
                                boolean tau2 = LookJarringPoint(sms1a.getFirst(), sms1.getFirst(), diemVC);
                                if (tau1 || tau2) {//neu1 trong 2 tau hoac ca 2 tau deu da qua diem va cham thi tim 8 diem
                                    HandleSecurityPoint(sms, sms1, diemVC);
                                } else {//th2: ca 2 tau deu huong den diem va cham
                                    //hinh thanh 1 duong thang song song voi duong thng cua tu chay nhanh hon roi tim 
                                    //diem tren duong thang do va gan diem cua tau chay nhanh hon, tai do ta dung thuat toan tim
                                    //8 diem
                                    if (smsa.getAverageVelocity() <= sms1a.getAverageVelocity()) {//tim voi tau 1
                                        if (HandleSecurityPoint2(sms, sms1a, sms1, diemVC)) {//k dc thi tim voi tau 2
                                            if (HandleSecurityPoint2(sms1, smsa, sms, diemVC)) {//k dc nua thi dung thuat toan tim 8 diem an toan ban dau
                                                HandleSecurityPoint(sms, sms1, diemVC);
                                            }
                                        }
                                    } else {
                                        if (HandleSecurityPoint2(sms1, smsa, sms, diemVC)) {
                                            if (HandleSecurityPoint2(sms, sms1a, sms1, diemVC)) {
                                                HandleSecurityPoint(sms1, sms, diemVC);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
//tim 8 diem an toan cho truong hop 2 tau nguoc phia
    public boolean HandleSecurityPoint2(MessageCls sms, MessageCls sms1a, MessageCls sms1, CoordinateCls diemVC) {
        ItineraryCls ht1 = new ItineraryCls(sms1.getFirst(), sms1a.getLast());
        //dt ss la dt co cung a ,b nhung khac c //
        double A = ht1.getA();
        double B = ht1.getB();
        double C = ht1.getC();
        //tim diem C cua hanh trinh song song, dt nay di qua diem tau a
        double C2 = A * sms.getFirst().getX() + B * sms.getFirst().getY();
        if (C2 != C) {
            ItineraryCls htss = new ItineraryCls(A, B, C2);
            //tim diem gan tau 1a nhat
            double y = (htss.getC() - htss.getA() * sms1.getFirst().getX()) / htss.getB();
            CoordinateCls dss = new CoordinateCls(sms1.getFirst().getX(), y);
            MessageCls smsss = new MessageCls();
            smsss.setMa(sms.getMa());
            smsss.setFirst(dss);
            CoordinateCls dAT = FindSecurityPoint(smsss, sms1, diemVC);
            if (!CheckInMap(dAT)) {
                AddHashWarn(sms, sms1, sms.getFirst(),sms1.getFirst());
            }
            AddHashWarn(sms, sms1, dAT,new CoordinateCls());
            return true;
        }
        return false;
    }
//tim 8 diem an toan

    public void HandleSecurityPoint(MessageCls sms, MessageCls sms1, CoordinateCls diemVC) {
        if (CheckInMap(diemVC)) {
            if (ConstantsCls.hashFirstData.get(sms.getMa()).getAverageVelocity() < ConstantsCls.hashFirstData.get(sms1.getMa()).getAverageVelocity()) {
                CoordinateCls dAT = FindSecurityPoint(sms, sms1, diemVC);
                if (!CheckInMap(dAT)) {
                    dAT = FindSecurityPoint(sms1, sms, diemVC);//neu k tim dc thi tim cho tau 2
                    if (!CheckInMap(dAT)) {
                        AddHashWarn(sms, sms1, sms.getFirst(),sms1.getFirst());//neu tau 2 van k tim dc thi cho dung yen
                    }
                    AddHashWarn(sms1, sms, sms1.getFirst(),new CoordinateCls());
                } else {
                    AddHashWarn(sms, sms1, dAT,new CoordinateCls());
                }
            } else {
                CoordinateCls dAT = FindSecurityPoint(sms1, sms, diemVC);
                if (!CheckInMap(dAT)) {//neuk tim dc thi tim cho tau 1
                    dAT = FindSecurityPoint(sms, sms1, diemVC);
                    if (!CheckInMap(dAT)) {
                        AddHashWarn(sms1, sms, sms.getFirst(),sms1.getFirst());//neu tau 2 van k tim dc thi cho dung yen
                    }
                    AddHashWarn(sms, sms1, sms.getFirst(),new CoordinateCls());
                } else {
                    AddHashWarn(sms1, sms, dAT,new CoordinateCls());
                }
            }
        } else {
            CoordinateCls dAT = FindSecurityPoint(sms, sms1, diemVC);
            if (!CheckInMap(dAT)) {
                dAT = FindSecurityPoint(sms1, sms, diemVC);//neu k tim dc thi tim cho tau 2
                if (!CheckInMap(dAT)) {
                    AddHashWarn(sms, sms1, sms.getFirst(),sms1.getFirst());//neu tau 2 van k tim dc thi cho dung yen
                }
                AddHashWarn(sms1, sms, sms1.getFirst(),new CoordinateCls());
            } else {
                AddHashWarn(sms, sms1, dAT,new CoordinateCls());
            }
        }
    }

    public CoordinateCls FindSecurityPoint(MessageCls sms1, MessageCls sms2, CoordinateCls diemVaCham) {
        //xet lan luot 8 diem o cac huong B - DB - D - DN - N - TN - T - TB
        double a[] = new double[8];
        CoordinateCls huong[] = new CoordinateCls[8];
        double x = sms1.getFirst().getX();
        double y = sms1.getFirst().getY() - 50;
        int vt = 0;
        huong[vt++] = new CoordinateCls(x, y);
        if(ConstantsCls.FindDistance(huong[0],ConstantsCls.hashFirstData.get(sms1.getMa()).getFirst()) >
                ConstantsCls.FindDistance(sms1.getFirst(),ConstantsCls.hashFirstData.get(sms1.getMa()).getFirst())){
            a[0] = CheckDistanceFull(sms1, sms2, huong[0], diemVaCham);
        }
        //DB
        x = sms1.getFirst().getX() + 50;
        y = sms1.getFirst().getY() - 50;
        huong[vt++] = new CoordinateCls(x, y);
        if(ConstantsCls.FindDistance(huong[1],ConstantsCls.hashFirstData.get(sms1.getMa()).getFirst()) >
                ConstantsCls.FindDistance(sms1.getFirst(),ConstantsCls.hashFirstData.get(sms1.getMa()).getFirst())){
            a[1] = CheckDistanceFull(sms1, sms2, huong[1], diemVaCham);
        }
        //D
        x = sms1.getFirst().getX() + 50;
        y = sms1.getFirst().getY();
        huong[vt++] = new CoordinateCls(x, y);
        if(ConstantsCls.FindDistance(huong[2],ConstantsCls.hashFirstData.get(sms1.getMa()).getFirst()) >
                ConstantsCls.FindDistance(sms1.getFirst(),ConstantsCls.hashFirstData.get(sms1.getMa()).getFirst())){
            a[2] = CheckDistanceFull(sms1, sms2, huong[2], diemVaCham);
        }
        //DN
        x = sms1.getFirst().getX() + 50;
        y = sms1.getFirst().getY() + 50;
        huong[vt++] = new CoordinateCls(x, y);
        if(ConstantsCls.FindDistance(huong[3],ConstantsCls.hashFirstData.get(sms1.getMa()).getFirst()) >
                ConstantsCls.FindDistance(sms1.getFirst(),ConstantsCls.hashFirstData.get(sms1.getMa()).getFirst())){
            a[3] = CheckDistanceFull(sms1, sms2, huong[3], diemVaCham);
        }
        //N
        x = sms1.getFirst().getX();
        y = sms1.getFirst().getY() + 50;
        huong[vt++] = new CoordinateCls(x, y);
        if(ConstantsCls.FindDistance(huong[4],ConstantsCls.hashFirstData.get(sms1.getMa()).getFirst()) >
                ConstantsCls.FindDistance(sms1.getFirst(),ConstantsCls.hashFirstData.get(sms1.getMa()).getFirst())){
            a[4] = CheckDistanceFull(sms1, sms2, huong[4], diemVaCham);
        }
        //TN
        x = sms1.getFirst().getX() - 50;
        y = sms1.getFirst().getY() + 50;
        huong[vt++] = new CoordinateCls(x, y);
        if(ConstantsCls.FindDistance(huong[5],ConstantsCls.hashFirstData.get(sms1.getMa()).getFirst()) >
                ConstantsCls.FindDistance(sms1.getFirst(),ConstantsCls.hashFirstData.get(sms1.getMa()).getFirst())){
            a[5] = CheckDistanceFull(sms1, sms2, huong[5], diemVaCham);
        }
        //T
        x = sms1.getFirst().getX() - 50;
        y = sms1.getFirst().getY();
        huong[vt++] = new CoordinateCls(x, y);
        if(ConstantsCls.FindDistance(huong[6],ConstantsCls.hashFirstData.get(sms1.getMa()).getFirst()) >
                ConstantsCls.FindDistance(sms1.getFirst(),ConstantsCls.hashFirstData.get(sms1.getMa()).getFirst())){
            a[6] = CheckDistanceFull(sms1, sms2, huong[6], diemVaCham);
        }
        //TB
        x = sms1.getFirst().getX() - 50;
        y = sms1.getFirst().getY() - 50;
        huong[vt++] = new CoordinateCls(x, y);
        if(ConstantsCls.FindDistance(huong[7],ConstantsCls.hashFirstData.get(sms1.getMa()).getFirst()) >
                ConstantsCls.FindDistance(sms1.getFirst(),ConstantsCls.hashFirstData.get(sms1.getMa()).getFirst())){
            a[7] = CheckDistanceFull(sms1, sms2, huong[7], diemVaCham);
        }
        //sap xeo tim cai lon nhat
        double max = a[0];
        int ii = 0;
        for (int i = 0; i < 8; i++) {
            System.out.println(" a["+i+"] = "+a[i]);
            if (max < a[i] && huong[i].getX() > 0 && huong[i].getY() > 0) {
                max = a[i];
                ii = i;
            }
        }
        if (max >= 70) {
            System.out.println("DIEM AN TOAN "+sms1.getMa()+"  " + huong[ii].toString());
            return huong[ii];
        } else {
            return new CoordinateCls(-10, -10);
        }
    }
//tra ve khoang cach ngan nhat giua diem dang xet voi tat cac cac tau ,va diem va cham//ngoại trừ chính nó và tàu đang gặp nguy hiểm với nó

    public double CheckDistanceFull(MessageCls sms1, MessageCls sms2, CoordinateCls diemXet, CoordinateCls diemVaCham) {//tim khoang cach nho nhat cua tau 1 voi cac tau khac
        double kcat = ConstantsCls.FindDistance(diemXet, diemVaCham);
        System.out.println("DIEM VA CHAM " + diemVaCham.toString());
        if ((diemVaCham.getX() + "").equals("NaN")) {
            kcat = 500;
            System.out.println("KCAT ========= " + kcat);
        }
        for (int i = 0; i < ConstantsCls.distionMapNewest.size(); i++) {
            String key = ConstantsCls.distionMapNewest.get(i);
            if (!key.equals(sms1.getMa()) ) {
                MessageCls sms3 = ConstantsCls.hashNewest.get(key);
                if (kcat > ConstantsCls.FindDistance(diemXet, sms3.getFirst())) {
                    kcat = ConstantsCls.FindDistance(diemXet, sms3.getFirst());
                }
            }
        }
        return kcat;//khoag cach an toan
    }

    //kha nang va cham se tinh kc tu diem hien thoi cua 2 tau den vi tri va cham , 
    // no se lay thoi gian den diem va cham tru cho nhau 
    //tra ve la thoi gian giua 2 lan den diem va cham cua cac tau
    public double CheckDegreOfSecurity(MessageCls sms, MessageCls smsa, MessageCls sms1, MessageCls sms1a) {//tinh do an toan
        double kcTau1 = 0;
        double kcTau2 = 0;
        CoordinateCls diemVaCham = ConstantsCls.hashJarringPoint.get(sms.getMa() + "//" + sms1.getMa());
            kcTau1 = ConstantsCls.FindDistance(sms.getFirst(), diemVaCham);
            kcTau2 = ConstantsCls.FindDistance(sms1.getFirst(), diemVaCham);
        //double thoiGianDenDiemVC1 = (kcTau1 / smsa.getAverageVelocity());
       // double thoiGianDenDiemVC2 = (kcTau2 / sms1a.getAverageVelocity());
        //double kNKVC = Math.abs(thoiGianDenDiemVC1 - thoiGianDenDiemVC2);
        double kNKVC = Math.abs(kcTau1/ConstantsCls.donViBD-kcTau2/ConstantsCls.donViBD);
        System.out.println("KCVC= "+kNKVC);
        //neu 1 trong 2 tau co diem ket thuc trc diem va cham thi k co kha nang va cham
        double firstToLast1 = ConstantsCls.FindDistance(smsa.getFirst(), smsa.getLast());
        double firstToVC1 = ConstantsCls.FindDistance(smsa.getFirst(), diemVaCham);
        double firstToLast2 = ConstantsCls.FindDistance(sms1a.getFirst(), sms1a.getLast());
        double firstToVC2 = ConstantsCls.FindDistance(sms1a.getFirst(), diemVaCham);
        //neu diem va cham nam ngoaif ban do thi kha nang k va cham tang len 1
        if (!CheckInMap(diemVaCham)) {
            kNKVC += 1;
        }
        //neu 1 trong 2 da qua diem va cham thi kha nang va cham rat thap
        if (LookJarringPoint(smsa.getFirst(), sms.getFirst(), diemVaCham)) {
            kNKVC += 1;
        }
        if (LookJarringPoint(sms1a.getFirst(), sms1.getFirst(), diemVaCham)) {
            kNKVC++;
        }
        if ((firstToLast1 < firstToVC1) || (firstToLast2 < firstToVC2)) {
            kNKVC = 100;
        }
        if (ConstantsCls.FindDistance(sms.getFirst(), sms1.getFirst()) <= ((smsa.getLon() / 2 + sms1a.getLon() / 2) + 2 * ConstantsCls.donViBD)) {
            kNKVC = 1;//neu khoang cach giua 2 tau ma be hon 3 lan don vi ban do + voi 1/2 chieu dai moi tau
        }
        /*
         System.out.println(sms.getMa() + " vitri " + sms.getFirst().toString()
         + "  " + sms1.getMa() + "  vitri " + sms1.getFirst().toString()
         + " diem va cham " + diemVacham.toString() + " kcvc1 " + kcTau1 + "  kcvc2 " + kcTau2);
         System.out.println("TIME1 = " + thoiGianDenDiemVC1 + "  TIME2=" + thoiGianDenDiemVC2 + " KNVC = " + kNVC);
         */
        return kNKVC;
    }

    public void NoticeJarring() {//thong bao va cham den cac tau
        if (ConstantsCls.hashSmsWarn.size() > 0) {
            try {
                for (int i = 0; i < ConstantsCls.distionWarn.size(); i++) {
                    String key0 = ConstantsCls.distionWarn.get(i);
                    String s[] = key0.split("//");
                   
                    if (!ConstantsCls.arrSentWarn.contains(key0) && !ConstantsCls.arrSentWarn.contains((s[1] + "//" + s[0]))) {
                         String tbFul[] =ConstantsCls.hashSmsWarn.get(key0).split("//");
                        String[] thongBao = tbFul[0].split("\t");
                        MessageCls sms = ConstantsCls.hashFirstData.get(s[0]);
                        byte[] b = (thongBao[0] + " " + s[1] + "\t" + thongBao[1] + "\t" + thongBao[2]).getBytes();
                        DatagramPacket data = new DatagramPacket(b, 0, b.length, sms.getInet(), sms.getPort());
                        this.sk.send(data);

                        System.out.println("da gui den tau " + thongBao[0] + " " + s[1] + "\t" + thongBao[1] + "\t" + thongBao[2]);
                        //gui den cho sk thu 2, chi gui diem tranh cho sk1 , sk2 thi gui canh bao thoi
                        MessageCls sms1 = ConstantsCls.hashFirstData.get(s[1]);
                        byte[] b2 = (thongBao[0] + " " + s[0]+"\t"+tbFul[1]).getBytes();
                        DatagramPacket data2 = new DatagramPacket(b2, b2.length, sms1.getInet(), sms1.getPort());
                        this.sk.send(data2);
                        //
                        System.out.println("da gui den tau " +new String(b2));
                        ConstantsCls.hashSmsWarn.remove(key0);
                        ConstantsCls.distionWarn.remove(key0);

                        ConstantsCls.hashSmsWarn.remove(s[1] + "//" + s[0]);
                        ConstantsCls.distionWarn.remove(s[1] + "//" + s[0]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void AddHashWarn2(MessageCls sms1, MessageCls sms, CoordinateCls dAT,CoordinateCls dAT2) {
        System.out.println("CANH BAO " + sms1.getMa());
        if (ConstantsCls.distionWarn.contains(sms1.getMa() + "//" + sms.getMa())) {
            ConstantsCls.hashSmsWarn.replace(sms1.getMa() + "//" + sms.getMa(), "can than co kha nang va cham voi tau " + " \t " + dAT.toString()+"// "+dAT2.toString());
            ConstantsCls.distionWarnDraw.add(sms1.getMa() + "//" + sms.getMa());
        } else {
            ConstantsCls.hashSmsWarn.put(sms1.getMa() + "//" + sms.getMa(), "can than co kha nang va cham voi tau " + " \t " + dAT.toString()+"// "+dAT2.toString());
            ConstantsCls.distionWarn.add(sms1.getMa() + "//" + sms.getMa());
            ConstantsCls.distionWarnDraw.add(sms1.getMa() + "//" + sms.getMa());
        }
        if (ConstantsCls.hashPresentMark.containsKey(sms.getMa())) {
            ConstantsCls.hashPresentMark.replace(sms1.getMa(), dAT);
            //System.out.println("da add DICH HIEN TAI vao " + sms1.getMa() + " " + dAT.toString());
        } else {
            ConstantsCls.hashPresentMark.put(sms1.getMa(), dAT);
           // System.out.println("***da add vao " + sms1.getMa() + " " + dAT.toString());
        }
        //System.out.println("DIEM AN TOAN " + sms1.getMa() + "   " + dAT.toString());
        //diem quay ve se la diem va cham, neu k co diem va cham thi diem quay ve la chinh vi tri no roi khoi hanh trinh
        if (!ConstantsCls.hashNextMark.containsKey(sms1.getMa())) {
            CoordinateCls vtkt = null;
            //xet xem co ton tai diem va cham k 
            if (ConstantsCls.hashJarringPoint.containsKey(sms1.getMa() + "//" + sms.getMa())) {
                CoordinateCls dvc = ConstantsCls.hashJarringPoint.get(sms1.getMa() + "//" + sms.getMa());
                if (CheckInMap(dvc)) {//neu diem va cham co trong ban do 
                    MessageCls sms1a = ConstantsCls.hashFirstData.get(sms1.getMa());
                    if (ConstantsCls.FindDistance(dvc, dAT) < ConstantsCls.FindDistance(sms1.getFirst(), dAT)) {
                        vtkt = dvc;//so sanh khoang cah cua diem an toan voi diem va cham va diem no bat dau roikhoi hanh trinh, khoang cach nao nho hon thi no se tien den
                       // System.out.println("toi vi tri va cham");
                    } else {
                        vtkt = sms1.getFirst();
                        //System.out.println("QUAY  VE DIEM VA CHAM");
                    }
                } else {//neu diem va cham k co trong ban do thi xem nhu k co diem va cham
                    //System.out.println("KHONG CO DIEM VA CHAM " + sms1.getMa() + "//" + sms.getMa());
                    vtkt = sms1.getFirst();
                }
            } else {
                //System.out.println("KHONG CO DIEM VA CHAM " + sms1.getMa() + "//" + sms.getMa());
                vtkt = sms1.getFirst();
            }
            //System.out.println("diem ke tiep dc PUT vao " + vtkt.toString());
            ConstantsCls.hashNextMark.put(sms1.getMa(), vtkt);
        }
    }
     public void AddHashWarn(MessageCls sms1, MessageCls sms, CoordinateCls dAT,CoordinateCls dAT2) {
        System.out.println("CANH BAO " + sms1.getMa());
        if (ConstantsCls.distionWarn.contains(sms1.getMa() + "//" + sms.getMa())) {
            ConstantsCls.hashSmsWarn.replace(sms1.getMa() + "//" + sms.getMa(), "can than co kha nang va cham voi tau " + "\t" + dAT.toString()+"//"+dAT2.toString());
            ConstantsCls.distionWarnDraw.add(sms1.getMa() + "//" + sms.getMa());
        } else {
            ConstantsCls.hashSmsWarn.put(sms1.getMa() + "//" + sms.getMa(), "can than co kha nang va cham voi tau " + "\t" + dAT.toString()+"//"+dAT2.toString());
            ConstantsCls.distionWarn.add(sms1.getMa() + "//" + sms.getMa());
            ConstantsCls.distionWarnDraw.add(sms1.getMa() + "//" + sms.getMa());
        }
        if (ConstantsCls.hashPresentMark.containsKey(sms.getMa())) {
            ConstantsCls.hashPresentMark.replace(sms1.getMa(), dAT);
            //System.out.println("da add DICH HIEN TAI vao " + sms1.getMa() + " " + dAT.toString());
        } else {
            ConstantsCls.hashPresentMark.put(sms1.getMa(), dAT);
           // System.out.println("***da add vao " + sms1.getMa() + " " + dAT.toString());
        }
        //System.out.println("DIEM AN TOAN " + sms1.getMa() + "   " + dAT.toString());
        //diem quay ve se la diem va cham, neu k co diem va cham thi diem quay ve la chinh vi tri no roi khoi hanh trinh
        if (!ConstantsCls.hashNextMark.containsKey(sms1.getMa())) {
            CoordinateCls vtkt = null;
           //tim diem D thuoc dt dt d, sao cho vector DC* vector AC =0 ==>   (C la diem an toan, A la diem roi khoi hanh trinh,
            int Xac = (int)dAT.getX()-(int)sms1.getFirst().getX();// D la diem se quay ve
            int Yac = (int)dAT.getY()-(int)sms1.getFirst().getY();//
            //vector DC
            //pt duong thang di qua A,D
            ItineraryCls iti = new ItineraryCls(ConstantsCls.hashFirstData.get(sms1.getMa()).getFirst(), ConstantsCls.hashFirstData.get(sms1.getMa()).getLast());
            double Xd =   ((iti.getB()*(dAT.getX()*dAT.getX()-sms1.getFirst().getX()*dAT.getX())/(dAT.getY()-sms1.getFirst().getY())+
                    iti.getB()*dAT.getY()-iti.getC())/(iti.getB()*(dAT.getX()-sms1.getFirst().getY())-iti.getA()));
            //--toa do diem D
            double Yd = ((-iti.getA() * Xd + iti.getC()) / iti.getB());
            vtkt = new CoordinateCls(Xd, Yd);
            //System.out.println("diem ke tiep dc PUT vao " + vtkt.toString());
            ConstantsCls.hashNextMark.put(sms1.getMa(), vtkt);
        }
    }


    public void CheckFinish(MessageCls sms) {
        MessageCls smsa = ConstantsCls.hashFirstData.get(sms.getMa());
        if (sms.getFirst().getX() == smsa.getLast().getX() && sms.getFirst().getY() == smsa.getLast().getY()) {
            ConstantsCls.hashNewest.remove(sms.getMa());//neu da cuoi hanh trinh thi thoi k ve nua
            ConstantsCls.distionMapNewest.remove(sms.getMa());
            ConstantsCls.arrItineraryFinish.add(sms.getMa());
            System.out.println(sms.getMa() + "  KET THUC ****************");
        } else {
            CoordinateCls dichHT = ConstantsCls.hashPresentMark.get(sms.getMa());
            System.out.println(sms.getMa() + "  " + sms.getFirst().toString() + "  DICH HIEN TAI  " + dichHT.toString());
            if (sms.getFirst().getX() == dichHT.getX() && sms.getFirst().getY() == dichHT.getY()) {//neu da di den diem ket thuc thi kiem tra
                //neu do chua la cuoi hanh trinh thi cho tau di toi diem quay ve , neu da cuoi hanh tirnh thi dung no , k ve nua
                //System.out.println("da den dich ++++++++++++++++++=");
                //chua la cuoi hinh trinh
                try {
                    //System.out.println("den diem ke tiep   ");
                    CoordinateCls dKT = ConstantsCls.hashFirstData.get(sms.getMa()).getLast();
                    if (ConstantsCls.hashNextMark.containsKey(sms.getMa())) {
                        dKT = ConstantsCls.hashNextMark.get(sms.getMa());
                        System.out.println("CO");
                    }
                    //System.out.println(sms.getMa() + "   DIEM KE TIEP " + dKT.toString());
                    String tinGui = "di chuyen toi " + "\t" + dKT.toString();
                    byte[] b = tinGui.getBytes();
                    DatagramPacket data = new DatagramPacket(b, b.length, smsa.getInet(), smsa.getPort());
                    this.sk.send(data);
                    ///System.out.println("database  =========== " + smsa.totbl_ThongTin());
                    ConstantsCls.hashPresentMark.replace(smsa.getMa(), dKT);
                    //System.out.println("xoa diem ke  tiep " + sms.getMa() + " " + dKT.toString());
                    ConstantsCls.hashNextMark.remove(sms.getMa());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
//xet diem va cham

    public boolean LookJarringPoint(CoordinateCls viTriBD, CoordinateCls viTriHT, CoordinateCls diemVaCham) {
        if (ConstantsCls.FindDistance(viTriHT, viTriBD) < ConstantsCls.FindDistance(diemVaCham, viTriBD)) {
            return false;//chua qua
        }
        //System.out.println("DA QUA DIEM VA CHAM");
        return true;
    }

    public boolean CheckInMap(CoordinateCls diemVacham) {//kiem tra xem co o trong ban do hay k 
        if (diemVacham.getX() > ConstantsCls.width || diemVacham.getX() < 0
                || diemVacham.getY() > ConstantsCls.heigh || diemVacham.getY() < 0
                || (diemVacham.getX() + "").equals("NaN")) {
            return false;   //o ngaoi ban do
        }
        return true;
    }

    public int LookQuadran(MessageCls sms1, CoordinateCls diemVaCham) {//xet goc phan tu
        if (sms1.getFirst().getX() < diemVaCham.getX()) {
            if (sms1.getFirst().getY() < diemVaCham.getY()) {
                return 1;
            } else {
                return 4;
            }
        } else {
            if (sms1.getFirst().getY() < diemVaCham.getY()) {
                return 2;
            } else {
                return 3;
            }
        }
    }

    public CoordinateCls SolveSystemEquation(ItineraryCls a, ItineraryCls b) {//giai he phuong trinh
        //System.out.println("HANH TRINH A " + a.toString());
        //System.out.println("HANH TRINH B " + b.toString());
        double d = a.getA() * b.getB() - b.getA() * a.getB();
        double dx = a.getC() * b.getB() - b.getC() * a.getB();
        double dy = a.getA() * b.getC() - b.getA() * a.getC();
        double x = dx / d;
        double y = dy / d;
        return new CoordinateCls(x, y);
    }
}
