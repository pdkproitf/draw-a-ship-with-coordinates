/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package da_java;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author pdkpro
 */
public class DA_Java extends JFrame {

    /**
     *
     * @param args the command line arguments
     */
    public DA_Java() {
        setSize(new Dimension(ConstantsCls.width+50, ConstantsCls.heigh+50));
        //setSize(Constants.width+50, Constants.heigh+50);
        setVisible(true);
        setTitle("MAP");
        setIconImage(null);
        setLocationRelativeTo(null);
                this.getContentPane().add(new DrawToMapThreadCls("ve len bang do "));
       show();
    }
    public static void main(String[] args) {
        ServerThreadCls server = new ServerThreadCls("server");
        GetHashToQueueThreadCls setQueue = new GetHashToQueueThreadCls("set queue");
        GetQueueSetDBThreadCls setDB =  new GetQueueSetDBThreadCls("dua vao db");
        server.start();
        setQueue.start();
        setDB.start();;
        new DA_Java();
    }
}
