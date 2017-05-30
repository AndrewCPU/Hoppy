package client.drawing;

import client.Game;
import client.gui.Chat;
import client.gui.Message;
import client.gui.Notification;
import client.world.Bullet;
import client.world.GameObject;
import client.world.Player;
import client.world.World;
import utils.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by stein on 5/14/2017.
 */
public class Canvas extends JComponent implements MouseMotionListener{
    private World world;
    private Image background;
    private Point cur;
    private ImageDatabase database = new ImageDatabase();
    public Canvas(World world){
        //addMouseMotionListener(this);
        this.world = world;
        try{
            background = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/imgs/background.png"));
        }catch (Exception ex){ex.printStackTrace();}
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }


    @Override
    public void mouseDragged(MouseEvent e) {
        this.cur = e.getPoint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.cur = e.getPoint();
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(new Color(127,160, 167)); //todo follow uid
        int yOffset = 0;
        int xOffset = 0;
        //250 = 10 + y
        if(Game.getInstance().getUUID() != null && world!=null && Game.getInstance() != null && world.getPlayerFromUUID(Game.getInstance().getUUID())!=null){
            xOffset = getWidth() / 2 - world.getPlayerFromUUID(Game.getInstance().getUUID()).getX();
            yOffset = getHeight() - ((int)(getHeight() / 2.5)) - world.getPlayerFromUUID(Game.getInstance().getUUID()).getY();
        }

        g.fillRect(0,0,getWidth(),getHeight());


        int yOf = (yOffset / 6);
        g.drawImage(background,   -getWidth() + (xOffset / 6),0 + yOf, getWidth(), getHeight(), null);
        g.drawImage(background, (xOffset / 6),0 + yOf, getWidth(), getHeight(), null);
        g.drawImage(background, getWidth() + (xOffset / 6),0 + yOf, getWidth(), getHeight(), null);

        try{
            for(Player player : world.getPlayers()){
                g.setColor(player.getColor());
                g.fillRoundRect(player.getX() + xOffset,player.getY() + yOffset,50,50, 10, 10);
                g.setColor(Color.WHITE);




                g.fillOval(player.getX() + xOffset + 10, player.getY() + yOffset + 10, 10, 10);
                g.fillOval(player.getX() + xOffset + 30, player.getY() + yOffset + 10, 10, 10);
                g.setColor(Color.RED);
                g.fillRoundRect(player.getX() + xOffset + 10, player.getY() + yOffset + 30, 30, 10, 6, 6);
                int width = g.getFontMetrics().stringWidth(player.getName());
                g.setColor(new Color(0,0,0,190));

                g.fillRoundRect(player.getX() + 25 + xOffset - (width / 2) - 5, player.getY() + yOffset - 40, width + 10, 30, 2, 2);

                g.setColor(Color.WHITE);




                g.drawString(player.getName(), player.getX() + 25 + xOffset - (width/2), player.getY() + yOffset - 20);
            }
            for(GameObject object : world.getGameObjects()){
                g.setColor(Color.GREEN);
                java.util.List<Rectangle> intersections = new ArrayList<>();
                for(GameObject o2 : world.getGameObjects()){
                    if(o2 != object){
                        Rectangle rectangle = new Rectangle(object.getRectangle().x ,object.getRectangle().y,object.getRectangle().width, 1);
                        if(rectangle.intersects(o2.getRectangle()))
                        {
                            intersections.add(rectangle.intersection(o2.getRectangle()));
                        }
                    }
                }




                g.fillRoundRect(object.getRectangle().x + xOffset,object.getRectangle().y + yOffset,object.getRectangle().width,25,6,6);

                g.setColor(new Color(139,69,19));

                for(Rectangle rectangle : intersections){
                    g.fillRoundRect(rectangle.x + xOffset,rectangle.y + yOffset,rectangle.width,rectangle.height,6,6);
                }

                g.fillRoundRect(object.getRectangle().x + xOffset,object.getRectangle().y + yOffset + 20,object.getRectangle().width,object.getRectangle().height - 20,6,6);
                if(object.getImage().length()>0) {
                    BufferedImage img = database.get(object.getImage());
                    g.drawImage(img,object.getRectangle().x + xOffset,object.getRectangle().y + yOffset,object.getRectangle().width, object.getRectangle().height, null);
                }
            }
            for(Bullet bullet : world.getBullets()){
                if(bullet.getX() + xOffset < 0 || bullet.getX() + xOffset> getWidth() || bullet.getY() + yOffset > getHeight() || bullet.getY() + yOffset < 0)
                    continue;

                g.setColor(Color.RED);
                g.fillOval(bullet.getX() + xOffset,bullet.getY() + yOffset,bullet.getWidth(),bullet.getHeight());

            }
        }catch (ConcurrentModificationException ex){}
        Graphics2D g2 = (Graphics2D)g;
        g2.setFont(new Font("Arial Black",0,25));
        g.setColor(Color.WHITE);
        g2.drawString("Leader Board",getWidth() - 220, 25);
        HashMap<Player,Integer> ints = new HashMap<>();
        for(Player player : world.getPlayers()){
            ints.put(player,player.getScore());
        }
        Map<Player,Integer> sorted = sortByValue(ints);
        int y = 60;


        g2.setFont(new Font("Calibri",0,20));
        for(int i = sorted.keySet().size() - 1; i>= 0; i--){
            if(((Player)sorted.keySet().toArray()[i]).isShowInLeaderBoard())
                y+=25;
        }
        g.setColor(new Color(255,255,255,90));
        g.fillRoundRect(getWidth() - 250, -5, 250, y + 5, 5, 5);
        g.setColor(Color.WHITE);
        y = 60;
        for(int i = sorted.keySet().size() - 1; i>= 0; i--){
            Player player = (Player)sorted.keySet().toArray()[i];
            if(player.isShowInLeaderBoard()) {
                g2.drawString(player.getName(), getWidth() - 230, y);
                g2.drawString(player.getScore() + "", getWidth() - 75, y);
                y += 25;
            }
        }

//        g.fillOval(cur.x,cur.y,50,50);

        Notification notification = notifications.size() > 0 ? notifications.get(0) : null;
        //drawChatGUI(g);
        if(notification == null)
            return;
        if(notification.getUUID() == null){
            int notificationWidth = getWidth() - (getWidth() / 2);
            g.setColor(new Color(0,0,0, 224));
            notification.step();
            g.fillRoundRect(getWidth() / 4 ,notification.getY(),notificationWidth, 50, 6, 6);
            //notification.setY(0);

            int width = g.getFontMetrics().stringWidth(notification.getMessage());

            g.setColor(Color.WHITE);
            g.drawString(notification.getMessage(), getWidth() / 2 - (width / 2), 30 + notification.getY() );
        }
        else {
            Player player = world.getPlayerFromUUID(notification.getUUID());
            if (player != null) {
                notification.step();
                g.setColor(new Color(0, 0, 0, 224));
                int width = g.getFontMetrics().stringWidth(notification.getMessage());
                g.fillRoundRect(player.getX() + (25) - (width / 2) + xOffset - 3, player.getY() - 100 + yOffset, width + 6, 50, 2, 2);
                g.setColor(Color.WHITE);
                g.drawString(notification.getMessage(), player.getX() + (25) - (width / 2) + xOffset + 3, player.getY() - 100 + yOffset + 30);
            }
        }


        if(!notification.isAlive())
            notifications.remove(0);


    }

