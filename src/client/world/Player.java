package client.world;

import packets.PlayerPacket;

import java.awt.*;
import java.util.UUID;

/**
 * Created by stein on 5/14/2017.
 */
public class Player {

    public static Player fromPacket(PlayerPacket packet){
        return new Player(packet.getX(),packet.getY(), UUID.fromString(packet.getUuid()), packet.getName(), new Color(packet.getRed(), packet.getGreen(), packet.getBlue()));
    }


    private int score = 0;
    private int x, y;
    private UUID uuid;
    private String name = "";
    private Color color;
    public Player(int x, int y, UUID uuid, Color color) {
        this.x = x;
        this.y = y;
        this.uuid = uuid;
        this.color = color;
    }

    public Player(int x, int y, UUID uuid, String name, Color color) {
        this.x = x;
        this.y = y;
        this.uuid = uuid;
        this.name = name;
        this.color = color;
    }


    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public UUID getUUID() {
        return uuid;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void apply(PlayerPacket packet){
        setX(packet.getX());
        setY(packet.getY());
        setScore(packet.getScore());
        setName(packet.getName());
    }

}
