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

    private Dot player;
    public Game(){

    }

    public void searchPlatforms(Robot r){
        dots = new ArrayList<Dot>();
        Color previous = null, now = null;
        int x = 660, y;
        BufferedImage screen = r.createScreenCapture(new Rectangle(1920, 1080));
        while(x <= 1259){
            y = 205;
            while(y <= 1029){
                now = new Color(screen.getRGB(x, y));
                if(previous == null){
                    previous = now;
                }
                else{
                    if(now.getBlue() == 0 && now.getRed() == 0 && now.getGreen() == 0){
                        if(r.getPixelColor(x + 40, y).getRed() == 0 &&
                                r.getPixelColor(x + 40, y).getBlue() == 0 &&
                                r.getPixelColor(x + 40, y).getGreen() == 0)
                        player = new Dot(x, y);
                    }
                    if(Math.abs(now.getBlue() + now.getRed() + now.getGreen() - previous.getRed() - previous.getBlue() - previous.getGreen()) > 400){
                        boolean danger = false;
                        for(int i = x - 70; i < x + 70; i++){
                            for(int j = y - 20; j < y + 20; j++){
                                if(now.equals(new Color(244,180,83))){
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
            x = x + 8;
        }
    }

    public void choseTarget(){
        int ymin = 1000;
        int xmin = 1000;
        for(int i = 0; i < dots.size(); i++){
            if(dots.get(i).y < ymin && (dots.get(i).y - player.y) + (dots.get(i).x - player.x) > 100){
                ymin = dots.get(i).y;
                xmin = dots.get(i).x;
                target = dots.get(i);
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
            if(r.getPixelColor(841, 162).equals(new Color(249, 223, 65))) {
                searchPlatforms(r);
                if(target != null && player != null){
                    if(target.y > player.y + 100){
                        choseTarget();
                    }
                }
                else{
                    choseTarget();
                }
                for (int i = 0; i < 20; i++) {
                    if(target.x < player.x){
                        r.keyPress(KeyEvent.VK_A);
                        r.mousePress(InputEvent.BUTTON1_MASK);
                        r.delay(5);
                        r.mouseRelease(InputEvent.BUTTON1_MASK);
                        r.keyRelease(KeyEvent.VK_A);
                    }
                    else if(target.x >= player.x){
                        r.keyPress(KeyEvent.VK_D);
                        r.mousePress(InputEvent.BUTTON1_MASK);
                        r.delay(5);
                        r.mouseRelease(InputEvent.BUTTON1_MASK);
                        r.keyRelease(KeyEvent.VK_D);
                    }
                    else{
                        r.mousePress(InputEvent.BUTTON1_MASK);
                        r.delay(5);
                        r.mouseRelease(InputEvent.BUTTON1_MASK);
                    }
                }
                if (!r.getPixelColor(841, 162).equals(new Color(249, 223, 65))) {
                    break;
                }
            }
        }

    }


}
