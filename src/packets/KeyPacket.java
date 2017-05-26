package packets;

/**
 * Created by stein on 5/14/2017.
 */
public class KeyPacket {
    private boolean press;
    private int key;

    public KeyPacket(boolean press, int key) {
        this.press = press;
        this.key = key;
    }

    public KeyPacket() {
    }

    public boolean isPress() {
        return press;
    }

    public void setPress(boolean press) {
        this.press = press;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }
}
