package gooeyn.bored;

public class MyChat {

    String name;
    String profile;
    String id;
    String status;

    public MyChat(String name, String profile, String id, String status)
    {
        this.name = name;
        this.profile = profile;
        this.id = id;
        this.status = status;
    }

    public String getName()
    {
        return name;
    }
    public String getId()
    {
        return id;
    }

    public void setProfile(String profile)
    {
        this.profile = profile;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public void setId(String id)
    {
        this.id = id;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

}
