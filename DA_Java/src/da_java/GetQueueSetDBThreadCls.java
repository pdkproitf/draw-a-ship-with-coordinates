/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package da_java;

import com.evivaultil.ThreadSyn;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import org.apache.log4j.Logger;

/**
 *
 * @author pdkpro
 */
public class GetQueueSetDBThreadCls extends Thread{
    Logger log;
    public GetQueueSetDBThreadCls(String name){
        super(name);
        log = CreateLogCls.CreateLog();
    }
    public void run(){
        while (true) {            
            try {
                GetQueue();
                Thread.sleep(ConstantsCls.sleep);
            } catch (Exception e) {
            }
        }
    }
    private void GetQueue(){
        try {
		if((ConstantsCls.qData1.size() > ConstantsCls.q1Size)||(ConstantsCls.arrItineraryFinish.size()==ConstantsCls.countBoat&&ConstantsCls.qData1.size()>0)){
			Connection conn = ConstantsCls.OpenConnect();
                        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			PreparedStatement pstmt = conn.prepareStatement("insert into tbl_Coordinate values(?, ?, ?,?)");
			for(int i = 0; i < ConstantsCls.qData1.size(); i++){
				MessageCls sms = ConstantsCls.qData1.remove();
				pstmt.setString(1, sms.getMa());
				pstmt.setDouble(2, sms.getFirst().getX());
				pstmt.setDouble(3, sms.getFirst().getY());
                                pstmt.setString(4, format.format(sms.getTime()));
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			ConstantsCls.CloseConnect(pstmt);
			ConstantsCls.CloseConnect(conn);
		}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
