package utils;

import com.esotericsoftware.kryo.Kryo;
import packets.*;

/**
 * Created by stein on 5/14/2017.
 */
public class ConnectionManager {
    public static void register(Kryo kryo){
        kryo.register(BulletPacket.class);
        kryo.register(KeyPacket.class);
        kryo.register(ObjectPacket.class);
        kryo.register(PlayerPacket.class);
        kryo.register(RemovePlayerPacket.class);
        kryo.register(ClickPacket.class);
        kryo.register(RemoveBulletPacket.class);
        kryo.register(NamePacket.class);
    }
}
