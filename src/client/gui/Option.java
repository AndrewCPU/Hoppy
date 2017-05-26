package client.gui;

/**
 * Created by stein on 5/25/2017.
 */
public class Option {

    private String text;

    private Runnable action;

    public Option(String text, Runnable action) {
        this.text = text;
        this.action = action;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Runnable getAction() {
        return action;
    }

    public void setAction(Runnable action) {
        this.action = action;
    }
}
