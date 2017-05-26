package packets;

/**
 * Created by stein on 5/14/2017.
 */
public class RemoveBulletPacket {
    private String UUID;

    public RemoveBulletPacket(String UUID) {
        this.UUID = UUID;
    }

    public RemoveBulletPacket() {
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }
}
