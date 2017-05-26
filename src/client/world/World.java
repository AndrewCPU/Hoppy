package client.world;

import client.Game;
import client.gui.Notification;
import packets.BulletPacket;
import packets.ObjectPacket;
import packets.PlayerPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by stein on 5/14/2017.
 */
public class World {
    private List<Bullet> bullets = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private List<GameObject> gameObjects = new ArrayList<>();
    public World(){

    }

    public void addBullet(Bullet bullet){
        bullets.add(bullet);
    }

    public void addPlayer(Player player){
        players.add(player);
    }

    public void removePlayer(Player player){
        players.remove(player);
    }

    public void addObject(GameObject object){
        gameObjects.add(object);
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public void setBullets(List<Bullet> bullets) {
        this.bullets = bullets;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public void setGameObjects(List<GameObject> gameObjects) {
        this.gameObjects = gameObjects;
    }

    public void removeBullet(Bullet bullet){
        bullets.remove(bullet);
    }

    public Bullet getBulletFromUUID(UUID uuid){
        for(Bullet bullet : bullets)
            if(bullet.getUUID().toString().equals(uuid.toString()))
                return bullet;
        return null;
    }
    public Player getPlayerFromUUID(UUID uuid){
        for(Player player : players)
            if(player.getUUID().toString().equals(uuid.toString()))
                return player;
        return null;
    }
    public GameObject getGameObjectFromUUID(UUID uuid){
        for(GameObject object: gameObjects)
            if(object.getUUID().toString().equals(uuid.toString()))
                return object;
        return null;
    }

    public void interpurtPlayerPacket(PlayerPacket packet){
        if(getPlayerFromUUID(UUID.fromString(packet.getUuid())) == null) {
            players.add(Player.fromPacket(packet));
        }
        else {
            String name = getPlayerFromUUID(UUID.fromString(packet.getUuid())).getName();
            getPlayerFromUUID(UUID.fromString(packet.getUuid())).apply(packet);
            String name2 = getPlayerFromUUID(UUID.fromString(packet.getUuid())).getName();
            if(!name.equals(name2))
                Game.getInstance().canvas.showNotification(new Notification(name2 + " has joined the game..."));


        }
    }
    public void interpurtObjectPacket(ObjectPacket packet){
        if(getGameObjectFromUUID(UUID.fromString(packet.getUUID())) == null)
            gameObjects.add(GameObject.fromPacket(packet));
        else
            getGameObjectFromUUID(UUID.fromString(packet.getUUID())).apply(packet);
    }
    public void interpurtBulletPacket(BulletPacket packet){
        if(getBulletFromUUID(UUID.fromString(packet.getUUID())) == null)
            bullets.add(Bullet.fromPacket(packet));
        else
            getBulletFromUUID(UUID.fromString(packet.getUUID())).apply(packet);
    }
}
