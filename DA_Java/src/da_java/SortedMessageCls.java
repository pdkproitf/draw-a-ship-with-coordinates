/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package da_java;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

/**
 *
 * @author pdkpro
 */
public class SortedMessageCls implements Comparator<MessageCls>{

    @Override
    public int compare(MessageCls o1, MessageCls o2) {
        if(o1.getTime().getTime()>o2.getTime().getTime()){
            return 1;
        }else{
            return -1;
        }
    }
    
}
