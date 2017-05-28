package server.world;

/**
 * Created by stein on 5/28/2017.
 */
public class Action {
    private MPNPC npc;
    private int duration;


    public Action(MPNPC npc, int duration) {
        this.npc = npc;
        this.duration = duration;
    }

    public MPNPC getNpc() {
        return npc;
    }

    public void setNpc(MPNPC npc) {
        this.npc = npc;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void run(){

    }
    public void finish(){

    }
}
