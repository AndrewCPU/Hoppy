package client.world;

import packets.ObjectPacket;

import java.awt.*;
import java.util.UUID;

/**
 * Created by stein on 5/14/2017.
 */
public class GameObject {
    public static GameObject fromPacket(ObjectPacket packet){
        return new GameObject(new Rectangle(packet.getX(),packet.getY(),packet.getWidth(),packet.getHeight()), UUID.fromString(packet.getUUID()), packet.getType(),packet.getImageURL());
    }

    private Rectangle rectangle;
    private UUID uuid;
    private int type;
    private String image;

    public GameObject(Rectangle rectangle, UUID uuid, int type, String image) {
        this.rectangle = rectangle;
        this.uuid = uuid;
        this.type = type;
        this.image = image;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void apply(ObjectPacket packet){
        rectangle = new Rectangle(packet.getX(),packet.getY(),packet.getWidth(),packet.getHeight());
    }
}
