package client.world;

import packets.BulletPacket;

import java.util.UUID;

/**
 * Created by stein on 5/14/2017.
 */
public class Bullet {

    public static Bullet fromPacket(BulletPacket packet){
        return new Bullet(packet.getX(),packet.getY(),packet.getWidth(),packet.getHeight(),UUID.fromString(packet.getUUID()));
    }

    private int x, y, width, height;
    private UUID uuid;

    public Bullet(int x, int y, int width, int height, UUID uuid) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.uuid = uuid;
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

    public UUID getUUID() {
        return uuid;
    }

    public void apply(BulletPacket packet){
        setX(packet.getX());
        setY(packet.getY());
        setWidth(packet.getWidth());
        setHeight(packet.getHeight());
    }
}
