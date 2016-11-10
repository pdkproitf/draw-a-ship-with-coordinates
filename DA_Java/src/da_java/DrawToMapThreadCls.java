/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package da_java;

import com.evivaultil.ThreadSyn;
import java.util.TreeSet;
import javax.swing.JFrame;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.util.Random;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author pdkpro
 */
public class DrawToMapThreadCls extends Canvas implements Runnable {

    BufferedImage image;
    BufferedImage image2;
    Image img;
    Image img2;

    public DrawToMapThreadCls(String nam) {
        try {
            this.image = ImageIO.read(new FileInputStream(ConstantsCls.linkImage));
            this.image2 = ImageIO.read(new FileInputStream("C:\\Users\\pdkpro\\Desktop\\DO_AN_JAVA\\DA_Java\\MAP.jpg"));

            new Thread(this).start();
        } catch (IOException ex) {
            Logger.getLogger(DrawToMapThreadCls.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void run() {
        while (true) {
            try {
                repaint();
                Thread.sleep(100);
            } catch (Exception e) {
            }
        }
    }

    public void paint(Graphics g) {
        Color c = Color.red;
        g.setColor(c);

        img2 = createImage(getWidth(), getHeight());
        if (img2 == null) {
            return;
        }
        Graphics gg = img2.getGraphics();
        gg.drawImage(image2, 0, 0, getWidth(), getHeight(), this);
        g.drawImage(img2, 0, 0, getWidth(), getHeight(), this);
        //DrawGrid(g);
        //ve thuyen
        if (ConstantsCls.hashNewest.size() > 0) {
            for (int i = 0; i < ConstantsCls.distionMapNewest.size(); i++) {
                String key = ConstantsCls.distionMapNewest.get(i);
                MessageCls sms = ConstantsCls.hashNewest.get(key);
                MessageCls sms2 = ConstantsCls.hashFirstData.get(key);
                g.setColor(Color.red);
                g.setFont(new java.awt.Font("aria", 10, 10));
                int viTrix = (int) sms.getFirst().getX() - (int) (sms2.getLon() / 2);
                int viTriy = (int) sms.getFirst().getY() - (int) (sms2.getLag() / 2);

                img = createImage((int) sms2.getLon(), (int) sms2.getLag());
                if (img == null) {
                    return;
                }
                Graphics g1 = img.getGraphics();
                g1.drawRect(0, 0, getWidth(), getHeight());
                g1.drawImage(image, 0, 0, (int) sms2.getLon(), (int) sms2.getLag(), this);

                g.drawImage(img, viTrix, viTriy, this);
                //g.drawImage(img, (int) sms.getFirst().getX() - (int) (sms2.getLon() / 2), (int) sms.getFirst().getY() - (int) (sms2.getLag() / 2), (int) sms2.getLon(), (int) sms2.getLag(), this);
                g.drawString(key, (int) sms.getFirst().getX() - (int) (sms2.getLon() / 2), (int) sms.getFirst().getY());
                System.out.println(sms.getMa() + "  x = " + sms.getFirst().getX() + "  y = " + sms.getFirst().getY());

                System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                System.out.println("draw " + sms.getMa() + "  &>>>>>>>>>> " + ((int) sms.getFirst().getX() ) + "   "
                        + ((int) sms.getFirst().getY() ) + " lon/lag " + ((int) sms2.getLon()) + "  " + ((int) sms2.getLag()));
            }
            System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
            //ve duong tron canh bao
            DrawOvalWarm(g);
        }
    }

    private void DrawOvalWarm(Graphics g) {
        if (ConstantsCls.distionWarnDraw.size() > 0) {//draw oval jarring
            for (int i = 0; i < ConstantsCls.distionWarnDraw.size(); i++) {
                String key = ConstantsCls.distionWarnDraw.get(i);
                String s[] = key.split("//");
                MessageCls sms1 = ConstantsCls.hashNewest.get(s[0]);
                MessageCls sms2 = ConstantsCls.hashNewest.get(s[1]);
                Color c3 = new Color(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255));
                g.setColor(c3);
                if (ConstantsCls.hashNewest.containsKey(s[0]) && ConstantsCls.hashNewest.containsKey(s[1])) {
                    g.drawOval((int) sms1.getFirst().getX() - ConstantsCls.delta, (int) sms1.getFirst().getY() - ConstantsCls.delta, 2 * ConstantsCls.delta, 2 * ConstantsCls.delta);
                    g.drawOval((int) sms2.getFirst().getX() - ConstantsCls.delta, (int) sms2.getFirst().getY() - ConstantsCls.delta, 2 * ConstantsCls.delta, 2 * ConstantsCls.delta);
                    DrawPointJarring(g, sms1, sms2);//draw area jarring
                }
                ConstantsCls.distionWarnDraw.remove(key);
            }
        }
    }

    private void DrawPointJarring(Graphics g, MessageCls sms1, MessageCls sms2) {//ve diem va cham,hih vuong nguy hiem
        CoordinateCls diemVaCham = ConstantsCls.hashJarringPoint.get(sms1.getMa() + "//" + sms2.getMa());
        //
        if (ConstantsCls.FindDistance(sms1.getFirst(), diemVaCham) < 70 && ConstantsCls.FindDistance(sms2.getFirst(), diemVaCham) < 70) {
            g.fillOval((int) diemVaCham.getX(), (int) diemVaCham.getY(), 5, 5);
            g.drawRect((int) diemVaCham.getX() - 70, (int) diemVaCham.getY() - 70, 140, 140);
        }
    }

    private void DrawGrid(Graphics g) {
        //while (true) {
        g.setColor(Color.black);
        for (int i = 0; i <= ConstantsCls.width; i += 50) {
            g.drawString(i + "", i, 10);//ve truc x tren
        }
        for (int i = 50; i <= ConstantsCls.width; i += 50) {
            g.drawString(i + "", i, ConstantsCls.heigh);//ve truc x duoi
        }
        g.setColor(Color.red);
        for (int i = 50; i <= ConstantsCls.heigh; i += 50) {
            g.drawString(i + "", 0, i);//ve truc y trai
        }
        for (int i = 50; i < ConstantsCls.heigh; i += 50) {
            g.drawString(i + "", ConstantsCls.width + 10, i);//ve truc y phai
        }        //ve luoi ngang
        g.setColor(Color.blue);
        for (int i = 0; i <= ConstantsCls.heigh - 25; i += 25) {
            for (int j = 0; j <= ConstantsCls.width; j += 100) {
                g.drawString("--------------------------", j, i);
            }
        }
        //ve luoi doc
        for (int i = 25; i <= ConstantsCls.width; i += 50) {
            for (int j = 0; j <= ConstantsCls.heigh; j += 105) {
                g.drawString("|", i, j);
                g.drawString("|", i, j + 15);
                g.drawString("|", i, j + 30);
                g.drawString("|", i, j + 45);

                g.drawString("|", i, j + 60);
                g.drawString("|", i, j + 75);
                g.drawString("|", i, j + 90);
                g.drawString("|", i, j + 105);
                //
                g.drawString("|", i + 25, j);
                g.drawString("|", i + 25, j + 15);
                g.drawString("|", i + 25, j + 30);
                g.drawString("|", i + 25, j + 45);
                g.drawString("|", i + 25, j + 60);
                g.drawString("|", i + 25, j + 75);
                g.drawString("|", i + 25, j + 90);
                g.drawString("|", i + 25, j + 105);

            }
        }
        //}
    }
}
