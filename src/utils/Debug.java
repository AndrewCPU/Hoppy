package utils;

import client.Game;
import server.MPServer;

/**
 * Created by stein on 5/14/2017.
 */
public class Debug {
    public static void main(String[] args) {
        new MPServer();
        new Game("127.0.0.1");
    }
}
