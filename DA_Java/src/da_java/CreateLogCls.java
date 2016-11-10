/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package da_java;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
public class CreateLogCls {
    public static Logger CreateLog(){
        DOMConfigurator.configureAndWatch(ConstantsCls.linkFileXML);
        Logger log = Logger.getLogger(ConstantsCls.linkFileLog);
        return log;
    }
}