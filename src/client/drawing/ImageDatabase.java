package client.drawing;

import utils.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by stein on 5/29/2017.
 */
public class ImageDatabase {
    private HashMap<String,BufferedImage> images = new HashMap<>();
    private static ImageDatabase instance = null;
    public static ImageDatabase getInstance(){
        if(instance == null)
            instance = new ImageDatabase();
        return instance;
    }
    public ImageDatabase(){
        instance = this;
    }
    public BufferedImage get(String s){
        boolean found = false;
        for(String string : images.keySet())
            if(string.equalsIgnoreCase(s))
                found = true;
        if(!found)
        {
            try{
                Log.d("Loading new image: " + s.toUpperCase());
                BufferedImage img = ImageIO.read(new URL(s));
                images.put(s.toUpperCase(),img);
                Log.d("Loaded images: " + images.keySet().size());
            }catch (Exception ex){}
        }
        return images.get(s.toUpperCase());
    }
}
