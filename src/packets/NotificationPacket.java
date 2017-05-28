package packets;

import java.util.UUID;

/**
 * Created by stein on 5/25/2017.
 */
public class NotificationPacket {
    private String message;
    private String uuid = "";

    public NotificationPacket(String message) {
        this.message = message;
    }

    public NotificationPacket(String message, String uuid) {
        this.message = message;
        this.uuid = uuid;
    }

    public NotificationPacket(){

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }
}
