package server.world;

import com.esotericsoftware.kryonet.Connection;
import packets.PlayerPacket;
import server.world.interfaces.Tickable;
import utils.Log;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;

/**
 * Created by stein on 5/14/2017.
 */
public class MPPlayer implements Tickable {
    private int width = 50, height = 50, x, y, score = 0;
    private UUID uuid = null;
    private java.util.List<Integer> keys = new ArrayList<>();
    private Connection connection;
    private MPWorld world;
    private int yMod = 0;
    private Color color = getRandomColor();

    private double velX = 0, velY = 0;


    private String name = "Guy" + new Random().nextInt(100);
    public MPPlayer(int x, int y, Connection connection, MPWorld world) {
        this.x = x;
        this.y = y;
        this.uuid = UUID.randomUUID();
        this.connection = connection;
        this.world = world;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Connection getConnection() {
        return connection;
    }

    public UUID getUUID(){
        return uuid;
    }


    public Rectangle getBounds(){
        return new Rectangle(getX(),getY(),getWidth(),getHeight());
    }


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


    public Color getRandomColor(){
        Random random = new Random();
        return new Color(random.nextInt(256),random.nextInt(256),random.nextInt(256));
    }

    public void pressKey(int i){
        if(!keys.contains(i))
            keys.add(i);
    }
    public void releaseKey(int i){
        if(keys.contains(i))
            keys.remove(Integer.valueOf(i));
    }
    public boolean isPressing(int i){
        return keys.contains(i);
    }

    public MPWorld getWorld() {
        return world;
    }

    public void setWorld(MPWorld world) {
        this.world = world;
    }

    public boolean canMove(int x, int y){
        int width = 50;
        int height = 50;

        return world.isSafeMove(x,y) && world.isSafeMove(x + width, y) && world.isSafeMove(x, y + height) && world.isSafeMove(x + width, y + height);

    }

    public double getVelX() {
        return velX;
    }

    public void setVelX(double velX) {
        this.velX = velX;
    }

    public double getYMod(){
        return yMod;
    }

    @Override
    public void tick() {

        int speed = isPressing(KeyEvent.VK_SHIFT) ? 8 : 5;

        if(isPressing(KeyEvent.VK_A)) {
            if (canMove(x - speed, y)) {
                velX = -speed;
            }
            else{
                velX = 0;
            }
        }
        if(isPressing(KeyEvent.VK_D)) {
            if (canMove(x + speed, y)) {
                velX = speed;
            }
            else{
                velX = 0;
            }
        }
        if(isPressing(KeyEvent.VK_SPACE)) {
            if (yMod == 0 && getWorld().isSafeMove(x,y + 5))
                yMod = -15;

        }
        if(isPressing(KeyEvent.VK_R))
        {
            Random random = new Random();
            setX(random.nextBoolean() ? random.nextInt(4000) : -random.nextInt(4000));
            setY(0);
//            world.getBullets().forEach((b)->b.setAlive(false));
        }
        x+=velX;
        if(velX > 0)
            velX-=0.5;
        else if(velX < 0)
            velX+=0.5;

        yMod++;
        if(yMod > 20 && !isPressing(KeyEvent.VK_SHIFT))
            yMod = 20;
        else if(yMod > 60  && isPressing(KeyEvent.VK_SHIFT))
            yMod = 60;

        if(yMod > 0){
//            Log.d("+" + yMod);
            for(int i = 0; i<yMod; i++){
                if(canMove(x,y+1))
                    y+=1;
                else {
                    yMod = (int)(-yMod / 1.5);

                    break;
                }
            }
        }
        else if(yMod < 0){
//            Log.d("-" + yMod);
            for(int i = yMod; i<0; i++){
                if(canMove(x,y-1))
                    y-=1;
                else {
                    yMod = 0;
                    velX = 0;
                    break;
                }
            }
        }
        if(getY() > 5000) {
            setY(-5000);
            setScore(getScore() - 1);
        }
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void death(MPPlayer killer){
        if(killer != null){
            killer.setScore(killer.getScore() + 1);
            setScore( score - 1);
            setX(50);
            setY(50);
        }
    }

    public PlayerPacket getPacket(){
        return new PlayerPacket(getUUID().toString(),getX(),getY(),  getScore(), color.getRed(), color.getGreen(), color.getBlue(), getName());
    }
}
