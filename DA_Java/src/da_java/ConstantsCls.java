/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package da_java;

import com.evivaultil.BlockingQueue;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import javax.naming.spi.DirStateFactory;

/**
 *
 * @author pdkpro
 */
public class ConstantsCls {

    public static String path = "C:\\Users\\pdkpro\\Desktop\\DO_AN_JAVA\\DATA\\properties.properties";
    public static String linkFileXML = setValue("duongDanDenFileXML");
    public static String linkFileLog = setValue("duongDanDenFileLog");
    public static String user = setValue("user");
    public static String pass = setValue("pass");
    public static String url = setValue("url");
    public static String drive = setValue("drive");
    public static  int countBoat = 0;

    public static int h1Size = Integer.parseInt(setValue("h1Size"));//size hashData
    public static int q1Size = Integer.parseInt(setValue("q1Size"));//
    public static int sleep = Integer.parseInt(setValue("sleep"));
    public static int width = Integer.parseInt(setValue("width"));//do rong frame
    public static int heigh = Integer.parseInt(setValue("heigh"));//do cao frame
    public static int delta = Integer.parseInt(setValue("deltal"));
    public static int donViBD = Integer.parseInt(setValue("donViBD"));
    public static String linkImage = setValue("linkImage");
    public static boolean CfVaCham = false;

    public static HashMap<String, MessageCls> hashFirstData = new HashMap<String, MessageCls>();//luu thong tin ban dau 

    public static ConcurrentHashMap<String, TreeSet<MessageCls>> hashData = new ConcurrentHashMap<String, TreeSet<MessageCls>>();//luu toa do update
    public static Queue<MessageCls> qData1 = new BlockingQueue<MessageCls>("thong Tin", 1000);//luu data update
    public static ArrayList<String> distionaries = new ArrayList<String>();//tu dien

    public static HashMap<String, MessageCls> hashNewest = new HashMap<String, MessageCls>();//dung de lien tuc lay ra cac tin moi nhat
    public static ArrayList<String> distionMapNewest = new ArrayList<String>();

    public static HashMap<String, String> hashSmsWarn = new HashMap<String, String>();//hash canh bao
    public static ArrayList<String> distionWarn = new ArrayList<String>();
    public static ArrayList<String> distionWarnDraw = new ArrayList<String>();//tu dien de ve

    public static HashMap<String, CoordinateCls> hashJarringPoint = new HashMap<String, CoordinateCls>();//luu diem va cham cua tau x,y voi key = x//y va value se la toa do tranh

    //public static HashMap<String, Boolean> hashPassJarringPoint = new HashMap<String, Boolean>();//da qua diem va cham

    public static ArrayList<String> arrSentWarn = new ArrayList<String>();//k dung

    public static HashMap<String, CoordinateCls> hashPresentMark = new HashMap<String, CoordinateCls>();//dich hien tai cua moi tau se di den
    public static HashMap<String, CoordinateCls> hashNextMark = new HashMap<String, CoordinateCls>();//diem de no di toi vi tri ke tiep

    public static ArrayList<String> arrItineraryFinish = new ArrayList<String>();//mang luu danh sach tau da ket thuc hanh trinh

    public static String setValue(String name) {
        try {
            Properties p = new Properties();
            p.load(new FileInputStream(path));
            return p.getProperty(name);
        } catch (Exception e) {
        }
        return null;
    }

    public static double FindDistance(CoordinateCls a, CoordinateCls b) {//tim khoang cach
        if((b.getX()+"").equals("NaN")){
            return 16000;
        }
        double x = b.getX() - a.getX();
        double y = b.getY() - a.getY();
        return Math.sqrt(x * x + y * y);
    }

    //sql
    public static Connection OpenConnect() {
        try {
            Class.forName(drive);
            Connection con = DriverManager.getConnection(url, user, pass);
            return con;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void CloseConnect(Connection con) {
        try {
            if (con != null) {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void CloseConnect(Statement stm) {
        try {
            if (stm != null) {
                stm.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        public static void CloseConnect(PreparedStatement pstm) {
        try {
            if (pstm != null) {
                pstm.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void CloseConnect(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean CheckDB(String ma) {
        try {
            Connection con = ConstantsCls.OpenConnect();
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery("select t_ma from tbl_InformBoat where t_ma = '" + ma + "'");
            if (rs.next()) {
                ConstantsCls.CloseConnect(stm);
                ConstantsCls.CloseConnect(con);
                return true;
            }
            ConstantsCls.CloseConnect(stm);
            ConstantsCls.CloseConnect(con);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
