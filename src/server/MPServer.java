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
        //outside.queue(new MPObject(new Rectangle(0,250,1000,50),world));
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
        MPRoom castle = world.createRoom("CASTLE");
        castle.setBackgroundImage("http://www.drodd.com/images14/black11.jpg");
        Random random = new Random();
        MPObject object = new MPObject(new Rectangle(x,y,width,height),world);
        MPRoom outside = world.getRoomFromID("OUTSIDE");
        world.getRoomFromID("OUTSIDE").queue(object);
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
        MPInteractableObject door = new MPInteractableObject(new Rectangle(2000,800,100,199), Interaction.DOOR,world){
            @Override
            public void interact() {
                super.interact();
            }

            @Override
            public void interact(MPPlayer player) {

                MPRoom currentRoom = player.getWorld().getRoomFromPlayer(player);
                currentRoom.leaveRoom(player);

                castle.queue(player);

                //super.interact(player);
            }
        };




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
                    if(getNpc().getX() >= 1900 && getNpc().getX() < 2150)
                        door.interact();
                    if(getNpc().getX() >= 3250)
                        setDuration(0);
                }
            }

            @Override
            public void finish() {
                getNpc().speakTo(getNpc().getTrigger(),"Thank you so much! I can finally sleep in peace!");

                getNpc().speakTo(getNpc().getTrigger(), "Take $100 to show my gratitude!");

                getNpc().getTrigger().speakTo(getNpc().getTrigger(), "Not a problem! My pleasure! I enjoyed it anyways, I hate ninjas..");

                getNpc().speakTo(getNpc().getTrigger(), "If you ever need anything, I'll be right here in my castle..");

                getNpc().getTrigger().setScore(getNpc().getTrigger().getScore() + 100);

                getNpc().setMessages("*mumbling* Man... That " + getNpc().getTrigger().getName() + " character is such a nice person...");

                getNpc().releaseKey(KeyEvent.VK_D);

                spawnVillagers();
            }


            public void spawnVillagers(){
                for(int i = 1500; i<1900; i+=random.nextInt(120 - 50) + 50){
                    int stopX =  new Random().nextInt(4000 - 2500) + 2500;
                    MPNPC npc = NPCMaker.createNPC(i, 500, world,"I can't wait to get back out there and fight some monsters!", "For some reason the ground floats around way above the castle...","Life is great*!","Perhaps the castle is haunted, that's why it's always getting taken over.","Gosh sometimes I wish our castle was bigger...","Did you hear about the ninjas?!?","I'm so glad that " + getNpc().getTrigger().getName() + " got rid of the ninjas. What a legend...","I'm going to start writing this in the history books!","It is 10:58PM and I'm writing dialog. help.","Well hello!", "I work for " + getNpc().getName() + "...", "My name is %name%.", getNpc().getName() + " likes to make stupid James Bond quotes whenever he meets new people..", "Man. I missed our castle thank you so much!");
                    npc.addAction(new Action(npc,99999){
                        @Override
                        public void run() {
                            getNpc().pressKey(KeyEvent.VK_D);
                            if(getNpc().getX() >= 1900 && getNpc().getX() < 2150)
                                door.interact();
                            if(getNpc().getX() >= stopX){
                                setDuration(0);
                            }
                        }

                        @Override
                        public void finish() {
                            getNpc().releaseKey(KeyEvent.VK_D);
                        }
                    });
                    npc.interact(getNpc().getTrigger());
                    outside.queue(npc);
                }
            }
        });
        outside.queue(questGiver);
        door.setImage("http://www.glenviewdoors.com/PRODUCT-DETAILS-Stock-Entry-Doors-GD/012T_Mahogany-Walnut/big.jpg");
        outside.queue(door);
        MPObject wall = new MPObject(new Rectangle(3900,0, 100, 999), world);
        MPObject wall2 = new MPObject(new Rectangle(-4100,0, 100, 1999), world);
        MPObject roof = new MPObject(new Rectangle(2000,500, 1900, 100), world);

        MPObject topDoor = new MPObject(new Rectangle(2000,500,100,300),world);



        wall.setImage("http://media.indiedb.com/images/articles/1/119/118275/auto/Brick_Wall_01.jpg");
        topDoor.setImage(wall.getImage());
        wall2.setImage("http://media.indiedb.com/images/articles/1/119/118275/auto/Brick_Wall_01.jpg");

        outside.queue(topDoor);
        outside.queue(wall);
        outside.queue(wall2);

        roof.setImage("https://s-media-cache-ak0.pinimg.com/736x/42/54/51/4254512a8d78430c334f8faec2e5367d.jpg");
        outside.queue(roof);
        for(int i = 0; i<5; i++){
            int randX = random.nextInt(3900 - 2100) + 2100 ;
            MPEnemy enemy = new MPEnemy(randX, 600, world){
                @Override
                public void death(MPPlayer killer) {
                    removePlayer(this);
                }
            };
            Log.d("ENEMY:" + randX);
            enemy.setName("Ninja");
            outside.queue(enemy);
        }
        for(int YY = 0; YY<=480; YY+=150){
            int XX = random.nextBoolean() ? -random.nextInt(3500) : random.nextInt(1500);
            MPPhysicsObject physicsObject = new MPPhysicsObject(new Rectangle(XX, YY, 200, 140),world, false);
            physicsObject.setImage("https://s-media-cache-ak0.pinimg.com/originals/dc/a6/95/dca6956e7b22e07ed1fc6110d124914b.jpg");
            outside.queue(physicsObject);
        }

        castle.queue(new MPObject(new Rectangle(-2000,1000,4000,100),world));
    }

