package client.gui;

import java.util.UUID;

/**
 * Created by stein on 5/28/2017.
 */
public class Message {
    private UUID sender;
    private String message;

    public Message(UUID sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public UUID getSender() {
        return sender;
    }

    public void setSender(UUID sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
