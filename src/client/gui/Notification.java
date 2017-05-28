package client.gui;

import java.util.UUID;

/**
 * Created by stein on 5/25/2017.
 */
public class Notification {
    private int y = -50;
    private int time = 0;
    private int maxTime = 500;
    private String message;
    private boolean opening = true;
    private boolean alive = true;
    private UUID uuid;

    public Notification(String message) {
        this.message = message;
    }


    public Notification(String message, UUID uuid) {
        this.message = message;
        this.uuid = uuid;
    }

    public void step(){
        if(opening){
            if(y != 0)
                y++;
            if(y == 0) {
                time++;
                if(time >= maxTime)
                    opening = false;
            }
        }
        else{
            y--;
            if(y == -51)
                alive = false;
        }
    }

    public int getY() {
        return y;
    }
    public void setY(int y){
        this.y = y;
    }

    public void reset(){
        y = -50;
        time = 0;
        alive = true;
        opening = true;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }
}
