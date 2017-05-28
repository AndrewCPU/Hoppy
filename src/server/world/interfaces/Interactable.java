package server.world.interfaces;

import server.world.MPPlayer;

/**
 * Created by stein on 5/27/2017.
 */
public interface Interactable {
    void interact();
    void interact(MPPlayer player);
    Interaction getInteraction();
}
