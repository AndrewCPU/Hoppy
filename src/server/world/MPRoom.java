package server.world;

import packets.BulletPacket;
import packets.RemoveBulletPacket;
import packets.RemoveObjectPacket;
import packets.RemovePlayerPacket;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by stein on 5/31/2017.
 */
public class MPRoom {
    private List<MPObject> objects = new ArrayList<>();
    private List<MPPlayer> players = new ArrayList<>();
    private List<MPBullet> bullets = new ArrayList<>();

    private Point spawnPoint = new Point(50,50);

    private String ID;

    private List<Object> queue = new ArrayList<>();

    private String backgroundImage = "";

    public MPRoom(){
        ID = "RM-" + new Random().nextInt(2000);
    }
    public MPRoom(String s){
        this.ID = s;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public List<MPObject> getObjects() {
        return objects;
    }

    public void setObjects(List<MPObject> objects) {
        this.objects = objects;
    }

    public List<MPPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<MPPlayer> players) {
        this.players = players;
    }

    public List<MPBullet> getBullets() {
        return bullets;
    }

    public void setBullets(List<MPBullet> bullets) {
        this.bullets = bullets;
    }

    public Point getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(Point spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    public void queue(Object o){
        queue.add(o);
    }

    public void tick(){
        for(Object o : queue){
            if(o instanceof MPBullet)
                bullets.add((MPBullet)o);
            if(o instanceof MPObject)
                objects.add((MPObject)o);
            if(o instanceof MPPlayer)
                joinRoom((MPPlayer)o);
        }
        queue.clear();
    }

    public void leaveRoom(MPPlayer player){
        players.remove(player);
        for(MPObject object : objects){
            if(player.getConnection() != null){
                player.getConnection().sendTCP(new RemoveObjectPacket(object.getUUID()));
            }
        }
        for(MPPlayer p : players){
            if(player.getConnection() != null)
                player.getConnection().sendTCP(new RemovePlayerPacket(p.getUUID().toString()));
            if(p.getConnection() != null)
                p.getConnection().sendTCP(new RemovePlayerPacket(player.getUUID().toString()));
        }
        for(MPBullet bullet : bullets){
            if(player.getConnection() != null)
                player.getConnection().sendTCP(new RemoveBulletPacket(bullet.getUUID().toString()));
        }
    }
    public void joinRoom(MPPlayer player){
            players.add(player);
            player.setX(spawnPoint.x);
            player.setY(spawnPoint.y);
            for(MPObject object : objects){
                if(player.getConnection() != null){
                    player.getConnection().sendTCP(object.getPacket());
                }
            }
            for(MPPlayer p : players){
                if(player.getConnection() != null){
                    player.getConnection().sendTCP(p.getPacket());
                }
                if(p.getConnection() != null)
                    p.getConnection().sendTCP(player.getPacket());
            }
            for(MPBullet bullet : bullets){
                if(player.getConnection() != null)
                    player.getConnection().sendTCP(bullet.getPacket());
            }
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
