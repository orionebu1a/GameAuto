package org.example;

import net.bytebuddy.build.Plugin;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.awt.event.KeyAdapter;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;


import static com.sun.java.accessibility.util.AWTEventMonitor.addKeyListener;

public class Game {
    public class Dot{
        public int x;
        public int y;
        public Dot(int x, int y){
            this.x = x;
            this.y = y;
        }
    }
    private int stop = 0;
    private ArrayList<Dot> dots;

    private Dot target;

    private Dot prevTarget;

    private Dot player;

    private Dot prevPlayer;

    private Date lastTime;
    public Game(){

    }

    public void searchPlatforms(Robot r){
        dots = new ArrayList<Dot>();
        Color previous = null, now = null;
        int x = 700, y;
        BufferedImage screen = r.createScreenCapture(new Rectangle(1920, 1080));
        while(x <= 1200){
            y = 205;
            while(y <= 1029){
                now = new Color(screen.getRGB(x, y));
                if(previous == null){
                    previous = now;
                }
                else{
                    if(now.getBlue() == 0 && now.getRed() == 0 && now.getGreen() == 0){
                        Color second = new Color(screen.getRGB(x + 38, y));
                        if(second.getRed() == 0 && second.getBlue() == 0 && second.getGreen() == 0){
                            player = new Dot(x + 20, y + 30);
                        }
                    }
                    if(Math.abs(now.getBlue() + now.getRed() + now.getGreen() - previous.getRed() - previous.getBlue() - previous.getGreen()) > 300){
                        boolean danger = false;
                        for(int i = x - 70; i < x + 70; i++){
                            for(int j = y - 20; j < y + 20; j++){
                                if(now.equals(new Color(244,180,83)) || (now.getRed() == now.getGreen() && now.getBlue() == now.getRed() && now.getGreen() == now.getBlue())){
                                    danger = true;
                                }
                            }
                        }
                        if(!danger){
                            dots.add(new Dot(x, y));
                        }
                        y = y + 30;
                    }
                    previous = now;
                }
                y++;
            }
            x = x + 1;
        }
    }

    public void choseTarget(){
        int ymin = 0;
        int xmin = 1000;
        prevTarget = target;
        for(int i = 0; i < dots.size(); i++){
            if(dots.get(i).y > ymin
                    && Math.abs((dots.get(i).y - player.y) + (dots.get(i).x - player.x)) > 200
                    && dots.get(i).y < 900 && dots.get(i).y > 400
                    && Math.abs(dots.get(i).y - player.y) < 400){
                /*if(prevTarget != null){
                    if(dots.get(i).y < prevTarget.y){
                        ymin = dots.get(i).y;
                        xmin = Math.abs(dots.get(i).x - player.x);
                        target = dots.get(i);
                    }
                }
                else{*/
                    ymin = dots.get(i).y;
                    xmin = Math.abs(dots.get(i).x - player.x);
                    target = dots.get(i);
                //}
            }
        }
    }

    public void play(){
        Robot r = null;
        try {
            r = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        r.delay(10000);
        while(true){
            if(player != null){
                System.out.printf("player: %d, %d\n", player.x, player.y);
            }
            if(target != null){
                System.out.printf("target: %d, %d\n", target.x, target.y);
            }
            if (!r.getPixelColor(841, 162).equals(new Color(249, 223, 65))) {
                break;
            }
            if(r.getPixelColor(841, 162).equals(new Color(249, 223, 65))) {
                searchPlatforms(r);
                if(target != null && player != null){
                    Date newDate = new Date();
                    if(Math.abs(target.y - player.y + target.x - target.y) < 100 || newDate.getTime() - lastTime.getTime() > 2000){
                        lastTime = new Date();
                        choseTarget();
                    }
                }
                else{
                    lastTime = new Date();
                    choseTarget();
                }
                if(player == null || target == null){
                    continue;
                }
                for(int i = 0; i < 10; i++) {
                    if (target.x < player.x) {
                        r.keyPress(KeyEvent.VK_A);
                        //r.mousePress(InputEvent.BUTTON1_MASK);
                        r.delay(1);
                        //r.mouseRelease(InputEvent.BUTTON1_MASK);
                        r.keyRelease(KeyEvent.VK_A);
                    } else if (target.x >= player.x) {
                        r.keyPress(KeyEvent.VK_D);
                        //r.mousePress(InputEvent.BUTTON1_MASK);
                        r.delay(1);
                        //r.mouseRelease(InputEvent.BUTTON1_MASK);
                        r.keyRelease(KeyEvent.VK_D);
                    } else {
                        //r.mousePress(InputEvent.BUTTON1_MASK);
                        r.delay(1);
                        //r.mouseRelease(InputEvent.BUTTON1_MASK);
                    }
                }
            }
        }

    }


}
