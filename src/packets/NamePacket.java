package packets;

/**
 * Created by stein on 5/15/2017.
 */
public class NamePacket {
    private String name;
    private String uuid;

    public NamePacket(String name) {
        this.name = name;
    }

    public NamePacket() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
