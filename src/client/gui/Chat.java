package client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by stein on 5/28/2017.
 */
public class Chat {
    private UUID user;
    private List<Message> messages = new ArrayList<>();
    public Chat(UUID user){
        this.user = user;
    }

    public UUID getUser() {
        return user;
    }

    public void setUser(UUID user) {
        this.user = user;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
