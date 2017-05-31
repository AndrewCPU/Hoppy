package packets;

/**
 * Created by stein on 5/31/2017.
 */
public class BackgroundPacket {
    private String image;

    public BackgroundPacket(String image) {
        this.image = image;
    }

    public BackgroundPacket() {

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
