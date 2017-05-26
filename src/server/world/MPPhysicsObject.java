package server.world;

import server.world.MPObject;
import server.world.MPWorld;
import utils.Log;

import java.awt.*;

/**
 * Created by stein on 5/25/2017.
 */
public class MPPhysicsObject extends MPObject {
    public int velocity = 1;
    public boolean up = false;
    public MPPhysicsObject(Rectangle rectangle, MPWorld world, boolean up) {
        super(rectangle, world);
        this.up = up;
    }
    @Override
    public void tick() {
        Rectangle lastRect = getRectangle();
        __outerLoop:
        for(int y = velocity; velocity > 0 ? y > 0 : y <0 ; y=getUpdate(y,velocity)){
//            Log.d(y + "");
            Rectangle rectangle = (Rectangle)lastRect.clone();
//            rectangle.add(0,velocity > 0 ? 1 : -1);
            if(up)
                rectangle = new Rectangle(rectangle.x + 0, rectangle.y + (velocity > 0 ? 1 : -1), rectangle.width, rectangle.height);
            if(!up)
                rectangle = new Rectangle(rectangle.x + (velocity > 0 ? 1 : -1), rectangle.y , rectangle.width, rectangle.height);
            for(MPObject o : getWorld().getObjects()){
                if(o != this){
                    if(o.getRectangle().intersects(rectangle)){
                        setRectangle(lastRect);
                        velocity = -velocity;
                        Log.d("BREAKING");
                        break __outerLoop;
                    }
                }
            }
            lastRect = rectangle;
            setRectangle(lastRect);
        }

    }
    public int getUpdate(int y, int velocity){
        if(velocity > 0){
            y--;
        }
        else{
            y++;
        }
        return y;
    }
}
