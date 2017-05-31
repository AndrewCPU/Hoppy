package client;

import client.drawing.Canvas;
import client.drawing.ImageDatabase;
import client.gui.Notification;
import client.world.GameObject;
import client.world.Player;
import client.world.World;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import packets.*;
import server.MPServer;
import utils.ConnectionManager;
import utils.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by stein on 5/14/2017.
 */
public class Game extends JFrame implements KeyListener,MouseListener{
    private World world;
    public Canvas canvas;
    private Client client;
    public UUID uuid;

    private static Game instance;

    public static Game getInstance(){
        return instance;
    }

    public Game(String IP){

        instance = this;
        world = new World();
        canvas = new Canvas(world);
        client = new Client();
        ConnectionManager.register(client.getKryo());
        client.addListener(new Listener(){
            @Override
            public void received(Connection connection, Object o) {
                canvas.repaint();
                receive(o);
            }

            @Override
            public void connected(Connection connection) {
                NamePacket packet = new NamePacket(JOptionPane.showInputDialog("Please enter your name:"));
                connection.sendTCP(packet);
            }
        });
        client.start();
        if(IP.length() == 0){
            IP =client.discoverHost(25565,25565).getHostAddress().toString();
        }
        try {
            client.connect(5000,IP,25565,25565);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setLayout(null);
        setBounds(0,0,750,600);
        canvas.setBounds(0,0,750,600);
        addKeyListener(this);
        addMouseListener(this);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                canvas.setBounds(0,0,getWidth(),getHeight());
            }
        });
        add(canvas);

        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.close();
            }
        });
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    public void receive(Object o){
        if(o instanceof PlayerPacket){
            world.interpurtPlayerPacket((PlayerPacket)o);
        }
        if( o instanceof ObjectPacket){
            world.interpurtObjectPacket((ObjectPacket)o);
        }
        if(o instanceof BulletPacket){
            world.interpurtBulletPacket((BulletPacket)o);
        }
        if(o instanceof RemovePlayerPacket ){
            Player player = world.getPlayerFromUUID(UUID.fromString(((RemovePlayerPacket)o).getUUID()));
            world.removePlayer(world.getPlayerFromUUID(UUID.fromString(((RemovePlayerPacket) o).getUUID())));
            //canvas.showNotification(new Notification(player.getName() + " has left the game..."));
        }
        if( o instanceof RemoveBulletPacket){
            world.removeBullet(world.getBulletFromUUID(UUID.fromString(((RemoveBulletPacket)o).getUUID())));
        }
        if(o instanceof NamePacket){
            UUID uuid = UUID.fromString(((NamePacket) o).getUuid());
            this.uuid = uuid;
        }
        if(o instanceof NotificationPacket){
            NotificationPacket packet = (NotificationPacket)o;
            if(packet.getUUID().length() != 0)
                canvas.showNotification(new Notification(packet.getMessage(),UUID.fromString(packet.getUUID())));
            else
                canvas.showNotification(new Notification(((NotificationPacket)o).getMessage()));
        }
        if(o instanceof RemoveObjectPacket){
            GameObject object = world.getGameObjectFromUUID(UUID.fromString(((RemoveObjectPacket) o).getUUID()));
            if(object != null)
                world.removeObject(object);
        }
        if(o instanceof BackgroundPacket){
            if(((BackgroundPacket) o).getImage().length() > 0) {
                ImageDatabase.getInstance().get(((BackgroundPacket) o).getImage());
            }
            canvas.setBackgroundPath(((BackgroundPacket) o).getImage());
        }
//        Log.d("PACKET RECEIVED");
    }

    public Point getOffsets(){

        int yOffset = 0;
        int xOffset = 0;
        //250 = 10 + y
        if(Game.getInstance().getUUID() != null && world!=null && Game.getInstance() != null && world.getPlayerFromUUID(Game.getInstance().getUUID())!=null){
            xOffset = getWidth() / 2 - world.getPlayerFromUUID(Game.getInstance().getUUID()).getX();
            yOffset = getHeight() - ((int)(getHeight() / 2.5)) - world.getPlayerFromUUID(Game.getInstance().getUUID()).getY();
        }

        return new Point(xOffset,yOffset);
    }

    public UUID getUUID(){
        return this.uuid;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        KeyPacket keyPacket = new KeyPacket(true,e.getKeyCode());
        client.sendTCP(keyPacket);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        KeyPacket keyPacket = new KeyPacket(false,e.getKeyCode());
        client.sendTCP(keyPacket);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        ClickPacket clickPacket = new ClickPacket(e.getX() - getOffsets().x,e.getY() - getOffsets().y);
        client.sendTCP(clickPacket);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
