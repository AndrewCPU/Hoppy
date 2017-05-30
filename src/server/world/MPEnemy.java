package server.world;

import com.esotericsoftware.kryonet.Connection;
import packets.NotificationPacket;
import packets.PlayerPacket;
import server.MPServer;
import server.world.interfaces.Interactable;
import server.world.interfaces.Interaction;
import utils.Log;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * Created by stein on 5/26/2017.
 */
public class MPEnemy extends MPPlayer implements Interactable {
    private MPPlayer target = null;
    private int idle = 0;
    private int delay = 0;
    private Random random = new Random();
    private int tracking_distance = random.nextInt(2000 - 1000) + 1000;
    private int shooting_distance = random.nextInt(1000 - 200);
    private int sight_distance = random.nextInt(700 - 200);
    private boolean left = new Random().nextBoolean();
    private Interaction interaction = Interaction.CHAT;

    public boolean AI = true;

    public MPEnemy(int x, int y, MPWorld world) {
        super(x, y, null, world);
        setColor(Color.BLACK);
    }

    @Override
    public void interact() {

    }

    @Override
    public void interact(MPPlayer player) {
        player.getConnection().sendTCP(new NotificationPacket( "Hi " + player.getName() + ", I'm " + getName() + " and I'm here to destroy you!", getUUID().toString()));
    }

    @Override
    public Interaction getInteraction() {
        return null;
    }

    @Override
    public void tick(){

        if(!AI)
            return;

        if(random.nextInt(50) == 2){
            if(isPressing(KeyEvent.VK_SHIFT))
                releaseKey(KeyEvent.VK_SHIFT);
            else
                pressKey(KeyEvent.VK_SHIFT);
        }

        if(target!=null){
            if(getWorld().getNearestPlayer(this) != target)
                target = getWorld().getNearestPlayer(this);
            if(target.distance(this) > tracking_distance)
                target = null;
            else{


                    if (target.getX() > getX()) {
                        releaseKey(KeyEvent.VK_A);
                        pressKey(KeyEvent.VK_D);
                    } else if (target.getX() < getX()) {
                        releaseKey(KeyEvent.VK_D);
                        pressKey(KeyEvent.VK_A);
                    }



                if(target.distance(this) <= shooting_distance){
                        if(target.distance(this) <= shooting_distance / 2) {
                            releaseKey(KeyEvent.VK_D);
                            releaseKey(KeyEvent.VK_A);
                        }
                    if(delay < 0) {
                        MPServer.getInstance().click(this, new Point(target.getX(), target.getY()));
                        delay = new Random().nextInt(50);
                    }
                    delay--;
                }
            }
        }


        if(target == null){

            MPPlayer closest = getWorld().getNearestPlayer(this);
            if(closest.distance(this) <= sight_distance){
                target = closest;
            }


            idle++;
            if(idle > 500){
                left = !left;
                idle = 0;
            }
            if(left){
                releaseKey(KeyEvent.VK_D);
                pressKey(KeyEvent.VK_A);
            }
            else{
                releaseKey(KeyEvent.VK_A);
                pressKey(KeyEvent.VK_D);
            }

        }

        super.tick();
    }
    @Override
    public PlayerPacket getPacket() {
        return new PlayerPacket(getUUID().toString(),getX(),getY(),  getScore(), getColor().getRed(), getColor().getGreen(), getColor().getBlue(), getName(), false);
    }
}
