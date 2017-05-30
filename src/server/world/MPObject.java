package server.world;

import packets.ObjectPacket;
import server.world.interfaces.Body;
import server.world.interfaces.Tickable;

import java.awt.*;
import java.util.UUID;

/**
 * Created by stein on 5/14/2017.
 */
public class MPObject implements Tickable, Body{
    private Rectangle rectangle;
    private MPWorld world;
    private String uuid = UUID.randomUUID().toString();
    private String image = "";
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ObjectPacket getPacket(){
        int type = 0;
        if(this instanceof MPPhysicsObject){
            type = 1;
        }
        else if(this instanceof MPInteractableObject){
            type = 2;
        }
        return new ObjectPacket(rectangle.x,rectangle.y,rectangle.width,rectangle.height,uuid, type, image);
    }

    @Override
    public double distance(Body body) {
        double least = Integer.MAX_VALUE;
        for(int x = getRectangle().x; x<getRectangle().x + getRectangle().width; x++ ){
            for(int y = getRectangle().y; y<getRectangle().y + getRectangle().height; y++){
                if(new Point(body.getRectangle().x,body.getRectangle().y).distance(new Point(x,y)) < least)
                    least = new Point(body.getRectangle().x,body.getRectangle().y).distance(new Point(x,y));
            }
        }
        return least;
    }
}
