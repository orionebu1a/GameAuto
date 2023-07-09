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
    private boolean downChosen = false;
    private int pause = 5000;
    private int stop = 0;
    private ArrayList<Dot> dots;

    private Dot target;

    private Dot prevTarget;

    private Dot player;

    private Dot prevPlayer;

    private Date lastTime;

    private BufferedImage img;
    public Game(){

    }

    public void searchPlayer(Robot r){
        int x = 0, y;
        if(prevPlayer != null){
            for(int i = Math.max(prevPlayer.x - 250, 0); i < prevPlayer.x + 250 && i < 600; i ++){
                for(int j = Math.max(prevPlayer.y - 250, 0); j < prevPlayer.y + 250 && j < 1080; j ++){
                    Color now = new Color(img.getRGB(i, j));
                    if (now.getBlue() == 0 && now.getRed() == 0 && now.getGreen() == 0) {
                        Color second = new Color(img.getRGB(Math.min(i + 38, 599), j));
                        if (second.getRed() == 0 && now.getRed() == 0) {
                            prevPlayer = player;
                            player = new Dot(i + 20, j + 30);
                        }
                    }
                }
            }
        }
        else {
            while (x < 600) {
                y = 400;
                while (y < 1080) {
                    Color now = new Color(img.getRGB(x, y));
                    if (now.getBlue() == 0 && now.getRed() == 0 && now.getGreen() == 0) {
                        Color second = new Color(img.getRGB(Math.min(x + 38, 599), y));
                        if (second.getRed() == 0 && now.getRed() == 0) {
                            prevPlayer = player;
                            player = new Dot(x + 20, y + 30);
                        }
                    }
                    y = y + 5;
                }
                x = x + 5;
            }
        }
    }

    public void searchPlatforms(Robot r){
        dots = new ArrayList<Dot>();
        Color previous = null, now = null;
        int x = 0, y;
        while(x < 600){
            y = 400;
            while(y <= 850){
                now = new Color(img.getRGB(x, y));
                if(previous == null){
                    previous = now;
                }
                else{
                    if(Math.abs(now.getBlue() + now.getRed() + now.getGreen() - previous.getRed() - previous.getBlue() - previous.getGreen()) > 300){
                        boolean danger = false;
                        for(int i = x - 100; i < x + 100; i++){
                            for(int j = y - 40; j < y + 40; j++){
                                if(now.equals(new Color(244,180,83)) || now.equals(new Color(208,208,208))){
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
            x = x + 50;
        }
    }

    public void searchDownPlatforms(Robot r){
        dots = new ArrayList<Dot>();
        Color previous = null, now = null;
        int x = 0, y;
        if(player == null){
            return;
        }
        while(x < 600){
            y = player.y;
            while(y < 1080){
                now = new Color(img.getRGB(x, y));
                if(previous == null){
                    previous = now;
                }
                else{
                    if(Math.abs(now.getBlue() + now.getRed() + now.getGreen() - previous.getRed() - previous.getBlue() - previous.getGreen()) > 300){
                        boolean danger = false;
                        for(int i = x - 150; i < x + 150; i++){
                            for(int j = y - 60; j < y + 60; j++){
                                if(now.equals(new Color(244,180,83)) || (now.getBlue() == now.getRed() && now.getBlue() < 250 && now.getBlue() > 150)){
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
            x = x + 50;
        }
    }

    public void choseDownTarget(){
        int xmin = 1000;
        if(player == null){
            return;
        }
        for(int i = 0; i < dots.size(); i++){
            if(Math.abs(dots.get(i).x - player.x) < xmin && dots.get(i).y < 1029 &&
                    (dots.get(i).y - player.y) > 100 &&
                    Math.abs((dots.get(i).y - player.y) + (dots.get(i).x - player.x)) > 150) {
                xmin = Math.abs(dots.get(i).x - player.x);
                prevTarget = target;
                target = new Dot(dots.get(i).x, dots.get(i).y);
            }
        }
    }

    public void choseTarget(){
        int ymin = 0;
        if(player == null){
            return;
        }
        for(int i = 0; i < dots.size(); i++){
            if(dots.get(i).y > ymin
                    && Math.abs((dots.get(i).y - player.y) + (dots.get(i).x - player.x)) > 100
                    && Math.abs(dots.get(i).y - player.y) < 350){
                ymin = dots.get(i).y;
                prevTarget = target;
                target = new Dot(dots.get(i).x, dots.get(i).y);
            }
        }
    }

    public void updateTarget(Robot r){
        downChosen = false;
        if(target != null && player != null){
            Date newDate = new Date();
            if(Math.abs(target.y - player.y + target.x - player.x) < 50
                    || newDate.getTime() - lastTime.getTime() > 10000){
                lastTime = new Date();
                choseTarget();
            }
        }
        else{
            lastTime = new Date();
            choseTarget();
        }
    }

    public void updateDownTarget(Robot r){
        if(downChosen == false){
            downChosen = true;
            choseDownTarget();
        }
    }

    public class shootThread extends Thread {
        private Robot r;
        public shootThread(Robot r){
            this.r = r;
        }
        public void run() {
            for(int i = 0; i < 50; i++) {
                r.mousePress(InputEvent.BUTTON1_MASK);
                r.delay(1);
                r.mouseRelease(InputEvent.BUTTON1_MASK);
            }
        }
    }

    public class updateThread extends Thread {
        private Robot r;
        public updateThread(Robot r){
            this.r = r;
        }
        public void run() {
            if(player.y <= prevPlayer.y){
                searchDownPlatforms(r);
                updateDownTarget(r);
            }
            else{
                searchPlatforms(r);
                updateTarget(r);
            }
        }
    }

    public class pressThread extends Thread {
        private Robot r;
        public pressThread(Robot r){
            this.r = r;
        }
        public void run() {
            for(int i = 0; i < 50; i++) {
                if (target.x - player.x < -15) {
                    r.keyPress(KeyEvent.VK_A);
                    //r.mousePress(InputEvent.BUTTON1_MASK);
                    r.delay(1);
                    //r.mouseRelease(InputEvent.BUTTON1_MASK);
                    r.keyRelease(KeyEvent.VK_A);
                } else if (target.x - player.x > 15) {
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

    public void play(){
        Robot r = null;
        try {
            r = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        r.delay(pause);
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
                Date start = new Date();
                img = r.createScreenCapture(new Rectangle(660, 0, 600, 1080));
                searchPlayer(r);
                if(player == null){
                    continue;
                }
                (new updateThread(r)).start();
                Date end = new Date();
                System.out.println(start.getTime() - end.getTime());
                start = new Date();
                (new pressThread(r)).start();
                (new shootThread(r)).start();
                end = new Date();
                System.out.println(end.getTime() - start.getTime());
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
