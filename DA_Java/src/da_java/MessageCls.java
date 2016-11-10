/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package da_java;

import java.net.InetAddress;
import java.util.Date;

/**
 *
 * @author pdkpro
 */
public class MessageCls implements Comparable<MessageCls> {

    private CoordinateCls first, last;
    double lon, lag;
    private Date time;
    private String ma, name;
    private double averageVelocity;//van toc trung binh
    private InetAddress inet;
    private int port;

    public CoordinateCls getFirst() {
        return first;
    }

    public void setFirst(CoordinateCls first) {
        this.first = first;
    }

    public CoordinateCls getLast() {
        return last;
    }

    public void setLast(CoordinateCls last) {
        this.last = last;
    }

    public InetAddress getInet() {
        return inet;
    }

    public void setInet(InetAddress inet) {
        this.inet = inet;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setAverageVelocity(double averageVelocity) {
        this.averageVelocity = averageVelocity;
    }

    public double getAverageVelocity() {
        return averageVelocity;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLag() {
        return lag;
    }

    public void setLag(double lag) {
        this.lag = lag;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(MessageCls o) {
        return this.getTime().getTime() > o.getTime().getTime() ? -1 : 1;//this.getTime().compareTo(o.getTime());
    }

    public String totbl_ThongTin() {
        String lenh = "insert into tbl_InformBoat values('" + this.getName() + "'," + "'" + this.getMa() + "'," + this.getFirst().getX() +","+ this.getFirst().getY() +","
                + this.getLast().getX()+","+ this.getLast().getY() +","+ this.getLon() +","+ this.getLag() + ",'" + this.getTime() + "',"+
                + this.getAverageVelocity() + ",'" + this.getInet()
                + "'," + this.getPort() + ")";
        System.out.println("lenh = "+lenh);
        return lenh;
               
       // return "insert into tbl_thongTin values('tau b','mt02',20.4	,20.4	,500.50,500.0,	10.0,	10.0,'05/21/2015 00:17:29',2.2,'127/157/14',55)";
    }

    public String totbl_ViTri() {
        return "insert into tbl_Coordinate values('" + this.getMa() + "'," + this.getFirst().getX() +","+ this.getFirst().getY()+",'"+this.getTime()+"')";
    }
}