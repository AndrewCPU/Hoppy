package packets;

/**
 * Created by stein on 5/14/2017.
 */
public class PlayerPacket {
    private String uuid;
    private int x, y, score, red, green, blue;
    private String name;

    public PlayerPacket(String uuid, int x, int y, String name) {
        this.uuid = uuid;
        this.x = x;
        this.y = y;
        this.name = name;
    }
    public PlayerPacket(String uuid, int x, int y, String name, int score) {
        this.uuid = uuid;
        this.x = x;
        this.y = y;
        this.name = name;
        this.score = score;
    }

    public PlayerPacket(String uuid, int x, int y, int score, int red, int green, int blue, String name) {
        this.uuid = uuid;
        this.x = x;
        this.y = y;
        this.score = score;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.name = name;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public PlayerPacket() {
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
