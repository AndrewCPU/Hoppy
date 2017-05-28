package server.world;

import client.world.Player;
import com.esotericsoftware.kryonet.Connection;
import packets.RemoveBulletPacket;
import server.MPServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stein on 5/14/2017.
 */
public class MPWorld {
    private List<MPPlayer> players = new ArrayList<>();
    private List<MPObject> objects = new ArrayList<>();
    private List<MPBullet> bullets = new ArrayList<>();
    public MPWorld(){

    }

    public List<MPPlayer> getPlayers() {
        return players;
    }


    public void addPlayer(MPPlayer player){
        queue(player);
    }
    public void addObject(MPObject object){
        queue(object);
    }


    public void setPlayers(List<MPPlayer> players) {
        this.players = players;
    }

    public List<MPObject> getObjects() {
        return objects;
    }

    public void setObjects(List<MPObject> objects) {
        this.objects = objects;
    }

    private List<Object> objectQueue = new ArrayList<>();

    public void queue(Object o){
        objectQueue.add(o);
    }

    public void addBullet(MPBullet bullet){
        queue(bullet);
    }

    public List<MPBullet> getBullets() {
        return bullets;
    }

    public void setBullets(List<MPBullet> bullets) {
        this.bullets = bullets;
    }

    public boolean isSafeMove(int x, int y){
        for(MPObject object : objects){
            if(object.getRectangle().contains(x,y)) return false;
        }
        return true;
    }

    public MPPlayer doesBulletHitPlayer(int x, int y){
        for(MPPlayer player : players){
            if(player.getBounds().contains(x,y))
                return player;
        }
        return null;
    }

    public MPPlayer getPlayer(Connection connection){
        for(MPPlayer player : players){
            if(player.getConnection() == connection)
                return player;
        }
        return null;
    }
    public MPPlayer getPlayer(String name){
        for(MPPlayer player : players){
            if(player.getName().equalsIgnoreCase(name))
                return player;
        }
        return null;
    }


    public MPPlayer getNearestPlayer(MPPlayer player){
        MPPlayer closest = null;
        for(MPPlayer p : players){
            if(p != player && !(p instanceof MPEnemy)){
                if(closest == null)
                    closest = p;
                else if(p.distance(player) < player.distance(closest)){
                    closest = p;
                }
            }
        }
        return closest;
    }

    public MPBullet getNearestBullet(MPPlayer player){
        MPBullet closest = null;
        for(MPBullet p : bullets){
            if(p.getShooter() == player)
                continue;
                if(closest == null)
                    closest = p;
                else if(p.distance(player) < player.distance(closest)){
                    closest = p;
                }
        }
        return closest;
    }


    public void tick(){
        for(Object o : objectQueue){
            if(o instanceof MPBullet)
                bullets.add((MPBullet)o);
            if(o instanceof MPPlayer)
                players.add((MPPlayer)o);
            if(o instanceof MPObject)
                objects.add((MPObject)o);
        }
        objectQueue.clear();
        for(MPBullet bullet : bullets)
            bullet.tick();
        for(MPPlayer player : players)
            player.tick();
        for(MPObject object : objects)
            object.tick();
        for(int i = bullets.size() - 1; i>=0; i--) {
            if (!bullets.get(i).isAlive()) {
                MPServer.getInstance().getServer().sendToAllTCP(new RemoveBulletPacket(bullets.remove(i).getUUID()));
            }
        }
    }
}