//    public void generateMap(){
//        int x = -4000;
//        int y = 1000;
//        int width = 8000;
//        int height = 500;
//        MPObject object = new MPObject(new Rectangle(x,y,width,height),world);
//        outside.queue(object);
//        Random r = new Random();
//        outer:
//        for(int i = 0; i< 20; i++){
//            int blockX = r.nextBoolean() ? r.nextInt(4000) : -r.nextInt(4000);
//            int blockY = r.nextInt(800);
//            int blockWidth = r.nextInt(400 - 100) + 100;
//            int blockHeight = r.nextInt(200 - 50) + 50;
//            blockY -= blockHeight;
////            boolean bad = false;
//            MPObject o = new MPObject(new Rectangle(blockX,blockY,blockWidth,blockHeight),world);
//            for(MPObject mpObject : world.getObjects()){
//                if(mpObject.getRectangle().intersects(o.getRectangle())){
//                    continue outer;
////                    i--;
//                }
//            }
//            outside.queue(o);
//        }
//
//
//
//        MPObject wallLeft = new MPObject(new Rectangle(-4050,-1000,50,4000), world);
//        MPObject wallRight = new MPObject(new Rectangle(4000,-1000,50,4000), world);
//        outside.queue(wallLeft);
//        outside.queue(wallRight);
//
//        for(int i = 0; i<5; i++){
//            int pX = r.nextBoolean()  ? -r.nextInt(3000) : r.nextInt(3000);
//            int pY = -r.nextInt(1000);
//            int pW = r.nextInt(400 - 200) + 200;
//            int pH = 100;
//            MPPhysicsObject phys = new MPPhysicsObject(new Rectangle(pX,pY,pW,pH),world,false);
////            phys.velocity = r.nextInt(6 - 1) + 1;
//            outside.queue(phys);
//        }
//
//        for(int i = 0; i<3; i++){
//            int eX  = r.nextBoolean() ? -r.nextInt(3000)  : r.nextInt(3000);
////            MPEnemy enemy = new MPEnemy(eX,0, world);
////            enemy.setName("Enemy");
//
//            outside.queue(NPCMaker.createNPC(eX,0,world,"Hi there how are you today?", "I'm really sad lately,", "Do you want to be my friend?", "They call me %name%."));
//        }
//
//
//    }
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
                    //outside.queue(object);
                }
                else if(split[1].equalsIgnoreCase("load")){
                    String path = "";
                    for(int i = 2; i<split.length; i++){
                        path+= split[i] + " ";
                    }
                    //world.setObjects(getObjectsFromFile(new File(path)));
                    for(MPPlayer player : world.getPlayers())
                    {
                        player.setX(50);
                        player.setY(50);
                    }
                }
                else if(split[1].equals("gravity")){
                    MPPlayer player = world.getPlayer(split[2]);

                    MPPhysicsObject physicsObject = new MPPhysicsObject(new Rectangle(player.getX(),player.getY() - 100,200,50),world, false);
                    //outside.queue(physicsObject);
                }
            }
            else if(split[0].equals("e")){
                if(split[1].equals("c")){
                    MPPlayer player = world.getPlayer(split[2]);
                    MPEnemy enemy = new MPEnemy(player.getX(),player.getY(),world);
                    enemy.setName(split[3]);
                    //outside.queue(enemy);
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
        for(MPRoom room : world.getRooms()){
            List<Object> packets = new ArrayList<>();
            for(MPPlayer player : room.getPlayers())
                packets.add(player.getPacket());
            for(MPObject object : room.getObjects())
                packets.add(object.getPacket());
            for(MPBullet bullet : room.getBullets())
                packets.add(bullet.getPacket());
            packets.add(new BackgroundPacket(room.getBackgroundImage()));
            for(MPPlayer player : room.getPlayers()) {
                if (player.getConnection() != null) {
                    for (Object o : packets) {
                        player.getConnection().sendTCP(o);
                    }
                    NamePacket namePacket = new NamePacket();
                    namePacket.setUuid(player.getUUID().toString());
                    player.getConnection().sendTCP(namePacket);
                }
            }
        }
    }

    public void click(MPPlayer player, Point point){
        double rise = 1 - (point.y - (player.getY() - 1));
        double run = 1 - (point.x - (player.getX() - 1));
        MPBullet bullet = new MPBullet(player.getX() + 25, player.getY() + 25, -run, -rise,player,world);
        world.getRoomFromPlayer(player).queue(bullet);
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

            //connection.sendTCP(packet);

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
        world.getRoomFromID("OUTSIDE").queue(player);
        Log.i("A player has connected from " + connection.getRemoteAddressTCP().toString());
    }

    public Server getServer() {
        return server;
    }
}
