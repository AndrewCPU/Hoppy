package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Created by stein on 5/15/2017.
 */
public class MapMaker extends JFrame {
    public static void main(String[] args) {
        new MapMaker();
    }
    public MapMaker(){
        setLayout(null);
        setBounds(0,0,1000,750);
        Canvas canvas = new Canvas();
        canvas.setBounds(getBounds());
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                canvas.setBounds(0,0,getWidth(),getHeight());
            }
        });
        add(canvas);
        setVisible(true);
    }
}
class Canvas extends JComponent implements MouseMotionListener,MouseListener{

    boolean pressing = false;
    private int key = 0;
    private Point press = null;
    private Point current = null;
    private java.util.List<Object> objects = new ArrayList<>();
    public Canvas(){
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        current= e.getPoint();
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        current= e.getPoint();
        repaint();

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON3){
            List<Object> inter = new ArrayList<>();
            for(Object object : objects){
                if(new Rectangle(object.getX(),object.getY(),object.getWidth(),object.getHeight()).contains(e.getPoint().x,e.getPoint().y)){
                    inter.add(object);
                }
            }
            objects.removeAll(inter);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

        if(new Rectangle(getWidth() - 75, 0, getWidth(), 25).contains(e.getPoint())){
            save();
            return;
        }


        pressing = true;
        press = e.getPoint();
        key = e.getButton();
    }

    public void save(){
        for(Object o : objects){
            System.out.println(o.getX() + " " + o.getY() + " " + o.getWidth() + " " + o.getHeight());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(pressing == false)
            return;
        pressing = false;
        if(key == 1){
            createBox(press,e.getPoint());
        }
        else{
            removeBoxes(press,e.getPoint());
        }

        press = null;
        key = 0;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
    public void createBox(Point point, Point point2){
        int x = point.x - point2.x;
        int y = point.y - point2.y;
        Point p = point2;
        if(x < 0){
            x = point2.x - point.x;
            y = point2.y - point.y;
            p = point;
        }
        objects.add(new Object(p.x,p.y,x,y));
    }
    public void removeBoxes(Point point, Point point2){
        Rectangle rect= new Rectangle();
        rect.setFrameFromDiagonal(point, point2);
        List<Object> os = new ArrayList<>();
        for(Object object : objects){
            if(rect.contains(new Rectangle(object.getX(),object.getY(),object.getWidth(),object.getHeight()))){
                os.add(object);
            }
        }
        objects.removeAll(os);
    }

    @Override
    public void paint(Graphics g) {

        g.setColor(Color.BLACK);

        g.drawString("SAVE",getWidth() - 50, 15);

        for(Object o : objects){
            g.fillRect(o.getX(),o.getY(),o.getWidth(),o.getHeight());
        }
        if(pressing){
            g.setColor(new Color(0,0,0,85));
            g.fillRect(press.x,press.y,current.x - press.x,current.y - press.y);
        }
    }
}
class Object{
    private int width, height, x, y;
    public Object(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}