    public void drawChatGUI(Graphics g){
        int chatWidth = getWidth() / 2;
        int chatHeight = getHeight() - (getHeight() / 10 * 2);
        g.setColor(new Color(255,255,255,210));
        g.fillRoundRect((getWidth() / 2) - (chatWidth / 2), getHeight() / 10, chatWidth, chatHeight , 2, 2);
        List<Chat> chats = new ArrayList<>();
        chats.add(new Chat(Game.getInstance().getUUID()));
        chats.get(0).getMessages().add(new Message(Game.getInstance().getUUID(), "test"));

        int y = getHeight() / 10;
        int x = (getWidth() / 2) - (chatWidth / 2);

        for(Chat chat : chats){
            Player player = world.getPlayerFromUUID(chat.getUser());
            if(player == null)
                continue;
            g.setColor(Color.BLACK);
            g.drawString(player.getName(), x+20, y + 50);
            y += 100;
            g.drawLine(x,y,x+chatWidth,y);
        }


    }
    public List<Notification> notifications = new ArrayList<>();
//    public Notification notification = null;
    boolean movingDown = true;
    public static Map<Player, Integer> sortByValue(Map<Player, Integer> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(/*Collections.reverseOrder()*/))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public void showNotification(Notification notification){
        notifications.add(notification);

    }
}
