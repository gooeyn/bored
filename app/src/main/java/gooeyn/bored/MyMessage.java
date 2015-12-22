package gooeyn.bored;

import java.util.ArrayList;

public class MyMessage {

    public String name;
    public ArrayList<String> messages;

    public MyMessage(String name)
    {
        this.name = name;
        messages = new ArrayList<>();
    }

    public void addMessage(String message)
    {
        messages.add(message);
    }

    public String getName()
    {
        return this.name;
    }
}
