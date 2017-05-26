package packets;

/**
 * Created by stein on 5/14/2017.
 */
public class RemovePlayerPacket {
    private String UUID;

    public RemovePlayerPacket(String UUID) {
        this.UUID = UUID;
    }

    public RemovePlayerPacket() {
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }
}
