package server.world;

import packets.BulletPacket;
import server.world.interfaces.Body;
import server.world.interfaces.Tickable;
import utils.Log;

import java.awt.*;
import java.util.UUID;

/**
 * Created by stein on 5/14/2017.
 */
public class MPBullet implements Tickable,Body{
    private double x, y, velX, velY, width = 5, height = 5;
    private MPWorld world;
    private String uuid = UUID.randomUUID().toString();
    private boolean alive = true;
    private MPPlayer shooter;
    private double speed = 5;
    public MPPlayer getShooter() {
        return shooter;
    }

    public void setShooter(MPPlayer shooter) {
        this.shooter = shooter;
    }

    public MPBullet(double x, double y, double velX, double velY, MPPlayer shooter, MPWorld world) {
        this.x = x;
        this.y = y;
        this.velX = velX;
        this.velY = velY;
        this.world = world;
        this.shooter = shooter;
        if(shooter.getYMod() != 0 || shooter.getVelX() != 0){
            double d = shooter.getYMod() + shooter.getVelX();
            //
            d *= 2;

            speed = Math.abs(d);
        }

    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getVelX() {
        return velX;
    }

    public void setVelX(double velX) {
        this.velX = velX;
    }

    public double getVelY() {
        return velY;
    }

    public void setVelY(double velY) {
        this.velY = velY;
    }

    public MPWorld getWorld() {
        return world;
    }

    public void setWorld(MPWorld world) {
        this.world = world;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getUUID() {
        return uuid;
    }

    @Override
    public void tick() {
        step();
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public Rectangle getRectangle() {
        return new Rectangle((int)getX(),(int)getY(),(int)width,(int)height);
    }

    public void step(){
        double slope = velY / velX;

        if(velX > 0) {
            y += slope * 5;
            x += speed;
        }
        else{
            y -= slope * speed;
            x-=speed;
        }
        MPPlayer player = world.doesBulletHitPlayer((int)x,(int)y);

        if(world.isSafeMove((int)x,(int)y)) {
            if (player != null && player != getShooter()) {
               // Log.e("BULLET DIED WHEN HITTING " + player.getUUID().toString() + " , " + player.getName());
                setAlive(false);
                player.death(getShooter());
            }
        }
        else{
            ///Log.e("DIED BC HIT SOMETHING");
//            setVelX(0);
//            setVelY(0);
                setAlive(false);
        }
//        x+=slope > 0 ? -1 : 1;

        if(getX() > 50000 || getX() < - 50000 || getY() > 50000 || getY() < - 50000){
        //    Log.e("DIED");ddaa
            setAlive(false);
        }
    }

    public BulletPacket getPacket(){
        return new BulletPacket((int)getX(),(int)getY(),(int)getWidth(),(int)getHeight(),getUUID());
    }

    @Override
    public double distance(Body body) {
        return new Point(body.getRectangle().x,body.getRectangle().y).distance(new Point(getRectangle().x,getRectangle().y));
    }

}
