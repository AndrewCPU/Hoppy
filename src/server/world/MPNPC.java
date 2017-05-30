package server.world;

import com.esotericsoftware.kryonet.Connection;
import packets.NotificationPacket;
import packets.PlayerPacket;
import server.MPServer;
import server.world.interfaces.Interactable;
import server.world.interfaces.Interaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by stein on 5/28/2017.
 */
public class MPNPC extends MPPlayer implements Interactable{
    private Interaction interaction = Interaction.CHAT;
    private String[] messages;
    private List<Action> actions = new ArrayList<>();
    private int currentAction = -1;
    private MPPlayer trigger = null;
    public MPNPC(int x, int y, MPWorld world) {
        super(x, y, null, world);
    }

    public void setMessages(String... messages){
        for(int i = 0; i<messages.length; i++){
            messages[i] = messages[i].replaceAll("%name%", getName()).replaceAll("%first_name%",getName().split(" ")[0]).replaceAll("%last_name%", getName().split(" ")[1]);
        }
        this.messages = messages;
    }


    public void addActions(Action... action){
        for(Action a : action)
            addAction(a);
    }
    public void addAction(Action action){
        actions.add(action);
    }

    @Override
    public void interact() {

    }

    @Override
    public void interact(MPPlayer player) {
        Random r = new Random();
        int i = r.nextInt(messages.length);
        player.getConnection().sendTCP(new NotificationPacket(messages[i], getUUID().toString()));
        currentAction = 0;
        if(trigger == null)
            trigger = player;
    }

    @Override
    public void tick(){
        if(currentAction != -1 && actions.size() != 0){
            actions.get(0).run();
            currentAction++;
            if(currentAction > actions.get(0).getDuration()){
                actions.get(0).finish();
                actions.remove(0);
                currentAction = 0;
            }
        }
        super.tick();
    }

    @Override
    public PlayerPacket getPacket() {
        return new PlayerPacket(getUUID().toString(),getX(),getY(),  getScore(), getColor().getRed(), getColor().getGreen(), getColor().getBlue(), getName(), false);
    }

    @Override
    public Interaction getInteraction() {
        return null;
    }

    public MPPlayer getTrigger() {
        return trigger;
    }

    public void setTrigger(MPPlayer trigger) {
        this.trigger = trigger;
    }

}
