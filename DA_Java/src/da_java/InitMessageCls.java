/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package da_java;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.TreeSet;

/**
 *
 * @author pdkpro
 */
public class InitMessageCls {

    static MessageCls sms;

    public static void setMessage(String ma, String name, double xa, double ya, double xb, double yb, double lon, double lag, double averageVelocity, Date time, InetAddress inet, int port) {
        sms = new MessageCls();
        CoordinateCls first = new CoordinateCls(xa, ya);
        CoordinateCls last = new CoordinateCls(xb, yb);
        sms.setFirst(first);
        sms.setLast(last);
        sms.setName(name);
        sms.setMa(ma);
        sms.setLon(lon);
        sms.setLag(lag);
        sms.setTime(time);
        sms.setAverageVelocity(averageVelocity);
        sms.setInet(inet);
        sms.setPort(port);
        ConstantsCls.hashPresentMark.put(ma, last);
        //dua vao database
        SetDB(sms);
    }

    public static void setMessage(String ma, double xa, double ya, Date time) {
        sms = new MessageCls();
        CoordinateCls first = new CoordinateCls(xa, ya);
        sms.setMa(ma);
        sms.setFirst(first);
        sms.setTime(time);
    }

    public static MessageCls getMessage() {
        return sms;
    }

    public static void SetDB(MessageCls sms) {
        try {
            Connection con = ConstantsCls.OpenConnect();
            System.out.println("tao ket noi thanh cong");
            if (!ConstantsCls.CheckDB(sms.getMa())) {
                int cre = con.createStatement().executeUpdate(sms.totbl_ThongTin());
                System.out.println("da dua vao db cre = " + cre);
                System.out.println("da dua vao tbl_thongTin");
            }
            ConstantsCls.CloseConnect(con);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
