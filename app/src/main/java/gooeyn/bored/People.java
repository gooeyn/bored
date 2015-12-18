package gooeyn.bored;

public class People {

    String name;
    String status;

    public People(String name, String status)
    {
        this.name = name;
        this.status = status;
    }

    public String getName()
    {
        return name;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }
}
