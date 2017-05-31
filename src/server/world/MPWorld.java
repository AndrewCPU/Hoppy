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
    private List<MPRoom> rooms = new ArrayList<>();
    private MPRoom outside;
    public MPWorld(){
        outside = new MPRoom("OUTSIDE");
        rooms.add(outside);
    }

    public MPRoom createRoom(){
        rooms.add(new MPRoom());
        return rooms.get(rooms.size() - 1);
    }
    public MPRoom createRoom(String s){
        rooms.add(new MPRoom(s));
        return rooms.get(rooms.size() - 1);
    }

    public List<MPRoom> getRooms() {
        return rooms;
    }

    public MPRoom getRoomFromID(String id){
        for(MPRoom room : rooms){
            if(room.getID().equalsIgnoreCase(id))
                return room;
        }
        return null;
    }

    public List<MPPlayer> getPlayers(){
        List<MPPlayer> players = new ArrayList<>();
        for(MPRoom room : rooms)
            for(MPPlayer player : room.getPlayers())
                players.add(player);
        return players;
    }

    public MPRoom getRoomFromPlayer(MPPlayer player){
        for(MPRoom room : rooms){
            if(room.getPlayers().contains(player))
                return room;
        }
        return null;
    }
    public MPRoom getRoomFromObject(MPObject object){
        for(MPRoom room : rooms){
            if(room.getObjects().contains(object))
                return room;
        }
        return null;
    }
    public MPRoom getRoomFromBullet(MPBullet bullet){
        for(MPRoom room : rooms)
            if(room.getBullets().contains(bullet))
                return room;
        return null;
    }
    public boolean isSafeMove(int x, int y, MPRoom room){
        for(MPObject object : room.getObjects()){
            if(object.getRectangle().contains(x,y)) return false;
        }
        return true;
    }
    public MPPlayer doesBulletHitPlayer(int x, int y, MPRoom room){
        for(MPPlayer player : room.getPlayers()){
            if(player.getBounds().contains(x,y))
                return player;
        }
        return null;
    }

    public MPPlayer getPlayer(Connection connection){
        for(MPPlayer player : getPlayers()){
            if(player.getConnection() == connection)
                return player;
        }
        return null;
    }

    public MPPlayer getPlayer(String name){
        for(MPPlayer player : getPlayers()){
            if(player.getName().equalsIgnoreCase(name))
                return player;
        }
        return null;
    }

    public MPPlayer getNearestPlayer(MPPlayer player){
        MPPlayer closest = null;
        for(MPPlayer p : getRoomFromPlayer(player).getPlayers()){
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
        for(MPBullet p : getRoomFromPlayer(player).getBullets()){
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

    public List<MPBullet> getBullets(){
        List<MPBullet> bullets = new ArrayList<>();
        for(MPRoom room : rooms)
            bullets.addAll(room.getBullets());
        return bullets;
    }
    public List<MPObject> getObjects(){
        List<MPObject> objects = new ArrayList<>();
        for(MPRoom room: rooms)
            objects.addAll(room.getObjects());
        return objects;
    }


    public void tick(){
        for(MPRoom room : rooms)
            room.tick();
        for(MPBullet bullet : getBullets())
            bullet.tick();
        for(MPPlayer player : getPlayers())
            player.tick();
        for(MPObject object : getObjects())
            object.tick();
        for(int i = getBullets().size() - 1; i>=0; i--) {
            if (!getBullets().get(i).isAlive()) {
                MPServer.getInstance().getServer().sendToAllTCP(new RemoveBulletPacket(getBullets().remove(i).getUUID()));
            }
        }
    }
}
