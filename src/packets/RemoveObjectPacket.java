package packets;

/**
 * Created by stein on 5/31/2017.
 */
public class RemoveObjectPacket {
    private String UUID;

    public RemoveObjectPacket(String UUID) {
        this.UUID = UUID;
    }

    public RemoveObjectPacket() {
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }
}
