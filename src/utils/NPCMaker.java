package utils;

import com.sun.deploy.net.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import server.world.MPNPC;
import server.world.MPWorld;
import sun.net.www.http.HttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static javafx.css.StyleOrigin.USER_AGENT;

/**
 * Created by stein on 5/28/2017.
 */
public class NPCMaker {
    public static void main(String[] args) {
        System.out.println(getRandomName());
    }
    public static String getRandomName(){
        JSONObject object = new JSONObject(getData());
        JSONArray array = object.getJSONArray("results");
        JSONObject name = array.getJSONObject(0).getJSONObject("name");
//        Log.d(name.get("first").toString());
        String firstName = name.getString("first");
        String lastName = name.getString("last");
        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
        lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1);
        return firstName + " " + lastName;
    }
    public static String getData(){
        try{
            URL yahoo = new URL("https://randomuser.me/api/?nat=us&inc=name&noinfo");
            URLConnection yc = yahoo.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            yc.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null)
                return inputLine;
            in.close();

        }catch (Exception ex){
            Log.e(ex.getLocalizedMessage());
        }
        return "";
    }

    public static MPNPC createNPC(String name, int x, int y, MPWorld world, String... messages){
        MPNPC npc = new MPNPC(x,y,world);
        npc.setName(name);
        npc.setMessages(messages);
        return npc;
    }
    public static MPNPC createNPC(int x, int y, MPWorld world, String... messages){
        return createNPC(getRandomName(),x,y,world,messages);
    }

}
