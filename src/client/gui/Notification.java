package client.gui;

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

    public Notification(String message) {
        this.message = message;
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
}
