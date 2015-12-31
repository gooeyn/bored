package gooeyn.bored;

public class MyMessage {

    public String name;
    public boolean isFromMe = false;

    public MyMessage(String name, boolean isFromMe)
    {
        this.name = name;
        this.isFromMe = isFromMe;
    }

    public String getName()
    {
        return this.name;
    }
}
