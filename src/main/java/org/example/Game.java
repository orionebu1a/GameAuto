package org.example;



import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;

public class Game {
    public class Dot{
        public int x;
        public int y;
        public Dot(int x, int y){
            this.x = x;
            this.y = y;
        }
    }
    private boolean runned = false;
    private boolean downChosen = false;
    private int pause = 5000;
    private int stop = 0;
    private ArrayList<Dot> dots;

    private Dot target;

    private Dot prevTarget;

    private Dot player = null;

    private Dot prevPlayer = null;

    private Date lastTime = null;

    private BufferedImage img;
    public Game(){

    }

    public void searchPlayer(Robot r){
        if(prevPlayer != null){
            for(int i = Math.max(prevPlayer.x - 100, 0); i < prevPlayer.x + 100 && i < 600; i ++){
                for(int j = Math.max(prevPlayer.y - 100, 0); j < prevPlayer.y + 100 && j < 1080; j ++){
                    Color now = new Color(img.getRGB(i, j));
                    if (now.getBlue() == 0 && now.getRed() == 0 && now.getGreen() == 0) {
                        Color second = new Color(img.getRGB(Math.min(i + 40, 599), j));
                        if (second.getRed() == 0) {
                            setPlayer(new Dot(i + 20, j + 30));
                            break;
                        }
                    }
                }
            }
        }
        else {
            for(int i = 0; i < 600; i = i + 4){
                for(int j = 400; j < 1080; j = j + 4){
                    Color now = new Color(img.getRGB(i, j));
                    if (now.getBlue() == 0 && now.getRed() == 0 && now.getGreen() == 0) {
                        Color second = new Color(img.getRGB(Math.min(i + 40, 599), j));
                        if (second.getRed() == 0) {
                            setPlayer(new Dot(i + 20, j + 30));
                            break;
                        }
                    }
                }
            }
        }
    }

    public void setPlayer(Dot p){
        synchronized(this){
            this.prevPlayer = this.player;
            this.player = p;
        }
    }

    public void searchPlatforms(Robot r){
        dots = new ArrayList<Dot>();
        Color previous = null, now = null;
        int x = 50, y;
        while(x < 550){
            y = 400;
            while(y <= 850){
                now = new Color(img.getRGB(x, y));
                if(previous == null){
                    previous = now;
                }
                else{
                    if(Math.abs(now.getBlue() + now.getRed() + now.getGreen() - previous.getRed() - previous.getBlue() - previous.getGreen()) > 300){
                        boolean danger = false;
                        /*for(int i = Math.max(x - 70, 0); i < Math.min(559, x + 70); i++){
                            for(int j = Math.max(0, y - 30); j < Math.min(y + 30, 1079); j++){
                                Color around = new Color(img.getRGB(i, j));
                                if(around.equals(new Color(244,180,83)) || around.equals(new Color(208,208,208))){
                                    danger = true;
                                }
                            }
                        }*/
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
        int x = 50, y;
        if(player == null){
            return;
        }
        while(x < 550){
            y = player.y;
            while(y < 1080){
                now = new Color(img.getRGB(x, y));
                if(previous == null){
                    previous = now;
                }
                else{
                    if(Math.abs(now.getBlue() + now.getRed() + now.getGreen() - previous.getRed() - previous.getBlue() - previous.getGreen()) > 300){
                        boolean danger = false;
                        /*for(int i = Math.max(x - 70, 0); i < Math.min(559, x + 70); i++){
                            for(int j = Math.max(0, y - 30); j < Math.min(y + 30, 1079); j++){
                                Color around = new Color(img.getRGB(i, j));
                                if(around.equals(new Color(244,180,83)) || around.equals(new Color(208,208,208))){
                                    danger = true;
                                }
                            }
                        }*/
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
                    && Math.abs(dots.get(i).y - player.y) < 400
                    && dots.get(i).y - player.y <= 0){
                ymin = dots.get(i).y;
                prevTarget = target;
                target = new Dot(dots.get(i).x, dots.get(i).y);
            }
        }
    }

    public void updateTarget(Robot r){
        downChosen = false;
        choseTarget();
    }

    public void updateDownTarget(Robot r){
        if(downChosen == false){
            downChosen = true;
            choseDownTarget();
        }
    }

    public class ShootThread extends Thread {
        private Robot r;
        public ShootThread(Robot r){
            this.r = r;
        }
        public void run() {
            while(runned) {
                r.keyPress(KeyEvent.VK_SPACE);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                r.keyPress(KeyEvent.VK_SPACE);
            }
        }
    }

    public class UpdateThread extends Thread {
        private Robot r;
        public UpdateThread(Robot r){
            this.r = r;
        }
        public void run() {
            while(runned) {
                Date start = new Date();
                img = r.createScreenCapture(new Rectangle(660, 0, 600, 1080));
                searchPlayer(r);
                if(player == null){
                    continue;
                }
                if (player.y - prevPlayer.y >= 15) {
                    searchDownPlatforms(r);
                    updateDownTarget(r);
                } else {
                    searchPlatforms(r);
                    updateTarget(r);
                }
                Date end = new Date();
                System.out.printf("\n%d\n", end.getTime() - start.getTime());
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public class PressThread extends Thread {
        private Robot r;
        public PressThread(Robot r){
            this.r = r;
        }
        public void run() {
            while(runned) {
                if(target == null || player == null){
                    continue;
                }
                if (target.x - player.x < -5) {
                    r.keyPress(KeyEvent.VK_A);
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    r.keyRelease(KeyEvent.VK_A);
                } else if (target.x - player.x > 5) {
                    r.keyPress(KeyEvent.VK_D);
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    r.keyRelease(KeyEvent.VK_D);
                } else {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
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
                runned = false;
            }
            if(r.getPixelColor(841, 162).equals(new Color(249, 223, 65))) {
                if(!runned){
                    (new UpdateThread(r)).start();
                    (new PressThread(r)).start();
                    (new ShootThread(r)).start();
                    runned = true;
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
