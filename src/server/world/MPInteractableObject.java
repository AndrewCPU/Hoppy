package server.world;

import org.w3c.dom.css.Rect;
import server.world.interfaces.Interactable;
import server.world.interfaces.Interaction;

import java.awt.*;

/**
 * Created by stein on 5/27/2017.
 */
public class MPInteractableObject extends MPObject implements Interactable {

    private Interaction interaction;

    private int delay = 60;

    private int curTime = 0;

    private Rectangle originalState = null;


    public MPInteractableObject(Rectangle rectangle, Interaction interaction, MPWorld world) {
        super(rectangle, world);
        originalState = (Rectangle)rectangle.clone();
        this.interaction = interaction;
    }

    @Override
    public Interaction getInteraction() {
        return interaction;
    }


    @Override
    public void tick(){
        if(curTime != -1)
            curTime++;
        if(curTime > delay)
        {
            reverseAction();
            curTime = -1;
        }
    }

    public void reverseAction(){
        if(getInteraction() == Interaction.DOOR){
            setRectangle(originalState);
        }
    }

    @Override
    public void interact() {
        if(getInteraction() == Interaction.DOOR && curTime == -1){
            curTime = 0;
            originalState = (Rectangle)getRectangle().clone();
            setRectangle(new Rectangle(getRectangle().x,getRectangle().y,0,0));
        }
    }

    @Override
    public void interact(MPPlayer player) {
        interact();
    }
}
