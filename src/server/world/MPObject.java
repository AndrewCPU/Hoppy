package server.world;

import packets.ObjectPacket;
import server.world.interfaces.Tickable;

import java.awt.*;
import java.util.UUID;

/**
 * Created by stein on 5/14/2017.
 */
public class MPObject implements Tickable{
    private Rectangle rectangle;
    private MPWorld world;
    private String uuid = UUID.randomUUID().toString();

    public MPObject(Rectangle rectangle, MPWorld world) {
        this.rectangle = rectangle;
        this.world = world;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public MPWorld getWorld() {
        return world;
    }

    public void setWorld(MPWorld world) {
        this.world = world;
    }

    public String getUUID() {
        return uuid;
    }

    @Override
    public void tick() {

    }

    public ObjectPacket getPacket(){
        return new ObjectPacket(rectangle.x,rectangle.y,rectangle.width,rectangle.height,uuid);
    }
}