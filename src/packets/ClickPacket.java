package packets;

/**
 * Created by stein on 5/14/2017.
 */
public class ClickPacket {
    private int x, y;

    public ClickPacket(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public ClickPacket() {
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
}
