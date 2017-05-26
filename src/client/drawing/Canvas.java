package client.drawing;

import client.Game;
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
import java.util.stream.Collectors;

/**
 * Created by stein on 5/14/2017.
 */
public class Canvas extends JComponent implements MouseMotionListener{
    private World world;
    private Image background;
    private Point cur;
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
        g.setColor(new Color(135,206,250)); //todo follow uid
        int yOffset = 0;
        int xOffset = 0;
        //250 = 10 + y
        if(Game.getInstance().getUUID() != null && world!=null && Game.getInstance() != null && world.getPlayerFromUUID(Game.getInstance().getUUID())!=null){
            xOffset = getWidth() / 2 - world.getPlayerFromUUID(Game.getInstance().getUUID()).getX();
            yOffset = getHeight() / 2 - world.getPlayerFromUUID(Game.getInstance().getUUID()).getY();
        }
        //g.drawImage(background, (xOffset / 50) - xO,0, getWidth() + xO, getHeight(), null);

        g.fillRect(0,0,getWidth(),getHeight());
        try{
            for(Player player : world.getPlayers()){
                g.setColor(player.getColor());
                g.fillRoundRect(player.getX() + xOffset,player.getY() + yOffset,50,50, 10, 10);
                g.setColor(Color.WHITE);
                g.drawString(player.getName(), player.getX() + xOffset, player.getY() + yOffset - 10);
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


            }
            for(Bullet bullet : world.getBullets()){
                if(bullet.getX() + xOffset < 0 || bullet.getX() + xOffset> getWidth() || bullet.getY() + yOffset > getHeight() || bullet.getY() + yOffset < 0)
                    continue;

                g.setColor(Color.RED);
                g.fillOval(bullet.getX() + xOffset,bullet.getY() + yOffset,bullet.getWidth(),bullet.getHeight());

            }
        }catch (ConcurrentModificationException ex){}

        g.setColor(new Color(255,255,255,90));
        g.fillRoundRect(getWidth() - 250, -5, 250, 500, 5, 5);
        g.setColor(Color.WHITE);
        Graphics2D g2 = (Graphics2D)g;
        g2.setFont(new Font("Arial Black",0,25));
        g2.drawString("Leader Board",getWidth() - 220, 25);
        HashMap<Player,Integer> ints = new HashMap<>();
        for(Player player : world.getPlayers()){
            ints.put(player,player.getScore());
        }
        Map<Player,Integer> sorted = sortByValue(ints);
        int y = 60;
        g2.setFont(new Font("Calibri",0,20));
        for(int i = sorted.keySet().size() - 1; i>= 0; i--){
            Player player = (Player)sorted.keySet().toArray()[i];
            g2.drawString(player.getName() , getWidth() - 230, y);
            g2.drawString(player.getScore() + "", getWidth() - 75, y);
            y+=25;
        }

//        g.fillOval(cur.x,cur.y,50,50);

        if(notification == null)
            return;

        int notificationWidth = getWidth() - (getWidth() / 2);
        g.setColor(new Color(0,0,0, 224));
        notification.step();
        g.fillRoundRect(getWidth() / 4 ,notification.getY(),notificationWidth, 50, 6, 6);
        //notification.setY(0);

        int width = g.getFontMetrics().stringWidth(notification.getMessage());

        g.setColor(Color.WHITE);
        g.drawString(notification.getMessage(), getWidth() / 2 - (width / 2), 30 + notification.getY() );




    }
    public Notification notification = null;
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
        this.notification = notification;
    }
}
