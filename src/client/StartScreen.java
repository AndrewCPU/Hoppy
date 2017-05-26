package client;

import client.gui.Option;
import server.MPServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stein on 5/25/2017.
 */
public class StartScreen extends JFrame{
    public static void main(String[] args) {
        if(args.length  == 0)
            new StartScreen();
        else
            new MPServer();
    }
    public StartScreen(){
        setLayout(null);
        setBounds(0,0,500,500);
        setTitle("Andrew's Game v1.0.0");

        Page page = new Page();

        page.setBounds(getBounds());
        add(page);
        page.addOption(new Option("Connect to a Server IP...",()->{
            setVisible(false);
            new Game(JOptionPane.showInputDialog("Please enter a server IP"));
        }));
        page.addOption(new Option("Connect to a local server...",()->{
            setVisible(false);
            new Game("");
        }));
        page.addOption(new Option("Start a server....",()->{
            setVisible(false);
            new MPServer();
        }));
        page.addOption(new Option("Exit",()->{
            System.out.println("Exitting");
            dispose();
        }));

        setVisible(true);
        addKeyListener(page);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}
class Page extends JComponent implements KeyListener{
    private List<Option> options = new ArrayList<>();

    private int selected = 0;

    public void next(){
        selected++;
        if(selected >= options.size())
            selected = 0;
    }
    public void back(){
        selected--;
        if(selected < 0)
            selected = options.size() - 1;
    }
    public void select(){
        options.get(selected).getAction().run();
    }

    public Page(List<Option> options) {
        this.options = options;
    }

    public Page(){
        addKeyListener(this);
    }

    public void addOption(Option option){
        options.add(option);
    }

    @Override
    public void paint(Graphics g) {
        int topY = 150;
        g.setColor(Color.BLACK);
        g.fillRect(0,0,getWidth(),getHeight());
        int i = 0;
        for(Option option : options){

            int width = g.getFontMetrics().stringWidth(option.getText());
            int height = (int)g.getFontMetrics().getStringBounds(option.getText(),g).getHeight();
            if(i == selected) {
                g.setColor(new Color(55, 71, 79));
                g.fillRect((getWidth() / 2) - width / 2 , topY - height, width + 5, height + 5);
            }
            g.setColor(Color.WHITE);
            g.drawString(option.getText(), (getWidth() / 2) - width / 2, topY);
            topY+=50;
            i++;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_RIGHT){
            next();
        }
        else if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_LEFT){
            back();
        }
        else if(e.getKeyCode()== KeyEvent.VK_ENTER){
            select();
        }
        //System.out.println("test");
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
