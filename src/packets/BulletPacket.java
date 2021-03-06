package packets;

/**
 * Created by stein on 5/14/2017.
 */
public class BulletPacket {
    private int x, y, width, height;
    private String uuid;

    public BulletPacket(int x, int y, int width, int height, String uuid) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.uuid = uuid;
    }

    public BulletPacket() {
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
