package server;

import client.world.Player;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import packets.*;
import server.world.*;
import server.world.Action;
import server.world.interfaces.Interaction;
import utils.ConnectionManager;
import utils.Log;
import utils.NPCMaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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
        server = new Server(16384 * 2, 2048 * 2);
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
//        generateMap();
        generateDemo();
        //acceptCommand("object load J:\\Map1.txt");

    }

    public void generateDemo(){
        int x = -4000;
        int y = 1000;
        int width = 8000;
        int height = 500;
        Random random = new Random();
        MPObject object = new MPObject(new Rectangle(x,y,width,height),world);
        world.addObject(object);
        MPNPC questGiver = NPCMaker.createNPC(50,50,world,"Welcome! They call me %last_name%, %name%... I need your help fighting the ninjas! Follow me!");
        Action reset = new Action(questGiver,9999999){
            @Override
            public void run() {
                getNpc().pressKey(KeyEvent.VK_A);
                if(getNpc().getX() <= 50)
                    setDuration(0);
            }

            @Override
            public void finish() {
                getNpc().releaseKey(KeyEvent.VK_A);
                questGiver.setMessages("Oh hello! The castle is to the right! Go check it out if you haven't yet. But beware!");
                //getNpc().addAction(starterAction);
            }
        };
        Action starterAction = new Action(questGiver,99999) {
            @Override
            public void run() {
                getNpc().pressKey(KeyEvent.VK_D);
                if(getNpc().getX() >= 1800)
                    setDuration(0);
            }

            @Override
            public void finish() {
                getNpc().releaseKey(KeyEvent.VK_D);
                getNpc().getTrigger().getConnection().sendTCP(new NotificationPacket("The ninjas are right past this door! To get through the door walk up to it and press 'E'.", getNpc().getUUID().toString()));
            }
        };

        questGiver.addAction(starterAction);
        questGiver.addAction(reset);
        MPInteractableObject door = new MPInteractableObject(new Rectangle(2000,0,100,999), Interaction.DOOR,world);
        questGiver.addAction(new Action(questGiver,999999){
            @Override
            public void run() {
                int ninjasLeft = 0;
                for(MPPlayer p : world.getPlayers()){
                    if(p instanceof MPEnemy){
                        if(p.getName().equals("Ninja")){
                            ninjasLeft++;
                        }
                    }
                }
                if(ninjasLeft == 0){
                    getNpc().pressKey(KeyEvent.VK_D);
//                    getNpc().pressKey(KeyEvent.VK_E);
                    door.interact();
                    if(getNpc().getX() >= 3250)
                        setDuration(0);
                }
            }

            @Override
            public void finish() {
                getNpc().getTrigger().getConnection().sendTCP(new NotificationPacket("Thank you so much! I can finally sleep in peace!", getNpc().getUUID().toString()));
//                getNpc().releaseKey(KeyEvent.VK_E);
                getNpc().releaseKey(KeyEvent.VK_D);
            }
        });
        world.addPlayer(questGiver);

        world.addObject(door);
        MPObject wall = new MPObject(new Rectangle(3900,0, 100, 999), world);
        MPObject roof = new MPObject(new Rectangle(2000,0, 2000, 100), world);
        world.addObject(wall);
        world.addObject(roof);
        for(int i = 0; i<5; i++){
            int randX = random.nextInt(3900 - 2100) + 2100 ;
            MPEnemy enemy = new MPEnemy(randX, 150, world){
                @Override
                public void death(MPPlayer killer) {
                    removePlayer(this);
                }
            };
            Log.d("ENEMY:" + randX);
            enemy.setName("Ninja");
            world.addPlayer(enemy);
        }

    }

    public void generateMap(){
        int x = -4000;
        int y = 1000;
        int width = 8000;
        int height = 500;
        MPObject object = new MPObject(new Rectangle(x,y,width,height),world);
        world.addObject(object);
        Random r = new Random();
        outer:
        for(int i = 0; i< 20; i++){
            int blockX = r.nextBoolean() ? r.nextInt(4000) : -r.nextInt(4000);
            int blockY = r.nextInt(800);
            int blockWidth = r.nextInt(400 - 100) + 100;
            int blockHeight = r.nextInt(200 - 50) + 50;
            blockY -= blockHeight;
//            boolean bad = false;
            MPObject o = new MPObject(new Rectangle(blockX,blockY,blockWidth,blockHeight),world);
            for(MPObject mpObject : world.getObjects()){
                if(mpObject.getRectangle().intersects(o.getRectangle())){
                    continue outer;
//                    i--;
                }
            }
            world.addObject(o);
        }



        MPObject wallLeft = new MPObject(new Rectangle(-4050,-1000,50,4000), world);
        MPObject wallRight = new MPObject(new Rectangle(4000,-1000,50,4000), world);
        world.addObject(wallLeft);
        world.addObject(wallRight);

        for(int i = 0; i<5; i++){
            int pX = r.nextBoolean()  ? -r.nextInt(3000) : r.nextInt(3000);
            int pY = -r.nextInt(1000);
            int pW = r.nextInt(400 - 200) + 200;
            int pH = 100;
            MPPhysicsObject phys = new MPPhysicsObject(new Rectangle(pX,pY,pW,pH),world,false);
//            phys.velocity = r.nextInt(6 - 1) + 1;
            world.addObject(phys);
        }

        for(int i = 0; i<3; i++){
            int eX  = r.nextBoolean() ? -r.nextInt(3000)  : r.nextInt(3000);
//            MPEnemy enemy = new MPEnemy(eX,0, world);
//            enemy.setName("Enemy");

            world.addPlayer(NPCMaker.createNPC(eX,0,world,"Hi there how are you today?", "I'm really sad lately,", "Do you want to be my friend?", "They call me %name%."));
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
            else if(split[0].equals("e")){
                if(split[1].equals("c")){
                    MPPlayer player = world.getPlayer(split[2]);
                    MPEnemy enemy = new MPEnemy(player.getX(),player.getY(),world);
                    enemy.setName(split[3]);
                    world.addPlayer(enemy);
                }
            }
            else if(split[0].equalsIgnoreCase("kill")){
                if(split[1].equals("all")){
                    for(MPPlayer player : world.getPlayers())
                        player.death(null);
                }
                else{
                    MPPlayer p = world.getPlayer(split[2]);
                    if(p == null) return;
                    p.death(null);
                }
            }
        }
    }
    public void updatePlayers(){
        List<MPPlayer> deadPlayers = new ArrayList<>();
        for(MPPlayer player : world.getPlayers()) {

            if(player.getConnection() == null || player.getConnection().isConnected())
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

    public void click(MPPlayer player, Point point){
        double rise = 1 - (point.y - (player.getY() - 1));
        double run = 1 - (point.x - (player.getX() - 1));
        // Log.d("");
        //Log.d(rise + " = rise");
        //Log.d(run+ " = run");


        MPBullet bullet = new MPBullet(player.getX() + 25, player.getY() + 25, -run, -rise,player,world);
        //System.out.println(slope);
        world.addBullet(bullet);
    }
    public void receive(Connection connection, Object o){
        if(o instanceof KeyPacket){
            KeyPacket packet = (KeyPacket)o;
            if(world.getPlayer(connection) == null)
                return;
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
            click(player, new Point(packet.getX(),packet.getY()));
        }
        if(o instanceof NamePacket){
            MPPlayer player = world.getPlayer(connection);
            if(player == null)
                return;
            player.setName(((NamePacket)o).getName());

            //((NamePacket) o).setUuid(player.getUUID().toString());

            NamePacket packet = new NamePacket("");
            packet.setUuid(player.getUUID().toString());

            connection.sendTCP(packet);

            server.sendToAllTCP(new NotificationPacket(player.getName() + " has joined the game..."));


        }
    }

    public void removePlayer(MPPlayer player){
        RemovePlayerPacket removePlayerPacket = new RemovePlayerPacket(player.getUUID().toString());
        server.sendToAllTCP(removePlayerPacket);
        world.getPlayers().remove(player);
        //server.sendToAllTCP(new NotificationPacket(player.getName() + " has left the game..."));
    }
    public void disconnect(Connection connection){
        MPPlayer player = world.getPlayer(connection);
        removePlayer(player);
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
