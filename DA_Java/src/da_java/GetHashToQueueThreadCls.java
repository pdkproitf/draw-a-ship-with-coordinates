/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package da_java;

import com.evivaultil.ThreadSyn;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author pdkpro
 */
public class GetHashToQueueThreadCls extends Thread{

    Logger log;
    public GetHashToQueueThreadCls(String name){
        super(name);
        log = CreateLogCls.CreateLog();
    }
    @Override
    public void run() {
        while (true) {            
            try {
                getHashToQueue();
                Thread.sleep(ConstantsCls.sleep);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    //dua vao queue 
            public void getHashToQueue() {
          try {
              if((ConstantsCls.hashData.size()>ConstantsCls.h1Size)||(ConstantsCls.arrItineraryFinish.size()==ConstantsCls.countBoat)){
                   for (int i = 0; i < ConstantsCls.distionaries.size(); i++) {
                       String key = ConstantsCls.distionaries.get(i);
                       TreeSet<MessageCls> arr = ConstantsCls.hashData.get(key);
                       Iterator<MessageCls> it = arr.iterator();
                       while (it.hasNext()) {
                           MessageCls sms = (MessageCls)it.next();
                           ConstantsCls.qData1.offer(sms);//dua vao queue
                           //log.info("da dua  vao queue"+sms);
                       }
                  }
              }
          } catch (Exception e) {
          }
    }
}
