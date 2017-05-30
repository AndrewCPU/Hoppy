package packets;

/**
 * Created by stein on 5/14/2017.
 */
public class ObjectPacket {
    private int x, y, width, height;
    private String uuid;
    private int type = 0;
    private String imageURL;

    public ObjectPacket(int x, int y, int width, int height, String uuid, int type, String imageURL) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.uuid = uuid;
        this.type = type;
        this.imageURL = imageURL;
    }

    public ObjectPacket() {
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUUID() {
        return uuid;
    }

}
