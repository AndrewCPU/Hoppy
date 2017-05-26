package server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import packets.ClickPacket;
import packets.KeyPacket;
import packets.NamePacket;
import packets.RemovePlayerPacket;
import server.world.MPBullet;
import server.world.MPObject;
import server.world.MPPlayer;
import server.world.MPWorld;
import server.world.MPPhysicsObject;
import utils.ConnectionManager;
import utils.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by stein on 5/14/2017.
 */
public class MPServer {
    public static void main(String[] args) {
        new MPServer();
    }
    private Server server;
    private int TCP = 25565, UDP = 25565;
    private MPWorld world = new MPWorld();
    public static String VERSION = "1.0.0";

    private static MPServer instance = null;
    public static MPServer getInstance(){
        return instance;
    }

    public MPServer(){
        instance = this;
        server = new Server();
        try {
            server.bind(TCP,UDP);
        } catch (IOException e) {
            Log.e("Unable to bind ports: " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        Log.i("Starting server on port(s): " + TCP + "/" + UDP );

        ConnectionManager.register(server.getKryo());

        Log.i("Registered packets...");
        server.addListener(new Listener(){
            @Override
            public void connected(Connection connection) {
                connect(connection);
            }

            @Override
            public void disconnected(Connection connection) {
                disconnect(connection);
            }

            @Override
            public void received(Connection connection, Object o) {
                receive(connection,o);
            }

            @Override
            public void idle(Connection connection) {
                super.idle(connection);
            }
        });
        Log.i("Registered listener...");
        server.start();
        Log.i("Starting server...");
        //world.addObject(new MPObject(new Rectangle(0,250,1000,50),world));
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(()->{
            world.tick();
            updatePlayers();
            }, 16, 16, TimeUnit.MILLISECONDS);

        JFrame command = new JFrame("Enter a command:");
        command.setBounds(0,0,300,120);
        JTextField text = new JTextField();
        text.setBounds(0,0,150,100);
        JButton submit = new JButton("Submit");
        submit.setBounds(150,0,150,100);
        command.setLayout(null);
        command.add(text);
        command.add(submit);
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                acceptCommand(text.getText());
                text.setText("");
            }
        });
        command.setVisible(true);
        generateMap();
        //acceptCommand("object load J:\\Map1.txt");

    }
    public void generateMap(){
        int x = -4000;
        int y = 1000;
        int width = 8000;
        int height = 500;
        MPObject object = new MPObject(new Rectangle(x,y,width,height),world);
        world.addObject(object);
        Random r = new Random();
        for(int i = 0; i< 20; i++){
            int blockX = r.nextBoolean() ? r.nextInt(4000) : -r.nextInt(4000);
            int blockY = r.nextInt(800);
            int blockWidth = r.nextInt(400 - 100) + 100;
            int blockHeight = r.nextInt(200 - 50) + 50;
            blockY -= blockHeight;
            boolean bad = false;
            MPObject o = new MPObject(new Rectangle(blockX,blockY,blockWidth,blockHeight),world);
            for(MPObject mpObject : world.getObjects()){
                if(mpObject.getRectangle().intersects(o.getRectangle())){
                    bad = true;
                    i--;
                }
            }
            if(!bad)
                world.addObject(o);
        }

    }
    public java.util.List<MPObject> getObjectsFromFile(File file){
        List<MPObject> objects = new ArrayList<>();
        Scanner scanner = null;
        try{
            scanner = new Scanner(file);
        }catch (Exception ex){}
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] split = line.split(" ");
            int x = Integer.parseInt(split[0]);
            int y = Integer.parseInt(split[1]);
            int w = Integer.parseInt(split[2]);
            int h = Integer.parseInt(split[3]);
            MPObject object = new MPObject(new Rectangle(x,y,w,h),world);
            objects.add(object);
        }
        return objects;
    }

    public void acceptCommand(String s){
        String[] split = s.split(" ");
        if(split.length == 0){
            if(s.equals("info")){
                Log.i("Version: " + VERSION);
            }
        }
        else{
            if(split[0].equalsIgnoreCase("object")){
                if(split[1].equalsIgnoreCase("add")){
                    int x = Integer.parseInt(split[2]);
                    int y = Integer.parseInt(split[3]);
                    int width = Integer.parseInt(split[4]);
                    int height = Integer.parseInt(split[5]);
                    MPObject object = new MPObject(new Rectangle(x,y,width,height),world);
                    world.addObject(object);
                }
                else if(split[1].equalsIgnoreCase("load")){
                    String path = "";
                    for(int i = 2; i<split.length; i++){
                        path+= split[i] + " ";
                    }
                    world.setObjects(getObjectsFromFile(new File(path)));
                    for(MPPlayer player : world.getPlayers())
                    {
                        player.setX(50);
                        player.setY(50);
                    }
                }
                else if(split[1].equals("gravity")){
                    MPPlayer player = world.getPlayer(split[2]);

                    MPPhysicsObject physicsObject = new MPPhysicsObject(new Rectangle(player.getX(),player.getY() - 100,200,50),world, false);
                    world.addObject(physicsObject);
                }
            }
        }
    }
    public void updatePlayers(){
        List<MPPlayer> deadPlayers = new ArrayList<>();
        for(MPPlayer player : world.getPlayers()) {
            if(player.getConnection().isConnected())
            {
                server.sendToAllTCP(player.getPacket());
            }
            else{
                deadPlayers.add(player);
            }
        }
        for(MPPlayer p : deadPlayers){
            disconnect(p.getConnection());
        }
        for(MPObject object : world.getObjects())
            server.sendToAllTCP(object.getPacket());
        for(MPBullet bullet : world.getBullets())
            server.sendToAllTCP(bullet.getPacket());
    }


    public void receive(Connection connection, Object o){
        if(o instanceof KeyPacket){
            KeyPacket packet = (KeyPacket)o;
            if(packet.isPress()){
                world.getPlayer(connection).pressKey(packet.getKey());
            }
            else{
                world.getPlayer(connection).releaseKey(packet.getKey());
            }
        }
        if(o instanceof ClickPacket){
            ClickPacket packet = (ClickPacket)o;
            MPPlayer player = world.getPlayer(connection);

            double rise = 1 - (packet.getY() - (player.getY() - 1));
            double run = 1 - (packet.getX() - (player.getX() - 1));
           // Log.d("");
            //Log.d(rise + " = rise");
            //Log.d(run+ " = run");


            MPBullet bullet = new MPBullet(player.getX() + 25, player.getY() + 25, -run, -rise,player,world);
            //System.out.println(slope);
            world.addBullet(bullet);
        }
        if(o instanceof NamePacket){
            MPPlayer player = world.getPlayer(connection);
            player.setName(((NamePacket)o).getName());

            ((NamePacket) o).setUuid(player.getUUID().toString());



            connection.sendTCP(o);

        }
    }
    public void disconnect(Connection connection){
        RemovePlayerPacket removePlayerPacket = new RemovePlayerPacket(world.getPlayer(connection).getUUID().toString());
        server.sendToAllTCP(removePlayerPacket);
        world.getPlayers().remove(world.getPlayer(connection));
        //Log.i("A player has disconnected from " + connection.getRemoteAddressTCP().toString());
    }

    public void connect(Connection connection){
        MPPlayer player = new MPPlayer(50,50,connection, world) ;
        world.addPlayer(player);
        Log.i("A player has connected from " + connection.getRemoteAddressTCP().toString());
    }

    public Server getServer() {
        return server;
    }
}
