package client.world;

import packets.ObjectPacket;

import java.awt.*;
import java.util.UUID;

/**
 * Created by stein on 5/14/2017.
 */
public class GameObject {
    public static GameObject fromPacket(ObjectPacket packet){
        return new GameObject(new Rectangle(packet.getX(),packet.getY(),packet.getWidth(),packet.getHeight()), UUID.fromString(packet.getUUID()));
    }

    private Rectangle rectangle;
    private UUID uuid;
    public GameObject(Rectangle rectangle, UUID uuid) {
        this.rectangle = rectangle;
        this.uuid = uuid;
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

    public void apply(ObjectPacket packet){
        rectangle = new Rectangle(packet.getX(),packet.getY(),packet.getWidth(),packet.getHeight());
    }
}
